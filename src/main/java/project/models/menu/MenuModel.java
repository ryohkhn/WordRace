package project.models.menu;

public class MenuModel {
	private GameMode gameMode;
	private int playersNumber;
	private int lives;
	private int wordShown;

	public MenuModel() {
		this.gameMode = GameMode.Solo;
	}

	public GameMode getGameMode() {
		return gameMode;
	}

	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
	}

	public enum GameMode {Solo, Host, Join}
}
