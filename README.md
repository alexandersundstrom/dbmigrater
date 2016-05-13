# DB Migrater
## Overview
This module is used to export data from the old conference database, and to import it into the new conference database.

It contains two classes for this purpose:

* Exporter
* Importer


Both classes have JavaDocs and tests for the important methods.

The Exporter export each table into a sql file. This file is then used by the Importer to import it into the new system. It only imports rows to the new database that hasn't already been imported, this is to ensure support for multiple imports over a period of time where changes might be made in both systems.

## Setup
There is a few things that needs to be updated *one* time before Exporter and Importer can be used.

* A view needs to be created in the old system for some querying, this can be copied from `resources/event-view.sql`

### Speakers
* ALTER TABLE speakers ADD COLUMN middle_name character varying(255);
* ALTER TABLE speakers ADD COLUMN address character varying(50);
* ALTER TABLE speakers ADD COLUMN zip_code character varying(10);
* ALTER TABLE speakers ADD COLUMN city character varying(50);
* ALTER TABLE speakers ADD COLUMN postalbox character varying(30);
* ALTER TABLE speakers ADD COLUMN country character varying(255);
* ALTER TABLE speakers ADD COLUMN homepage character varying(255);
* ALTER TABLE speakers ADD COLUMN testimonial text;
* ALTER TABLE speakers ADD COLUMN state character varying(100);
* ALTER TABLE speakers ADD COLUMN twitter_name character varying(35);
* ALTER TABLE speakers ADD COLUMN company_old text;
* ALTER TABLE speakers ADD COLUMN old_id bigint;


* ALTER TABLE speakers
  ADD CONSTRAINT old_id UNIQUE(old_id);

### Conferences
* ALTER TABLE conferences ADD COLUMN old_id bigint;


* ALTER TABLE conferences
  ADD CONSTRAINT old_id_conference UNIQUE(old_id);

### Sessions
* ALTER TABLE sessions ADD COLUMN old_id bigint;
* ALTER TABLE sessions ADD COLUMN note text;
* ALTER TABLE sessions ADD COLUMN short_title character varying(50);
* ALTER TABLE sessions ADD COLUMN slide_url text;
* ALTER TABLE sessions ADD COLUMN video_url text;
* ALTER TABLE public.sessions ADD COLUMN old_event_slot_slot_id bigint;


* ALTER TABLE sessions
    ADD CONSTRAINT sessions_old_event_slot_slot_id UNIQUE(old_event_slot_slot_id);

### Locations
* ALTER TABLE locations ADD COLUMN old_id bigint;


* ALTER TABLE locations
    ADD CONSTRAINT locations_old_id UNIQUE(old_id);

### Tracks
* ALTER TABLE tracks ADD COLUMN old_id bigint;


* ALTER TABLE tracks
    ADD CONSTRAINT tracks_old_id UNIQUE(old_id);

### Time_slots
* ALTER TABLE time_slots ADD COLUMN old_id bigint;


* ALTER TABLE time_slots
    ADD CONSTRAINT "time _slots_old_id" UNIQUE(old_id);

## How to use it
As of 2016-05-12, both classes are setup to work against a local copy of both databases.
To use it against the real databases, update the following strings in both classes so that they are pointing to the correct databases:

* DB_URL
* USER 
* PASS

Next step is to simply run the main method of Exporter, and then Importer, that's all!

## Supported tables
Exporting is as of 2016-05-12 supported for Speakers, Conferences, Locations, Tracks, Slots and Events. 



