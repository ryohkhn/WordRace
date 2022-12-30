package project.views.network;

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
	private NetworkModel model;

	public NetworkView() {
		playersList = new PlayersListView();
		title = new Label("");
		title.setFont(new Font(20));

		setTop(title);
		setCenter(playersList);

		setAlignment(title, Pos.CENTER);
		setAlignment(playersList, Pos.CENTER);
	}

	public void setModel(NetworkModel model) {
		this.model = model;
		update();
	}

	@Override public void update() {
		System.out.println("NetworkView.update()");
		switch(MenuController.getInstance().getModel().getGameMode()) {
			case Normal, Competitive -> setVisible(false);
			case Host -> {
				setVisible(true);
				if(NetworkController.getInstance().isRunning())
					title.setText("Server running");
				else title.setText("Server not running");
			}
			case Join -> {
				setVisible(true);
				if(NetworkController.getInstance().isRunning()) title.setText(
						"Connected to server");
				else title.setText("Not connected to server");
			}
		}

		if(NetworkController.getInstance().isRunning()) {
			try {
				playersList.setModels(model.getPlayersList());
				playersList.update();
			} catch(IOException ignored) {}
		}
	}
}