# package-data

This is the command line tool used to process and manage transit data for the Transpo server. 

If order to use this tool, you must have a valid `config.properties` file located inside of the executable directory. You can see `example.config.properties` for a sample configuration file. 

The configuration file must map to an already-setup PostgreSQL server instance that has the user (role) that you specify in the configuration file, as well as an empty `transit` database (or whatever database you have set in the configuration). 
Before using any of the commands to process and manage data, you must run the `configure` command which will handle the creation of the database schema and other initial configuration details in the server.
All future commands will use the configuration details stored in the `config.properties` file.

For packaging purposes, your local PostgreSQL instance must also have a `packaging` database that can be used to package the dataset. You must also specify `PG_DUMP` and `PG_RESTORE` in your `config.properties`.

## Commands

### `info`
Displays the configuration information that is being used by the program.

### `configure`
The configure command will apply the database schema to the transit database as specified in the configuration file.

### `package [options] <gtfs file>`
Packages a GTFS file into a data package that can be uploaded to the server using the `upload` command. 
This command converts the GTFS data into both the set of data that will be used by individual devices, as well as a full GTFS dataset which is uploaded directly into the server.

This command outputs a .zip file with the version name of the data package.

Options:
 * `--revision <n>`: Specifies the revision number of the data package if multiple data packages are generated on the same day.
 
### `upload <version>` 
Uploads a specified data package version to the server. This will copy the generated .zip file created by the package command to the server specified in configuration file and copy all data to the SQL server.

This command **must** be run on the target machine. The zip files are copied directly through the filesystem.

You must specify `PG_RESTORE` in your `config.properties`.

Options:
 * `<version>` The version number of the data package to upload. Will look for `<version>.zip` as the package.