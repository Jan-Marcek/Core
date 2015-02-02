package cz.cuni.mff.xrg.odcs.db.updater;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUpdater {
    
    private String dbType;
    private String connectionString;
    private String driver;
    private DBHelper dbHelper;

    /**
     * Constructor
     * 
     * @param dbType 
     *          'mysql' and 'postgres' / 'postgresql' currently supported
     * @param connectionString
     *          full connection string with DB name too !
     * @param driver
     *          driver class string
     *          
     * @throws ClassNotFoundException
     *          when the driver for given driver string couldn't be found 
     * @throws IOException
     *          if property file couldn't be found
     */
    public DBUpdater(String dbType, String connectionString, String driver) throws ClassNotFoundException, IOException {
        this.dbType = dbType;
        this.connectionString = connectionString;
        this.driver = driver;
        
        this.dbHelper = DBHelperFactory.getInstance(dbType);
        Class.forName(driver);
    }
    
    /**
     * Creates the DB and runs the schema and data sql files on it.
     * But before that checks if user with username exists and creates
     * it if necessary.
     * 
     * @param adminUsername user with privileges to create DB and users (will be db owner)
     * @param adminPassword
     * @param username user with privileges on created DB
     * @param password
     * @throws SQLException
     * @throws FileNotFoundException when schema.sql or data.sql could not be found in the resources 
     */
    public void createAndInitDB(String adminUsername, String adminPassword,
            String username, String password) throws SQLException, FileNotFoundException {
        
        System.out.println("STARTING CREATE AND INIT DB");
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
            conn.close();
            
            if (!dbExists) {
                conn = DriverManager.getConnection(connectionString, username, password);
                conn.setAutoCommit(false);
                dbHelper.initDB(conn);
                conn.commit();
            } else {
                System.out.println("DB exist => NO db init (schema and data)");
            }
            
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
        System.out.println("END OF CREATE AND INIT DB");
    }

    /**
     * 
     * 
     * @param username
     * @param password
     * @throws SQLException
     * @throws FileNotFoundException
     */
    public void startUpdate(String username, String password) throws SQLException, FileNotFoundException {
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
     * 
     * @param adminUsername user with privileges to access the information
     * @param adminPassword
     * @return
     * @throws SQLException
     */
    public boolean dbExists(String adminUsername, String adminPassword) throws SQLException {
        Connection conn = null;
        
        try {
            // we need default connection string in case the db doesn't exist and needs to be created
            String initConnString = dbHelper.getInitConnectionString(connectionString);
            conn = DriverManager.getConnection(initConnString, adminUsername, adminPassword);
            String dbName = getDBName(connectionString);
            
            return dbHelper.checkDBExists(conn, dbName);
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
}
