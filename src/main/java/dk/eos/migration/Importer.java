package dk.eos.migration;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.sql.*;

/**
 * Imports rows from a file to the new database.
 * Created by Alexander Sundstrom 20/04/16.
 */
public class Importer {
  private static final String JDBC_DRIVER = "org.postgresql.Driver";
  private static final String DB_URL = "jdbc:postgresql://localhost:5432/conf_dev";
  private static final String USER = "postgres";
  private static final String PASS = "postgres";

  public static void main(String[] args) {
    Importer importer = new Importer();
    importer.importRowsFromSQLFile(Exporter.CONFERENCE_FILE);
    importer.importRowsFromSQLFile(Exporter.SPEAKER_FILE);
    importer.importRowsFromSQLFile(Exporter.LOCATION_FILE);
    importer.importRowsFromSQLFile(Exporter.TRACK_FILE);
    importer.importRowsFromSQLFile(Exporter.TIME_SLOT_FILE);
    importer.importRowsFromSQLFile(Exporter.SESSION_FILE);
  }

  /**
   * Import rows from a file to the new database.
   * @param sqlFilePath Specifies which file to import from.
   */
  public void importRowsFromSQLFile(String sqlFilePath) {
    long addedRows = 0;

    Connection conn = null;
    Statement statement;

    try {
      System.out.println("Connecting to database, only new rows will be imported from " + sqlFilePath);

      Class.forName(JDBC_DRIVER);
      conn = DriverManager.getConnection(DB_URL, USER, PASS);
      conn.setAutoCommit(false);
      statement = conn.createStatement();

      addedRows = statement.executeUpdate(FileUtils.readFileToString(new File(sqlFilePath)));

      conn.commit();
      conn.close();

    } catch (SQLException se) {
      se.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (conn != null)
          conn.close();
      } catch (SQLException se) {
        se.printStackTrace();
      }
    }
    System.out.println("Number of new rows imported: " + addedRows);
    System.out.println();
  }
}
