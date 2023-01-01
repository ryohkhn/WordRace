package project.models.menu;

import project.controllers.menu.MenuController;
import project.models.Model;
import project.models.game.PlayerModel;

import java.io.Serializable;

public class MenuModel extends Model implements Serializable {
	String host, port;
	private GameMode gameMode;
	private int playersNumber;
	private int lives;
	private int nbWord;

	public MenuModel() {
		this.gameMode = GameMode.Normal;
		this.playersNumber = this.lives = this.nbWord = 0;
		this.host = this.port = "";
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
			case Normal -> PlayerModel.withoutLivesAndLevel();
			case Competitive, Host, Join ->
					PlayerModel.withLivesAndLevel(lives);
		};
	}

	public enum GameMode {Normal, Competitive, Host, Join}
}
