package bonusbot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
	
	private static String connectionString;
	
	public static Connection get() {
		if (connectionString == null) {
			try {
				Class.forName("org.mariadb.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace(Logging.getPrintWrite());
			}
			String dbhost = Settings.get("mariaDBHost");
			int dbport = Settings.get("mariaDBPort");
			String dbname = Settings.get("mariaDBName");
			String dbuser = Settings.get("mariaDBUser");
			String dbpassword = Settings.get("mariaDBPassword");
			if (dbhost != null && dbuser != null && dbpassword != null && dbname != null && dbport != -1) {
				connectionString = String.format("jdbc:mariadb://%s:%d/%s?user=%s&password=%s", dbhost, dbport, dbname, dbuser, dbpassword);
			} else {
				connectionString = "-";
				return null;
			}
		} else if (connectionString.equals("-")) {
			return null;
		}
		try {
			Connection conn = DriverManager.getConnection(connectionString);
			return conn;
		} catch (SQLException e) {
			e.printStackTrace(Logging.getPrintWrite());
			return null;
		}
	}
}
