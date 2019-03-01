package com.axonivy.ivy.supplements.logviewer;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import com.axonivy.ivy.supplements.logviewer.parser.LogFileParser;
import com.axonivy.ivy.supplements.logviewer.parser.LogLevel;
import com.axonivy.ivy.supplements.logviewer.parser.MainLogEntry;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController implements Initializable {
	@FXML
	private MenuBar menuBar;

	@FXML
	private MenuItem menuPointOpen;

	@FXML
	private MenuItem menuPointCopy;

	@FXML
	private MenuItem menuPointQuit;

	@FXML
	private MenuItem menuPointCollapse;

	@FXML
	private MenuItem menuPointAbout;

	@FXML
	private TreeView<Object> logTreeView;

	@FXML
	private AnchorPane treeAnchorPane;

	@FXML
	private ComboBox<LogLevel> minimalLevel;

	private LogLevel selectedLogLevel = LogLevel.ERROR;

	private List<MainLogEntry> logEntries;

	private List<File> currentFiles;

	@FXML
	private Button applyNewFilter;

	@FXML
	private Button searchButton;

	@FXML
	private TextField startTimeField;
	@FXML
	private TextField endTimeField;

	@FXML
	private TextField searchField;

	private String textToSearch = "";

	@FXML
	private Label filepathLabel;

	private final KeyCombination ctrlC = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		buildMenu();
		configureDragAndDrop();
		configureMinLogLevelSelection();
		configureSearch();
		configureFilter();
		configureSelectionMode();
		addCtrlCSupport();
	}

	private void addCtrlCSupport() {
		treeAnchorPane.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			if (ctrlC.match(event)) {
				copySelectionToClipboard();
			}
		});
	}

	private void configureSelectionMode() {
		MultipleSelectionModel<TreeItem<Object>> defaultSelectionModel = logTreeView.getSelectionModel();
		defaultSelectionModel.setSelectionMode(SelectionMode.MULTIPLE);

		logTreeView.setSelectionModel(new MultipleSelectionModel<TreeItem<Object>>() {

			{
				setSelectionMode(SelectionMode.MULTIPLE);
			}

			@Override
			public ObservableList<Integer> getSelectedIndices() {
				return defaultSelectionModel.getSelectedIndices();
			}

			@Override
			public ObservableList<TreeItem<Object>> getSelectedItems() {
				return defaultSelectionModel.getSelectedItems();
			}

			@Override
			public void selectRange(int start, int end) {
				List<TreeItem<Object>> items = new ArrayList<>();
				for (int i = start; i < end; i++) {
					items.add(logTreeView.getTreeItem(i));
				}
				for (int i = start; i > end; i--) {
					items.add(logTreeView.getTreeItem(i));
				}
				items.forEach(this::select);
			}

			@Override
			public void selectIndices(int index, int... indices) {
				TreeItem<Object> item = logTreeView.getTreeItem(index);
				List<TreeItem<Object>> leaves = new ArrayList<>();
				findEntries(item, leaves);
				for (TreeItem<Object> leaf : leaves) {
					defaultSelectionModel.select(leaf);
				}
				for (int i : indices) {
					item = logTreeView.getTreeItem(i);
					leaves = new ArrayList<>();
					findEntries(item, leaves);
					for (TreeItem<Object> leaf : leaves) {
						defaultSelectionModel.select(leaf);
					}
				}
			}

			@Override
			public void selectAll() {
				defaultSelectionModel.selectAll();
			}

			@Override
			public void selectFirst() {
				TreeItem<Object> firstLeaf;
				for (firstLeaf = logTreeView.getRoot(); !firstLeaf.isLeaf(); firstLeaf = firstLeaf.getChildren().get(0))
					;
				defaultSelectionModel.select(firstLeaf);
			}

			@Override
			public void selectLast() {
				TreeItem<Object> lastLeaf;
				for (lastLeaf = logTreeView.getRoot(); !lastLeaf.isLeaf(); lastLeaf = lastLeaf.getChildren()
						.get(lastLeaf.getChildren().size() - 1))
					;
				defaultSelectionModel.select(lastLeaf);
			}

			@Override
			public void clearAndSelect(int index) {
				TreeItem<Object> item = logTreeView.getTreeItem(index);
				defaultSelectionModel.clearSelection();
				List<TreeItem<Object>> leaves = new ArrayList<>();
				findEntries(item, leaves);
				for (TreeItem<Object> leaf : leaves) {
					defaultSelectionModel.select(leaf);
				}
			}

			@Override
			public void select(int index) {
				select(logTreeView.getTreeItem(index));
			}

			@Override
			public void select(TreeItem<Object> item) {
				List<TreeItem<Object>> children = new ArrayList<>();
				children = findEntries(item, children);
				for (TreeItem<Object> leaf : children) {
					defaultSelectionModel.select(leaf);
				}
			}

			@Override
			public void clearSelection(int index) {
				defaultSelectionModel.clearSelection(index);
			}

			@Override
			public void clearSelection() {
				defaultSelectionModel.clearSelection();
			}

			@Override
			public boolean isSelected(int index) {
				return defaultSelectionModel.isSelected(index);
			}

			@Override
			public boolean isEmpty() {
				return defaultSelectionModel.isEmpty();
			}

			@Override
			public void selectPrevious() {
				defaultSelectionModel.selectPrevious();
			}

			@Override
			public void selectNext() {
				defaultSelectionModel.selectNext();
			}

			private List<TreeItem<Object>> findEntries(TreeItem<Object> node, List<TreeItem<Object>> nodes) {
				if (node.getValue() instanceof MainLogEntry) {
					nodes.add(node);
					node.setExpanded(true);
					for (TreeItem<Object> child : node.getChildren()) {
						nodes.add(child);
					}
				} else {
					findEntries(node.getParent(), nodes);
				}
				return nodes;
			}
		});
	}

	private void configureSearch() {
		searchButton.setOnAction(event -> {
			textToSearch = searchField.getText();
			displayLogEntries();
		});
	}

	private void configureFilter(){
		startTimeField.setPromptText("StartTime");
		endTimeField.setPromptText("EndTime");
		applyNewFilter.setOnAction(event -> {
			if(validateTimeString(startTimeField.getText(), endTimeField.getText())){
				displayFilteredEntries(filterByTime(startTimeField.getText(), endTimeField.getText(), logEntries));
			}
		});
	}
	private void configureMinLogLevelSelection() {
		minimalLevel.getItems().addAll(FXCollections.observableArrayList(LogLevel.valuesDesc()));

		minimalLevel.setOnAction(event -> {
			selectedLogLevel = minimalLevel.getValue();
			clearSearch();
			displayFilteredEntries(filterByTime(startTimeField.getText(), endTimeField.getText(), logEntries));
		});

		minimalLevel.setValue(selectedLogLevel);
	}

	private void clearSearch() {
		textToSearch = "";
		searchField.setText(textToSearch);
		displayLogEntries();
	}

	private void buildMenu() {
		menuPointOpen.setOnAction(event -> {
			openFileDialog();
		});

		menuPointCopy.setOnAction(event -> {
			copySelectionToClipboard();
		});

		menuPointCollapse.setOnAction(event -> {
			collapseTreeView(logTreeView.getRoot());
		});

		menuPointQuit.setOnAction(event -> {
			System.exit(0);
		});

		menuPointAbout.setOnAction(event -> {
			AboutDialog.showAbout();
		});
	}

	private void collapseTreeView(TreeItem<Object> item) {
		for (TreeItem<Object> child : item.getChildren()) {
			if (child != null) {
				child.setExpanded(false);
				if (!child.isLeaf()) {
					collapseTreeView(child);
				}
			}
		}
	}

	private void configureDragAndDrop() {
		treeAnchorPane.setOnDragOver(event -> {
			Dragboard dragboard = event.getDragboard();
			if (dragboard.hasFiles()) {
				event.acceptTransferModes(TransferMode.COPY);
			} else {
				event.consume();
			}
		});

		treeAnchorPane.setOnDragDropped(event -> {
			Dragboard dragboard = event.getDragboard();
			boolean success = false;
			if (dragboard.hasFiles()) {
				success = true;
				List<File> files = dragboard.getFiles();
				openFiles(files);
				event.setDropCompleted(success);
				event.consume();
			}});
	}

	private void openFileDialog() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Log File(s)");

		Stage stage = (Stage) treeAnchorPane.getScene().getWindow();
		currentFiles = fileChooser.showOpenMultipleDialog(stage);
		if(!currentFiles.isEmpty()) {
			openFiles(currentFiles);
		}
	}

	private void openFiles(List<File> files) {
		if (files.isEmpty()) {
			return;
		}
		LogFileParser logFileParser = new LogFileParser(files);
		try {
			logEntries = logFileParser.parse();
		} catch (IOException e) {
			new ExceptionDialog().showException(e);
			e.printStackTrace();
		}
		for (File file : files) {
			filepathLabel.setText(filepathLabel.getText() +" " + file.getAbsolutePath());
		}
		displayLogEntries();
	}

	private void displayLogEntries() {
		TreeItem<Object> rootItem = new TreeItem<Object>(new MainLogEntry("All", "All", LogLevel.DEBUG));
		logTreeView.setRoot(rootItem);
		logTreeView.setShowRoot(false);
		rootItem.setExpanded(true);

		if (logEntries == null) {
			return;
		}
		orderEntryListByChronology(logEntries);

		for (MainLogEntry entry : logEntries) {
			LogLevel logLevel = selectedLogLevel;
			if (entry.getSeverity().ordinal() < logLevel.ordinal()) {
				continue;
			}

			if (!textToSearch.isEmpty()) {
				if (!entry.getDetails().toLowerCase().contains(textToSearch.toLowerCase())) {
					continue;
				}
			}

			TreeItem<Object> item = new TreeItem<Object>(entry, getIcon(entry.getSeverity()));

			if (entry.getDetailLogEntry() != null) {
				TreeItem<Object> detailItem = new TreeItem<Object>(entry.getDetails());
				item.getChildren().add(detailItem);
			}
			rootItem.getChildren().add(item);
		}
	}

	private void copySelectionToClipboard() {
		String selectedEntries = new String();
		for (TreeItem<Object> selectedEntry : logTreeView.getSelectionModel().getSelectedItems()) {
			if (!(selectedEntry.getValue() instanceof MainLogEntry)) {
				selectedEntries = selectedEntries.concat(selectedEntry.getValue() + "\n");
			}
		}
		StringSelection selection = new StringSelection(selectedEntries);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);
	}

	private static ImageView getIcon(LogLevel level) {
		return IconUtil.getIcon(level);
	}

	public static List<MainLogEntry> filterByTime(String start, String end, List<MainLogEntry> logEntries){
		LocalTime startTime = LocalTime.parse(start);
		LocalTime endTime = LocalTime.parse(end);
		if(startTime.isAfter(endTime) || endTime.isBefore(startTime)){
			startTime = LocalTime.parse(end);
			endTime = LocalTime.parse(start);
		}
		List<MainLogEntry> filteredEntries = new ArrayList<>();
		for (MainLogEntry entry : logEntries) {
			LocalTime entryTime = LocalTime.parse(entry.getTime());
			if((!entryTime.isBefore(startTime)) && (!entryTime.isAfter(endTime))){
				filteredEntries.add(entry);
			}
		}
		return filteredEntries;
	}

	private void displayFilteredEntries(List<MainLogEntry> filteredEntries){
		TreeItem<Object> rootItem = new TreeItem<Object>(new MainLogEntry("All", "All", LogLevel.DEBUG));
		logTreeView.setRoot(rootItem);

		for (MainLogEntry entry : filteredEntries) {
			LogLevel logLevel = selectedLogLevel;
			if (entry.getSeverity().ordinal() < logLevel.ordinal()) {
				continue;
			}
			TreeItem<Object> item = new TreeItem<Object>(entry, getIcon(entry.getSeverity()));
			if (entry.getDetailLogEntry() != null) {
				TreeItem<Object> detailItem = new TreeItem<Object>(entry.getDetails());
				item.getChildren().add(detailItem);
			}
			rootItem.getChildren().add(item);
		}

	}

	public static List<MainLogEntry> orderEntryListByChronology(List<MainLogEntry> logEntryList){
		Collections.sort(logEntryList);
		return logEntryList;
	}

	public static Boolean validateTimeString(String time1, String time2){
		if(!time1.isEmpty() && !time2.isEmpty()){
			try{
				LocalTime.parse(time1);
				LocalTime.parse(time2);
				return true;
			}
			catch (Exception e) {
				new ExceptionDialog().showException(e, "Please enter  a valid timeformat (HH:mm:ss or HH:mm:ss.sss)");
				return false;
			}
		}
		else{
			return false;
		}
	}


}