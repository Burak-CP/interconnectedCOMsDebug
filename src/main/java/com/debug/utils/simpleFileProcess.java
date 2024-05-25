package com.debug.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

public class simpleFileProcess {

	private static String DefaultEncoding = "UTF-8";

	public static boolean ensureFolder(String folderPath) {
		try {
			File file = new File(folderPath);
			if (!file.exists()) {
				file.mkdirs();
			}
			return true;
		} catch (Exception e) {
			logger.ErrorLogger(simpleFileProcess.class, e);
		}
		return false;
	}

	public static boolean ensureFolderForFile(String filePath) {
		try {
			filePath = filePath.replace("\\", "/");
			filePath = filePath.replace("/", File.separator);
			int lastIndex = filePath.lastIndexOf(File.separator);
			if (lastIndex != -1) {
				filePath = filePath.substring(0, lastIndex);
				return ensureFolder(filePath);
			}
		} catch (Exception e) {
			logger.ErrorLogger(simpleFileProcess.class, e);
		}
		return false;
	}

	public static boolean ensureFile(String filePath) {
		File path = new File(simpleFileProcess.getFolderPath(filePath));
		if (!path.exists()) {
			path.mkdir();
		}
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) {
				logger.ErrorLogger(simpleFileProcess.class, e);
				return false;
			}
		}
		return true;
	}

	public static void write(String path, String content) {
		writeWithEncoding(path, content, DefaultEncoding);
	}

	public static void writeWithEncoding(String path, String content, String encoding) {
		if (ensureFile(path)) {
			File file = new File(path);
			try {
				FileOutputStream dOutputStream = new FileOutputStream(file);
				dOutputStream.write(content.getBytes(encoding));
				dOutputStream.close();
			} catch (Exception e) {
				logger.ErrorLogger(simpleFileProcess.class, e);
			}
		}
	}

	public static boolean appendToFile(String path, String content) {
		if (ensureFile(path)) {
			try {
				BufferedWriter bufferedwriter = new BufferedWriter(new FileWriter(path, true));
				bufferedwriter.write(content);
				bufferedwriter.close();
			} catch (Exception e) {
				logger.ErrorLogger(simpleFileProcess.class, e);
			}
			return true;
		}
		return false;
	}

	public static String getFolderPath(String filePath) {
		String tmpFilePath = filePath;
		int strIndex1 = filePath.lastIndexOf("\\");
		int strIndex2 = filePath.lastIndexOf("/");
		strIndex1 = Math.max(strIndex1, strIndex2);
		if (strIndex1 != -1) {
			tmpFilePath = filePath.substring(0, strIndex1);
		}
		return tmpFilePath.trim();
	}
}
