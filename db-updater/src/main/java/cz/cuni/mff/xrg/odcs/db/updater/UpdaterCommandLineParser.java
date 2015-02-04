package cz.cuni.mff.xrg.odcs.db.updater;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class UpdaterCommandLineParser {

    public static void main(String[] args) {
        Options options = new Options();
        
        options.addOption("C", "create", false, "create and init DB and user");
        options.addOption("U", "update", false, "use update sql file on DB");
        
        options.addOption("t", "dbtype", true, "database type e.g. mysql or postgresql (REQUIRED)");
        options.addOption("c", true, "full db connection string (REQUIRED)");
        options.addOption("d", "driver", true, "driver class string (REQUIRED)");
        
        options.addOption("u", "user", true, "db username  (REQUIRED)");
        options.addOption("p", "password", true, "db user password  (REQUIRED)");
        options.addOption("au", true, "db admin username  (REQUIRED for -C)");
        options.addOption("ap", true, "db admin password (REQUIRED for -C)");
        
        CommandLineParser parser = new BasicParser();
        
        try {
            // parse the command line arguments
            CommandLine line = parser.parse( options, args );
            
            int numberOfActionOption = 0;
            if (line.hasOption("C")) {
                numberOfActionOption++;
            }
            if (line.hasOption("U")) {
                numberOfActionOption++;
            }
            
            if (numberOfActionOption != 1) {
                throw new ParseException("One and only one of option -U and -C is required.");
            }
            
            String dbType = getOption("t", line, true);
            String connectionString = getOption("c", line, true);
            String driver = getOption("d", line, true);

            String user = getOption("u", line, true);
            String userPassword = getOption("p", line, true);

            DBUpdater updater = new DBUpdater(dbType, connectionString, driver, user, userPassword);

            if (line.hasOption("C")) {
                String admin = getOption("au", line, true);
                String adminPassword = getOption("ap", line, true);

                updater.createAndInitDB(admin, adminPassword);
            } else {
                updater.startUpdate();
            }
        } catch (ParseException e) {
            System.out.println("Unexpected exception:" + e.getMessage());
            new HelpFormatter().printHelp("jar", options);
        } catch (Exception e) {
            System.err.println("UPDATER FAILED !!!");
            e.printStackTrace();
        }
    }
    
    public static String getOption(String option, CommandLine line, boolean required) throws ParseException {
        if (required && !line.hasOption(option)) {
            throw new ParseException("Option " + option + " is required!");
        }
        return line.getOptionValue(option);
    }
}
