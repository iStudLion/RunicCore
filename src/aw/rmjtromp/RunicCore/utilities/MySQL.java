package aw.rmjtromp.RunicCore.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {
	
	private Connection connection = null;
	private String host, username, password, database;
	private int port;
	
	public MySQL(String host, String username, String password, String database, int port) {
		this.host = host; this.username = username; this.password = password; this.database = database; this.port = port;
		establishConnection();
	}
	
	private void establishConnection() {
		try {
			synchronized (this) {
				if (getConnection() != null && !getConnection().isClosed()) {
					return;
				}

				Class.forName("com.mysql.jdbc.Driver");
				setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password));
				System.out.println("[RunicCore] MySQL connection successfully established.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		return connection;
	}

	private void setConnection(Connection connection) {
		this.connection = connection;
	}
	
}
