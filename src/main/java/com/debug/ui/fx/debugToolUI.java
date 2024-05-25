package com.debug.ui.fx;

import java.util.Map;

import com.debug.ui.fx.component.fxComponentHandler;
import com.debug.utils.globalSymbols;
import com.debug.utils.logger;
import com.debug.utils.simpleFileProcess;
import com.google.common.base.Splitter;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class debugToolUI extends Application {

	@Override
	public void start(Stage stage) {
		try {
			Text portLabel = new Text("Port:");
			ChoiceBox portchoicebox = new ChoiceBox();
			portchoicebox.getItems().addAll(globalSymbols.Port1Name, globalSymbols.Port2Name, "BOTH");
			portchoicebox.setValue("BOTH");

			Text dateLabel = new Text("Date");
			ChoiceBox datechoicebox = new ChoiceBox();
			fxComponentHandler.populateDateChoiceBox(datechoicebox, globalSymbols.dbFolder);
			datechoicebox.setOnMouseClicked(event -> {
				fxComponentHandler.populateDateChoiceBox(datechoicebox, globalSymbols.dbFolder);
			});
			datechoicebox.setValue("CURRENT");

			Text messageLabel = new Text("MessageType");
			ChoiceBox Messagechoicebox = new ChoiceBox();
			Messagechoicebox.getItems().addAll("ERROR", "DEBUG", "INFO", "LOCAL", "All");
			Messagechoicebox.setValue("INFO");

			CheckBox autoScrollCheckBox = new CheckBox("Auto Scroll");
			autoScrollCheckBox.setSelected(false);

			CheckBox showJustMessage = new CheckBox("Show Just Message");
			showJustMessage.setSelected(false);

			CheckBox formatMessage = new CheckBox("Format Message");
			formatMessage.setSelected(false);

			CheckBox stopRefresh = new CheckBox("Stop");
			stopRefresh.setSelected(false);

			TextArea logTextArea = new TextArea();
			logTextArea.setPrefRowCount(25);
			logTextArea.setEditable(false);

			portchoicebox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
				fxComponentHandler.displayLogTextArea(portchoicebox, datechoicebox, Messagechoicebox, logTextArea);
			});
			datechoicebox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
				fxComponentHandler.displayLogTextArea(portchoicebox, datechoicebox, Messagechoicebox, logTextArea);
			});

			GridPane gridPane = new GridPane();

			gridPane.setMinSize(1024, 500);

			gridPane.setPadding(new Insets(10, 10, 10, 10));

			gridPane.setVgap(5);
			gridPane.setHgap(5);

			gridPane.setAlignment(Pos.TOP_CENTER);

			gridPane.add(portLabel, 0, 0);
			gridPane.add(portchoicebox, 1, 0);

			gridPane.add(dateLabel, 2, 0);
			gridPane.add(datechoicebox, 3, 0);

			gridPane.add(messageLabel, 4, 0);
			gridPane.add(Messagechoicebox, 5, 0);

			gridPane.add(autoScrollCheckBox, 6, 0);

			gridPane.add(showJustMessage, 7, 0);

			gridPane.add(formatMessage, 8, 0);

			gridPane.add(stopRefresh, 9, 0);

			GridPane.setColumnSpan(logTextArea, 10);
			GridPane.setRowSpan(logTextArea, 1);
			gridPane.add(logTextArea, 0, 1, 10, 1);

			portLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
			dateLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
			messageLabel.setStyle("-fx-font: normal bold 15px 'serif' ");

			gridPane.setStyle("-fx-background-color: BEIGE;");

			Scene scene = new Scene(gridPane);

			stage.setTitle("COM Port Debug Tool");

			stage.setScene(scene);

			logTextArea.prefWidthProperty().bind(scene.widthProperty());
			logTextArea.prefHeightProperty().bind(scene.heightProperty());

			stage.show();

			Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
				fxComponentHandler.displayLogTextArea(portchoicebox, datechoicebox, Messagechoicebox, logTextArea);
			}));
			timeline.setCycleCount(Timeline.INDEFINITE);
			timeline.play();

			stage.sceneProperty().addListener((obs, oldScene, newScene) -> {
				Platform.runLater(() -> {
					Stage stage2 = (Stage) newScene.getWindow();
					stage2.setOnCloseRequest(e -> {
						Platform.exit();
						System.exit(0);
					});
				});
			});

			autoScrollCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue) {
					globalSymbols.LogScrollTop = false;
					globalSymbols.LogScrollLeft = false;
				} else {
					globalSymbols.LogScrollTop = true;
					globalSymbols.LogScrollLeft = true;
				}
			});

			showJustMessage.selectedProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue) {
					// bottom
					globalSymbols.ShowJustMessage = true;
				} else {
					globalSymbols.ShowJustMessage = false;
				}
			});

			formatMessage.selectedProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue) {
					globalSymbols.FormatMessage = true;
				} else {
					globalSymbols.FormatMessage = false;
				}
			});

			stopRefresh.selectedProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue) {
					globalSymbols.StopRefresh = true;
				} else {
					globalSymbols.StopRefresh = false;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Map<String, String> params;
		if (args.length > 0) {
			params = Splitter.on(";").withKeyValueSeparator("=").split(args[0]);

			if ( //
			params.containsKey(globalSymbols.Port1str) || //
					params.containsKey(globalSymbols.Port2str) //
			) {
				globalSymbols.Port1Name = params.get(globalSymbols.Port1str);
				globalSymbols.Port2Name = params.get(globalSymbols.Port2str);
			}

			simpleFileProcess.ensureFolder(globalSymbols.dbFolder);

			launch(args);
		} else {
			logger.DebugLogger(debugToolUI.class, "No input parameter!!!");
			return;
		}
	}
}
