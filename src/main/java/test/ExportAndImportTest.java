package test;

import dk.eos.migration.Exporter;
import dk.eos.migration.Importer;
import org.junit.Test;

import java.io.*;
import java.sql.*;

import static org.junit.Assert.*;

/**
 * Created by alex on 02/05/16.
 */
public class ExportAndImportTest {
  private static final String JDBC_DRIVER = "org.postgresql.Driver";
  private static final String DB_URL = "jdbc:postgresql://localhost:5432/conf_dev";
  private static final String USER = "postgres";
  private static final String PASS = "postgres";
  private final String TEMP_SQL_FILE = "test.sql";

  Exporter exporter = new Exporter();


  @Test
  public void exportAndImportAndReadEscapedContentFromDatabase() throws Exception {
    //  EXPORT  /////////////////////////////////////////
    writeASpeakerToSQLFile();


    //  IMPORT  /////////////////////////////////////////
    new Importer().importRowsFromSQLFile(TEMP_SQL_FILE);

    Connection conn = null;
    Statement stmt = null;

    String resultFromDatabase = "";

    try {
      Class.forName(JDBC_DRIVER);

      conn = DriverManager.getConnection(DB_URL, USER, PASS);
      stmt = conn.createStatement();

      //  READ FROM DATABASE  //////////////////////////////
      String selectBio = "SELECT bio FROM speakers WHERE first_name='Alexander' AND last_name='Sundström'";
      ResultSet rs = stmt.executeQuery(selectBio);

      //  GET THE RESULT //////////////////////////////////
      while (rs.next()) {
        resultFromDatabase = rs.getString("bio");
      }
      //  CLEAN UP DATABASE FROM TEMP SPEAKER FOR TEST //////
      String deleteTempSpeaker = "DELETE FROM speakers WHERE first_name='Alexander' AND last_name='Sundström'";
      stmt.executeUpdate(deleteTempSpeaker);

      rs.close();
      stmt.close();
      conn.close();
    } catch(SQLException se){
      se.printStackTrace();
    } catch(Exception e) {
      e.printStackTrace();
    } finally {
      if(stmt != null) {
        stmt.close();
      }
      if(conn != null) {
        conn.close();
      }
    }
    //  DELETE THE TEMPORARY FILE FOR THE TEST IS FINISHED /////////////
    new File(TEMP_SQL_FILE).delete();

    assertEquals("Strings should be the same", "Alexander;\n is an \"intern\" and developer' at' Trifork", resultFromDatabase);
  }

  public void writeASpeakerToSQLFile() {
    PrintWriter writer = null;
    try {
      writer = new PrintWriter(TEMP_SQL_FILE, Exporter.UTF8);

      writer.println(Exporter.SPEAKER_START);
      writer.print("(");
      writer.print(1000001 + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase("Alexander") + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase(null) + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase("Sundström") + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase(null) + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase(null) + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase(null) + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase(null) + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase(null) + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase(null) + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase(null) + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase(null) + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase(null) + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase(null) + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase(null) + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase("Alexander;\n is an \"intern\" and developer' at' Trifork") + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase(null) + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase(null) + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase("2016-04-19 14:54:19.899") + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase("2016-04-19 14:54:19.899"));
      writer.print(")");
      writer.println(Exporter.ON_CONFLICT_STATEMENT_OLD_ID);
      writer.println();

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }


}
