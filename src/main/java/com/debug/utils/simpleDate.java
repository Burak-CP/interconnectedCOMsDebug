package com.debug.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class simpleDate {

	public static String DATETIMES = "dd.MM.yyyy HH:mm:ss";

	public static String format(String format, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setTimeZone(TimeZone.getDefault());
		String formatedString = sdf.format(date);
		return formatedString;
	}

	public static String getCurrentTime(String dateTimePattern) {
		return format(dateTimePattern, new Date(System.currentTimeMillis()));
	}
}
