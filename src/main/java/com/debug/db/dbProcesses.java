package com.debug.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.debug.utils.globalSymbols;
import com.debug.utils.logger;

public class dbProcesses {

	protected Connection conn;
	protected Object lock;

	public dbProcesses() {
		try {
			Class.forName("org.sqlite.JDBC");
			lock = new Object();
		} catch (Exception e) {
			logger.ErrorLogger(dbProcesses.class, e);
		}
	}

	protected boolean connect(String path) {
		boolean isConnected = false;
		synchronized (lock) {
			try {
				conn = DriverManager.getConnection(globalSymbols.jdbcPrefix + path);
				isConnected = true;
			} catch (Exception e) {
				logger.ErrorLogger(dbProcesses.class, e);
			}
		}
		return isConnected;
	}

	protected void disconnect() {
		try {
			conn.close();
		} catch (Exception e) {
			logger.ErrorLogger(dbProcesses.class, e);
		}
	}

	public boolean insert(String source, String destination, String message, String time) {
		return false;
	}

	public Integer getTableSize(String path, String tableName) {
		int count = 0;

		String sql = "SELECT count(*) FROM " + tableName;

		Statement stmt = null;
		ResultSet resultset = null;
		try {
			if (connect(path)) {
				stmt = conn.createStatement();
				resultset = stmt.executeQuery(sql);

				resultset.next();
				count = resultset.getInt(1);

				resultset.close();
				stmt.close();
				disconnect();
			}

		} catch (Exception e) {
			logger.ErrorLogger(dbProcesses.class, e);
		}
		return count;
	}

	public boolean isTableExist(String path, String tableName) {
		boolean isEmpty = false;
		ResultSet resultset = null;
		try {
			if (connect(path)) {
				DatabaseMetaData dmd = conn.getMetaData();
				resultset = dmd.getTables(null, null, tableName, null);
				resultset.next();
				if (resultset.getRow() > 0) {
					resultset.close();
					dmd = null;
					isEmpty = true;
				}
				disconnect();
			}
		} catch (Exception e) {
			logger.ErrorLogger(dbProcesses.class, e);
		}
		return isEmpty;
	}

	public synchronized boolean execute(String path, String sql) {
		boolean result = false;
		try {
			synchronized (lock) {
				if (connect(path)) {
					Statement stmt = conn.createStatement();
					result = stmt.execute(sql);
					stmt.close();
					disconnect();
				}
			}
		} catch (Exception e) {
			logger.ErrorLogger(dbProcesses.class, e);
		}
		return result;
	}
}
