package dk.eos.migration;


import java.io.PrintWriter;
import java.sql.*;
import java.util.Calendar;

/**
 * This class is used to Export tables from the old conference database into sql files, that can be imported to the new conference database, using the Importer.
 * Created by Alexander Sundstrom on 15/04/16.
 */
public class Exporter {
  //  DATABASE SETUP
  private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
  private static final String DB_URL = "jdbc:mysql://localhost:8889/oldconf";
  private static final String USER = "root";
  private static final String PASS = "root";

  //  MISC
  public static final String DELIMITER = ",";
  public static final String UTF8 = "UTF-8";
  private int count = 0;

  //  ON CONFLICT STATEMENTS
  public static final String ON_CONFLICT_STATEMENT_OLD_ID = "ON CONFLICT (old_id) DO NOTHING;";
  private static final String ON_CONFLICT_STATEMENT_TRACKS = "ON CONFLICT (old_event_id) DO NOTHING;";
  private static final String ON_CONFLICT_STATEMENT_SESSIONS = "ON CONFLICT (old_event_slot_slot_id) DO NOTHING;";

  //  FILES
  private static final String PATH = "./dbmigration/files/";
  public static final String SPEAKER_FILE = PATH + "speakers.sql";
  public static final String CONFERENCE_FILE = PATH + "conferences.sql";
  public static final String LOCATION_FILE = PATH + "locations.sql";
  public static final String TRACK_FILE = PATH + "tracks.sql";
  public static final String TIME_SLOT_FILE = PATH + "timeslot.sql";
  public static final String SESSION_FILE = PATH + "sessions.sql";

  //  TABLES
  public static final String SPEAKER_TABLE = "oldconf.speaker";
  public static final String CONFERENCE_TABLE = "oldconf.conference";
  public static final String LOCATION_TABLE = "oldconf.location";
  public static final String TRACK_TABLE = "oldconf.event_view";
  public static final String SLOT_TABLE = "oldconf.slot";
  public static final String SESSION_TABLE = "oldconf.event_view";

  //  INSERT START SNIPPETS
  //  TWO OF THEM ARE PUBLIC BECAUSE THEY ARE USED IN TESTS
  public static final String SPEAKER_START = "INSERT INTO speakers (old_id,first_name,middle_name,last_name,title,company_old,address,zip_code,city,postalbox,country,email,homepage,twitter_name,phone,bio,testimonial,state,inserted_at,updated_at) \n VALUES ";
  public static final String CONFERENCE_START = "INSERT INTO conferences(old_id,name,start_date,inserted_at,updated_at) \n VALUES ";
  private static final String LOCATION_START = "INSERT INTO locations(old_id,name,description,conference_id,inserted_at,updated_at) \n VALUES ";
  private static final String TRACK_START = "INSERT INTO tracks(old_id,old_event_id,name,conference_id,description,color,location_id,inserted_at,updated_at) \n VALUES ";
  private static final String SLOT_START = "INSERT INTO time_slots(old_id,conference_id,start_time,end_time) \n VALUES ";
  private static final String SESSION_START = "INSERT INTO sessions(old_id,old_event_slot_slot_id,title,description,track_id,time_slot_id,note,short_title,slide_URL,video_URL,inserted_at,updated_at) \n VALUES ";


  public static void main(String[] args) {
    Exporter exporter = new Exporter();
    exporter.exportToSQLFile(Exporter.SPEAKER_TABLE, Exporter.SPEAKER_FILE);
    exporter.exportToSQLFile(Exporter.CONFERENCE_TABLE, Exporter.CONFERENCE_FILE);
    exporter.exportToSQLFile(Exporter.LOCATION_TABLE, Exporter.LOCATION_FILE);
    exporter.exportToSQLFile(Exporter.TRACK_TABLE, Exporter.TRACK_FILE);
    exporter.exportToSQLFile(Exporter.SLOT_TABLE, Exporter.TIME_SLOT_FILE);
    exporter.exportToSQLFile(Exporter.SESSION_TABLE, Exporter.SESSION_FILE);
  }

  /**
   * Exports all rows from a selected table into a file that can be imported to the new conference database.
   *
   * @param tableName The name of the table from which you want to export columns. Throws an IllegalArgumentException if the table is not yet supported for export.
   * @param fileName  The name of the file for the exported columns. If it exists it will be truncated, otherwise it will be created.
   *
   */
  public void exportToSQLFile(String tableName, String fileName) {
    if(!isTableNameSupported(tableName)){
      throw new IllegalArgumentException("Table is not yet supported for export");
    }
    PrintWriter writer = null;
    Connection conn = null;
    Statement stmt = null;
    try {
      Class.forName(JDBC_DRIVER);

      System.out.println("Connecting to the old conference database.");
      conn = DriverManager.getConnection(DB_URL, USER, PASS);

      stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("select * from " + tableName);
      writer = new PrintWriter(fileName, UTF8);

      System.out.println("Copying from " + tableName + " to " + fileName);

      if (tableName.equals(SPEAKER_TABLE)) {
        writeToSpeakerFile(writer, rs);
      } else if (tableName.equals(CONFERENCE_TABLE)) {
        writeToConferenceFile(writer, rs);
      } else if (tableName.equals(LOCATION_TABLE)) {
        writeToLocationFile(writer, rs);
      } else if (tableName.equals(TRACK_TABLE) && fileName.equals(TRACK_FILE)) {
        writeToTracksFile(writer, rs);
      } else if (tableName.equals(SLOT_TABLE)) {
        writeToTimeSlotFile(writer, rs);
      } else if (tableName.equals(SESSION_TABLE) && fileName.equals(SESSION_FILE)) {
        writeToSessionFile(writer, rs);
      }

      writer.println();

      rs.close();
      stmt.close();
      conn.close();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (stmt != null)
          stmt.close();
        if (writer != null) {
          writer.close();
        }
        if (conn != null)
          conn.close();
      } catch (SQLException se2) {
        se2.printStackTrace();
      }
    }

  }

  // METHODS FOR THE DIFFERENT TABLES TO MIGRATE /////////////////////////////////////////////////////////////////////////
  private void writeToSpeakerFile(PrintWriter writer, ResultSet rs) {
    count = 0;
    writer.println(SPEAKER_START);
    try {
      while (rs.next()) {
        count++;
        writer.print("(");
        writer.print(rs.getInt("oid") + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("firstname")) + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("middlename")) + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("lastname")) + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("title")) + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("company")) + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("address")) + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("zipCode")) + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("city")) + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("postalbox")) + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("country")) + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("email")) + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("homepage")) + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("twittername")) + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("phone")) + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("bio")) + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("testimonial")) + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("state")) + DELIMITER);
        writer.print(formatStringsForDatabase(getCurrentTimestamp()) + DELIMITER);
        writer.print(formatStringsForDatabase(getCurrentTimestamp()));
        writer.print(")");
        if (!rs.isLast()) {
          writer.print(",");
        }
        writer.println();

      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    writer.println(ON_CONFLICT_STATEMENT_OLD_ID);
    System.out.println(count + " speakers copied");
    System.out.println();
  }

  private void writeToConferenceFile(PrintWriter writer, ResultSet rs) {
    count = 0;
    writer.println(CONFERENCE_START);
    try {
      while (rs.next()) {
        count++;
        writer.print("(");
        writer.print(rs.getInt("oid") + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("name")) + DELIMITER);
        writer.print(formatStringsForDatabase(String.valueOf(rs.getTimestamp("startDate"))) + DELIMITER);
        writer.print(formatStringsForDatabase(String.valueOf(rs.getTimestamp("lastModified"))) + DELIMITER);
        writer.print(formatStringsForDatabase(getCurrentTimestamp()));
        writer.print(")");
        if (!rs.isLast()) {
          writer.print(",");
        }
        writer.println();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    writer.println(ON_CONFLICT_STATEMENT_OLD_ID);
    System.out.println(count + " conferences copied");
    System.out.println();
  }

  private void writeToLocationFile(PrintWriter writer, ResultSet rs) {
    count = 0;

    try {
      writer.println(LOCATION_START);

      while (rs.next()) {
        count++;

        //HACK, TODO remove timestamps that for some reason evaluates to 'null' the proper way
        String locationModifiedAt = String.valueOf(rs.getTimestamp("lastModified")).equals("null") ? getCurrentTimestamp() : String.valueOf(rs.getTimestamp("lastModified"));

        writer.print("(");
        writer.print(rs.getInt("oid") + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("name")) + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("description")) + DELIMITER);
        writer.print("(SELECT id FROM conferences where old_id=" + rs.getInt("oid") + ")" + DELIMITER);
        writer.print(formatStringsForDatabase(locationModifiedAt) + DELIMITER);
        writer.print(formatStringsForDatabase(locationModifiedAt));
        writer.print(")");

        if (!rs.isLast()) {
          writer.print(",\n");
        }

      }
      writer.println();

    } catch (SQLException e) {
      e.printStackTrace();
    }
    writer.println(ON_CONFLICT_STATEMENT_OLD_ID);
    System.out.println(count + " Locations copied");
    System.out.println();
  }

  private void writeToTracksFile(PrintWriter writer, ResultSet rs) {
    count = 0;

    try {
      writer.println(TRACK_START);

      while (rs.next()) {
        count++;

        //HACK, TODO remove timestamps that for some reason evaluates to 'null' the proper way
        String trackModifiedAt = String.valueOf(rs.getTimestamp("track_last_modified")).equals("null") ? getCurrentTimestamp() : String.valueOf(rs.getTimestamp("track_last_modified"));
        writer.print("(");
        writer.print(rs.getInt("track_oid") + DELIMITER);
        writer.print(rs.getInt("event_oid") + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("track_name")) + DELIMITER);
        writer.print("(SELECT id FROM conferences where old_id=" + rs.getInt("track_conference_id") + ")" + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("track_description")) + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("track_color")) + DELIMITER);
        writer.print("(SELECT id FROM locations where old_id=" + rs.getInt("location_oid") + ")" + DELIMITER);
        writer.print(formatStringsForDatabase(trackModifiedAt) + DELIMITER);
        writer.print(formatStringsForDatabase(trackModifiedAt));
        writer.print(")");
        if (!rs.isLast()) {
          writer.print(",\n");
        }

      }
      writer.println();

    } catch (SQLException e) {
      e.printStackTrace();
    }
    writer.println(ON_CONFLICT_STATEMENT_TRACKS);
    System.out.println(count + " tracks copied");
    System.out.println();
  }

  private void writeToTimeSlotFile(PrintWriter writer, ResultSet rs) {
    count = 0;

    try {
      writer.println(SLOT_START);

      while (rs.next()) {
        count++;

        //HACK, TODO remove timestamps that for some reason evaluates to 'null' the proper way
        writer.print("(");
        writer.print(rs.getInt("oid") + DELIMITER);
        writer.print("(SELECT id FROM conferences WHERE old_id=" + rs.getInt("conference_oid") + ")" + DELIMITER);
        writer.print(formatStringsForDatabase(String.valueOf(rs.getTimestamp("startDate"))) + DELIMITER);
        writer.print(formatStringsForDatabase(String.valueOf(rs.getTimestamp("endDate"))));
        writer.print(")");
        if (!rs.isLast()) {
          writer.print(",\n");
        }

      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    writer.println();
    writer.println(ON_CONFLICT_STATEMENT_OLD_ID);
    System.out.println(count + " time slots copied");
    System.out.println();
  }

  private void writeToSessionFile(PrintWriter writer, ResultSet rs) {
    count = 0;

    try {
      writer.println(SESSION_START);

      while (rs.next()) {
        String trackModifiedAt = String.valueOf(rs.getTimestamp("event_last_modified")).equals("null") ? getCurrentTimestamp() : String.valueOf(rs.getTimestamp("event_last_modified"));
        count++;
        writer.print("(");
        writer.print(rs.getInt("event_oid") + DELIMITER);
        writer.print(rs.getInt("event_slot_slot_id") + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("event_title")) + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("event_abstract")) + DELIMITER); //Abstract will be imported as description in the new db
        writer.print("(SELECT id FROM tracks WHERE old_event_id=" + rs.getInt("event_track_oid") + ")" + DELIMITER);
        writer.print("(SELECT id FROM time_slots WHERE old_id=" + rs.getInt("event_slot_slot_id") + ")" + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("event_note")) + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("event_shortTitle")) + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("event_slideURL")) + DELIMITER);
        writer.print(formatStringsForDatabase(rs.getString("event_videoURL")) + DELIMITER);
        writer.print(formatStringsForDatabase(trackModifiedAt) + DELIMITER);
        writer.print(formatStringsForDatabase(trackModifiedAt));
        writer.print(")");
        if (!rs.isLast()) {
          writer.print(",\n");
        }

      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    writer.println();
    writer.println(ON_CONFLICT_STATEMENT_SESSIONS);
    System.out.println(count + " sessions copied");
    System.out.println();
  }


  // HELPERS ////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Verifies if the table name is supported for export.
   * @param tableName The name of the table to validate
   * @return True if supported, false if not.
   */
  private boolean isTableNameSupported(String tableName) {
    if (tableName.equals(SPEAKER_TABLE) ||
      tableName.equals(CONFERENCE_TABLE) ||
      tableName.equals(LOCATION_TABLE) ||
      tableName.equals(TRACK_TABLE) ||
      tableName.equals(SLOT_TABLE) ||
      tableName.equals(SESSION_TABLE)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Prepares the string to be insertable into the database by escaping single quotes and turning empty strings into null
   *
   * @param string The string that you want to alter
   * @return A string that can be inserted into the database
   */
  public String formatStringsForDatabase(String string) {
    if (string != null) {
      if (string.trim().length() == 0 || string.equals("null")) {
        string = null;
      } else {
        string = "'" + string.replace("'", "''") + "'";
      }
    }
    return string;
  }

  /**
   * @return The current timestamp when the method was called.
   */
  public String getCurrentTimestamp() {
    return new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()).toString();
  }

}
