package project.views.game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.fxmisc.richtext.StyleClassedTextArea;
import project.controllers.game.GameController;
import project.models.game.GameModel;
import project.models.game.Word;
import project.views.View;

import java.util.stream.Collectors;

public class GameView extends Application implements View {
	private final GameModel gameModel;
	private final StyleClassedTextArea inputText;
	private final StyleClassedTextArea displayText;
	private final ListProperty<Word> wordsList;
	private final SimpleObjectProperty<Character> currentLetter;
	private final SimpleStringProperty textOfInput;
	private final SimpleStringProperty currentInput;
	private final SimpleStringProperty currentWord;
	private final SimpleIntegerProperty currentLevel;
	private final SimpleIntegerProperty currentScore;
	private final SimpleIntegerProperty lives;
	private final double width = 900;
	private final double height = 600;
	private Stage stage;
	private BorderPane root;

	public GameView(GameModel gameModel) {
		this.gameModel = gameModel;
		this.inputText = new StyleClassedTextArea();
		this.displayText = new StyleClassedTextArea();
		this.wordsList = new SimpleListProperty<>(FXCollections.observableArrayList());
		this.currentLetter = new SimpleObjectProperty<>();
		this.textOfInput = new SimpleStringProperty();
		this.currentInput = new SimpleStringProperty();
		this.currentWord = new SimpleStringProperty();
		this.currentLevel = new SimpleIntegerProperty();
		this.currentScore = new SimpleIntegerProperty();
		this.lives = new SimpleIntegerProperty();
	}

	/**
	 * Start function of the Game Gui, initiate the text area to display
	 * and the input text area.
	 * Handle the characters inputted by the player.
	 *
	 * @param primaryStage the stage created when the Thread starts
	 * @throws Exception not yet
	 */
	@Override public void start(Stage primaryStage) throws Exception {
		this.stage = primaryStage;
		this.root = new BorderPane();

		MenuBar menuBar = initMenuBar();

		// font of the texts
		displayText.setStyle("-fx-font-size: 3em");
		inputText.setStyle("-fx-font-size: 3em");

		// wrap the texts so they go back to line at the end of the text area
		displayText.setWrapText(true);
		inputText.setWrapText(true);

		displayText.setEditable(false);

		// add a listener on the list of words to update the display text area
		wordsList.addListener((observable, oldValue, newValue) -> {
			if(newValue.size() != 0) {
				displayText.replaceText(wordsList.stream().map(Word::toString).collect(Collectors.joining(" ")));
			}
		});

		// call the model (should be the controller) to handle the input
		inputText.setOnKeyPressed(event -> {
			GameController.getInstance().handle(event);
		});

		// set the textareas/menubar on the root pane
		this.root.setTop(menuBar);
		this.root.setCenter(displayText);
		this.root.setBottom(inputText);

		displayText.setBorder(new Border(new BorderStroke(
				Color.BLACK,
				BorderStrokeStyle.SOLID,
				CornerRadii.EMPTY,
				new BorderWidths(1)
		)));
		inputText.setBorder(new Border(new BorderStroke(
				Color.BLACK,
				BorderStrokeStyle.SOLID,
				CornerRadii.EMPTY,
				new BorderWidths(1)
		)));
		BorderPane.setMargin(displayText, new Insets(10));
		BorderPane.setMargin(inputText, new Insets(10));

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
	 * Color the entire display text in grey
	 */
	private void colorNewText() {
		Platform.runLater(() -> {
			int size = wordsList.stream().mapToInt(s -> s.length() + 1).sum();
			displayText.setStyleClass(0, size - 1, "grey");
			colorBonusMalus();
		});
	}

	/**
	 * Colors bonus and malus words
	 */
	private void colorBonusMalus(){
		int lenght=0,count=0;
		for(Word word:wordsList){
			if(count!=0){
				if(word.isBonus()){
					displayText.setStyleClass(lenght,lenght+word.length(),"blue");
				}
				if(word.isMalus()){
					displayText.setStyleClass(lenght,lenght+word.length(),"red");
				}
			}
			count++;
			lenght+=word.length()+1;
		}
	}

	/**
	 * Update the list of words from the model list of words
	 */
	public void updateWords() {
		//System.out.println("Thread: " + Thread.currentThread().getName());
		wordsList.clear();
		gameModel.getWordsIterator().forEachRemaining(wordsList::add);
		colorNewText();
	}

	public void resetInputText(){
		Platform.runLater(inputText::clear);
	}

	private void updateRunnable() {
		String inputWord = gameModel.getInputWord();
		String currentWord = gameModel.getWords().getCurrentWord().content();

		if(inputWord.isEmpty()) {
			displayText.setStyleClass(0, currentWord.length(), "grey");
			return;
		}

		int length = Math.min(inputWord.length(), currentWord.length());
		displayText.setStyleClass(
				0,
				length,
				currentWord.startsWith(inputWord) ? "green" : "red"
		);
		if(length < currentWord.length())
			displayText.setStyleClass(length, currentWord.length(), "grey");
	}

	/**
	 * Update the GameView
	 */
	@Override public void update() {
		Platform.runLater(this::updateRunnable);
	}

	@Override public void setVisible(boolean visible) {

	}
}
