package com.debug.db.record;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.debug.db.dbProcesses;
import com.debug.utils.globalSymbols;
import com.debug.utils.logger;

public class recordProcesses {

	dbProcesses dbprocesses = null;

	public recordProcesses() {
		dbprocesses = new dbProcesses();
	}

	protected String getDbFileName() {
		String filename = "db-" + new SimpleDateFormat("yyyy.MM.dd.HH").format(new Date()) + ".db";
		return filename;
	}

	protected String getFilePath(String dbFileName) {
		String filepath = globalSymbols.dbFolder + "/" + dbFileName;
		return filepath;
	}

	private String creatrInsertSql(recordTable table) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ");
		sb.append(table.getSource());
		sb.append(" (");
		sb.append(globalSymbols.sourceDeviceColumn);
		sb.append(", ");
		sb.append(globalSymbols.destinationDeviceColumn);
		sb.append(", ");
		sb.append(globalSymbols.messageColumn);
		sb.append(", ");
		sb.append(globalSymbols.timeColumn);
		sb.append(") VALUES ('");
		sb.append(table.getSource());
		sb.append("', '");
		sb.append(table.getDestination());
		sb.append("', '");
		sb.append(table.getMessage());
		sb.append("', '");
		sb.append(table.getTime());
		sb.append("');");
		return sb.toString();
	}

	public void createRecordTable(String filePath, String tableName) {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("CREATE TABLE IF NOT EXISTS ");
			sb.append(tableName);
			sb.append(" (");
			sb.append(globalSymbols.timeColumn);
			sb.append(" TEXT NOT NULL, ");
			sb.append(globalSymbols.sourceDeviceColumn);
			sb.append(" TEXT NOT NULL, ");
			sb.append(globalSymbols.destinationDeviceColumn);
			sb.append(" TEXT NOT NULL, ");
			sb.append(globalSymbols.messageColumn);
			sb.append(" TEXT NOT NULL);");

			dbprocesses.execute(filePath, sb.toString());
		} catch (Exception e) {
			logger.ErrorLogger(recordProcesses.class, e);
		}
	}

	public void insert(recordTable table) {
		if (!dbprocesses.isTableExist(getFilePath(getDbFileName()), table.getSource())) {
			createRecordTable(getFilePath(getDbFileName()), table.getSource());
		}

		dbprocesses.execute(getFilePath(getDbFileName()), creatrInsertSql(table));
	}

	public List<String> getLogDataFromDatabase(String databaseName, String tableName, String messageType) {
		List<String> logData = new ArrayList<>();
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			// Veritabanına bağlanma
			connection = DriverManager.getConnection("jdbc:sqlite:" + getFilePath(databaseName));

			// Veritabanı sorgusu
			statement = connection.createStatement();
			String sqlQuery = null;
			if (!messageType.equalsIgnoreCase("All")) {
				sqlQuery = "SELECT * FROM " + tableName + " WHERE " + globalSymbols.messageColumn + " LIKE '%"
						+ messageType + "%' ORDER BY " + globalSymbols.timeColumn;
			} else {
				sqlQuery = "SELECT * FROM " + tableName + " ORDER BY " + globalSymbols.timeColumn;
			}
			resultSet = statement.executeQuery(sqlQuery);

			// Sorgudan dönen sonuçları al
			while (resultSet.next()) {
				String sourceDevice = resultSet.getString(globalSymbols.sourceDeviceColumn);
				String destinationDevice = resultSet.getString(globalSymbols.destinationDeviceColumn);
				String message = resultSet.getString(globalSymbols.messageColumn);
				String time = resultSet.getString(globalSymbols.timeColumn);

				// Log verilerini tek bir string olarak oluştur ve listeye ekle
				String logEntry = String.format("%s | %s | %s | %s", sourceDevice, destinationDevice, message, time);
				logData.add(logEntry);
			}
		} catch (SQLException e) {
			logger.ErrorLogger(recordProcesses.class, e);
		} finally {
			// Kullanılan kaynakları kapatma
			try {
				if (resultSet != null)
					resultSet.close();
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				logger.ErrorLogger(recordProcesses.class, e);
			}
		}

		return logData;
	}
}
