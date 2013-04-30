package ish.oncourse.test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DerbyUtils {
	
	/**
     * Clean a complete database
     * @param conn Connection to be used, must not be in auto-commit mode.
     * @param compress True if selected system tables are to be compressed
     * to avoid potential ordering differences in test output.
     * @throws SQLException database error
     */
     public static void cleanDatabase(Connection conn, boolean compress) throws SQLException {
         clearProperties(conn);
         removeObjects(conn);
         if (compress)
             compressObjects(conn);
         removeRoles(conn);
     }
     
     /**
      * Set of database properties that will be set to NULL (unset)
      * as part of cleaning a database.
      */
     private static final String[] CLEAR_DB_PROPERTIES =
     {
         "derby.database.classpath",
     };
     
     /**
      * Clear all database properties.
      */
     private static void clearProperties(Connection conn) throws SQLException {

         PreparedStatement ps = conn.prepareCall(
           "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY(?, NULL)");

	     for (String CLEAR_DB_PROPERTY : CLEAR_DB_PROPERTIES) {
		     ps.setString(1, CLEAR_DB_PROPERTY);
		     ps.executeUpdate();
	     }
         ps.close();
         conn.commit();
     }
     
     
     /**
      * Remove all objects in all schemas from the database.
      */
     @SuppressWarnings({ "rawtypes", "unchecked" })
	private static void removeObjects(Connection conn) throws SQLException {
   
        DatabaseMetaData dmd = conn.getMetaData();

        SQLException sqle = null;
        // Loop a number of arbitary times to catch cases
        // where objects are dependent on objects in
        // different schemas.
        for (int count = 0; count < 5; count++) {
            // Fetch all the user schemas into a list
            List schemas = new ArrayList();
            ResultSet rs = dmd.getSchemas();
            while (rs.next()) {
    
                String schema = rs.getString("TABLE_SCHEM");
                if (schema.startsWith("SYS"))
                    continue;
                if (schema.equals("SQLJ"))
                    continue;
                if (schema.equals("NULLID"))
                    continue;
    
                schemas.add(schema);
            }
            rs.close();
    
            // DROP all the user schemas.
            sqle = null;
	        for (Object schema1 : schemas) {
		        String schema = (String) schema1;
		        try {
			        JDBC.dropSchema(dmd, schema);
		        } catch (SQLException e) {
			        sqle = e;
		        }
	        }
            // No errors means all the schemas we wanted to
            // drop were dropped, so nothing more to do.
            if (sqle == null)
                return;
        }
        throw sqle;
    }

    private static void removeRoles(Connection conn) throws SQLException {
        // No metadata for roles, so do a query against SYSROLES
        Statement stm = conn.createStatement();
        Statement dropStm = conn.createStatement();

        // cast to overcome territory differences in some cases:
        ResultSet rs = stm.executeQuery(
            "select roleid from sys.sysroles where " +
            "cast(isdef as char(1)) = 'Y'");

        while (rs.next()) {
            dropStm.executeUpdate("DROP ROLE " + JDBC.escape(rs.getString(1)));
        }

        stm.close();
        dropStm.close();
        conn.commit();
    }

     /**
      * Set of objects that will be compressed as part of cleaning a database.
      */
     private static final String[] COMPRESS_DB_OBJECTS =
     {
         "SYS.SYSDEPENDS",
     };
     
     /**
      * Compress the objects in the database.
      * 
      * @param conn the db connection
      * @throws SQLException database error
      */
     private static void compressObjects(Connection conn) throws SQLException {
    	 
    	 CallableStatement cs = conn.prepareCall
    	     ("CALL SYSCS_UTIL.SYSCS_INPLACE_COMPRESS_TABLE(?, ?, 1, 1, 1)");

	     for (String COMPRESS_DB_OBJECT : COMPRESS_DB_OBJECTS) {
		     int delim = COMPRESS_DB_OBJECT.indexOf(".");
		     cs.setString(1, COMPRESS_DB_OBJECT.substring(0, delim));
		     cs.setString(2, COMPRESS_DB_OBJECT.substring(delim + 1));
		     cs.execute();
	     }
    	 
    	 cs.close();
    	 conn.commit();
     }
}
