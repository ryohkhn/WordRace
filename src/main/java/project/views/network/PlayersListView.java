package project.views.network;

import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import project.models.game.PlayerModel;
import project.views.View;

import java.util.List;
import java.util.stream.Collectors;

public final class PlayersListView extends TableView<PlayerView> implements View {
	private final TableColumn<PlayerView, Integer> lives;
	private final TableColumn<PlayerView, Integer> score;
	private final TableColumn<PlayerView, Integer> level;
	private final TableColumn<PlayerView, Integer> nbCorrectWords;
	private List<PlayerModel> models;

	public PlayersListView() {
		setEditable(false);
		setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		setPlaceholder(new Label("No players"));
		setItems(FXCollections.observableArrayList());

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

	public void setModels(List<PlayerModel> models) {
		this.models = models;
	}

	@Override public void update() {
		setItems(models.stream()
					   .map(PlayerView::new)
					   .collect(Collectors.toCollection(FXCollections::observableArrayList)));
	}
}
