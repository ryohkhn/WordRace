package project.models.menu;

import project.models.Model;

public class MenuModel extends Model {
	private GameMode gameMode;
	private int playersNumber;
	private int lives;
	private int nbWord;
	String host, port;

	public MenuModel() {
		this.gameMode = GameMode.Solo;
		this.playersNumber = this.lives = this.nbWord = 0;
		this.host = this.port = "";
	}

	public GameMode getGameMode() {
		return gameMode;
	}

	public void setGameMode(GameMode gameMode) {
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

	public enum GameMode {Solo, Host, Join}
}
