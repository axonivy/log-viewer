package com.axonivy.ivy.supplements.logviewer.knownissues;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.axonivy.ivy.supplements.logviewer.ExceptionDialog;
import com.axonivy.ivy.supplements.logviewer.parser.MainLogEntry;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class KnownIssues {
	private List<MainLogEntry> logEntries;

	public KnownIssues(List<MainLogEntry> logEntries) {
		this.logEntries = logEntries;
	}

	public void open() {
		boolean hasAccepted = DownloadWarningDialog.showWarning();
		if (hasAccepted) {
			URL resource = getClass().getResource("/foundIssues.fxml");
			try {
				openNewWindow(resource);
			} catch (IOException ex) {
				new ExceptionDialog().showException(ex);
			}
		}
	}

	private void openNewWindow(URL resource) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(resource);
		Parent root = (Parent) fxmlLoader.load();
		Scene scene = new Scene(root, 640, 320);
		Stage stage = new Stage();
		stage.setTitle("Known Issues (Experimental)");
		stage.setScene(scene);

		KnownIssuesController controller = fxmlLoader.<KnownIssuesController>getController();
		controller.setLogEntries(logEntries);
		controller.getKnownIssues();

		stage.showAndWait();
	}
}
