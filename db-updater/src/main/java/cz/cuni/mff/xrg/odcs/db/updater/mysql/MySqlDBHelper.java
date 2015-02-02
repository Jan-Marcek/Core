package cz.cuni.mff.xrg.odcs.db.updater.mysql;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cz.cuni.mff.xrg.odcs.db.updater.DBHelper;


public class MySqlDBHelper extends DBHelper {
    private static final String SCHEMA_PATH = "/db/mysql/schema.sql";
    private static final String DATA_PATH = "/db/mysql/data.sql";
    private static final String UPDATES_DIR_PROPERTY = "mysql.updates.dir";
    private static final String UPDATES_FILES_PROPERTY = "mysql.updates.files";
    
    public MySqlDBHelper() throws IOException {
        super();
    }

    @Override
    public void createDB(Connection conn, String dbName, String owner) throws SQLException {
        System.out.println("CREATING DB");
        Statement st = conn.createStatement();
        st.execute("CREATE DATABASE " + dbName + 
                    " DEFAULT CHARACTER SET utf8 " +
                    " DEFAULT COLLATE utf8_general_ci;");
    }

    @Override
    public void createUser(Connection conn, String username, String password) throws SQLException {
        System.out.println("CREATING USER");
        // check if user exists
        PreparedStatement prepSt = conn.prepareStatement("select count(1) FROM mysql.user WHERE User = ?");
        prepSt.setString(1, username);
        ResultSet rs = prepSt.executeQuery();
        boolean userExists = false;
        
        if (rs.next()) {
            userExists = rs.getInt(1) == 1;
        }
                
        // create if it doesn't exist
        Statement st = null;
        if (!userExists) {
            st = conn.createStatement();
            st.execute("CREATE USER '" + username + "' IDENTIFIED BY '" + password + "'");
        } else {
            System.out.println("USER already exists");
        }
        
    }

    @Override
    public void grantPrivilegesToUser(Connection conn, String username, String dbName) throws SQLException {
        Statement st = conn.createStatement();
        st.execute("GRANT ALL ON " + dbName + ".* TO " + username);
    }
    
    @Override
    public boolean checkDBExists(Connection conn, String dbName) throws SQLException {
        ResultSet result;
        result = conn.getMetaData().getCatalogs();
        while (result.next()) {
            if (dbName.equals(result.getString(1))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public InputStream getSchemaResourceInputStream() throws FileNotFoundException {
        InputStream is = this.getClass().getResourceAsStream(SCHEMA_PATH);
        if (is == null) {
            throw new FileNotFoundException("Couldn't find schema in resources: " + SCHEMA_PATH);
        }
        return is;
    }

    @Override
    public InputStream getDataResourceInputStream() throws FileNotFoundException {
        InputStream is = this.getClass().getResourceAsStream(DATA_PATH);
        if (is == null) {
            throw new FileNotFoundException("Couldn't find data in resources: " + DATA_PATH);
        }
        return is;
    }

    @Override
    public String[] getUpdatesFileNames() {
        String filesProp = properties.getProperty(UPDATES_FILES_PROPERTY, "");
        if (filesProp.isEmpty()) {
            return null;
        }
        String[] fileNames = filesProp.trim().split("\\s*,\\s*");
        return fileNames;
    }

    @Override
    public InputStream getUpdateFileInputStream(String fileName) throws FileNotFoundException {
        String filePath = properties.getProperty(UPDATES_DIR_PROPERTY) + "/" + fileName;
        InputStream is = this.getClass().getResourceAsStream(filePath);
        if (is == null) {
            throw new FileNotFoundException("Couldn't find update file in resources: " + filePath);
        }
        return is;
    }
}
