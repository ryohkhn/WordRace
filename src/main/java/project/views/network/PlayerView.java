package project.views.network;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import project.models.game.PlayerModel;
import project.views.View;

import java.util.Objects;

public final class PlayerView implements View {
	private final ObjectProperty<Integer> lives;
	private final ObjectProperty<Integer> score;
	private final ObjectProperty<Integer> level;
	private final ObjectProperty<Integer> nbCorrectWords;
	private PlayerModel model;

	public PlayerView(PlayerModel model) {
		this.model = model;
		this.model.addViewer(this);
		this.lives = new SimpleIntegerProperty(model.getLives()).asObject();
		this.score = new SimpleIntegerProperty(model.getScore()).asObject();
		this.level = new SimpleIntegerProperty(model.getLevel()).asObject();
		this.nbCorrectWords = new SimpleIntegerProperty(model.getNbCorrectWords()).asObject();
	}

	public PlayerModel model() {
		return model;
	}

	public void setModel(PlayerModel model) {
		if(Objects.equals(this.model, model)) return;
		this.model = model;
		this.model.addViewer(this);
		update();
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

	@Override public void update() {
		lives.set(model.getLives());
		score.set(model.getScore());
		level.set(model.getLevel());
		nbCorrectWords.set(model.getNbCorrectWords());
	}

	@Override public void setVisible(boolean visible) {}

	@Override public boolean equals(Object obj) {
		if(obj == this) return true;
		if(obj == null || obj.getClass() != getClass()) return false;
		PlayerView other = (PlayerView) obj;
		return model.equals(other.model);
	}
}
