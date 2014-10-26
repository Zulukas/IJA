package InsightJournalApplication;

import java.io.File;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuItemBuilder;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
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
 *
 * @author Kevin Andres
 */
public class GUI extends Application {

    private Stage currentStage;
    private String fileName = "";

    private Scene mainScene;
    private Scene searchScene;
    
    public static Journal myJournal = new Journal();
    public Entry currentEntry = new Entry();

    private final String todayAsString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    private String selectedDate = "NO DATE";

    private GridPane grid;
    private GridPane searchGrid;
    private ListView<String> entries = new ListView<>();
    private ListView<String> scriptures = new ListView<>();
    private ListView<String> topics = new ListView<>();

    private TextArea entryText;

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
                        System.out.println ("Saving to: " + fileName);
                        myJournal.saveFiles(fileName);
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
        final Menu searchMenu = new Menu("Search");
        final MenuItem searchDateMenuItem = MenuItemBuilder.create().text("Search by Date").onAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        System.out.println("NOT IMPLEMENTED YET :(");
                    }
                }
        ).accelerator(new KeyCodeCombination(
                KeyCode.D, KeyCombination.ALT_DOWN))
                .build();

        final MenuItem searchScriptureMenuItem = MenuItemBuilder.create().text("Search by Scripture").onAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        System.out.println("NOT IMPLEMENTED YET :(");
                    }
                }
        ).accelerator(new KeyCodeCombination(
                KeyCode.S, KeyCombination.ALT_DOWN))
                .build();

        final MenuItem searchTopicMenuItem = MenuItemBuilder.create().text("Search by Topic").onAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        System.out.println("NOT IMPLEMENTED YET :(");
                    }
                }
        ).accelerator(new KeyCodeCombination(
                KeyCode.T, KeyCombination.ALT_DOWN))
                .build();

        searchMenu.getItems().add(searchDateMenuItem);
        searchMenu.getItems().add(searchScriptureMenuItem);
        searchMenu.getItems().add(searchTopicMenuItem);
        menuBar.getMenus().add(searchMenu);

        // Prepare 'Help' drop-down menu  
        final Menu helpMenu = new Menu("Help");
        final MenuItem aboutMenuItem = MenuItemBuilder.create().text("About").onAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        System.out.println("You clicked on About!");
                    }
                })
                .accelerator(
                        new KeyCodeCombination(
                                KeyCode.A, KeyCombination.CONTROL_DOWN))
                .build();
        helpMenu.getItems().add(aboutMenuItem);
        menuBar.getMenus().add(helpMenu);

        // bind width of menu bar to width of associated stage  
        menuBar.prefWidthProperty().bind(menuWidthProperty);

        return menuBar;
    }

    @Override
    public void start(final Stage stage) {
        //loadFile("C:\\Users\\kevin\\Documents\\NetBeansProjects\\InsightJournalApplication\\src\\InsightJournalApplication\\myJournal.xml");
        stage.setTitle("Insight Journal Application");
        stage.getIcons().add(new Image("file:src\\InsightJournalApplication\\icon.png"));
        final Group rootGroup = new Group();
        final Group searchGroup = new Group();
        mainScene = new Scene(rootGroup, 620, 600, Color.WHITE);
        searchScene = new Scene(searchGroup, 620, 600, Color.WHITE);
        final MenuBar menuBar = buildMenuBarWithMenus(stage.widthProperty());

        buildGrid();
        buildSearchGrid();
        
        rootGroup.getChildren().add(grid);
        searchGroup.getChildren().add(searchGrid);

        rootGroup.getChildren().add(menuBar);
        //searchGroup.getChildren().add(menuBar);

        //grid.setGridLinesVisible(true);
        currentStage = stage;
        currentStage.setScene(mainScene);
        currentStage.show();
    }
    
    public void search(final Stage stage) {
        buildSearchGrid();
    }

    public void buildSearchGrid() { //NEEDS TO BE ORGANIZED
        searchGrid = new GridPane();
        searchGrid.setAlignment(Pos.TOP_LEFT);
        searchGrid.setHgap(10);
        searchGrid.setVgap(10);
        searchGrid.setPadding(new Insets(25, 25, 25, 25));
        
        Button doneButton = new Button("Done");
        HBox hbDone = new HBox(10);
        
        doneButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                currentStage.setScene(mainScene);
            }
        });
        
        searchGrid.add(hbDone, 5, 5);       
    }
    
    public GridPane buildGrid() {
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

        return grid;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

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
                currentEntry = myEntries.get(selectedDate);
                entryText.setText(currentEntry.getContent());
                buildTopicsList();
                buildScripturesList();
            }
        });

        entries.setItems(entriesItems);
        entries.setPrefWidth(165);
        entries.setPrefHeight(243);

        if (!grid.getChildren().contains(entries)) {
            grid.add(entries, 0, 1, 1, 4);
        }
    }

    private void buildTopicsList() {
        if (selectedDate.equals("NO DATE")) {
            topics = new ListView();
            ObservableList<String> topicsItems = FXCollections.observableArrayList();
            topics.setPrefWidth(165);
            topics.setPrefHeight(243);
            grid.add(topics, 6, 6, 1, 4);
        } else {
            List<String> topicsList = currentEntry.getTopicList();
            Collections.sort(topicsList);

            //topics = new ListView();
            ObservableList<String> topicsItems = FXCollections.observableArrayList();

            for (String topic : topicsList) {
                topicsItems.add(topic);
            }

            System.out.println(topicsList);

            topics.setItems(topicsItems);
            topics.setPrefWidth(165);
            topics.setPrefHeight(243);

            if (!grid.getChildren().contains((topics))) {
                grid.add(topics, 6, 6, 1, 4);
            }
        }
    }

    private void buildScripturesList() {
        if (selectedDate.equals("NO DATE")) {
            scriptures = new ListView();
            ObservableList<String> scripturesItems = FXCollections.observableArrayList();

            scriptures.setPrefWidth(165);
            scriptures.setPrefHeight(243);
            grid.add(scriptures, 1, 6, 1, 4);
        } else {
            List<Scripture> scripturesList = currentEntry.getScriptureList();            
            //List<String> stringScripturesList = new ArrayList<>();

            scriptures = new ListView();
            ObservableList<String> scripturesItems = FXCollections.observableArrayList();

            for (Scripture scripture : scripturesList) {
                String temp = scripture.display();
                scripturesItems.add(temp);
            }

            Collections.sort(scripturesItems);
            
            scriptures.setItems(scripturesItems);
            scriptures.setPrefWidth(165);
            scriptures.setPrefHeight(243);

            if (!grid.getChildren().contains(scriptures)) {
                grid.add(scriptures, 1, 6, 1, 4);
            }
        }
    }

    private void buildEntryTextArea() {
        entryText = new TextArea();
        entryText.setPrefRowCount(15);
        entryText.setPrefColumnCount(32);
        entryText.wrapTextProperty();
        entryText.setWrapText(true);
        grid.add(entryText, 1, 1, 6, 4);
    }

    public void loadFile(String file) {
        try {
            fileName = file;
            currentStage.setTitle(file + " - Insight Journal Application");
            myJournal.loadFile(file);
            myJournal.display();
        } catch (NullPointerException e) {
            System.err.println("Unable to open \"" + file + "\"");
            e.printStackTrace();
        }
    }

    public void saveAsFile(String file) {
        
    }
    
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
        
        if (type.equals("open"))
            chosenFile = fileChooser.showOpenDialog(null);
        if (type.equals("save"))
            chosenFile = fileChooser.showSaveDialog(null);
        
        if (chosenFile != null) {
            String file = chosenFile.getPath();
            currentStage.setTitle(file + " - Insight Journal Application");
            return file;
        } else {
            return null;
        }
    }
}
