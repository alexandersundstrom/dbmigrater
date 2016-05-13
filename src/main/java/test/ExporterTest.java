package test;

import dk.eos.migration.Exporter;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by Alexander Sundstrom on 21/04/16.
 */
public class ExporterTest {

  Exporter exporter = new Exporter();


  @Test(expected=IllegalArgumentException.class)
  public void shouldThrowExceptionWhenTableIsNotSupported() throws Exception {
    exporter.exportToSQLFile("test", Exporter.LOCATION_FILE);
  }

  @Test
  public void surroundStringsWithSingleQuotes() throws Exception {
    assertEquals("Strings should be the same", "'Alexander; A Developer'", exporter.formatStringsForDatabase("Alexander; A Developer"));
  }

  @Test
  public void escapeTextContainingSingleQuotes() {
    assertEquals("Strings should be the same", "'Alexander; A'' Developer'", exporter.formatStringsForDatabase("Alexander; A' Developer"));
  }

  @Test
  public void replaceEmptyStringsWithNull() throws Exception {
    assertEquals("Empty string should be replaced with null", null, exporter.formatStringsForDatabase(" "));
  }

  @Test
  public void writeASpeakerToASQLFile() {

    final String FILE = "test.sql";
    String result = "";
    PrintWriter writer = null;
    try {
      writer = new PrintWriter(FILE, Exporter.UTF8);

      writer.print("1246735743434637335" + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase("Alexander") + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase("Johan") + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase("Sundström") + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase("Developer") + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase("Trifork AB") + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase("Ferkens Gränd 3") + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase("11662") + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase("Stockholm") + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase(null) + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase("SWEDEN") + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase("example@trifork.com") + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase("www.trifork.se") + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase("examplename") + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase("0704500522") + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase("Alexander; is an intern and developer at' Trifork") + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase("Testing some text") + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase(null) + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase("2016-04-19 14:54:19.899") + Exporter.DELIMITER);
      writer.print(exporter.formatStringsForDatabase("2016-04-19 14:54:19.899"));
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

    try {
      String nextLine;
      InputStreamReader reader = new InputStreamReader(new FileInputStream(FILE));
      BufferedReader br = new BufferedReader(reader);
      while ((nextLine = br.readLine()) != null) {
        result += nextLine;
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    result = Exporter.SPEAKER_START + "(" + result + ")\n" + Exporter.ON_CONFLICT_STATEMENT_OLD_ID;

    String expectedResult = "INSERT INTO speakers (old_id,first_name,middle_name,last_name,title,company_old,address,zip_code,city,postalbox,country,email,homepage,twitter_name,phone,bio,testimonial,state,inserted_at,updated_at) \n" +
      " VALUES (1246735743434637335,'Alexander','Johan','Sundström','Developer','Trifork AB','Ferkens Gränd 3','11662','Stockholm',null,'SWEDEN','example@trifork.com','www.trifork.se','examplename','0704500522','Alexander; is an intern and developer at'' Trifork','Testing some text',null,'2016-04-19 14:54:19.899','2016-04-19 14:54:19.899')\n" +
      "ON CONFLICT (old_id) DO NOTHING;";

    assertEquals("String should be the same", expectedResult, result);

    File file = new File(FILE);
    file.delete();
  }
}
