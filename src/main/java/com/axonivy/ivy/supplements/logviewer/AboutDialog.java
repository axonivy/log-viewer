package com.axonivy.ivy.supplements.logviewer;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AboutDialog {
	public static void showAbout() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("About IvyLogViewer");
		alert.setHeaderText(
				"Started as a simple JavaFX test project, but is actually useful as a small and simple log viewer.");
		alert.setContentText("(c) 2018 AXON IVY AG - pes");

		alert.showAndWait();
	}
}
