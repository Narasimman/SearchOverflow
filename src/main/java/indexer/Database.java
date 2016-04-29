package indexer;import java.sql.Connection;import java.sql.DriverManager;import java.sql.ResultSet;import java.sql.SQLException;import java.sql.Statement;/** * A database class that is written specially to handle SQLite3. * This requires a sqlite path to be passed to it. * @author Narasimman * */public class Database {  private final Connection connection;  private static final String JDBC_PROTOCOL = "jdbc:sqlite:";  private static final String ERR_INVALID_PATH = "Invalid db path";  /**   * Path to the SQLite db url   * @param dbUrl   * @throws SQLException   */  public Database(String dbUrl) throws SQLException{    if(dbUrl == null) {      throw new IllegalArgumentException(ERR_INVALID_PATH);    }     connection = DriverManager.getConnection(JDBC_PROTOCOL + dbUrl);      }  /**   * Execute the given query and return the result set   * @param query   * @return   * @throws SQLException   */  public ResultSet executeQuery(String query) throws SQLException{    if(query == null) {      return null;    }    Statement stmt = connection.createStatement();    ResultSet resultSet = stmt.executeQuery(query);    return resultSet;  }  /**   * Call this after to clean up the resources   * @throws SQLException   */  public void close() throws SQLException {    connection.close();  }  @Override  public void finalize() throws SQLException {    connection.close();  }}