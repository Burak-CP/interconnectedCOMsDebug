package com.debug.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.debug.db.record.recordTable;
import com.debug.utils.globalSymbols;

public class dbProcesses {

	private Connection conn;
	private Object lock;

	public dbProcesses() {
		try {
			Class.forName("org.sqlite.JDBC");
			lock = new Object();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean connect(String path) {
		boolean isConnected = false;
		synchronized (lock) {
			try {
				conn = DriverManager.getConnection(globalSymbols.jdbcPrefix + path);
				isConnected = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return isConnected;
	}

	private void disconnect() {
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
		}
		return result;
	}

	public synchronized int executeUpdate(String path, String sql, recordTable record) {
		int result = -1;
		try {
			synchronized (lock) {
				if (connect(path)) {
					PreparedStatement pstmt = conn.prepareStatement(sql);
					pstmt.setString(0, record.getSource());
					pstmt.setString(1, record.getDestination());
					pstmt.setString(2, record.getMessage());
					pstmt.setString(3, record.getTime());
					result = pstmt.executeUpdate();
					pstmt.close();
					disconnect();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
