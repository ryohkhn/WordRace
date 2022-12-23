package project.views.menu;
import project.controllers.game.GameController;
import project.models.game.GameModel;
import project.views.View;
import project.views.game.GameView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.control.*;


public class MenuView extends Application implements View{
    private Stage stage;
    private final VBox root;

    private final ListProperty<String> playersList;
    private final IntegerProperty playersNumber;
    private final IntegerProperty playersJoined;
    private int lives;
    private int wordShown;

    private final double width=900;
    private final double height=600;

    public MenuView(){
        this.root=new VBox();
        this.playersList=new SimpleListProperty<>(FXCollections.observableArrayList());
        this.playersNumber=new SimpleIntegerProperty();
        this.playersJoined=new SimpleIntegerProperty();

        try{
            start(new Stage());
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.stage=primaryStage;

        MenuBar menuBar=initMenuBar();
        this.root.getChildren().add(menuBar);
        initMenuButtons();

        Scene scene=new Scene(this.root, this.width, this.height);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Configuration");
        primaryStage.show();
    }

    /**
     * Create the landing page buttons
     * soloButton -> game configuration
     * hostButton -> game configuration then the host configuration
     * joinButton -> ip adress input
     * quitButton -> quit the GUI
     */
    private void initMenuButtons(){
        VBox vBox=newCenteredVbox();
        Label label=new Label("Word Race");
        label.setFont(new Font(50));
        vBox.getChildren().add(label);

        Button soloButton = new Button("Solo");
        Button hostButton = new Button("Host");
        Button joinButton = new Button("Join");
        Button quitButton = new Button("Quit");
        soloButton.setFont(new Font(14));
        hostButton.setFont(new Font(14));
        joinButton.setFont(new Font(14));
        quitButton.setFont(new Font(14));

        soloButton.setOnAction(e -> {
            this.root.getChildren().remove(vBox);
            gameConfiguration(false);
        });
        hostButton.setOnAction(e -> {
            this.root.getChildren().remove(vBox);
            gameConfiguration(true);
        });
        joinButton.setOnAction(e -> {
            this.root.getChildren().remove(vBox);
            joinConfiguration();
        });
        quitButton.setOnAction(e -> System.exit(0));

        vBox.getChildren().addAll(soloButton,hostButton,joinButton,quitButton);
        this.root.getChildren().add(vBox);
    }

    /**
     * Create the inputs for the game configuration and let the player start the game
     * If the game need to be hosted a menu with the number of players
     * and a list of the players connected is shown
     * @param hosting boolean if the player is hosting or not
     */
    private void gameConfiguration(boolean hosting){
        GridPane gridPane=new GridPane();
        gridPane.setPadding(new Insets(this.height/5));
        gridPane.setVgap(30);
        gridPane.setHgap(50);
        gridPane.setAlignment(Pos.CENTER);

        Label labelTitle = new Label("Game configuration");
        labelTitle.setFont(new Font(24));

        Label wordsLabel = new Label("Quantity of words shown");
        Spinner<Integer> wordsSpinner=new Spinner<>();
        wordsLabel.setLabelFor(wordsSpinner);
        SpinnerValueFactory<Integer> wordsValueFactory=new SpinnerValueFactory.IntegerSpinnerValueFactory(15,50,15);
        wordsSpinner.setValueFactory(wordsValueFactory);

        Label livesLabel = new Label("Quantity of lives");
        Spinner<Integer> livesSpinner=new Spinner<>();
        livesLabel.setLabelFor(livesSpinner);
        SpinnerValueFactory<Integer> livesValueFactory=new SpinnerValueFactory.IntegerSpinnerValueFactory(5,Integer.MAX_VALUE);
        livesSpinner.setValueFactory(livesValueFactory);


        gridPane.add(labelTitle,0,0,2,1);
        gridPane.add(wordsLabel,0,1);
        gridPane.add(wordsSpinner,1,1);
        gridPane.add(livesLabel,0,2);
        gridPane.add(livesSpinner,1,2);

        Button readyButton;
        if(hosting){
            readyButton = new Button("Next");
        } else{
            readyButton = new Button("Start");
        }

        VBox readyBox = new VBox();
        readyBox.setAlignment(Pos.TOP_CENTER);

        readyButton.setOnAction(event -> {
            this.wordShown= wordsSpinner.getValue();
            this.lives= livesSpinner.getValue();
            // TODO Set les variables dans le modèle
            if(hosting){
                this.root.getChildren().removeAll(gridPane,readyBox);
                hostConfiguration();
            }
            else{
                startGame();
            }
        });

        readyBox.getChildren().add(readyButton);
        this.root.getChildren().addAll(gridPane,readyBox);
    }

    /**
     * Create the inputs for the host configuration and show a list of the players
     */
    private void hostConfiguration(){
        GridPane gridPane=new GridPane();
        gridPane.setPrefHeight(height/2);
        gridPane.setVgap(30);
        gridPane.setHgap(50);
        gridPane.setAlignment(Pos.CENTER);

        Label playersLabel=new Label("Number of players");
        Spinner<Integer> playersSpinner=new Spinner<>();
        playersLabel.setLabelFor(playersSpinner);
        SpinnerValueFactory<Integer> playersValueFactory=new SpinnerValueFactory.IntegerSpinnerValueFactory(2,Integer.MAX_VALUE);
        playersSpinner.setValueFactory(playersValueFactory);

        VBox vBoxPlayersList=newCenteredVbox();
        vBoxPlayersList.setAlignment(Pos.CENTER);

        Button nextButton=new Button("Next");
        nextButton.setOnAction(event -> {
            setPlayersNumber(playersSpinner.getValue());
            playersSpinner.setDisable(true);
            vBoxPlayersList.getChildren().remove(nextButton);
            //this.root.getChildren().remove(vBoxPlayersList);

            Label stateLabel=new Label("Waiting for players");

            Label ipLabel=new Label("Ip Adress");
            TextField ipField=new TextField("xxx.xxx.xxx.xxx");
            ipLabel.setLabelFor(ipField);
            ipField.setEditable(false);

            TitledPane playersListPane=new TitledPane("Players list",null);

            /*
            Listener to the number of players that joined to update the "waiting" text
            and add a button when everyone joined
             */
            playersJoined.addListener((observable, oldValue, newValue) -> {
                System.out.println(oldValue+" to "+newValue);
                if(newValue.intValue()==getPlayersNumber() && newValue.intValue()!=0){
                    stateLabel.setText("All players ready");
                }
                if(getPlayersJoined()==getPlayersNumber()){
                    Button startButton=new Button("Start");
                    startButton.setOnAction(event1 -> {
                        startGame();
                    });
                    gridPane.add(startButton,0,6);
                }
            });

            // Bind the players list name to this ListView
            ListView<String> playersListView=new ListView<>();
            playersListView.itemsProperty().bind(playersList);

            // Set content of the TitledPane to the ListView to stay updated with le list of the players
            playersListPane.setContent(playersListView);

            gridPane.add(ipLabel,0,3);
            gridPane.add(ipField,1,3);
            vBoxPlayersList.getChildren().addAll(stateLabel,playersListPane);
        });

        gridPane.add(playersLabel,0,1);
        gridPane.add(playersSpinner,1,1);
        vBoxPlayersList.getChildren().add(nextButton);

        this.root.getChildren().addAll(gridPane,vBoxPlayersList);
    }

    /**
     * Create the input for the hoster to join
     */
    private void joinConfiguration(){
        VBox vBox=newCenteredVbox();

        Label label=new Label("IP Adress :");
        TextField ipAdress = new TextField();
        TextField nameField=new TextField();
        ipAdress.setPromptText("localhost");
        ipAdress.setMaxWidth(200);
        nameField.setMaxWidth(200);

        Button readyButton = new Button("Ready");

        readyButton.setOnAction(event -> {
            String ipInput = ipAdress.getCharacters().toString();
            System.out.println(ipInput);
            if(ipInput.length()==0 || ipInput.equals("localhost")){
                // TODO APPEL TEST IP AVEC LOCALHOST
                readyButton.setDisable(true);
                ipAdress.setDisable(true);
                Label waitingLabel = new Label("Waiting for game to start...");
                vBox.getChildren().add(waitingLabel);
            }
            else if(ipInput.contains("^(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[0-9]{1,2})(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[0-9]{1,2})){3}$")){
                // TODO TEST IP
                readyButton.setDisable(true);
                ipAdress.setDisable(true);
                Label waitingLabel = new Label("Waiting for game to start...");
                vBox.getChildren().add(waitingLabel);
            }
            else{
                Alert alert=new Alert(Alert.AlertType.ERROR,"Wrong IP format",ButtonType.OK);
                alert.setResizable(true);
                alert.getDialogPane().setPrefHeight(height/10);
                alert.getDialogPane().setPrefHeight(width/10);
                alert.showAndWait();
            }
            // TODO Cancel button
        });

        vBox.getChildren().addAll(label,ipAdress,readyButton);

        root.getChildren().add(vBox);
    }

    private VBox newCenteredVbox(){
        VBox vBox = new VBox(this.height/30);
        vBox.setPadding(new Insets(this.height/5));
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    @Override
    public void update(){
        // TODO Update values
    }

    @Override
    public void setVisible(boolean visible){
        if(visible) stage.show();
        else stage.hide();
    }

    public void startGame(){
        stage.close();
        GameController gameController=new GameController();
        gameController.start(this.lives,this.wordShown);
    }

    public static void main(String[] args){
        Platform.runLater(() -> {
            //MenuView menuView=new MenuView();
            // TODO TEMPORAIRE
            GameView gameView=new GameView(new GameModel(5,0,15));
        });
    }


    // GETTERS SETTERS

    public ListProperty<String> playersListProperty(){
        return playersList;
    }

    public ObservableList<String> getPlayersList(){
        return playersList.get();
    }

    public void setPlayersList(ObservableList<String> playersList){
        this.playersList.set(playersList);
    }

    public IntegerProperty playersNumberProperty(){
        return playersNumber;
    }

    public int getPlayersNumber(){
        return playersNumber.get();
    }

    public void setPlayersNumber(int playersNumber){
        this.playersNumber.set(playersNumber);
    }

    public IntegerProperty playersJoinedProperty(){
        return playersJoined;
    }

    public int getPlayersJoined(){
        return playersJoined.get();
    }

    public void setPlayersJoined(int playersJoined){
        this.playersJoined.set(playersJoined);
    }
}