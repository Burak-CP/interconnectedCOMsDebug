package com.debug.ui.fx.component;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.debug.db.record.recordProcesses;
import com.debug.utils.globalSymbols;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class fxComponentHandler {

	public static void displayLogTextArea(ChoiceBox<String> portchoicebox, ChoiceBox<String> datechoicebox,
			ChoiceBox<String> messagetypechoicebox, TextArea logTextArea) {
		if (!globalSymbols.StopRefresh) {
			String selectedPort = portchoicebox.getSelectionModel().getSelectedItem();
			String selectedDate = datechoicebox.getSelectionModel().getSelectedItem();
			String selectedMessageType = messagetypechoicebox.getSelectionModel().getSelectedItem();
			double scrollPosition = logTextArea.getScrollTop();
			double scrollPositionbottom = logTextArea.getScrollLeft();
			List<String> logData = new ArrayList<>();
			if ("All".equals(selectedDate)) {
				for (String date : datechoicebox.getItems()) {
					if (!"All".equals(date)) {
						String databaseName = "db-" + date + ".db";

						if ("BOTH".equals(selectedPort)) {
							for (String port : portchoicebox.getItems()) {
								if (!"BOTH".equals(port)) {
									String tableName = port;
									recordProcesses rp = new recordProcesses();
									List<String> logs = rp.getLogDataFromDatabase(databaseName, tableName,
											selectedMessageType);
									logData.addAll(logs);
								}
							}
						} else {
							String tableName = selectedPort;
							recordProcesses rp = new recordProcesses();
							List<String> logs = rp.getLogDataFromDatabase(databaseName, tableName, selectedMessageType);
							logData.addAll(logs);
						}

					}
				}
			} else {
				String databaseName = "db-" + selectedDate + ".db";

				if ("BOTH".equals(selectedPort)) {
					for (String port : portchoicebox.getItems()) {
						if (!"BOTH".equals(port)) {
							String tableName = port;
							recordProcesses rp = new recordProcesses();
							List<String> logs = rp.getLogDataFromDatabase(databaseName, tableName, selectedMessageType);
							logData.addAll(logs);
						}
					}
				} else {
					String tableName = selectedPort;
					recordProcesses rp = new recordProcesses();
					List<String> logs = rp.getLogDataFromDatabase(databaseName, tableName, selectedMessageType);
					logData.addAll(logs);
				}
			}

			Collections.sort(logData, new Comparator<String>() {
				@Override
				public int compare(String log1, String log2) {
					String[] log1Parts = log1.split("\\|");
					String[] log2Parts = log2.split("\\|");
					String time1 = log1Parts[3].trim();
					String time2 = log2Parts[3].trim();
					return time1.compareTo(time2);
				}
			});

			StringBuilder logText = new StringBuilder();
			for (String log : logData) {
				String[] logParts = log.split("\\|");
				String sourceDevice = logParts[0].trim();
				String destinationDevice = logParts[1].trim();
				String message = logParts[2].trim();
				String time = logParts[3].trim();

				String formattedLog = String.format("%-6s -> %-6s | %-25s | %s", sourceDevice, destinationDevice, time,
						message);
				if (globalSymbols.ShowJustMessage) {
					logText.append(message).append("\n");
				} else {
					logText.append(formattedLog).append("\n");
				}
			}

			if (globalSymbols.FormatMessage && globalSymbols.ShowJustMessage) {
				logText = formatMessage(logText);
			}

			logTextArea.setText(logText.toString());

			if (globalSymbols.LogScrollTop) {
				logTextArea.setScrollTop(scrollPosition);
			} else {
				logTextArea.positionCaret(logTextArea.getLength()); // Scrolls to the end
			}
			if (globalSymbols.LogScrollLeft) {
				logTextArea.setScrollLeft(scrollPositionbottom);
			}
		}
	}

	public static StringBuilder formatMessage(StringBuilder input) {
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

	public static void populateDateChoiceBox(ChoiceBox<String> datechoicebox, String databaseDirectoryPath) {
		File databaseDirectory = new File(databaseDirectoryPath);
		Pattern pattern = Pattern.compile("db-(\\d{4}.\\d{2}.\\d{2}.\\d{2}).db");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HH");

		datechoicebox.getItems().clear();

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
		datechoicebox.getItems().addAll(dates);
	}
}
