package eu.unifiedviews.dataunit.relational.repository;

import java.sql.Connection;
import java.sql.SQLException;

import eu.unifiedviews.dataunit.relational.db.DataUnitDatabaseConnectionProvider;
import eu.unifiedviews.dataunit.relational.db.DatabaseWrapperIF;

public class DataUnitDatabaseConnectionProviderImpl implements DataUnitDatabaseConnectionProvider {
    private DatabaseWrapperIF databaseWrapper;

    public DataUnitDatabaseConnectionProviderImpl(DatabaseWrapperIF databaseWrapper) {
        this.databaseWrapper = databaseWrapper;
    }

    @Override
    public Connection getDatabaseConnection() throws SQLException {
        return this.databaseWrapper.getConnection();
    }

    @Override
    public void release() throws Exception {
        this.databaseWrapper.shutdown();
    }

}
