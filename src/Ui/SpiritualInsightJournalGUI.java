package Ui;

import Document.Entry;
import Document.Scripture;
import Document.TextFile;
import Document.XML;

import JournalMain.Journal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

/**
 * Contains the GUI for the spiritual insight journal logic.
 * @author Grant
 */
public class SpiritualInsightJournalGUI extends Application {
    // Containers for the menu bar
    private MenuBar mainMenu;
    private VBox topContainer;
    private BorderPane topBorder;
    
    // Containers for the main part of the screen    
    private TreeView<String> journalViewer[];
    private TabPane viewerTabs;
    private TabPane entryTabs;
    private HBox mainScreen;
    private VBox vbox;
    
    // Containers for the window
    private Scene primaryScene;
    private Stage primaryStage;
    
    private Journal spiritualJournal;
    
    /**
     * Sets up the containers, scene and stage and show the stage.
     * @param stage 
     */
    @Override
    public void start(final Stage stage) {
        try {
            spiritualJournal = new Journal();
            
            primaryStage = stage;
            primaryStage.setTitle("Spiritual Insight Journal");
            
            setUpMenuBar();
            setUpHBox();
            setUpGrid();
            setUpSceneWithStage();
            
            primaryStage.show();
        } catch (IOException ex) {
            writeErrorFile(ex.getMessage());
        }
    }

    /**
     * Sets ups the scene and how resizing works.
     */
    public void setUpSceneWithStage() {    
        primaryScene = new Scene(vbox, 700, 400);
        
        primaryScene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue,
                    Number oldSceneWidth, Number newSceneWidth) {
                vbox.setPrefWidth(newSceneWidth.doubleValue());
                viewerTabs.setMinWidth(viewerTabs.getWidth());
                viewerTabs.setMaxWidth(viewerTabs.getWidth());
                mainScreen.setPrefWidth(newSceneWidth.doubleValue());
                entryTabs.setPrefWidth(newSceneWidth.doubleValue());
            }
        });
        
        primaryScene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue,
                    Number oldSceneHeight, Number newSceneHeight) {
                vbox.setPrefHeight(newSceneHeight.doubleValue());
                viewerTabs.setPrefHeight(newSceneHeight.doubleValue());
                mainScreen.setPrefHeight(newSceneHeight.doubleValue());
                entryTabs.setPrefHeight(newSceneHeight.doubleValue());
            }
        });
        
        primaryStage.getIcons().add(new Image(SpiritualInsightJournalGUI.class.
                getResource("/Resources/byui_logo.png").toExternalForm()));
        primaryStage.setScene(primaryScene);
        primaryStage.setMinHeight(300);
        primaryStage.setMinWidth(450);
    }
    
    /**
     * Adds topBorder and mainScreen to the vbox VBox.
     */
    public void setUpGrid() {
        vbox = new VBox();
        vbox.getChildren().addAll(topBorder, mainScreen);
    }
    
    /**
     * Adds a leftPane node and rightPane node to the mainScreen HBox.
     */
    public void setUpHBox() {
        mainScreen = new HBox();
        Node leftPane  = setUpLeftPane();
        Node rightPane = setUpRightPane();
        mainScreen.getChildren().addAll(leftPane, rightPane);
    }
    
    /**
     * Returns the node that will be placed on the left side of the HBox.
     * Prepares the node to become usable before being added to the HBox.
     * 
     * @return a TreeView node
     */
    private Node setUpLeftPane() {
        TreeItem<String>[] root = new TreeItem[3];
        journalViewer = new TreeView[3];
        
        for (int i = 0; i < root.length; ++i) {
            root[i] = new TreeItem<>("Journal");
        }
        
        for (int i = 0; i < journalViewer.length; ++i) {
            journalViewer[i] = new TreeView<>(root[i]);
        }
        
        viewerTabs = new TabPane();
        
        // Set up each tab for viewing the entries in different ways.
        for (int i = 0; i < 3; ++i) {
            Tab newTab = new Tab();
            if (i == 0) {
                newTab.setText("By Entries");
            } else if (i == 1) {
                newTab.setText("By Scriptures");
            } else if (i == 2) {
                newTab.setText("By Topics");
            }
            newTab.setClosable(false);
            newTab.setContent(journalViewer[i]);
            viewerTabs.getTabs().add(newTab);
        }
        
        setJournalViewerEvents();
        
        return viewerTabs;
    }
    
    /**
     * Defines what happens of the event of clicking the mouse on viewerByEntries.
     */
    private void setJournalViewerEvents() {
        for (int i = 0; i < journalViewer.length; ++i) {
            final int finalI = i;
            journalViewer[i].setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    TreeItem<String> item = journalViewer[finalI].getSelectionModel()
                            .getSelectedItem();
                
                    // If the index is in range...
                    if (item != null) {
                        String title = item.getValue();
                        String pattern = "\\d\\d\\d\\d-\\d\\d-\\d\\d";
                        Pattern datePattern = Pattern.compile(pattern);
                        Matcher match = datePattern.matcher(title);
                    
                        String warningMessage =
                                "Are you sure you want to reopen up this entry and lose any unsaved work?";
                        
                        if (isTabOpen(title)) {
                            if (warningDialog(warningMessage)) {
                                for (int i = 0; i < entryTabs.getTabs().size(); ++i) {
                                    if (entryTabs.getTabs().get(i).getText().equals(title)) {
                                        entryTabs.getTabs().remove(i);
                                    }
                                }
                            } else {
                                return;
                            }
                        }
                        
                        if (match.find()) {
                            TextArea newArea = new TextArea();
                            String content = "";
                        
                            // Find the entry we want to put the information in tab.
                            for (Entry entry : spiritualJournal.getEntries()) {
                                if (title.equals(entry.getDate())) {
                                    content = entry.getText();
                                    break;
                                }
                            }
                                
                            // Add the tab to the list.
                            newArea.setText(content);
                            addTab(title, newArea, entryTabs.getTabs().size()-1);
                        }
                    }
                }
            });
        }
    }
    
    /**
     * Returns true or false depending on whether or not the tab given as a string
     * is open.
     * 
     * @param date  the tab text (or title) given as a string.
     * @return      the state of the tab being open or not.
     */
    private boolean isTabOpen(String date) {    
        // We want to see if the tab for that entry is already open
        for (int i = 0; i < entryTabs.getTabs().size(); ++i) {
            Tab tab = entryTabs.getTabs().get(i);
            if (tab.getText().equals(date)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Returns the node that will be placed on the right side of the mainScreen HBox.
     * Prepares the entryTabs TabPane to be used by the user.
     * 
     * @return entryTabs after it has been set up.
     */
    private Node setUpRightPane() {
        entryTabs = new TabPane();
        
        // Set up default tab with date
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        TextArea content = new TextArea("To start just start typing and when you are done\n" +
                                        "press save under File and it will automatically\n" +
                                        "save for you this journal entry");
        content.setWrapText(true);
        content.setPrefColumnCount(39);
        addTab(dateFormat.format(date), content, 0);
        
        // Set up "add new tab" tab
        String plus = "+";
        addTab(plus, null, 1);
        setPlusTabEvents();
        
        return entryTabs;
    }
    
    /**
     * Adds a tab on the entryTabs TabPane. Will add to the location of the index.
     * 
     * @param date    the name of the tab as a string.
     * @param content the node that the tab will set as it's content 
     * @param index   the placement of the tab.
     */
    private void addTab(String date, Node content, int index) {
        Tab newTab = new Tab();
        newTab.setText(date);
        if (content != null) {
            newTab.setContent(content);
        }
        entryTabs.getTabs().add(index, newTab);
    }
    
    /**
     * Defines what happens when the users clicks on the "add new tab" tab (or
     * plus sign tab).
     */
    private void setPlusTabEvents() {
        final Tab plusTab = entryTabs.getTabs().get(entryTabs.getTabs().size() - 1);
        plusTab.setClosable(false);
        plusTab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                if (plusTab.isSelected()) {
                    addNewTab();
                }
            }
        });
    }
    
    /**
     * Adds a new tab when prompted to do so.
     */
    private void addNewTab() {
        // Set up a new date for today's date
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
                    
        boolean has = false;
                    
        // Figure out if there is a tab already open of that date.
        for (int i = 0; i < entryTabs.getTabs().size(); ++i) {
            if (dateFormat.format(date).equals(entryTabs.getTabs().get(i).getText())) {
                has = true;
                break;
            }
        }
                
        // If there isn't, create a new tab.
        if (!has) {
            TextArea content = new TextArea();
            content.setWrapText(true);
            addTab(dateFormat.format(date), content, entryTabs.getTabs().size()-1);
        }
                    
        SingleSelectionModel<Tab> selectionModel = entryTabs.getSelectionModel();
        selectionModel.select(entryTabs.getTabs().get(entryTabs.getTabs().size()-2));
    }
    
    /**
     * Prepares the menu bar and puts it in the topContainer, which is put
     * in the topBorder.
     */
    public void setUpMenuBar() {
        topBorder = new BorderPane();
        topContainer = new VBox();
        mainMenu = new MenuBar();
        
        topContainer.getChildren().add(mainMenu);
        topBorder.setTop(topContainer);
        
        // Set up each menu item.
        Menu file = setUpFileMenu();
        Menu edit = setUpEditMenu();
 
        mainMenu.getMenus().addAll(file, edit);
    }
    
    /**
     * Returns the menu representing the file menu. Defines the functionality
     * of each MenuItem.
     * 
     * @return the menu after being prepared.
     */
    private Menu setUpFileMenu() {
        Menu file = new Menu("File");
        MenuItem openFile  = new MenuItem("Open Journal");
        MenuItem saveFile  = new MenuItem("Save Journal");
        MenuItem saveTab   = new MenuItem("Save Current Tab");
        MenuItem importTxt = new MenuItem("Import");
        MenuItem exportTxt = new MenuItem("Export");
        MenuItem exitApp   = new MenuItem("Exit");
        
        openFile.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
        openFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Create and call file chooser
                final FileChooser journalChooser = new FileChooser();
                journalChooser.setTitle("Open XML Journal File");
                journalChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
                File journalToRead = journalChooser.showOpenDialog(primaryStage);
                
                // If the user didn't click cancel..
                if (journalToRead != null) {
                    try {
                        readFile(journalToRead);
                    } catch (IOException | SAXException | ParserConfigurationException | InterruptedException ex) {
                        writeErrorFile(ex.getMessage());
                    }
                }
            } 
        });
        
        saveFile.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        saveFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Create and call file chooser
                final FileChooser journalChooser = new FileChooser();
                journalChooser.setTitle("Save XML Journal File");
                journalChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
                File journalToSave = journalChooser.showSaveDialog(primaryStage);
                
                if (journalToSave != null) {
                    try {
                        // Set up XML file and save it.
                        XML file = new XML(journalToSave);
                        spiritualJournal.setXmlFile(file);
                        spiritualJournal.saveXML();
                    } catch (TransformerException | ParserConfigurationException | FileNotFoundException ex) {
                        writeErrorFile(ex.getMessage());
                    }
                }
            }
        });
        
        saveTab.setAccelerator(KeyCombination.keyCombination("Ctrl+T"));
        saveTab.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                boolean has = false;
                
                // Check to see if the tab exits on the left pane.
                int index = entryTabs.getSelectionModel().getSelectedIndex();
                Tab entryTab = entryTabs.getTabs().get(index);
                ObservableList<TreeItem<String>> children = journalViewer[0].getRoot().getChildren();
                
                for (int i = 0; i < children.size(); ++i) {
                    if (entryTab.getText().equals(children.get(i).getValue())) {
                        has = true;
                        break;
                    }
                }
                
                // If there is an entry, overwrite entry, otherwise create a new entry.
                if (!has) {
                    Entry entry = new Entry();
                    entry.setDate(entryTab.getText());
                    TextArea ta = (TextArea)entryTab.getContent();
                    entry.setText(ta.getText());
                    
                    // Find all the scriptures and topics
                    spiritualJournal.getFinder().parseForScriptures(entry);
                    spiritualJournal.getFinder().parseForTopics(entry);
                    
                    spiritualJournal.getEntries().add(entry);
                    setUpTreeItem(spiritualJournal.getEntries().get(spiritualJournal.getEntries().size() - 1));
                } else {
                    for (Entry entry : spiritualJournal.getEntries()) {
                        if (entryTab.getText().equals(entry.getDate())) {
                            TextArea ta = (TextArea)entryTab.getContent();
                            entry.setText(ta.getText());
                            break;
                        }
                    }
                }
            }
        });
        
        importTxt.setAccelerator(KeyCombination.keyCombination("Ctrl+I"));
        importTxt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Create and call file chooser
                final FileChooser journalChooser = new FileChooser();
                journalChooser.setTitle("Import Text File As Journal File");
                journalChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("TXT", "*.txt"));
                File txtFile = journalChooser.showOpenDialog(primaryStage);
                
                // Import text file
                if (txtFile != null) {
                    try {
                        boolean has = false;
                        
                        // Set up TextFile Document in spiritualJournal and imports the file in.
                        TextFile file = new TextFile(txtFile);
                        spiritualJournal.setTextDoc(file);
                        spiritualJournal.importTxt();
                        String textDoc = spiritualJournal.getTextDoc().getPath().getName();
                        textDoc = textDoc.substring(0, textDoc.indexOf("."));
                        
                        Thread importTextThread = new Thread(new JournalViewerThread(textDoc));
                        importTextThread.start();
                    } catch (IOException ex) {
                        writeErrorFile(ex.getMessage());
                    }
                }
            }
        });
        
        exportTxt.setAccelerator(KeyCombination.keyCombination("Ctrl+E"));
        exportTxt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    // Choose save file
                    final FileChooser journalChooser = new FileChooser();
                    journalChooser.setTitle("Export Journal As Text File");
                    journalChooser.getExtensionFilters().add(
                            new FileChooser.ExtensionFilter("TXT", "*.txt"));
                    File txtFile = journalChooser.showSaveDialog(primaryStage);
                    
                    // Export text document
                    TextFile file = new TextFile(txtFile);
                    spiritualJournal.setTextDoc(file);
                    spiritualJournal.exportTxt();
                } catch (IOException ex) {
                    writeErrorFile(ex.getMessage());
                }
            } 
        });
        
        exitApp.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));
        exitApp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Platform.exit();
            } 
        });
        
        file.getItems().addAll(openFile, saveFile, saveTab, importTxt, exportTxt, exitApp);
        
        return file;
    }
    
    /**
     * Reads in the XML file it is given and calls another thread to set up the 
     * viewerByEntries.
     * 
     * @param journalToRead                 the file that will be read in.
     * @throws IOException                  test reading file
     * @throws SAXException                 test building a Document
     * @throws ParserConfigurationException test parsing a Document
     */
    private void readFile(File journalToRead) throws IOException, SAXException,
            ParserConfigurationException, InterruptedException {
        // Read XML file
        XML file = new XML(journalToRead);
        spiritualJournal.setXmlFile(file);
        spiritualJournal.readXML();
                        
        int indexOfEndOfName = journalToRead.getName().indexOf(".");
        String journalName = journalToRead.getName().substring(0, indexOfEndOfName);
        
        // Background thread setting up journal viewer.
        Thread readFileThread = new Thread(new JournalViewerThread(journalName));
        readFileThread.start();
    }
    
    /**
     * Removes all current items found in viewerByEntries.
     * Populates spiritualJournal's entry list and viewerByEntries with the new information found
     * in the spiritualJournal.
     * 
     * @param journalName the file name that will become the root of viewerByEntries.
     */
    private void setUpJournalViewerFromJournal(final String journalName) {
        final int i = 0;
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // Remove all items in viewerByEntries
                for (int j = 0; j < journalViewer.length; ++j) {
                    while (i < journalViewer[j].getRoot().getChildren().size()) {
                        journalViewer[j].getRoot().getChildren().remove(i);
                    }
                }
                
                // Remove all entryTabs except for the "add new tab" tab
                entryTabs.getTabs().remove(0, entryTabs.getTabs().size()-1);
                
                // Set root and expand it
                for (int j = 0; j < journalViewer.length; ++j) {
                    journalViewer[j].setRoot(new TreeItem<>(journalName + ":"));
                    journalViewer[j].getRoot().setExpanded(true);
                }
            }
        });
        
        // Add the entrys on the entry list and the viewerByEntries. Sleep to show progress.
        for (Entry entry : spiritualJournal.getEntries()) {
            try {
                Thread.sleep(500);
                setUpTreeItem(entry);
            } catch (InterruptedException ex) {
                writeErrorFile(ex.getMessage());
            }
        }
    }
    
    /**
     * Sets up one tree item for each tree view.
     * 
     * @param entry the entry that will be added to the viewerByEntries.
     */
    public void setUpTreeItem(final Entry entry) {
        setUpEntryTreeItem(entry);
        setUpScriptureTreeItem(entry);
        setUpTopicTreeItem(entry);
    }
    
    /**
     * Adds the entry date on the first level. Adds scripture and topic subsections 
     * on the second level and all the references on the third level.
     * 
     * @param entry 
     */
    private void setUpEntryTreeItem(final Entry entry) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ObservableList<TreeItem<String>> children = journalViewer[0].getRoot().getChildren();
                children.add(new TreeItem<>(entry.getDate()));
                children.get(children.size()-1).getChildren().add(new TreeItem<>("Scriptures: "));
                        
                ObservableList<TreeItem<String>> childrenOfScriptures =
                        children.get(children.size()-1).getChildren().get(0).getChildren();
                
                // Adds the scriptrues.
                for (Scripture scripture : entry.getScriptureList()) {
                    childrenOfScriptures.add(new TreeItem<>(scripture.getFullTitle()));
                }
                
                children.get(children.size()-1).getChildren().add(new TreeItem<>("Topics: "));
                        
                ObservableList<TreeItem<String>> childrenOfTopics =
                        children.get(children.size()-1).getChildren().get(1).getChildren();
                        
                // Adds the topics
                for (String topic : entry.getTopicsList()) {
                    childrenOfTopics.add(new TreeItem<>(topic));
                }     
            }
        });        
    }
    
    /**
     * Sets up the tree view that views by scriptures. First level is the scripture
     * reference node. 2nd is all the references themselves. The third is all the
     * entries under their appropriate scripture references.
     * 
     * @param entry 
     */
    private void setUpScriptureTreeItem(final Entry entry) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ObservableList<TreeItem<String>> childrenOfRoot = journalViewer[1].getRoot().getChildren();
                
                if (childrenOfRoot.size() == 0) {
                    childrenOfRoot.add(new TreeItem<>("Scripture References"));
                }
                
                ObservableList<TreeItem<String>> childrenOfScripture = childrenOfRoot.get(0).getChildren();
                
                // Create a scripture reference item and put the entry date under it.
                for (Scripture scripture : entry.getScriptureList()) {
                    boolean has = false;
                    
                    for (TreeItem<String> item : childrenOfScripture) {
                        if (item.getValue().equals(scripture.getFullTitle())) {
                            has = true;
                            item.getChildren().add(new TreeItem<>(entry.getDate()));
                            break;
                        }
                    }
                        
                    if (!has) {
                        childrenOfScripture.add(new TreeItem<>(scripture.getFullTitle()));
                        childrenOfScripture.get(childrenOfScripture.size() - 1).getChildren().add(new TreeItem<>(entry.getDate()));
                    }
                }                        
            }   
        });
    }
    
    /**
     * Sets up the tree view that views by topics. First level is the topic
     * reference node. 2nd is all the references themselves. The third is all the
     * entries under their appropriate topic references.
     * 
     * @param entry 
     */
    private void setUpTopicTreeItem(final Entry entry) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ObservableList<TreeItem<String>> childrenOfRoot = journalViewer[2].getRoot().getChildren();
             
                if (childrenOfRoot.size() == 0) {
                    journalViewer[2].getRoot().getChildren().add(new TreeItem<>("Topic References"));
                }
                    
                ObservableList<TreeItem<String>> childrenOfTopic = childrenOfRoot.get(0).getChildren();

                // Create a topic reference item and put the entry date under it.
                for (String topic : entry.getTopicsList()) {
                    boolean has = false;
                    
                    for (TreeItem<String> item : childrenOfTopic) {
                        if (item.getValue().equals(topic)) {
                            has = true;
                            item.getChildren().add(new TreeItem<>(entry.getDate()));
                            break;
                        }
                    }
                    
                    if (!has) {
                        childrenOfTopic.add(new TreeItem<>(topic));
                        childrenOfTopic.get(childrenOfTopic.size() - 1).getChildren().add(new TreeItem<>(entry.getDate()));
                    }   
                }
            }
        });
    }
    
    /**
     * Returns the menu that defines the edit menu. Sets up the actions of each
     * menu item.
     * @return the edit menu
     */
    private Menu setUpEditMenu() {
        Menu edit = new Menu("Edit");
        MenuItem addEntry = new MenuItem("Add Entry");
        
        addEntry.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
        addEntry.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                addNewTab();
            } 
        });
        
        edit.getItems().addAll(addEntry);
        
        return edit;
    }
    
    /**
     * Writes any string given to it to a file called error.txt. Used for
     * error checking.
     * @param ex error message to be written to a file.
     */
    private void writeErrorFile(String ex) {
        BufferedWriter bw = null;
        try {
            File errorFile = new File("error.txt");
            bw = new BufferedWriter(new FileWriter(errorFile));
            bw.write(ex);
            showErrorDialog(ex);
        } catch (IOException ex1) {
            Logger.getLogger(SpiritualInsightJournalGUI.class.getName()).log(Level.SEVERE, null, ex1);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException ex1) {
                Logger.getLogger(SpiritualInsightJournalGUI.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }
    
    /**
     * When an error occurs, show the error message on a new dialog box.
     * @param ex 
     */
    private void showErrorDialog(String ex) {
        Text errorTitle = new Text("ERROR:");
        errorTitle.setId("title");
        Text errorMessage = new Text(ex);
        errorMessage.setId("message");
        
        BorderPane messagePane = new BorderPane();
        messagePane.setTop(errorTitle);
        messagePane.setCenter(errorMessage);
        
        Scene errorScene = new Scene(messagePane, 400, 170);
        errorScene.getStylesheets().add(
                SpiritualInsightJournalGUI.class.getResource("/Resources/spiritualJournal.css")
                        .toExternalForm());
        
        Stage errorStage = new Stage();
        errorStage.setScene(errorScene);
        errorStage.initStyle(StageStyle.UTILITY);
        errorStage.setTitle("ERROR");
        errorStage.sizeToScene();
        errorStage.initOwner(primaryStage);
        errorStage.initModality(Modality.WINDOW_MODAL);
        errorStage.show();
    }
    
    /**
     * Give the user a warning about what the user is trying to do and see if he/she
     * wants to proceed or not.
     * @param warningMessage the message to be displayed
     * @return true or false depending on if the user presses "Proceed" or "Cancel."
     */
    private boolean warningDialog(String warningMessage) {
        // Set up grid with buttons
        GridPane buttonGrid = new GridPane();
        final Button proceed = new Button("Proceed");
        final Button cancel = new Button("Cancel");
        buttonGrid.add(proceed, 0, 0);
        buttonGrid.add(cancel, 1, 0);
        
        // Set up Text Elements
        Text errorTitle = new Text("WARNING:");
        errorTitle.setId("title");
        Text message = new Text(warningMessage);
        message.setId("message");
        
        // Set up BorderPane
        BorderPane messagePane = new BorderPane();
        messagePane.setTop(errorTitle);
        messagePane.setCenter(message);
        messagePane.setBottom(buttonGrid);
        
        // Set up scene
        Scene warningScene = new Scene(messagePane, 400, 170);
        warningScene.getStylesheets().add(
                SpiritualInsightJournalGUI.class.getResource("/Resources/spiritualJournal.css")
                        .toExternalForm());
        
        // Set up Stage
        final Stage warningStage = new Stage();
        warningStage.setScene(warningScene);
        warningStage.initStyle(StageStyle.UTILITY);
        warningStage.setTitle("WARNING");
        warningStage.sizeToScene();
        warningStage.initOwner(primaryStage);
        warningStage.initModality(Modality.WINDOW_MODAL);
        
        // Set up Button events
        proceed.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                warningStage.close();
            }
        });
        
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cancel.setCancelButton(true);
                warningStage.close();
            }
        });
        
        // wait...
        warningStage.showAndWait();
        
        return !cancel.isCancelButton();
    }
    
    /**
     * A class that allows when the user opens or imports a new file to be done
     * on a separate thread so as to see the progress better.
     */
    private class JournalViewerThread implements Runnable {
        String title;
        
        public JournalViewerThread(String title) {
            this.title = title;
        }
        
        @Override
        public void run() {
            setUpJournalViewerFromJournal(title);
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }    
}