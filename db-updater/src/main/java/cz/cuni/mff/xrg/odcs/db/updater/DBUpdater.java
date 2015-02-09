package cz.cuni.mff.xrg.odcs.db.updater;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUpdater {
    
    private String dbType;
    private String connectionString;
    private String driver;
    private DBHelper dbHelper;
    private String username;
    private String password;

    /**
     * Constructor
     * 
     * @param dbType 
     *          'mysql' and 'postgres' / 'postgresql' currently supported
     * @param connectionString
     *          full connection string with DB name too !
     * @param driver
     *          driver class string
     * @param username
     *          name of user operating under DB
     * @param password
     *          password of this user
     *          
     * @throws ClassNotFoundException
     *          when the driver for given driver string couldn't be found 
     * @throws IOException
     *          if property file couldn't be found
     */
    public DBUpdater(String dbType, String connectionString, String driver,
            String username, String password) throws ClassNotFoundException, IOException {
        this.dbType = dbType;
        this.connectionString = connectionString;
        this.driver = driver;
        this.username = username;
        this.password = password;
        
        this.dbHelper = DBHelperFactory.getInstance(dbType);
        Class.forName(driver);
    }
    
    public String getDbType() {
        return dbType;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public String getDriver() {
        return driver;
    }

    public String getUsername() {
        return username;
    }

    public boolean needsInitialization() throws SQLException {
        return !dbExists() || !dbInitialized();
    }
    
    /**
     * Creates the DB and runs the schema and data sql files on it.
     * But before that checks if user (see constructor) exists and creates
     * it if necessary.
     * 
     * @param adminUsername user with privileges to create DB and users (will be db owner)
     * @param adminPassword
     * @throws SQLException
     * @throws FileNotFoundException when schema.sql or data.sql could not be found in the resources 
     */
    public void createAndInitDB(String adminUsername, String adminPassword) throws SQLException, FileNotFoundException {
        
        System.out.println("STARTING CREATE AND INIT DB");
        
        createDB(adminUsername, adminPassword);
        
        if (!dbInitialized()) {
            initDB();
        } else {
            System.out.println("DB already initialized");
        }

        System.out.println("END OF CREATE AND INIT DB");
    }
    
    /**
     * Inits DB, that means runs the schema and data scripts 
     * 
     * @throws SQLException
     * @throws FileNotFoundException
     */
    private void initDB() throws SQLException, FileNotFoundException {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(connectionString, username, password);
            conn.setAutoCommit(false);
            dbHelper.initDB(conn);
            conn.commit();
        } catch(Exception e) {
            if(conn != null && !conn.getAutoCommit()) {
                conn.rollback();
                System.out.println("rollbacking changes ...");
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        
    }

    /**
     * Creates DB (if it doesn't exist already) and grants privileges to the given username 
     * 
     * @param adminUsername
     * @param adminPassword
     * @throws SQLException
     */
    private void createDB(String adminUsername, String adminPassword) throws SQLException {
        System.out.println("STARTING CREATE DB");
        Connection conn = null;
        try {
            // we need default connection string in case the db doesn't exist and needs to be created
            String initConnString = dbHelper.getInitConnectionString(connectionString);
            conn = DriverManager.getConnection(initConnString, adminUsername, adminPassword);
            String dbName = getDBName(connectionString);
            
            dbHelper.createUser(conn, username, password);
            
            boolean dbExists = dbHelper.checkDBExists(conn, dbName);
            if(!dbExists) {
                // DB doesn't exist => create it 
                dbHelper.createDB(conn, dbName, adminUsername);
            } else {
                System.out.println("DB exist => NO db create");
            }
            
            System.out.println("Setting user privileges ...");
            dbHelper.grantPrivilegesToUser(conn, username, dbName);
            System.out.println("User privileges set succesfully.");
        } catch(Exception e) {
            if(conn != null && !conn.getAutoCommit()) {
                conn.rollback();
                System.out.println("rollbacking changes ...");
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        System.out.println("END OF CREATE DB");
    }

    /**
     * 
     * 
     * @throws SQLException
     * @throws FileNotFoundException
     */
    public void startUpdate() throws SQLException, FileNotFoundException {
        System.out.println("STARTING UPDATE DB");
        
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(connectionString, username, password);
            conn.setAutoCommit(false);
            
            dbHelper.checkCurrentDbVersion(conn);
            
            dbHelper.applyUpdates(conn);
            conn.commit();

        } catch(Exception e) {
            if(conn != null && !conn.getAutoCommit()) {
                conn.rollback();
                System.out.println("rollbacking changes ...");
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        
        System.out.println("END OF UPDATE DB");
    }
    
    /**
     * Checks if there Database exists.
     * 
     * @return
     */
    private boolean dbExists() {
        Connection conn = null;
        
        try {
            // we need default connection string in case the db doesn't exist and needs to be created
            conn = DriverManager.getConnection(connectionString, username, password);
            return true;
        } catch (SQLException e) {
            System.err.println("Check DB exists: Error while connecting to DB: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Checks if db is initialized on the basis of existing the properties table
     * and the DB version being set.
     * 
     * @return
     * @throws SQLException
     */
    private boolean dbInitialized() throws SQLException {
        Connection conn = null;
        
        try {
            // we need default connection string in case the db doesn't exist and needs to be created
            conn = DriverManager.getConnection(connectionString, username, password);
            
            ResultSet result = conn.getMetaData().getTables(null, null, "properties", null);
            
            if (result.next()) { // table "properties" exists 
                dbHelper.checkCurrentDbVersion(conn);
                return true;
            } else {
                return false;
            }
            
        } catch (NumberFormatException e) {
            return false; // the version isn't set, or its set wrong
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    private String getDBName(String connectionString) {
        String[] splitted = connectionString.split("/");
        return splitted[splitted.length-1];
    }

    @Override
    public String toString() {
        return "DBUpdater [dbType=" + dbType + ", connectionString=" + connectionString + ", driver=" + driver + "]";
    }
    
}
