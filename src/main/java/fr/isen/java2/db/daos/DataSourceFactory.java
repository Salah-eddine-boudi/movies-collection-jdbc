package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

public class DataSourceFactory {

	private static final String DB_URL = "jdbc:sqlite:sqlite.db";

	private DataSourceFactory() {
		
		throw new IllegalStateException("This is a static class that should not be instantiated");
	}

	/**
	 * Factory class for database connections.
	 * Uses DriverManager for database-agnostic implementation.
	 * 
	 * @author SALAH EDDINE
	 * @version 1.0
	 * @since 2026-02
	 */
	public static DataSource getDataSource() {
		return new DataSource() {
			
			@Override
			public Connection getConnection() throws SQLException {
				return DriverManager.getConnection(DB_URL);
			}
			
			@Override
			public Connection getConnection(String username, String password) throws SQLException {
				return DriverManager.getConnection(DB_URL, username, password);
			}
			
			
			
			@Override
			public java.io.PrintWriter getLogWriter() throws SQLException {
				return null;
			}
			
			@Override
			public void setLogWriter(java.io.PrintWriter out) throws SQLException {
			}
			
			@Override
			public void setLoginTimeout(int seconds) throws SQLException {
			}
			
			@Override
			public int getLoginTimeout() throws SQLException {
				return 0;
			}
			
			@Override
			public java.util.logging.Logger getParentLogger() {
				return null;
			}
			
			@Override
			public <T> T unwrap(Class<T> iface) throws SQLException {
				return null;
			}
			
			@Override
			public boolean isWrapperFor(Class<?> iface) throws SQLException {
				return false;
			}
		};
	}
}