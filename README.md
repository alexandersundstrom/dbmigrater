# DB Migrater
### Overview
This module is used to export data from the old conference database, and to import it into the new conference database.

It contains two classes for this purpose:

* Exporter
* Importer


Both classes have JavaDocs and tests for the important methods.

The Exporter export each table into a sql file. This file is then used by the Importer to import it into the new system. It only imports rows to the new database that hasn't already been imported, this is to ensure support for multiple imports over a period of time where changes might be made in both systems.

### How to use it
As of 2016-05-12, both classes are setup to work against a local copy of both databases.
To use it against the real databases, update the following strings in both classes so that they are pointing to the correct databases:

* DB_URL
* USER 
* PASS

Next step is to simply run the main method of Exporter, and then Importer, that's all!

### Supported tables
Exporting is as of 2016-05-12 supported for Speakers, Conferences, Locations, Tracks, Slots and Events. 



