package project.models.game.network;

import javafx.application.Platform;
import project.controllers.GameController;
import project.controllers.MenuController;
import project.controllers.NetworkController;
import project.models.game.PlayerModel;

import java.io.IOException;

/**
 * A handler compute a response from a request
 */
@FunctionalInterface
public interface Handler {

	/**
	 * The empty handler always returns null
	 *
	 * @return null
	 */
	static Handler empty() {
		return request -> null;
	}

	/**
	 * Handle the players list request from a client and
	 * return the list of connected players
	 *
	 * @param server the server
	 * @return A handler to handle the players list request
	 */
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

	/**
	 * Handle the word request from a client and return a word who is send
	 * to all the other clients
	 *
	 * @return A handler to handle the word request
	 */
	static Handler wordRequest() {
		return request -> {
			if(request.getType() != Type.Word)
				throw new IllegalArgumentException(
						"Request must be of type Word");
			var word = ((Request.WordRequest) request).getWord();
			return Response.word(word);
		};
	}

	/**
	 * Handle the player model request from a client and return the player
	 *
	 * @return A handler to handle the player model request
	 */
	static Handler playerModelRequest() {
		return request -> {
			if(request.getType() != Type.PlayerModel)
				throw new IllegalArgumentException(
						"Request must be of type PlayerModel");
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

	/**
	 * Handle the configuration request from a client and return the configuration
	 *
	 * @return A handler to handle the configuration request
	 */
	static Handler configurationRequest() {
		return request -> {
			if(request.getType() != Type.Configuration)
				throw new IllegalArgumentException(
						"Request must be of type Configuration");
			return Response.configuration(MenuController.getInstance()
														.getModel());
		};
	}

	/**
	 * Handle the game start request from a client and start the game
	 *
	 * @return A handler to handle the game start request
	 */
	static Handler gameStartRequest() {
		return request -> {
			if(request.getType() != Type.GameStart)
				throw new IllegalArgumentException(
						"Request must be of type GameStart");
			if(!GameController.getInstance().isRunning()) {
				Platform.runLater(() -> {
					try {
						MenuController.getInstance().startGame();
					} catch(Exception e) {
						throw new RuntimeException(e);
					}
				});
			}
			return null;
		};
	}

	/**
	 * Compute a response from a request, if the request is not handled
	 * by this handler, an IllegalArgumentException is thrown
	 *
	 * @param request the request
	 * @return the response
	 * @throws IllegalArgumentException if the request is not handled
	 */
	Response handle(Request request) throws IllegalArgumentException;
}
