package com.axonivy.ivy.supplements.logviewer;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AboutDialog {
	public static void showAbout() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("About IvyLogViewer");
		alert.setHeaderText(
				"Started as a simple JavaFX test project, but is actually useful as a small and simple log viewer.");
		alert.setContentText("(c) 2018 AXON IVY AG - pes, bhu");
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("/images/icon.png"));
		alert.showAndWait();
	}
}
