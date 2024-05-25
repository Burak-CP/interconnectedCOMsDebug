package com.debug.ui.fx.component;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.debug.db.record.recordProcesses;
import com.debug.utils.globalSymbols;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class fxComponentHandler {

	public static void displayLogTextArea(ChoiceBox<String> portChoiceBox, ChoiceBox<String> dateChoiceBox,
			ChoiceBox<String> messageTypeChoiceBox, TextArea logTextArea) {
		if (!globalSymbols.StopRefresh) {
			String selectedPort = portChoiceBox.getSelectionModel().getSelectedItem();
			String selectedDate = dateChoiceBox.getSelectionModel().getSelectedItem();
			String selectedMessageType = messageTypeChoiceBox.getSelectionModel().getSelectedItem();

			List<String> logData = getLogData(portChoiceBox, dateChoiceBox, selectedPort, selectedDate,
					selectedMessageType);

			logData.sort((log1, log2) -> {
				String[] log1Parts = log1.split("\\|");
				String[] log2Parts = log2.split("\\|");
				String time1 = log1Parts[3].trim();
				String time2 = log2Parts[3].trim();
				return time1.compareTo(time2);
			});

			StringBuilder formattedLogs = formatLogs(logData);

			logTextArea.setText(formattedLogs.toString());

			if (globalSymbols.LogScrollTop) {
				logTextArea.setScrollTop(logTextArea.getScrollTop());
			} else {
				logTextArea.positionCaret(logTextArea.getLength()); // Scrolls to the end
			}

			if (globalSymbols.LogScrollLeft) {
				logTextArea.setScrollLeft(logTextArea.getScrollLeft());
			}
		}
	}

	public static List<String> getLogData(ChoiceBox<String> portChoiceBox, ChoiceBox<String> dateChoiceBox,
			String selectedPort, String selectedDate, String selectedMessageType) {
		List<String> logData = new ArrayList<>();
		if ("All".equals(selectedDate)) {
			for (String date : dateChoiceBox.getItems()) {
				if (!"All".equals(date)) {
					String databaseName = "db-" + date + ".db";
					addLogsFromDatabase(portChoiceBox, dateChoiceBox, selectedPort, selectedMessageType, logData,
							databaseName);
				}
			}
		} else {
			String databaseName = "db-" + selectedDate + ".db";
			addLogsFromDatabase(portChoiceBox, dateChoiceBox, selectedPort, selectedMessageType, logData, databaseName);
		}
		return logData;
	}

	private static void addLogsFromDatabase(ChoiceBox<String> portChoiceBox, ChoiceBox<String> dateChoiceBox,
			String selectedPort, String selectedMessageType, List<String> logData, String databaseName) {
		if ("BOTH".equals(selectedPort)) {
			for (String port : portChoiceBox.getItems()) {
				if (!"BOTH".equals(port)) {
					recordProcesses rp = new recordProcesses();
					List<String> logs = rp.getLogDataFromDatabase(databaseName, port, selectedMessageType);
					logData.addAll(logs);
				}
			}
		} else {
			recordProcesses rp = new recordProcesses();
			List<String> logs = rp.getLogDataFromDatabase(databaseName, selectedPort, selectedMessageType);
			logData.addAll(logs);
		}
	}

	private static StringBuilder formatLogs(List<String> logData) {
		StringBuilder formattedLogs = new StringBuilder();
		for (String log : logData) {
			String[] logParts = log.split("\\|");
			String sourceDevice = logParts[0].trim();
			String destinationDevice = logParts[1].trim();
			String message = logParts[2].trim();
			String time = logParts[3].trim();

			String formattedLog = String.format("%-6s -> %-6s | %-25s | %s", sourceDevice, destinationDevice, time,
					message);
			if (globalSymbols.ShowJustMessage) {
				formattedLogs.append(message).append("\n");
			} else {
				formattedLogs.append(formattedLog).append("\n");
			}
		}
		if (globalSymbols.FormatMessage && globalSymbols.ShowJustMessage) {
			formattedLogs = formatMessage(formattedLogs);
		}
		return formattedLogs;
	}

	private static StringBuilder formatMessage(StringBuilder input) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			char currentChar = input.charAt(i);
			if (currentChar == '\n') {
			} else if (currentChar == '#') {
				result.append("\n");
				result.append(currentChar);
			} else if (currentChar == '[') {
				result.append("\n");
				result.append(currentChar);
			} else {
				result.append(currentChar);
			}
		}
		return result;
	}

	public static void populateDateChoiceBox(ChoiceBox<String> dateChoiceBox, String databaseDirectoryPath) {
		File databaseDirectory = new File(databaseDirectoryPath);
		Pattern pattern = Pattern.compile("db-(\\d{4}.\\d{2}.\\d{2}.\\d{2}).db");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HH");

		dateChoiceBox.getItems().clear();

		List<String> dates = new ArrayList<>();
		for (File file : databaseDirectory.listFiles()) {
			String fileName = file.getName();
			Matcher matcher = pattern.matcher(fileName);
			if (matcher.matches()) {
				String datePart = matcher.group(1);
				dates.add(datePart);
			}
		}
		dates.add("CURRENT");
		dateChoiceBox.getItems().addAll(dates);
	}

}
