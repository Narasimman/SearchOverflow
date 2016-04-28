import java.sql.*;

public class dbConn
{
	public static void main( String args[] )
	{
    		Connection c = null;
   		Statement stmt = null;
		try {
   			Class.forName("org.sqlite.JDBC");
 			c = DriverManager.getConnection("jdbc:sqlite:/home/mk5376/WSE/SearchOverflow/dataparser/so-dump.db");
			System.out.println("Opened database successfully");   
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM smallPosts;" );
		        while ( rs.next() ) {
         		int id = rs.getInt("id");
			System.out.println( "ID = " + id );
      }
      rs.close();
      stmt.close();
	c.close();

 } catch ( Exception e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    }
   System.out.println("Operation done successfully");
  }
}

