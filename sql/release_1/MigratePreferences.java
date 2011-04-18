import java.sql.*;

public class MigratePreferences {
    

  public static void main(String[] args) {
    Connection conn = null;
    String url = "jdbc:mysql://db.office:3306/";
    String dbName = "w2dev_college";
    String driver = "com.mysql.jdbc.Driver";
    String userName = "willow"; 
    String password = "T7t,RMJRPGjFSq9m";
    String collegeid = args[0];
    try {
      Class.forName(driver).newInstance();
      conn = DriverManager.getConnection(url+dbName,userName,password);
      //      System.out.println("Connected to the database");

      Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                     ResultSet.CONCUR_READ_ONLY);
      String sql = "SELECT id,name,value FROM Preference WHERE collegeid="+collegeid;
      ResultSet preference=st.executeQuery(sql);
      System.out.println("id,name,value");
      while (preference.next()) {
	  int id= preference.getInt("id");
	  String name = preference.getString("name");
	  String value = preference.getString("value");
	  System.out.println(id +","+name + ",\"" + value +"\"" );
	  if (value != null) {
	      sql = "UPDATE Preference SET value_string='"+ value+"' WHERE id="+id+" AND collegeid=" + collegeid;	 
	      Statement update = conn.createStatement();
	      update.execute(sql);
	  }
      }
      conn.close();
      //System.out.println("Disconnected from database");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
