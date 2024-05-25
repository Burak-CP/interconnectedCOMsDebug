package com.debug.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class simpleFileProcessTest {

	private static final String TEST_DIR = "./testDir/";
	private static final String TEST_FILE = TEST_DIR + "testFile.txt";
	private static final String TEST_CONTENT = "This is a test content";

	@BeforeEach
	void setUp() {
		try {
			Files.createDirectories(Paths.get(TEST_DIR));
		} catch (IOException e) {
			fail("Could not create test directory");
		}
	}

	@AfterEach
	void tearDown() {
		try {
			Files.walk(Paths.get(TEST_DIR)).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		} catch (IOException e) {
			// Ignore cleanup issue
		}
	}

	@Test
	void testEnsureFolder() {
		String folderPath = TEST_DIR + "subDir";
		assertTrue(simpleFileProcess.ensureFolder(folderPath));
		assertTrue(Files.exists(Paths.get(folderPath)));
	}

	@Test
	void testEnsureFolderForFile() {
		String filePath = TEST_DIR + "subDir" + File.separator + "file.txt";
		assertTrue(simpleFileProcess.ensureFolderForFile(filePath));
		assertTrue(Files.exists(Paths.get(TEST_DIR + "subDir")));
	}

	@Test
	void testEnsureFile() {
		assertTrue(simpleFileProcess.ensureFile(TEST_FILE));
		assertTrue(Files.exists(Paths.get(TEST_FILE)));
	}

	@Test
	void testWrite() {
		simpleFileProcess.write(TEST_FILE, TEST_CONTENT);
		try {
			String content = new String(Files.readAllBytes(Paths.get(TEST_FILE)));
			assertEquals(TEST_CONTENT, content);
		} catch (IOException e) {
			fail("Could not read test file");
		}
	}

	@Test
	void testWriteWithEncoding() {
		simpleFileProcess.writeWithEncoding(TEST_FILE, TEST_CONTENT, "UTF-8");
		try {
			String content = new String(Files.readAllBytes(Paths.get(TEST_FILE)), "UTF-8");
			assertEquals(TEST_CONTENT, content);
		} catch (IOException e) {
			fail("Could not read test file");
		}
	}

	@Test
	void testAppendToFile() {
		simpleFileProcess.write(TEST_FILE, TEST_CONTENT);
		simpleFileProcess.appendToFile(TEST_FILE, TEST_CONTENT);
		try {
			String content = new String(Files.readAllBytes(Paths.get(TEST_FILE)));
			assertEquals(TEST_CONTENT + TEST_CONTENT, content);
		} catch (IOException e) {
			fail("Could not read test file");
		}
	}

	@Test
	void testGetFolderPath() {
		String folderPath = simpleFileProcess.getFolderPath(TEST_FILE);
		assertEquals(TEST_DIR.substring(0, TEST_DIR.length() - 1), folderPath);
	}
}
