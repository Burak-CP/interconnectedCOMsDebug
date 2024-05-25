package com.debug.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class simpleDateTest {

	private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";
	private Date testDate;

	@BeforeEach
	void setUp() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		sdf.setTimeZone(TimeZone.getDefault());
		testDate = sdf.parse("25.05.2024 15:30:00");
	}

	@Test
	void testFormat() {
		String formattedDate = simpleDate.format(DATE_FORMAT, testDate);
		assertEquals("25.05.2024 15:30:00", formattedDate);
	}

	@Test
	void testGetCurrentTime() {
		String currentTime = simpleDate.getCurrentTime(DATE_FORMAT);
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		sdf.setTimeZone(TimeZone.getDefault());

		String expectedTime = sdf.format(new Date(System.currentTimeMillis()));
		assertEquals(expectedTime, currentTime.substring(0, DATE_FORMAT.length()));
	}
}
