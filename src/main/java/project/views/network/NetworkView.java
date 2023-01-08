package project.views.network;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.util.Duration;
import project.controllers.NetworkController;
import project.controllers.MenuController;
import project.models.game.network.NetworkModel;
import project.views.View;

import java.io.IOException;

public final class NetworkView extends BorderPane implements View {
	private final PlayersListView playersList;
	private final Label title;
	private final Timeline timer;
	private NetworkModel model;

	public NetworkView() {
		playersList = new PlayersListView();
		title = new Label("");
		title.setFont(new Font(20));
		timer = new Timeline(
				new KeyFrame(
						Duration.millis(500),
						e -> {
							update();
							updatePlayersList();
						}
				)
		);
		timer.setCycleCount(Timeline.INDEFINITE);
		timer.play();

		setTop(title);
		setCenter(playersList);

		setAlignment(title, Pos.CENTER);
		setAlignment(playersList, Pos.CENTER);

		setMaxHeight(playersList.getMaxHeight() + title.getMaxHeight() + 10);
	}

	public void setModel(NetworkModel model) {
		if(model != null) {
			model.addViewer(this);
			if(this.model != null) {
				this.model.removeViewer(this);
				NetworkController.getInstance()
								 .getModel()
								 .removeViewer(this);
			}
			NetworkController.getInstance()
							 .getModel()
							 .addViewer(this);
		}
		this.model = model;
		timer.play();
		update();
		updatePlayersList();
	}

	@Override public void update() {
		switch(MenuController.getInstance().getModel().getGameMode()) {
			case Normal, Competitive -> setVisible(false);
			case Host -> {
				setVisible(true);
				if(model != null)
					title.setText("Server running");
				else {
					title.setText("Server not running");
					playersList.setModels(null);
				}
			}
			case Join -> {
				setVisible(true);
				if(model != null)
					title.setText("Connected to server");
				else {
					title.setText("Not connected to server");
					playersList.setModels(null);
				}
			}
		}
	}

	private void updatePlayersList() {
		if(model != null) {
			try {
				playersList.setModels(model.getPlayersList());
			} catch(IOException ignored) {}
		} else timer.stop();
	}
}
