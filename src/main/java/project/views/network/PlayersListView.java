package project.views.network;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import project.models.game.PlayerModel;

import java.util.List;

public final class PlayersListView extends TableView<PlayerView> {
	private final TableColumn<PlayerView, Integer> lives;
	private final TableColumn<PlayerView, Integer> score;
	private final TableColumn<PlayerView, Integer> level;
	private final TableColumn<PlayerView, Integer> nbCorrectWords;
	private final ObservableList<PlayerView> models;

	public PlayersListView() {
		setItems(models = FXCollections.observableArrayList());
		setEditable(false);
		setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		setPlaceholder(new Label("No players"));
		setMaxHeight(200);

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
		if(models == null)
			this.models.clear();
		else
			this.models.setAll(models.stream()
									 .map(PlayerView::new)
									 .toList());
	}
}
