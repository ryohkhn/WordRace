package project.views.game;

import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.fxmisc.richtext.StyleClassedTextArea;
import project.controllers.menu.MenuController;
import project.models.game.GameModel;
import project.models.game.WordList;
import project.views.View;

import java.awt.*;
import java.util.Iterator;
import java.util.stream.Collectors;

public class GameView extends Application implements View{
    private final GameModel gameModel;

    private Stage stage;
    private BorderPane root;

    private final StyleClassedTextArea inputText;
    private final StyleClassedTextArea displayText;
    private final ListProperty<String> wordsList;
    private final SimpleObjectProperty<Character> currentLetter;
    private final SimpleStringProperty textOfInput;
    private final SimpleStringProperty currentInput;
    private final SimpleStringProperty currentWord;
    private final SimpleIntegerProperty currentLevel;
    private final SimpleIntegerProperty currentScore;
    private final SimpleIntegerProperty lives;

    private final double width=900;
    private final double height=600;

    public GameView(GameModel gameModel){
        this.gameModel=gameModel;
        this.inputText=new StyleClassedTextArea();
        this.displayText=new StyleClassedTextArea();
        this.wordsList=new SimpleListProperty<>(FXCollections.observableArrayList());
        this.currentLetter=new SimpleObjectProperty<>();
        this.textOfInput=new SimpleStringProperty();
        this.currentInput=new SimpleStringProperty();
        this.currentWord=new SimpleStringProperty();
        this.currentLevel=new SimpleIntegerProperty();
        this.currentScore=new SimpleIntegerProperty();
        this.lives=new SimpleIntegerProperty();

        try{
            start(new Stage());
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.stage=primaryStage;
        this.root=new BorderPane();

        /*
        StyleClassedTextArea textArea = new StyleClassedTextArea();
        textArea.replaceText("Lorem Ipsum");
        //textArea.setStyleClass( 5, 9, "red");
        root.getChildren().add(textArea);
         */

        //scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        /*
        MenuController<KeyEvent> menuController=new MenuController<>();

        root.setOnKeyPressed(menuController::hello);

         */

        //displayText.insertText(0,"ocoucoucuc");
        /*
        displayText.setPrefHeight(this.height/2);
        displayText.setPrefWidth(this.width);
        inputText.setPrefHeight(this.height/2);
        inputText.setPrefWidth(this.width);

         */

        //textOfInput.bind(inputText.textProperty());

        //displayText.setBackground(new Background(new BackgroundFill(Color.GRAY,CornerRadii.EMPTY, Insets.EMPTY)));
        displayText.setEditable(false);

        updateWords();
        displayText.append(String.join(" ", wordsList), "");

        wordsList.addListener((observable, oldValue, newValue) -> {
            displayText.clear();
            displayText.append(String.join(" ", wordsList), "");
        });

        root.setCenter(displayText);
        root.setBottom(inputText);
        displayText.setBorder(new Border(new BorderStroke(Color.BLACK,BorderStrokeStyle.SOLID,CornerRadii.EMPTY,new BorderWidths(1))));
        inputText.setBorder(new Border(new BorderStroke(Color.BLACK,BorderStrokeStyle.SOLID,CornerRadii.EMPTY,new BorderWidths(1))));

        BorderPane.setMargin(displayText,new Insets(10));
        BorderPane.setMargin(inputText,new Insets(10));

        inputText.requestFocus();
        Scene scene = new Scene(root, this.width, this.height);
        primaryStage.setTitle("Word Raceeee");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void updateWords(){
        wordsList.clear();
        gameModel.getWords().forEachRemaining(wordsList::add);
    }

    @Override
    public void update(){
        /*
        if(lives.get()!=gameModel.getLives()){
            setLives(gameModel.getLives());
        }
        if(currentLetter.get()!=gameModel.getCurrentLetter()){
            setCurrentLetter(gameModel.getCurrentLetter());
        }
        if(currentLetter.get()!=gameModel.getCurrentLetter()){
            setCurrentLetter(gameModel.getCurrentLetter());
        }
        if(!(currentWord.get().equals(gameModel.getCurrentWord()))){
            setCurrentWord(gameModel.getCurrentWord());
        }
        if(currentLevel.get()!=gameModel.getLevel()){
            setCurrentLevel(gameModel.getLevel());
        }
        if(currentScore.get()!=gameModel.getScore()){
            setCurrentScore(gameModel.getScore());
        }
        updateWords();
        */
    }

    @Override
    public void setVisible(boolean visible){

    }
    // TODO Use AnchorPane


    public void setCurrentInput(String currentInput){
        this.currentInput.set(currentInput);
    }

    public void setLives(int lives){
        this.lives.set(lives);
    }

    public void setCurrentLetter(Character currentLetter){
        this.currentLetter.set(currentLetter);
    }

    public void setCurrentLevel(int currentLevel){
        this.currentLevel.set(currentLevel);
    }

    public void setCurrentScore(int currentScore){
        this.currentScore.set(currentScore);
    }

    public void setCurrentWord(String currentWord){
        this.currentWord.set(currentWord);
    }
}
