package com.debug.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class dbProcessesTest {

	private dbProcesses db;
	private String testDBPath = "test.db";

	@BeforeEach
	public void setUp() throws Exception {
		db = new dbProcesses();
		Connection conn = DriverManager.getConnection("jdbc:sqlite:" + testDBPath);
		Statement stmt = conn.createStatement();
		stmt.execute("CREATE TABLE IF NOT EXISTS test_table (source TEXT, destination TEXT, message TEXT, time TEXT)");
		stmt.execute(
				"INSERT INTO test_table (source, destination, message, time) VALUES ('source1', 'destination1', 'message1', 'time1')");
		stmt.close();
		conn.close();
	}

	@AfterEach
	public void tearDown() throws Exception {
		Connection conn = DriverManager.getConnection("jdbc:sqlite:" + testDBPath);
		Statement stmt = conn.createStatement();
		stmt.execute("DROP TABLE IF EXISTS test_table");
		stmt.close();
		conn.close();

		File dbFile = new File(testDBPath);
		if (dbFile.exists()) {
			dbFile.delete();
		}
	}

	@Test
	public void testConnect() {
		assertTrue(db.connect(testDBPath));
		db.disconnect();
	}

	@Test
	public void testDisconnect() {
		db.connect(testDBPath);
		db.disconnect();
		try {
			assertTrue(db.conn.isClosed());
		} catch (Exception e) {
			fail("Disconnection failed");
		}
	}

	@Test
	public void testGetTableSize() {
		int size = db.getTableSize(testDBPath, "test_table");
		assertEquals(1, size);
	}

	@Test
	public void testIsTableExist() {
		assertTrue(db.isTableExist(testDBPath, "test_table"));
		assertFalse(db.isTableExist(testDBPath, "non_existing_table"));
	}

	@Test
	public void testExecute() {
		String sql = "CREATE TABLE IF NOT EXISTS test_table2 (id INTEGER PRIMARY KEY)";
		assertFalse(db.execute(testDBPath, sql));
		assertTrue(db.isTableExist(testDBPath, "test_table2"));
	}
}
