package project.views.game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.fxmisc.richtext.StyleClassedTextArea;
import project.models.game.GameModel;
import project.views.View;

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
        // TODO TEMPORAIRE
        gameModel.setView(this);
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

    /**
     * Start function of the Game Gui, initiate the text area to display
     * and the input text area.
     * Handle the characters inputted by the player.
     * @param primaryStage the stage created when the Thread starts
     * @throws Exception not yet
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        this.stage=primaryStage;
        this.root=new BorderPane();

        MenuBar menuBar=initMenuBar();

        // font of the texts
        displayText.setStyle("-fx-font-size: 3em");
        inputText.setStyle("-fx-font-size: 3em");

        // wrap the texts so they go back to line at the end of the text area
        displayText.setWrapText(true);
        inputText.setWrapText(true);

        displayText.setEditable(false);

        // add a listener on the list of words to update the display text area
        wordsList.addListener((observable, oldValue, newValue) -> {
            if(newValue.size()!=0){
                displayText.replaceText(String.join(" ", wordsList));
            }
        });

        // call the model (should be the controller) to handle the input
        inputText.setOnKeyPressed(event -> {
            String eventString=event.getText();
            // only consider backspace, space and an alphanumeric character
            if(event.getCode()==KeyCode.BACK_SPACE || event.getCode()==KeyCode.SPACE || (eventString.length()>0 && Character.isAlphabetic(eventString.charAt(0)))){
                gameModel.handleInput(event);
            }
        });

        // set the textareas/menubar on the root pane
        this.root.setTop(menuBar);
        this.root.setCenter(displayText);
        this.root.setBottom(inputText);

        displayText.setBorder(new Border(new BorderStroke(Color.BLACK,BorderStrokeStyle.SOLID,CornerRadii.EMPTY,new BorderWidths(1))));
        inputText.setBorder(new Border(new BorderStroke(Color.BLACK,BorderStrokeStyle.SOLID,CornerRadii.EMPTY,new BorderWidths(1))));
        BorderPane.setMargin(displayText,new Insets(10));
        BorderPane.setMargin(inputText,new Insets(10));

        // first initilization of the words list and color the text in gray
        updateWords();
        colorNewText();

        Scene scene = new Scene(root, this.width, this.height);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        inputText.requestFocus();
        primaryStage.setTitle("Word Raceeee");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Remove the red color of the error and set to black the well written characters
     * and to grey the non-written characters
     * @param inputWordLength lenght of well written word
     * @param currentWordLength lenght of the entire word
     */
    public void uncolorError(int inputWordLength,int currentWordLength){
        Platform.runLater(() -> {
            displayText.setStyleClass(0, inputWordLength,"black");
            displayText.setStyleClass(inputWordLength, currentWordLength,"grey");
        });
    }

    /**
     * Color in red the whole word
     * @param currentWordLenght lenght of the current word
     */
    public void colorError(int currentWordLenght){
        Platform.runLater(() -> {
            displayText.setStyleClass(0,currentWordLenght,"red");
        });
    }

    /**
     * Cor in black the character well written to simulate a cursor
     * @param currentWordIndex index of the last well written character
     */
    public void colorCursor(int currentWordIndex){
        Platform.runLater(() -> {
            displayText.setStyleClass(currentWordIndex, currentWordIndex+1,"black");
        });
    }

    /**
     * Clear the text input
     */
    public void clearInputArea(){
        Platform.runLater(inputText::clear);
    }

    /**
     * Color the entire display text in grey
     */
    public void colorNewText(){
        Platform.runLater(() -> {
            int size=wordsList.stream().mapToInt(s -> s.length()+1).sum();
            displayText.setStyleClass(0,size-1,"grey");
        });
    }

    /**
     * Update the list of words from the model list of words
     */
    public void updateWords(){
        wordsList.clear();
        gameModel.getWords().forEachRemaining(wordsList::add);
        //System.out.println(wordsList);
    }

    /**
     * Update function, not useful yet
     */
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

         */
    }

    @Override
    public void setVisible(boolean visible){

    }


    // GETTERS SETTERS

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
