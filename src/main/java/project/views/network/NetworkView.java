package project.views.network;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import project.controllers.game.NetworkController;
import project.controllers.menu.MenuController;
import project.models.game.network.NetworkModel;
import project.views.View;

import java.io.IOException;

public final class NetworkView extends BorderPane implements View {
	private final PlayersListView playersList;
	private final Label title;
	private final Thread timer;
	private NetworkModel model;

	public NetworkView() {
		playersList = new PlayersListView();
		title = new Label("");
		title.setFont(new Font(20));
		timer = new Thread(() -> {
			while(!Thread.interrupted()) {
				try {
					Thread.sleep(10000); // 10 seconds
				} catch(InterruptedException e) {break;}
				Platform.runLater(this::update);
			}
		});
		timer.start();

		setTop(title);
		setCenter(playersList);

		setAlignment(title, Pos.CENTER);
		setAlignment(playersList, Pos.CENTER);
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
		update();
	}

	@Override public void update() {
		switch(MenuController.getInstance().getModel().getGameMode()) {
			case Normal, Competitive -> setVisible(false);
			case Host -> {
				setVisible(true);
				if(model != null)
					title.setText("Server running");
				else title.setText("Server not running");
			}
			case Join -> {
				setVisible(true);
				if(model != null)
					title.setText("Connected to server");
				else title.setText("Not connected to server");
			}
		}

		if(model != null) {
			try {
				playersList.setModels(model.getPlayersList());
			} catch(IOException ignored) {}
		}
	}
}
