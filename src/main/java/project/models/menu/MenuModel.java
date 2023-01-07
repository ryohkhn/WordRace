package project.models.menu;

import project.controllers.MenuController;
import project.models.Model;
import project.models.game.PlayerModel;

import java.io.Serializable;

public class MenuModel extends Model implements Serializable {
	/**
	 * The host and port string from the menu input
	 *
	 * @see #setPort(String)
	 * @see #setHost(String)
	 * @see #getHost()
	 * @see #getPort()
	 */
	String host, port;
	/**
	 * The chosen GameMode
	 *
	 * @see #getGameMode()
	 * @see #setGameMode(GameMode)
	 */
	private GameMode gameMode;
	/**
	 * The number of player in multiplayer
	 *
	 * @see #getPlayersNumber()
	 * @see #setPlayersNumber(int)
	 */
	private int playersNumber;
	/**
	 * The initial lives value
	 *
	 * @see #getLives()
	 * @see #setLives(int)
	 */
	private int lives;
	/**
	 * The number of words chosen
	 *
	 * @see #getNbWord()
	 * @see #setNbWord(int)
	 */
	private int nbWord;
	/**
	 * The player's name
	 *
	 * @see #getPlayerName()
	 */
	private String playerName;

	public MenuModel() {
		gameMode = GameMode.Normal;
		playersNumber = lives = nbWord = 0;
		host = port = playerName = "";
	}

	public GameMode getGameMode() {
		return gameMode;
	}

	public void setGameMode(GameMode gameMode) {
		MenuController.getInstance().stopServer();
		this.gameMode = gameMode;
		notifyViewers();
	}

	public int getPlayersNumber() {
		return playersNumber;
	}

	public void setPlayersNumber(int playersNumber) {
		this.playersNumber = playersNumber;
		notifyViewers();
	}

	public int getLives() {
		return lives;
	}

	public void setLives(int lives) {
		this.lives = lives;
		notifyViewers();
	}

	public int getNbWord() {
		return nbWord;
	}

	public void setNbWord(int nbWord) {
		this.nbWord = nbWord;
		notifyViewers();
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
		notifyViewers();
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
		notifyViewers();
	}

	public PlayerModel getPlayer() {
		return switch(gameMode) {
			case Normal -> PlayerModel.withoutLivesAndLevel(playerName);
			case Competitive, Host, Join ->
					PlayerModel.withLivesAndLevel(playerName, lives);
		};
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) throws Exception {
		this.playerName = playerName;
		checkPlayerName();
		notifyViewers();
	}

	public void checkPlayerName() throws Exception {
		if(playerName.isEmpty())
			throw new Exception("You must enter a name");
		if(playerName.length() < 3)
			throw new Exception("The name must be at least 3 characters long");
		if(playerName.length() > 20)
			throw new Exception("The name must be at most 20 characters long");
	}

	public enum GameMode implements Serializable {Normal, Competitive, Host, Join}
}
