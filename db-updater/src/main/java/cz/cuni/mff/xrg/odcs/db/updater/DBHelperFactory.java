package cz.cuni.mff.xrg.odcs.db.updater;

import java.io.IOException;

import cz.cuni.mff.xrg.odcs.db.updater.mysql.MySqlDBHelper;
import cz.cuni.mff.xrg.odcs.db.updater.postgres.PostgreSqlDBHelper;


public class DBHelperFactory {

    public static DBHelper getInstance(String dbType) throws IOException {
        if (dbType.equalsIgnoreCase("mysql")) {
            return new MySqlDBHelper();
        } else if (dbType.toLowerCase().contains("postgres")) {
            return new PostgreSqlDBHelper();
        }
        return null;
    }

}
