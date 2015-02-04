package cz.cuni.mff.xrg.odcs.db.updater;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;

public abstract class DBHelper {
    private static final String PROPERTIES_RESOURCE_PATH = "/db/resources.properties";
    protected String currentDbVersion;
    protected Properties properties;
    
    public DBHelper() throws IOException {
        this.properties = new Properties();
        properties.load(this.getClass().getResourceAsStream(PROPERTIES_RESOURCE_PATH));
    }
    
    public abstract boolean checkDBExists(Connection conn, String dbName) throws SQLException;
    
    public abstract InputStream getSchemaResourceInputStream() throws FileNotFoundException;
    
    public abstract InputStream getDataResourceInputStream() throws FileNotFoundException;
    
    public abstract String[] getUpdatesFileNames();
    
    public abstract InputStream getUpdateFileInputStream(String fileName) throws FileNotFoundException;
    

    /**
     * Gets the current version of UV core which can be also used to
     * determine DB version and so which updates script should be
     * applied 
     * 
     * @param conn
     * @throws SQLException
     * @throws NumberFormatException
     *          if the format the property is wrong
     */
    public void checkCurrentDbVersion(Connection conn) throws SQLException, NumberFormatException {
        String query = "select p.value from properties as p where p.key = 'UV.Core.version'";
        Statement st = conn.createStatement();
        ResultSet result = st.executeQuery(query);
        
        if (result.next()) {
            currentDbVersion = result.getString(1);
            String[] version = currentDbVersion.split("\\.");
            
            // formating version
            StringBuffer v = new StringBuffer();
            for (String ver : version) {
                if (v.length() != 0) {
                    v.append(".");
                }
                v.append(Integer.parseInt(ver));
            }
            currentDbVersion = v.toString();
        }
        
    }

    public String getCurrentDbVersion() {
        return currentDbVersion;
    }

    public abstract void createDB(Connection conn, String dbName, String owner) throws SQLException;

    public abstract void createUser(Connection conn, String username, String password) throws SQLException;
    
    public abstract void grantPrivilegesToUser(Connection conn, String username, String dbName) throws SQLException;
    
    public void initDB(Connection conn) throws FileNotFoundException, SQLException {
        
        System.out.println("EXECUTING schema\n=====================");
        runSchemaScript(conn);
        // don't need to close InputStream, runScript method does that through Scanner
        
        System.out.println("EXECUTING data\n=====================");
        runDataScript(conn);
    }
    
    protected void runSchemaScript(Connection conn) throws FileNotFoundException, SQLException {
        InputStream in = getSchemaResourceInputStream();
        runScript(in, conn);
    }
    
    protected void runDataScript(Connection conn) throws FileNotFoundException, SQLException {
        InputStream in = getDataResourceInputStream();
        runScript(in, conn);
    }
    
    /**
     * Runns all SQL commands in the specified file. Starts blocks
     * separately, usind ; as delimiter. 
     * 
     * @param sqlFile
     * @param conn
     * @throws FileNotFoundException
     * @throws SQLException
     */
    protected void runScript(InputStream sqlFile, Connection conn) throws FileNotFoundException, SQLException {
        Scanner read = new Scanner(sqlFile);
        
        read.useDelimiter(";");
        Statement st = null;
        while (read.hasNext()) {
            String query = read.next().trim();
            if (query.isEmpty()) {
                continue;
            }
            query += ";";
            System.out.println(query + "\n");
            st = conn.createStatement();
            st.execute(query);
        }
        
        read.close();
    }
    
    /**
     *  
     * 
     * @param conn
     * @throws FileNotFoundException
     * @throws SQLException
     */
    public void applyUpdates(Connection conn) throws FileNotFoundException, SQLException {
        String[] updateSqlFileNames = getUpdatesFileNames();
        if(updateSqlFileNames == null || updateSqlFileNames.length == 0) {
            System.out.println("There are no update files.");
            return;
        }
        
        System.out.println("Current version: " + currentDbVersion);
        
        InputStream in;
        for (String updateSqlFile : updateSqlFileNames) {
            in = getUpdateFileInputStream(updateSqlFile);
            
            String version = updateSqlFile.split("-")[0];
            if(versionCompare(version, currentDbVersion) > 0) {
                System.out.println("updating ... (" + updateSqlFile + ")");
                runScript(in, conn);
            } else {
                System.out.println("NOT using update file: " + updateSqlFile + " (version < current version)");
            }
        }        
    }
    
    /**
     * Compares two version strings. 
     * 
     * Use this instead of String.compareTo() for a non-lexicographical 
     * comparison that works for version strings. e.g. "1.10".compareTo("1.6").
     * 
     * @note It does not work if "1.10" is supposed to be equal to "1.10.0".
     * @see http://stackoverflow.com/questions/6701948/efficient-way-to-compare-version-strings-in-java
     * 
     * @param str1 a string of ordinal numbers separated by decimal points. 
     * @param str2 a string of ordinal numbers separated by decimal points.
     * @return The result is a negative integer if str1 is _numerically_ less than str2. 
     *         The result is a positive integer if str1 is _numerically_ greater than str2. 
     *         The result is zero if the strings are _numerically_ equal.
     */
    public static Integer versionCompare(String str1, String str2)
    {
        String[] vals1 = str1.split("\\.");
        String[] vals2 = str2.split("\\.");
        int i = 0;
        // set index to first non-equal ordinal or length of shortest version string
        while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
          i++;
        }
        // compare first non-equal ordinal number
        if (i < vals1.length && i < vals2.length) {
            int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
            return Integer.signum(diff);
        }
        // the strings are equal or one string is a substring of the other
        // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
        else {
            return Integer.signum(vals1.length - vals2.length);
        }
    }
    
    public String getInitConnectionString(String connectionString) {
        return getConnectionStringWithoutDbName(connectionString);
    }
    
    private String getConnectionStringWithoutDbName(String connectionString) {
        return connectionString.substring(0, connectionString.lastIndexOf("/") + 1);
    }

}
