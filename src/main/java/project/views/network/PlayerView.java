package project.views.network;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import project.models.game.PlayerModel;

public final class PlayerView {
	private final ObjectProperty<String> name;
	private final ObjectProperty<Integer> lives;
	private final ObjectProperty<Integer> score;
	private final ObjectProperty<Integer> level;
	private final ObjectProperty<Integer> nbCorrectWords;

	public PlayerView(PlayerModel model) {
		this.name = new SimpleObjectProperty<>(model.getName());
		this.lives = new SimpleObjectProperty<>(model.getLives());
		this.score = new SimpleObjectProperty<>(model.getScore());
		this.level = new SimpleObjectProperty<>(model.getLevel());
		this.nbCorrectWords = new SimpleIntegerProperty(model.getNbCorrectWords()).asObject();
	}

	public ObjectProperty<String> getName() {
		return name;
	}

	public ObjectProperty<Integer> getLives() {
		return lives;
	}

	public ObjectProperty<Integer> getScore() {
		return score;
	}

	public ObjectProperty<Integer> getLevel() {
		return level;
	}

	public ObjectProperty<Integer> getNbCorrectWords() {
		return nbCorrectWords;
	}
}
