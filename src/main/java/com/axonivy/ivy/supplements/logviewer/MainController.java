package com.axonivy.ivy.supplements.logviewer;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.axonivy.ivy.supplements.logviewer.parser.LogEntry;
import com.axonivy.ivy.supplements.logviewer.parser.LogFileParser;
import com.axonivy.ivy.supplements.logviewer.parser.LogLevel;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
  private MenuItem menuPointReload;

  @FXML
  private MenuItem menuPointQuit;

  @FXML
  private MenuItem menuPointAbout;

  @FXML
  private TreeView<String> logTreeView;

  @FXML
  private AnchorPane treeAnchorPane;

  @FXML
  private ComboBox<String> minimalLevel;

  private String selectedLogLevel = "ERROR";

  private List<LogEntry> logEntries;

  private File currentFile;

  @FXML
  private Button searchButton;

  @FXML
  private TextField searchField;

  private String textToSearch = "";

  @Override
  public void initialize(URL arg0, ResourceBundle arg1)
  {

    menuPointOpen.setOnAction(event -> {
      openFileDialog();
    });

    menuPointReload.setOnAction(event -> {
      openFile(currentFile);
    });

    menuPointQuit.setOnAction(event -> {
      System.exit(0);
    });

    menuPointAbout.setOnAction(event -> {
      AboutDialog.showAbout();
    });

    minimalLevel.getItems().addAll(FXCollections.observableArrayList(
            LogLevel.FATAL.name(),
            LogLevel.ERROR.name(),
            LogLevel.WARN.name(),
            LogLevel.INFO.name(),
            LogLevel.DEBUG.name()));

    minimalLevel.setOnAction(event -> {
      selectedLogLevel = minimalLevel.getValue();
      textToSearch = "";
      searchField.setText(textToSearch);
      displayLogEntries();
    });

    minimalLevel.setValue(selectedLogLevel);

    searchButton.setOnAction(event -> {
      textToSearch = searchField.getText();
      displayLogEntries();
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
    rootItem.setExpanded(true);
    
    if (logEntries == null)
    {
      return;
    }

    for (LogEntry entry : logEntries)
    {
      LogLevel logLevel = LogLevel.fromValue(selectedLogLevel);
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

  private static ImageView getIcon(LogLevel level)
  {

    String imageName;
    // TODO load the images static
    switch (level)
    {
      case FATAL:
      case ERROR:
        imageName = "ab_error_16.png";
        break;

      case WARN:
        imageName = "ab_warning_16.png";
        break;

      case INFO:
        imageName = "ab_information_16.png";
        break;

      default:
        imageName = "op_screwdriver_16.png";
        break;
    }

    Image image = new Image("/images/" + imageName);
    return new ImageView(image);
  }
}
