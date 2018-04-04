package com.axonivy.ivy.supplements.logviewer;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.axonivy.ivy.supplements.logviewer.parser.LogEntry;
import com.axonivy.ivy.supplements.logviewer.parser.LogFileParser;
import com.axonivy.ivy.supplements.logviewer.parser.LogLevel;

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

public class MainController implements Initializable
{
  @FXML
  private MenuBar menuBar;

  @FXML
  private MenuItem menuPointOpen;

  @FXML
  private MenuItem menuPointCopy;

  @FXML
  private MenuItem menuPointQuit;

  @FXML
  private MenuItem menuPointAbout;

  @FXML
  private TreeView<String> logTreeView;

  @FXML
  private AnchorPane treeAnchorPane;

  @FXML
  private ComboBox<LogLevel> minimalLevel;

  private LogLevel selectedLogLevel = LogLevel.ERROR;

  private List<LogEntry> logEntries;

  private File currentFile;

  @FXML
  private Button searchButton;

  @FXML
  private TextField searchField;

  private String textToSearch = "";

  @FXML
  private Label filepathLabel;

  private final KeyCombination ctrlC = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
  
  @Override
  public void initialize(URL arg0, ResourceBundle arg1)
  {
    buildMenu();
    configureDragAndDrop();
    configureMinLogLevelSelection();
    configureSearch();   
    configureSelectionMode();   
	addCtrlCSupport();
  }


private void addCtrlCSupport() {
	treeAnchorPane.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
		if (ctrlC.match(event)) {
			copySelectionToClipboard();
		}
	});
}


private void configureSelectionMode() {
	MultipleSelectionModel<TreeItem<String>> defaultSelectionModel = logTreeView.getSelectionModel() ;
    defaultSelectionModel.setSelectionMode(SelectionMode.MULTIPLE);

    logTreeView.setSelectionModel(new MultipleSelectionModel<TreeItem<String>>() {

        {
            setSelectionMode(SelectionMode.MULTIPLE);
        }

        @Override
        public ObservableList<Integer> getSelectedIndices() {
            return defaultSelectionModel.getSelectedIndices();
        }

        @Override
        public ObservableList<TreeItem<String>> getSelectedItems() {
            return defaultSelectionModel.getSelectedItems();
        }

        @Override
        public void selectRange(int start, int end) {
            List<TreeItem<String>> items = new ArrayList<>();
            for (int i = start; i < end; i++) {
                items.add(logTreeView.getTreeItem(i));
            }
            for (int i = start ; i > end; i--) {
                items.add(logTreeView.getTreeItem(i));
            }
            items.forEach(this::select);
        }

        @Override
        public void selectIndices(int index, int... indices) {
            TreeItem<String> item = logTreeView.getTreeItem(index);
            if (item.isLeaf()) {
                defaultSelectionModel.select(item);;
            } else {
                List<TreeItem<String>> leaves = new ArrayList<>();
                findLeavesAndExpand(item, leaves);
                for (TreeItem<String> leaf : leaves) {
                    defaultSelectionModel.select(leaf);
                }
            }
            for (int i : indices) {
                item = logTreeView.getTreeItem(i);
                if (item.isLeaf()) {
                    defaultSelectionModel.select(item);;                        
                } else {
                    List<TreeItem<String>> leaves = new ArrayList<>();
                    findLeavesAndExpand(item, leaves);
                    for (TreeItem<String> leaf : leaves) {
                        defaultSelectionModel.select(leaf);
                    }
                }
            }
        }

        @Override
        public void selectAll() {
            List<TreeItem<String>> leaves = new ArrayList<>();
            findLeavesAndExpand(logTreeView.getRoot(), leaves);
            for (TreeItem<String> leaf : leaves) {
                defaultSelectionModel.select(leaf);
            }
        }

        @Override
        public void selectFirst() {
            TreeItem<String> firstLeaf ;
            for (firstLeaf = logTreeView.getRoot(); ! firstLeaf.isLeaf(); firstLeaf = firstLeaf.getChildren().get(0)) ;
            defaultSelectionModel.select(firstLeaf);
        }

        @Override
        public void selectLast() {
            TreeItem<String> lastLeaf ;
            for (lastLeaf = logTreeView.getRoot(); ! lastLeaf.isLeaf(); 
                    lastLeaf = lastLeaf.getChildren().get(lastLeaf.getChildren().size()-1)) ;
            defaultSelectionModel.select(lastLeaf);
        }

        @Override
        public void clearAndSelect(int index) {
            TreeItem<String> item = logTreeView.getTreeItem(index);
            defaultSelectionModel.clearSelection();
            if (item.isLeaf()) {
                defaultSelectionModel.select(item);
            } else {
                List<TreeItem<String>> leaves = new ArrayList<>();
                findLeavesAndExpand(item, leaves);
                for (TreeItem<String> leaf : leaves) {
                    defaultSelectionModel.select(leaf);
                }
            }
        }

        @Override
        public void select(int index) {
            select(logTreeView.getTreeItem(index));
        }

        @Override
        public void select(TreeItem<String> item) {
        	List<TreeItem<String>> leaves = new ArrayList<>();
            leaves = findLeavesAndExpand(item, leaves);
            for (TreeItem<String> leaf : leaves) {
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
		
		private List<TreeItem<String>> findLeavesAndExpand(TreeItem<String> node, List<TreeItem<String>> nodes) {
			if (!node.isLeaf()) 
			{            
				nodes.add(node);
                node.setExpanded(true);
                for (TreeItem<String> child : node.getChildren()) {
                    nodes.add(child);
                }
            }
			else
			{
				findLeavesAndExpand(node.getParent(), nodes);
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


private void configureMinLogLevelSelection() {
	minimalLevel.getItems().addAll(FXCollections.observableArrayList(LogLevel.values()));

    minimalLevel.setOnAction(event -> {
      selectedLogLevel = minimalLevel.getValue();
      clearSearch();
      displayLogEntries();
    });

    minimalLevel.setValue(selectedLogLevel);
}


private void clearSearch() {
	textToSearch = "";
      searchField.setText(textToSearch);
}


private void buildMenu() {
	menuPointOpen.setOnAction(event -> {
      openFileDialog();
    });
    
    menuPointCopy.setOnAction(event -> {
      copySelectionToClipboard();
    });

    menuPointQuit.setOnAction(event -> {
      System.exit(0);
    });

    menuPointAbout.setOnAction(event -> {
      AboutDialog.showAbout();
    });
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
                openFile(dragboard.getFiles().get(0));
            }
            event.setDropCompleted(success);
            event.consume();
    });
}


  private void openFileDialog()
  {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Log File");

    Stage stage = (Stage) treeAnchorPane.getScene().getWindow();
    currentFile = fileChooser.showOpenDialog(stage);

    if (currentFile != null)
    {
      openFile(currentFile);
    }
  }

  private void openFile(File file)
  {
    if (file == null)
    {
      return;
    }
    LogFileParser logFileParser = new LogFileParser(file);
    try
    {
      logEntries = logFileParser.parse();
      displayLogEntries();
      filepathLabel.setText(file.getAbsolutePath());
    }
    catch (Exception ex)
    {
      new ExceptionDialog().showException(ex);
    }
  }

  private void displayLogEntries()
  {
    TreeItem<String> rootItem = new TreeItem<String>("All");
    logTreeView.setRoot(rootItem);
    logTreeView.setShowRoot(false);
    rootItem.setExpanded(true);

    if (logEntries == null)
    {
      return;
    }

    for (LogEntry entry : logEntries)
    {
      LogLevel logLevel = selectedLogLevel;
      if (entry.getSeverity().ordinal() < logLevel.ordinal())
      {
        continue;
      }

      if (!textToSearch.equals(""))
      {
        if (!entry.getDetails().toLowerCase().contains(textToSearch.toLowerCase()))
        {
          continue;
        }
      }

      TreeItem<String> item = new TreeItem<String>(entry.getTitleLine(), getIcon(entry.getSeverity()));
      TreeItem<String> detailItem = new TreeItem<String>(entry.getDetails());
      item.getChildren().add(detailItem);
      rootItem.getChildren().add(item);
    }
  }
  
  private void copySelectionToClipboard()
  {
	  String selectedEntries = new String();
	  for(TreeItem<String> selectedEntry : logTreeView.getSelectionModel().getSelectedItems())
	  {		  
		  selectedEntries = selectedEntries.concat(selectedEntry.getValue() + "\n");
	  }
	  
	  StringSelection selection = new StringSelection(selectedEntries);
	  Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	  clipboard.setContents(selection, selection);
  }

  private static ImageView getIcon(LogLevel level)
  {
    return IconUtil.getIcon(level);
  }
}