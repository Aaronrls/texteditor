

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connex {
	
	
	private static Connection con = null;
	public void start() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/texteditor", "root", "root");
			if (con != null) {
				System.out.println("Connected");
			}
		} catch (SQLException ex) {
			System.out.println("SQL Exception " + ex.getMessage());
			ex.printStackTrace();
		} catch (Exception ex) {
			System.out.println("Exception " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	public Connection setCon() {

		return con;
	}
	
	public void close() {

		try {
			if (con != null) {
				con.close();
				System.out.println("connection closed");
			}
		} catch (SQLException ex) {
			System.out.println("SQL Exception " + ex.getMessage());
			ex.printStackTrace();
		} catch (Exception ex) {
			System.out.println("Exception " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}

