package com.debug.db.record;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.debug.utils.globalSymbols;
import com.debug.utils.simpleFileProcess;

class RecordProcessesTest {

	private recordProcesses rp;
	private String dbFileName;

	@BeforeAll
	static void setUpClass() {
		simpleFileProcess.ensureFolder(globalSymbols.dbFolder);
	}

	@BeforeEach
	void setUp() {
		rp = new recordProcesses();
		dbFileName = rp.getDbFileName();
	}

	@AfterEach
	public void tearDown() throws Exception {
		rp = new recordProcesses();
		dbFileName = globalSymbols.dbFolder + "/" + rp.getDbFileName();
		Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFileName);
		Statement stmt = conn.createStatement();
		stmt.execute("DROP TABLE IF EXISTS test_table");
		stmt.close();
		conn.close();

		File dbFile = new File(dbFileName);
		if (dbFile.exists()) {
			dbFile.delete();
		}
	}

	@AfterAll
	static void cleanUp() {
		File index = new File(globalSymbols.dbFolder);
		String[] entries = index.list();
		for (String s : entries) {
			File currentFile = new File(index.getPath(), s);
			currentFile.delete();
		}
	}

	@Test
	void testCreateRecordTable() {
		String tableName = "testTable";
		String filePath = rp.getFilePath(dbFileName);
		rp.createRecordTable(filePath, tableName);
		assertTrue(rp.dbprocesses.isTableExist(filePath, tableName));
	}

	@Test
	void testInsert() {
		String tableName = "Source1";
		recordTable record = new recordTable(tableName, "Destination1", "Message1", "Time1");
		rp.insert(record);
		List<String> logData = rp.getLogDataFromDatabase(dbFileName, tableName, "All");
		assertFalse(logData.isEmpty());
	}

	@Test
	void testGetLogDataFromDatabase() {
		String tableName = "Source1";
		recordTable record = new recordTable(tableName, "Destination1", "Message1", "Time1");
		rp.insert(record);
		List<String> logData = rp.getLogDataFromDatabase(dbFileName, tableName, "Message1");
		assertFalse(logData.isEmpty());
		assertEquals(1, logData.size());
	}
}
