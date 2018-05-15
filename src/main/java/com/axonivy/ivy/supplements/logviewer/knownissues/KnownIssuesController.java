package com.axonivy.ivy.supplements.logviewer.knownissues;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.axonivy.ivy.supplements.logviewer.knownissues.webservice.KnownIssue;
import com.axonivy.ivy.supplements.logviewer.parser.MainLogEntry;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;

public class KnownIssuesController implements Initializable {

	@FXML
	private ListView<String> knownIssuesList;
	private IssuesParser issuesParser = new IssuesParser();
	private static Image ISSUE_ICON = new Image("/images/" + "knownissueicon.png");
	@FXML
	private Label foundIssuesLabel;
	@FXML
	private MenuBar menuBar;
	@FXML
	private MenuItem menuPointClose;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		configureListView();
		buildMenu();
	}

	private void configureListView() {
		knownIssuesList.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {

			@Override
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public ListCell<String> call(ListView<String> list) {
				final ListCell cell = new ListCell() {
					@Override
					public void updateItem(Object item, boolean empty) {
						super.updateItem(item, empty);
						if (!isEmpty()) {
							ImageView imageView = new ImageView();
							imageView.setId("imageView");
							imageView.applyCss();
							imageView.setImage(ISSUE_ICON);
							// TODO: Position image correctly
							setGraphic(imageView);
							setText(item.toString());
							setWrapText(true);
						}
					}
				};
				return cell;
			}
		});
		knownIssuesList.setPlaceholder(new Label("No Known Issues Found"));
	}

	private void buildMenu() {
		menuPointClose.setOnAction(event -> ((Stage) knownIssuesList.getScene().getWindow()).close());
	}

	public void getKnownIssues() {
		List<KnownIssue> knownIssues = issuesParser.getKnownIssues();
		ObservableList<String> items = FXCollections.observableArrayList();
		for (KnownIssue issue : knownIssues) {
			items.add(issue.issueID + ": " + issue.issueTitle + "\nFixed Versions: " + issue.fixedVersions
					+ "\nWorkaround: \n" + issue.workaround);
		}
		knownIssuesList.setItems(items);
		foundIssuesLabel.setText(Integer.toString(items.size())+ " Issue(s) found");
	}

	public void setLogEntries(List<MainLogEntry> logEntries) {
		issuesParser.logEntries = logEntries;
	}
}
