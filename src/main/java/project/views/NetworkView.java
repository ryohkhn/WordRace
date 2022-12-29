package project.views;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import project.models.game.PlayerModel;
import project.models.game.network.NetworkModel;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

public final class NetworkView extends TableView<NetworkView.PlayerView> implements View {
	private final NetworkModel model;
	private final TableColumn<PlayerView, Integer> lives;
	private final TableColumn<PlayerView, Integer> score;
	private final TableColumn<PlayerView, Integer> level;
	private final TableColumn<PlayerView, Integer> nbCorrectWords;

	public NetworkView(NetworkModel model) {
		this.model = model;
		this.model.addViewer(this);

		lives = new TableColumn<>("Lives");
		score = new TableColumn<>("Score");
		level = new TableColumn<>("Level");
		nbCorrectWords = new TableColumn<>("Correct words");

		lives.setCellValueFactory(c -> c.getValue().getLives());
		score.setCellValueFactory(c -> c.getValue().getScore());
		level.setCellValueFactory(c -> c.getValue().getLevel());
		nbCorrectWords.setCellValueFactory(c -> c.getValue()
												 .getNbCorrectWords());

		getColumns().add(lives);
		getColumns().add(score);
		getColumns().add(level);
		getColumns().add(nbCorrectWords);
	}

	@Override public void update() {
		try {
			var players = model.getPlayersList()
							   .stream()
							   .map(PlayerView::new)
							   .collect(Collectors.toCollection(
									   FXCollections::observableArrayList
							   ));
			setItems(players);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static final class PlayerView implements View {
		private final ObjectProperty<Integer> lives;
		private final ObjectProperty<Integer> score;
		private final ObjectProperty<Integer> level;
		private final ObjectProperty<Integer> nbCorrectWords;
		private PlayerModel model;

		private PlayerView(PlayerModel model) {
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

		@Override
		public void update() {
			lives.set(model.getLives());
			score.set(model.getScore());
			level.set(model.getLevel());
			nbCorrectWords.set(model.getNbCorrectWords());
		}

		@Override public void setVisible(boolean visible) {
			throw new UnsupportedOperationException();
		}

		@Override public boolean equals(Object obj) {
			if(obj == this)
				return true;
			if(obj == null || obj.getClass() != getClass())
				return false;
			PlayerView other = (PlayerView) obj;
			return model.equals(other.model);
		}
	}
}
