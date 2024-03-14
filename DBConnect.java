package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnect {

	public static Connection connect() {
		Properties prop = new Properties();
		Connection connection = null;
		try {
			prop.load(DBConnect.class.getClassLoader().getResourceAsStream("database.properties"));

			// Retrieve properties
			String driver = prop.getProperty("db.driver_class_name");
			String url = prop.getProperty("db.url");
			String userName = prop.getProperty("db.username");
			String password = prop.getProperty("db.password");

			Class.forName(driver);
			connection = DriverManager.getConnection(url, userName, password);

			if (connection != null)
				System.out.println("Connection established...");

		} catch (Exception e) {

		}
		return connection;

	}

}
