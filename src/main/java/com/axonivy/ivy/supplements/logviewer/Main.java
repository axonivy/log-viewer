package com.axonivy.ivy.supplements.logviewer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	public static void main(String[] args) {
		try {
			Application.launch(args);
		} catch (Exception ex) {
			ex.printStackTrace();
			new ExceptionDialog().showException(ex);
		}
	}

	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/ivylogviewer.fxml"));

		Scene scene = new Scene(root, 640, 320);

		stage.setTitle("IvyLogViewer");
		stage.setScene(scene);
		stage.show();
	}
}