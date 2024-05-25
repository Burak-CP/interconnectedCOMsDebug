package com.debug.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class loggerTest {

	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	private final PrintStream originalErr = System.err;
	private static final String LOG_DIR = "./log/";
	private static String logFileName;

	@BeforeAll
	static void setUpClass() {
		logFileName = new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".log";
	}

	@BeforeEach
	void setUp() {
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));
		try {
			Files.createDirectories(Paths.get(LOG_DIR));
		} catch (IOException e) {
			fail("Could not create log directory");
		}
	}

	@AfterEach
	void tearDown() {
		System.setOut(originalOut);
		System.setErr(originalErr);
		outContent.reset();
		errContent.reset();
		try {
			Files.deleteIfExists(Paths.get(LOG_DIR + logFileName));
		} catch (IOException e) {
			// Ignore, cleanup issue
		}
	}

	@AfterAll
	static void cleanUp() {
		try {
			Files.walk(Paths.get(LOG_DIR)).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		} catch (IOException e) {
			// Ignore cleanup issue
		}
	}

	@Test
	void testDebugLogger() {
		logger.DebugLogger(loggerTest.class, "Debug message");

		String logOutput = outContent.toString();
		assertTrue(logOutput.contains("Debug message"));

		String logFilePath = LOG_DIR + logFileName;
		try {
			String fileContent = new String(Files.readAllBytes(Paths.get(logFilePath)));
			assertTrue(fileContent.contains("Debug message"));
		} catch (IOException e) {
			fail("Could not read log file");
		}
	}

	@Test
	void testErrorLoggerWithMessage() {
		logger.ErrorLogger(loggerTest.class, "Error message");

		String logOutput = outContent.toString();
		assertTrue(logOutput.contains("Error message"));

		String logFilePath = LOG_DIR + logFileName;
		try {
			String fileContent = new String(Files.readAllBytes(Paths.get(logFilePath)));
			assertTrue(fileContent.contains("Error message"));
		} catch (IOException e) {
			fail("Could not read log file");
		}
	}

	@Test
	void testErrorLoggerWithException() {
		Exception e = new Exception("Test exception", new Throwable("Test cause"));
		logger.ErrorLogger(this, e);

		String logOutput = errContent.toString();
		assertTrue(logOutput.contains("Test exception"));
		assertTrue(logOutput.contains("Test cause"));

		String logFilePath = LOG_DIR + logFileName;
		try {
			String fileContent = new String(Files.readAllBytes(Paths.get(logFilePath)));
			assertTrue(fileContent.contains("Test exception"));
			assertTrue(fileContent.contains("Test cause"));
		} catch (IOException ex) {
			fail("Could not read log file");
		}
	}
}
