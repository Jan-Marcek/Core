package cz.cuni.mff.xrg.odcs.db.updater.postgres;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cz.cuni.mff.xrg.odcs.db.updater.DBHelper;


public class PostgreSqlDBHelper extends DBHelper {
    private static final String SCHEMA_PATH = "/db/postgresql/schema.sql";
    private static final String DATA_PATH = "/db/postgresql/data.sql";
    private static final String UPDATES_DIR_PROPERTY = "postgresql.updates.dir";
    private static final String UPDATES_FILES_PROPERTY = "postgresql.updates.files";
    
    public PostgreSqlDBHelper() throws IOException {
        super();
    }
    
    @Override
    public String getInitConnectionString(String connectionString) {
        return super.getInitConnectionString(connectionString) +  "template1";
    }

    @Override
    public void createDB(Connection conn, String dbName, String owner) throws SQLException {
        System.out.println("CREATING DB");
        Statement st = conn.createStatement();
        st.execute("CREATE DATABASE " + dbName + " WITH OWNER " + owner + " ENCODING = 'UTF-8'");
    }

    @Override
    public void createUser(Connection conn, String username, String password) throws SQLException {
        // check if user exists
        PreparedStatement prepSt = conn.prepareStatement("select count(1) from pg_user where usename = ?");
        prepSt.setString(1, username);
        ResultSet rs = prepSt.executeQuery();
        boolean userExists = false;
        
        if (rs.next()) {
            userExists = rs.getInt(1) == 1;
        }
        
        // create if it doesn't exist
        if (!userExists) {
            System.out.println("CREATING USER ");
            Statement st = conn.createStatement();
            st.execute("CREATE USER " + username + " WITH PASSWORD '" + password + "'");
        } else {
            System.out.println("USER already exists");
        }
        
    }

    @Override
    public void grantPrivilegesToUser(Connection conn, String username, String dbName) throws SQLException {
        Statement st = conn.createStatement();
        st.execute("GRANT ALL PRIVILEGES ON DATABASE " + dbName + " TO " + username);
    }

    @Override
    public boolean checkDBExists(Connection conn, String dbName) throws SQLException {
        PreparedStatement st = conn.prepareStatement("select count(1) from pg_catalog.pg_database as d where d.datname = ?");
        st.setString(1, dbName);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) == 1; // db exists count == 1
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
