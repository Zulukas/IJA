package InsightJournalApplication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuItemBuilder;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 *The GUI class acts as the main class and handles everything
 */
public class GUI extends Application {
    
    /*****************************************************
     * Private member variables kept at the 'class' level
     * to make for easier accessing
     ****************************************************/
    private Stage currentStage;
    
    private Scene mainScene;
    private Scene searchScene;
    private Scene topicScene;
    
    private Journal myJournal = new Journal();
    private Entry currentEntry = new Entry();
    
    private GridPane grid;
    private GridPane searchGrid;
    private GridPane topicGrid;
    private ListView<String> entries = new ListView<>();
    private ListView<String> scriptures = new ListView<>();
    private ListView<String> topics = new ListView<>();
    private TextArea entryText;
    
    private final String todayAsString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    private String selectedDate = "NO DATE";
    private String comboSelection = "NO SELECTION";
    private String defaultFile = "NO FILE";
    private String loadedFile = "NO FILE";
    
    /***************************************************************************
     * start handles everything from setting up the stage to building the
     * individuals scenes.
     * 
     * @param stage Pass in the stage in which this function will build scenes
     * @
     **************************************************************************/
    @Override
    public void start(final Stage stage) {
        stage.setTitle("Insight Journal Application");
        stage.getIcons().add(new Image("file:src\\InsightJournalApplication\\icon.png"));
        
        
        final Group rootGroup = new Group();
        final Group searchGroup = new Group();
        final Group topicGroup = new Group();
        mainScene = new Scene(rootGroup, 620, 600, Color.WHITE);
        searchScene = new Scene(searchGroup, 620, 600, Color.WHITE);
        topicScene = new Scene(topicGroup, 620, 600, Color.WHITE);
        final MenuBar menuBar = buildMenuBarWithMenus(stage.widthProperty());
        final MenuBar menuBarSearch = buildMenuBarWithMenus(stage.widthProperty());
        final MenuBar menuBarTopic = buildMenuBarWithMenus(stage.widthProperty());
        
        buildGrid();
        buildSearchGrid();
        buildTopicGrid();
                
        topicGroup.getChildren().add(topicGrid);
        rootGroup.getChildren().add(grid);
        searchGroup.getChildren().add(searchGrid);
        
        searchGroup.getChildren().add(menuBarSearch);
        rootGroup.getChildren().add(menuBar);                
        topicGroup.getChildren().add(menuBarTopic);
        
        //grid.setGridLinesVisible(true);
        currentStage = stage;
        currentStage.setScene(mainScene);
        currentStage.show();
        defaultFile = PropertiesHandler.defaultFile;
        System.out.println("Default file set to: " + defaultFile);
        loadFile(defaultFile);
        loadedFile = defaultFile;
        
        System.out.println("Loaded file set to: " + loadedFile);
    }
    
    /***************************************************************************
     * Build the topic grid which will be used to allow the user to edit the
     * terms file
     **************************************************************************/
    public void buildTopicGrid() {
        topicGrid = new GridPane();
        topicGrid.setAlignment(Pos.TOP_CENTER);
        topicGrid.setHgap(10);
        topicGrid.setVgap(10);
        topicGrid.setPadding(new Insets(25, 25, 25, 25));
        
        final String selectedMaster = "";
        final String selectedSlave = "";
        
        final ListView <String> masterList = new ListView<>();
        final ListView <String> slaveList = new ListView<>();
        
        Button add = new Button("Add");
        Button remove = new Button("Remove");
        Button saveButton = new Button("Save Topics");
        Button back = new Button("Back");
        
        Label l = new Label("Tips:\n*Use the text field to add a topic\n" +
                "*Use the drop down menu (or click on a respective list) to add a scripture\n" +
                "*Select a topic on either side and press \"Remove\" to remove that topic\n" +
                "*Press save to save these topics permanently\n" +
                "*Press back to return to the main screen");
        
        final TextField tf = new TextField();
        
        final ObservableList<String> master = FXCollections.observableArrayList();        
        
        master.addAll(Journal.termsToFind.keySet());
        FXCollections.sort(master);
        
        ObservableList<String> options
                = FXCollections.observableArrayList("Main Topic", "Sub Topic");
        final ComboBox comboBox = new ComboBox(options);
        comboBox.getSelectionModel().select(0);
        
        masterList.setItems(master);
        
        masterList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            
            @Override
            public void handle(MouseEvent arg0) {
                ObservableList<String> slave = FXCollections.observableArrayList();
                String selectedMaster = masterList.getSelectionModel().getSelectedItem();
                
                if (selectedMaster != null) {
                    slave.addAll(Journal.termsToFind.get(selectedMaster));
                    FXCollections.sort(slave);
                }
                
                comboBox.getSelectionModel().select(0);
                
                slaveList.setItems(slave);
                tf.setText(selectedMaster);
            }
        });
        
        slaveList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            
            @Override
            public void handle(MouseEvent arg0) {
                String selectedSlave = slaveList.getSelectionModel().getSelectedItem();
                comboBox.getSelectionModel().select(1);
                
                tf.setText(selectedSlave);
            }
        });
        
        add.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent arg0) {
                String sm = masterList.getSelectionModel().getSelectedItem();
                String selection = (String) comboBox.getSelectionModel().getSelectedItem();
                
                final ObservableList<String> slave = FXCollections.observableArrayList();
                
                if (selection.equals("Main Topic")) {
                    if (!Journal.termsToFind.containsKey(tf.getText())) {
                        List<String> slaves = new ArrayList<>();
                        slaves.add(tf.getText().toLowerCase());
                        Journal.termsToFind.put(tf.getText(), slaves);
                        
                        master.remove(0, master.size());
                        master.addAll(Journal.termsToFind.keySet());
                        FXCollections.sort(master);
                        masterList.setItems(master);
                    }
                } else {
                    List<String> values = Journal.termsToFind.get(sm);
                    
                    if (!values.contains(tf.getText())) {
                        values.add(tf.getText());
                        slave.remove(0, slave.size());
                        slave.addAll(values);
                        FXCollections.sort(slave);
                        slaveList.setItems(slave);
                    }
                }
            }
        });
        
        remove.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent arg0) {
                final ObservableList<String> slave = FXCollections.observableArrayList();
                String sm = masterList.getSelectionModel().getSelectedItem();   //Needed a non-final selected master (hence: sm)             
                String selection = (String) comboBox.getSelectionModel().getSelectedItem();

                if (selection.equals("Main Topic")) {
                    if (Journal.termsToFind.containsKey(tf.getText())) {
                        Journal.termsToFind.remove(tf.getText());
                        master.remove(0, master.size());
                        slave.remove(0, slave.size());
                        master.addAll(Journal.termsToFind.keySet());
                        FXCollections.sort(master);
                        masterList.setItems(master);
                        slaveList.setItems(slave);
                    }
                } else {
                    List<String> values = Journal.termsToFind.get(sm);

                    if (values.contains(tf.getText())) {
                        values.remove(tf.getText());
                        slave.remove(0, slave.size());
                        slave.addAll(Journal.termsToFind.get(sm));                        
                        FXCollections.sort(slave);
                        slaveList.setItems(slave);
                    }
                }
            }            
        });
        
        back.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                currentStage.setScene(mainScene);
            }
        });
        
        saveButton.setOnAction(new EventHandler<ActionEvent>() {
           @Override
           public void handle(ActionEvent e) {
               try {
                   BufferedWriter bw = new BufferedWriter(new FileWriter(PropertiesHandler.terms));
                   
                   for (Map.Entry<String, List<String>> term : Journal.termsToFind.entrySet()) {
                       bw.write(term.getKey() + ":");
                       List<String> values = term.getValue();
                       
                       Collections.sort(values);
                       
                       int counter = 0;
                       
                       for (String value : values) {
                           if (counter == values.size() - 1) {
                               bw.write(value);
                           } else {
                               bw.write(value + ",");
                               counter++;
                           }
                       }
                       
                       bw.write("\n");
                   }
                   
                   bw.close();
               } catch (Exception ex) {
                   System.err.println("UNABLE TO SAVE TO TERMS.TXT!");
               }
           }
        });
        
        topicGrid.add(masterList, 1, 1, 2, 3);
        topicGrid.add(slaveList, 3, 1, 2, 3);
        topicGrid.add(tf, 1, 4);
        topicGrid.add(add, 2, 4);
        topicGrid.add(remove, 2, 5);
        topicGrid.add(saveButton, 1, 7);
        topicGrid.add(back, 1, 8);
        topicGrid.add(comboBox, 1, 5);
        topicGrid.add(l, 4, 4, 4, 4);
        
    }
    
    /***************************************************************************
     * build the scene containing the gui objects for searching the journal.
     **************************************************************************/
    public void buildSearchGrid() { //NEEDS TO BE ORGANIZED
        searchGrid = new GridPane();
        searchGrid.setAlignment(Pos.TOP_LEFT);
        searchGrid.setHgap(10);
        searchGrid.setVgap(10);
        searchGrid.setPadding(new Insets(25, 25, 25, 25));
        
        final ListView <String> resultsList = new ListView<>();
        
        
        Button searchButton = new Button("Search");
        
        
        final TextField tf = new TextField();
        
        final ObservableList<String> results = FXCollections.observableArrayList();
        
        ObservableList<String> options
                = FXCollections.observableArrayList(
                        "Scripture",
                        "Topic",
                        "Other"
                );
        final ComboBox comboBox = new ComboBox(options);
        comboBox.setValue("Scripture");
        
        Button backButton = new Button("Back");
        
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                currentStage.setScene(mainScene);
                tf.setText("");
                results.clear();
                resultsList.setItems(results);
                comboBox.getSelectionModel().clearSelection();
            }
        });
        
        
        
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                comboSelection = (String) comboBox.getSelectionModel().getSelectedItem();
                results.clear();
                
                if (!comboSelection.equals("NO SELECTION")) {
                    Map<String, Entry> entries = myJournal.getEntries();
                    
                    if (comboSelection.equals("Scripture")) {
                        for (String key : entries.keySet()) {
                            Entry temp = entries.get(key);
                            List<Scripture> scriptures = temp.getScriptureList();
                            List<String> sScriptures = new ArrayList<>();
                            
                            for (Scripture tempS : scriptures) {
                                sScriptures.add(tempS.display());
                            }
                            
                            if (sScriptures.contains(tf.getText())) {
                                results.add(key);
                            }
                        }
                    }
                    if (comboSelection.equals("Topic")) {
                        for (String key : entries.keySet()) {
                            Entry temp = entries.get(key);
                            List<String> topics = temp.getTopicList();
                            
                            if (topics.contains(tf.getText())) {
                                results.add(key);
                            }
                        }
                    }
                    if (comboSelection.equals("Other")) {
                        for (String key : entries.keySet()) {
                            if (entries.get(key).getContent().contains(tf.getText())) {
                                results.add(key);
                            }
                        }
                    }
                }
                
                resultsList.setItems(results);
            }
        });
        
        Label label = new Label("Search by: ");
        Label hints = new Label("Searching tips:\n"
                + "*Scripture: Search by a scripture reference\n"
                + "*Topic: Search by a topic reference\n"
                + "*Other: Search by a specific keyword\\phrase\n"
                + "Press \"Done\" when done searching");
        
        searchGrid.add(hints, 1, 6, 3, 5);
        searchGrid.add(label, 1, 3, 1, 1);
        searchGrid.add(resultsList, 7, 1, 2, 7);
        searchGrid.add(searchButton, 1, 1);
        searchGrid.add(tf, 2, 1);
        searchGrid.add(comboBox, 2, 3);
        searchGrid.add(backButton, 1, 5);
    }
    
    /***************************************************************************
     * build the scene containing the gui objects for interacting with the
     * journal
     **************************************************************************/
    public void buildGrid() {
        grid = new GridPane();
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        Label dateLabel = new Label("Entry Date:");
        grid.add(dateLabel, 0, 0);
        
        Label textLabel = new Label("Selected Entry Text:");
        grid.add(textLabel, 1, 0);
        
        Label scripturesLabel = new Label("Scriptures Found:");
        grid.add(scripturesLabel, 1, 5);
        
        Label topicsLabel = new Label("Topics Found:");
        grid.add(topicsLabel, 6, 5);
        
        final Button newEntryBtn = new Button("New Entry");
        Button updateEntryBtn = new Button("Update Entry");
        Button removeEntryBtn = new Button("Remove Entry");
        Button searchBtn = new Button("Search");
        
        HBox hbNewEntry = new HBox(10);
        hbNewEntry.getChildren().add(newEntryBtn);
        
        HBox hbUpdateEntry = new HBox(10);
        hbUpdateEntry.getChildren().add(updateEntryBtn);
        
        HBox hbRemoveEntry = new HBox(10);
        hbRemoveEntry.getChildren().add(removeEntryBtn);
        
        HBox hbSearch = new HBox(10);
        hbSearch.getChildren().add(searchBtn);
        
        newEntryBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (!myJournal.getEntries().containsKey(todayAsString)) {
                    Entry newEntry = new Entry(todayAsString, "");
                    myJournal.addToEntries(todayAsString, newEntry);
                    buildEntriesList();
                }
            }
        });
        
        updateEntryBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Entry currentEntry = new Entry(selectedDate, entryText.getText());
                myJournal.addToEntries(selectedDate, currentEntry);
                
                buildEntriesList();
                buildScripturesList();
                buildTopicsList();
            }
        });
        
        removeEntryBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                myJournal.getEntries().remove(selectedDate);
                
                buildEntriesList();
                buildScripturesList();
                buildTopicsList();
            }
        });
        
        searchBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                currentStage.setScene(searchScene);
            }
        });
        
        grid.add(hbNewEntry, 0, 6);
        grid.add(hbUpdateEntry, 0, 7);
        grid.add(hbRemoveEntry, 0, 8);
        grid.add(hbSearch, 0, 9);
        
        buildEntryTextArea();
        buildTopicsList();
        buildScripturesList();
        buildEntriesList();
    }
    
    /***************************************************************************
     * Build the ListView object containing the entries within the journal.
     **************************************************************************/
    public void buildEntriesList() {
        final Map<String, Entry> myEntries = myJournal.getEntries();
        List<String> dates = new ArrayList<>();
        Collections.sort(dates);
        
        for (String date : myEntries.keySet()) {
            dates.add(date);
        }
        
        //entries = new ListView();
        ObservableList<String> entriesItems = FXCollections.observableArrayList();
        
        for (String date : dates) {
            entriesItems.add(date);
        }
        
        entries.setOnMouseClicked(new EventHandler<MouseEvent>() {
            
            @Override
            public void handle(MouseEvent arg0) {
                selectedDate = entries.getSelectionModel().getSelectedItem();
                
                if (selectedDate != null) {
                    currentEntry = myEntries.get(selectedDate);
                    entryText.setText(currentEntry.getContent());
                }
                
                
                buildTopicsList();
                buildScripturesList();
            }
        });
        
        FXCollections.sort(entriesItems);
        
        entries.setItems(entriesItems);
        entries.setPrefWidth(165);
        entries.setPrefHeight(243);
        
        if (!grid.getChildren().contains(entries)) {
            grid.add(entries, 0, 1, 1, 4);
        }
    }
    
    /***************************************************************************
     * Build the ListView object containing the topic references to the selected
     * journal entry
     **************************************************************************/
    private void buildTopicsList() {
        if (selectedDate == null) {
            topics = new ListView();            
            topics.setPrefWidth(165);
            topics.setPrefHeight(243);
            grid.add(topics, 6, 6, 1, 4);
        } else {
            List<String> topicsList = currentEntry.getTopicList();
            Collections.sort(topicsList);
                        
            ObservableList<String> topicsItems = FXCollections.observableArrayList();

            for (String topic : topicsList) {
                topicsItems.add(topic);
            }
            
            FXCollections.sort(topicsItems);
            
            topics.setItems(topicsItems);
            topics.setPrefWidth(165);
            topics.setPrefHeight(243);
            
            if (!grid.getChildren().contains((topics))) {
                grid.add(topics, 6, 6, 1, 4);
            }
        }
    }
    
    /***************************************************************************
     * build the ListView object containing the scripture references to the
     * selected journal entry
     **************************************************************************/
    private void buildScripturesList() {
        if (selectedDate == null) {
            scriptures = new ListView();
            scriptures.setPrefHeight(243);
            grid.add(scriptures, 1, 6, 1, 4);
        } else {
            List<Scripture> scripturesList = currentEntry.getScriptureList();
            
            scriptures = new ListView();
            ObservableList<String> scripturesItems = FXCollections.observableArrayList();
            
            for (Scripture scripture : scripturesList) {
                String temp = scripture.display();
                scripturesItems.add(temp);
            }
            
            FXCollections.sort(scripturesItems);
            
            scriptures.setItems(scripturesItems);
            scriptures.setPrefWidth(165);
            scriptures.setPrefHeight(243);
            
            if (!grid.getChildren().contains(scriptures)) {
                grid.add(scriptures, 1, 6, 1, 4);
            }
        }
    }
    
    /***************************************************************************
     * build the TextArea object containing the journal entry content to the
     * selected journal entry
     **************************************************************************/
    private void buildEntryTextArea() {
        entryText = new TextArea();
        entryText.setPrefRowCount(15);
        entryText.setPrefColumnCount(32);
        entryText.wrapTextProperty();
        entryText.setWrapText(true);
        grid.add(entryText, 1, 1, 6, 4);
    }
    
    /***************************************************************************
     * A simple function which changes the application title, loads the file,
     * and displays the results in the console for debugging purposes.
     * 
     * @param file The file to be loaded
     **************************************************************************/
    public void loadFile(String file) {
        try {
            //String file = file;
            currentStage.setTitle(file + " - Insight Journal Application");
            myJournal.loadFile(file);
            
            // JournalLoader jl = new JournalLoader(file, updater, myJournal);
            // Thread t = new Thread(jl);
            // t.start();
            
            buildEntriesList();
            buildTopicsList();
            buildScripturesList();
            
        } catch (NullPointerException e) {
            System.err.println("Unable to open \"" + file + "\"");
            e.printStackTrace();
        }
    }
    
    /***************************************************************************
     * Load a file dialogue to open a file or save to a file.
     * 
     * @param stage The stage which will hold this window
     * @param type "open" or "save"
     * @return String returns the file selected in the file dialogue.
     **************************************************************************/
    public String getFile(Stage stage, String type) {
        FileChooser fileChooser = new FileChooser();
        
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("XML Files", "*.xml"),
                new ExtensionFilter("Text Files", "*.txt"),
                new ExtensionFilter("All Files", "*.*"));
        
        //Set to user directory or go to default if cannot access
        //String userDirectoryString = System.getProperty("user.home" + "/Desktop");
        String userDirectoryString = System.getProperty("user.home");
        String applicationDirectory = "\\src\\InsightJournalApplication";
        File userDirectory = new File(applicationDirectory);
        if (!userDirectory.canRead()) {
            userDirectory = new File(userDirectoryString);
        }
        
        fileChooser.setInitialDirectory(userDirectory);
        
        //Choose the file
        File chosenFile = null;
        
        if (type.equals("open")) {
            chosenFile = fileChooser.showOpenDialog(null);
        }
        if (type.equals("save")) {
            chosenFile = fileChooser.showSaveDialog(null);
        }
        
        if (chosenFile != null) {
            String file = chosenFile.getPath();
            currentStage.setTitle(file + " - Insight Journal Application");
            return file;
        } else {
            return null;
        }
    }
    
    /***************************************************************************
     * Build the menu bar which is at the top of the application.
     * 
     * @param menuWidthProperty the width of the menu
     **************************************************************************/
    private MenuBar buildMenuBarWithMenus(final ReadOnlyDoubleProperty menuWidthProperty) {
        final MenuBar menuBar = new MenuBar();
        
        // Prepare left-most 'File' drop-down menu
        final Menu fileMenu = new Menu("File");
        
        // BEGIN FILE DROP DOWN ACTORS
        final MenuItem newMenuItem = MenuItemBuilder.create().text("New").onAction(
                new EventHandler<ActionEvent>() {
                    
                    @Override
                    public void handle(ActionEvent e) {
                        myJournal = new Journal();
                        System.out.println(todayAsString);
                        
                        Entry newEntry = new Entry(todayAsString, "");
                        myJournal.addToEntries(todayAsString, newEntry);
                        
                        buildEntriesList();
                    }
                }).accelerator(new KeyCodeCombination(
                        KeyCode.N, KeyCombination.CONTROL_DOWN))
                .build();
        
        final MenuItem openMenuItem = MenuItemBuilder.create().text("Open").onAction(
                new EventHandler<ActionEvent>() {
                    
                    @Override
                    public void handle(ActionEvent e) {
                        String file = "";
                        
                        try {
                            Stage newStage = new Stage();
                            file = getFile(newStage, "open");
                            System.out.println(file);
                            loadFile(file);
                            
                            buildEntriesList();
                        } catch (NullPointerException ex) {
                            System.err.println("Unable to open \"" + file + "\"");
                            ex.printStackTrace();
                        }
                    }
                }).accelerator(new KeyCodeCombination(
                        KeyCode.O, KeyCombination.CONTROL_DOWN))
                .build();
        
        final MenuItem saveMenuItem = MenuItemBuilder.create().text("Save").onAction(
                new EventHandler<ActionEvent>() {
                    
                    @Override
                    public void handle(ActionEvent e) {
                        System.out.println("Saving to: " + loadedFile);
                        myJournal.saveFiles(loadedFile);
                    }
                }).accelerator(new KeyCodeCombination(
                        KeyCode.S, KeyCombination.CONTROL_DOWN))
                .build();
        
        final MenuItem saveAsMenuItem = MenuItemBuilder.create().text("Save As").onAction(
                new EventHandler<ActionEvent>() {
                    
                    @Override
                    public void handle(ActionEvent e) {
                        String file = "";
                        
                        try {
                            Stage newStage = new Stage();
                            file = getFile(newStage, "save");
                            System.out.println(file);
                            myJournal.saveFiles(file);
                        } catch (NullPointerException ex) {
                            System.err.println("Unable to open \"" + file + "\"");
                            ex.printStackTrace();
                        }
                    }
                }).accelerator(new KeyCodeCombination(
                        KeyCode.S, KeyCombination.ALT_DOWN, KeyCombination.CONTROL_DOWN))
                .build();
        
        final MenuItem exitMenuItem = MenuItemBuilder.create().text("Exit").onAction(
                new EventHandler<ActionEvent>() {
                    
                    @Override
                    public void handle(ActionEvent e) {
                        currentStage.close();
                    }
                }).accelerator(new KeyCodeCombination(
                        KeyCode.F4, KeyCombination.ALT_DOWN))
                .build();
        
        fileMenu.getItems().add(newMenuItem);
        fileMenu.getItems().add(openMenuItem);
        fileMenu.getItems().add(saveMenuItem);
        fileMenu.getItems().add(saveAsMenuItem);
        fileMenu.getItems().add(new SeparatorMenuItem());
        fileMenu.getItems().add(exitMenuItem);
        menuBar.getMenus().add(fileMenu);
        
        // Prepare 'Search' drop-down menu
        final Menu editMenu = new Menu("Edit");
        final MenuItem modifyTopicsMenuItem = MenuItemBuilder.create().text("Add/Remove Topics").onAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        currentStage.setScene(topicScene);
                    }
                }
        ).accelerator(new KeyCodeCombination(
                KeyCode.D, KeyCombination.ALT_DOWN))
                .build();
        
        editMenu.getItems().add(modifyTopicsMenuItem);
        menuBar.getMenus().add(editMenu);
        
        // bind width of menu bar to width of associated stage
        menuBar.prefWidthProperty().bind(menuWidthProperty);
        
        return menuBar;
    }
    
    /***************************************************************************
     * main simply calls launch which gets the program going
     **************************************************************************/
    public static void main(String[] args) {
        launch(args);
    }
}

