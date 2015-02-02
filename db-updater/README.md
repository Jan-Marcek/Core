db-updater
==============
DB updater is responsible for automatic creation or update of DB. This module can:

* create DB, run schema and data scripts, grant privileges to selected user
* run update scripts from listed in properties file (runs only updates with appropriate version)
* be run from command line
* supports mysql and postgresql DB
