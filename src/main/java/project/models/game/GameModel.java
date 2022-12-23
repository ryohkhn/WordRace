package project.models.game;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import project.models.Model;
import project.views.game.GameView;

import java.util.Iterator;

public class GameModel extends Model {
	private final WordList words;
	private final Stats stats;
	private int lives, score, level, deleteCount;
	private String inputWord;
	private GameView gameView; // TODO TEMPORAIRE LE TEMPS DE DÉPLACER HANDLE DANS LE CONTROLLER

	public GameModel(int lives, int level, int nbWords) {
		this.lives = lives;
		this.score = 0;
		this.level = level;
		this.inputWord="";
		this.stats = new Stats();
		this.words = new WordList(nbWords);
	}

	/**
	 * Get the number of lives the player has left
	 *
	 * @return the number of lives
	 */
	public int getLives() {
		return lives;
	}

	/**
	 * Get the current score of the player
	 *
	 * @return the score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Get the current level of the player
	 *
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Get an iterator over the words
	 *
	 * @return the iterator
	 */
	public Iterator<String> getWords() {
		return words.iterator();
	}

	/**
	 * Get the current word
	 *
	 * @return the current word
	 */
	public String getCurrentWord() {
		return words.getCurrentWord();
	}

	/**
	 * Get the current letter of the current word
	 *
	 * @return the current letter
	 */
	public char getCurrentLetter() {
		return words.getCurrentLetter();
	}

	/**
	 * Get the elapsed time from the start of the game in milliseconds
	 *
	 * @return the elapsed time in milliseconds
	 */
	public int getElapsedTime() {
		return stats.getElapsedTime();
	}

	/**
	 * Get the elapsed time like from the start of the game in minutes
	 *
	 * @return the elapsed time in minutes
	 * @see #getElapsedTime()
	 */
	public int getElapsedTimeInMinutes() {
		return stats.getElapsedTimeInMinutes();
	}

	/**
	 * Get the ratio between the useful characters typed and the elapsed time
	 * in minutes divided by 5
	 *
	 * @return the ratio
	 */
	public int getMPM() {
		return stats.getMPM();
	}

	/**
	 * Get the percentage of useful characters typed compared to the total
	 *
	 * @return the percentage of useful characters
	 */
	public double getAccuracy() {
		return stats.getAccuracy();
	}

	/**
	 * Get the number of pressed keys
	 *
	 * @return the number of pressed keys
	 */
	public int getNumberOfPressedKeys() {
		return stats.getNumberOfPressedKeys();
	}

	/**
	 * Get the number of useful characters typed
	 *
	 * @return the number of useful characters
	 */
	public int getUsefulCharacters() {
		return stats.getUsefulCharacters();
	}

	/**
	 * Handle the keyEvent input of the player.
	 * If Space, goes to the next words and updates lives and levels.
	 * If Backspace, delete the last input character and goes to the previous letter if necessary.
	 * If an alphabetic character, goes to next letter if the character is correct.
	 * @param keyEvent the keyEvent of the input
	 */
	public void handleInput(KeyEvent keyEvent) {
		stats.incrementNumberOfPressedKeys();
		// si suppression de caractère et qu'on ne se trouve pas sur la première lettre
		if(keyEvent.getCode() == KeyCode.BACK_SPACE && words.getNumberOfValidLetters()>0){
			inputWord=inputWord.substring(0, inputWord.length()-1);
			// si l'input est égal au nombre de caractère bien écrits, on retire la couleur d'erreur
			if(inputWord.length()==words.getNumberOfValidLetters()){
				gameView.uncolorError(inputWord.length(),words.getCurrentWord().length());
			}
			/* si la taille de l'entrée est plus petite que le nombre
			de caractères bien écrits, on va à la lettre précédente */
			else if(inputWord.length()<words.getNumberOfValidLetters()){
				words.previousLetter();
				gameView.uncolorError(inputWord.length(),words.getCurrentWord().length());
			}
			deleteCount++;
			notifyViewers();
		}
		else if(keyEvent.getCode()==KeyCode.SPACE) {
			if(deleteCount==0){
				lives++;
			}
			else{
				lives-=deleteCount;
			}
			deleteCount=0;
			inputWord="";
			level++;

			// on pop le premier mot et on en ajoute un en fin de liste, à adapter
			words.push();
			words.pop();
			words.resetCurrentLetter();

			// updates de la Vue
			gameView.clearInputArea();
			gameView.colorNewText();
			gameView.updateWords();
			notifyViewers();
		}
		else if(Character.isAlphabetic(keyEvent.getText().charAt(0))){
			char c = keyEvent.getText().charAt(0);
			inputWord += c;
			/* si le caractère est valide et la longueur de l'entrée est la même
			que le nombre de lettres bien écrites on passe à la lettre suivante */
			if(c==words.getCurrentLetter() && (inputWord.length()==(words.getNumberOfValidLetters()+1) || words.getNumberOfValidLetters()==0)){
				stats.incrementUsefulCharacters();
				gameView.colorCursor(words.getNumberOfValidLetters());
				words.nextLetter();
			}
			else{
				gameView.colorError(words.getCurrentWord().length());
			}
			notifyViewers();
		}
	}

	// TODO TEMPORAIRE
	public void setView(GameView gameView){
		this.gameView=gameView;
		addViewer(gameView);
	}
}
