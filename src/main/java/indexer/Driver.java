package indexer;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Driver class
 * @author Narasimman
 *
 */
public class Driver {
  private final Indexer indexer;
  private final Database connection;
  private static final String ERR_INVALID_PATH = "Invalid index/db connection path";

  /**
   * Driver that creates an indexer instance and a db connection
   * @param indexPath
   * @param dbPath
   * @throws SQLException
   * @throws IOException
   * @throws ClassNotFoundException 
   */
  public Driver(String indexPath, String dbPath) throws SQLException, IOException, ClassNotFoundException {
    if(indexPath == null || dbPath == null) {
      throw new IllegalArgumentException(ERR_INVALID_PATH);
    }

    indexer = new Indexer(indexPath);
    connection = new Database(dbPath);
  }

  /**
   * Build the index for a given resultset
   * @throws SQLException
   * @throws IOException
   */
  void buildIndex(ResultSet rs) throws SQLException, IOException {
    int counter = 0;
    while(rs.next()) {
      int id = rs.getInt("id");
      String title = rs.getString("title");
      String body = rs.getString("body");
      int answerId = rs.getInt("acceptedAnswerId");

      Post post = new Post(id, title, body, answerId);
      indexer.index(post);

      ///////TODO:
      
      ++counter;
      if(counter % 100 == 0) {
        System.out.println("Indexed " + counter + " documents!");
      }
      /////
    }

    connection.close();
    indexer.close();
  }

  public ResultSet executeQuery(String query) throws SQLException {
    return connection.executeQuery(query);
  }

  public static void main(String[] args) throws Exception {

    String usage = "Usage: " + Driver.class.getName()
        + " [-index INDEX_PATH] [-db db_path][-update]\n\n"
        + "This indexes the entries in the sqlite db, creating a Lucene index"
        + "in INDEX_PATH that can be searched with SearchFiles";

    if(args.length < 2) {
      System.out.println(usage);
      System.exit(-1);
    }
    CommandLine cmd = null;

    // set options
    Options options = new Options();
    options.addOption("index", "index", true, "Index Path");
    options.addOption("db", "db", true, "SQLite DB Path");

    CommandLineParser parser = new DefaultParser();

    try {
      cmd = parser.parse(options, args);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    String indexPath = cmd.getOptionValue("index");
    String dbPath = cmd.getOptionValue("db");

    Driver driver = new Driver(indexPath, dbPath);
    String query = "Select * from SmallPosts";

    driver.buildIndex(driver.executeQuery(query));
  }
}
