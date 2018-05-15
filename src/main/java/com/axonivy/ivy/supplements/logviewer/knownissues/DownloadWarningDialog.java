package com.axonivy.ivy.supplements.logviewer.knownissues;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class DownloadWarningDialog {
	public static boolean showWarning() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Warning");
		alert.setHeaderText("The IvyLogViewer will download a list of known issues. "
				+ "No data will be uploaded.\n Do you want to continue?");

		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("/images/icon.png"));

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			alert.close();
			return true;
		} else {
			alert.close();
			return false;
		}
	}
}
