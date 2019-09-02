package com.iy.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionProvider {
	public static Connection getConnection() throws SQLException {		
		return DriverManager.getConnection("jdbc:apache:commons:dbcp:manage");	// DB name 수정
	}
}