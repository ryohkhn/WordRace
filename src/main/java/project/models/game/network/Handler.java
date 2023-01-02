package project.models.game.network;

import javafx.application.Platform;
import project.controllers.game.GameController;
import project.controllers.game.NetworkController;
import project.controllers.menu.MenuController;
import project.models.game.PlayerModel;

import java.io.IOException;

@FunctionalInterface
public interface Handler {

	static Handler empty() {
		return request -> null;
	}

	static Handler playersListRequest(Server server) {
		return new Handler() {
			private Response cached = null;

			private void updateCachedPlayersListResponse() {
				server.sendAll(Request.playerModel());
				var players = server.receiveAll(Type.PlayerModel)
									.map(r -> ((Response.PlayerModelResponse) r).getPlayer())
									.toList();
				cached = Response.playersList(players);
			}

			@Override public Response handle(Request request) {
				if(request.getType() != Type.PlayersList)
					throw new IllegalArgumentException(
							"Request must be of type PlayersList");
				long current = System.currentTimeMillis() - 500;
				if(cached == null || cached.getCreated() < current)
					updateCachedPlayersListResponse();
				return cached;
			}
		};
	}

	static Handler wordRequest() {
		return request -> {
			var word = ((Request.WordRequest) request).getWord();
			return Response.word(word);
		};
	}

	static Handler playerModelRequest() {
		return request -> {
			PlayerModel m;
			if(GameController.getInstance().isRunning())
				m = GameController.getInstance().getModel().getPlayer();
			else {
				try {
					m = NetworkController.getInstance()
										 .getModel()
										 .getConfiguration()
										 .getPlayer();
				} catch(IOException | InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
			return Response.playerModel(m);
		};
	}

	static Handler configurationRequest() {
		return request -> Response.configuration(MenuController.getInstance()
															   .getModel());
	}

	static Handler gameStartRequest() {
		return request -> {
			if(!GameController.getInstance().isRunning()) {
				Platform.runLater(() -> {
					try {
						MenuController.getInstance().startGame();
					} catch(Exception e) {
						throw new RuntimeException(e);
					}
				});
			} return null;
		};
	}

	Response handle(Request request);
}
