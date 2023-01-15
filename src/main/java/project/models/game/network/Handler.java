package project.models.game.network;

import javafx.application.Platform;
import project.controllers.GameController;
import project.controllers.MenuController;
import project.controllers.NetworkController;
import project.models.game.PlayerModel;
import project.models.menu.MenuModel;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

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
		return request -> CompletableFuture.completedFuture(null);
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

			private synchronized void updateCachedPlayersListResponse() {
				server.sendAll(Request.playerModel());
				var players = server.receiveAll(Type.PlayerModel)
									.map(r -> ((Response.PlayerModelResponse) r).getPlayer())
									.toList();
				cached = Response.playersList(players);
			}

			@Override
			public CompletableFuture<Response> handle(Request request) {
				return CompletableFuture.supplyAsync(() -> {
					if(request.getType() != Type.PlayersList)
						throw new IllegalArgumentException(
								"Request must be of type PlayersList");
					long current = System.currentTimeMillis() - 100;
					if(cached == null || cached.getCreated() < current)
						updateCachedPlayersListResponse();
					return cached;
				});
			}
		};
	}

	/**
	 * Handle the word request from a client and return a word who is sent
	 * to all the other clients
	 *
	 * @return A handler to handle the word request
	 */
	static Handler wordRequest() {
		return request -> {
			if(request instanceof Request.WordRequest req)
				return CompletableFuture.completedFuture(
						Response.word(req.getWord())
				);
			else
				throw new IllegalArgumentException(
						"Request must be of type Word"
				);
		};
	}

	/**
	 * Handle the player model request from a client and return the player
	 *
	 * @return A handler to handle the player model request
	 */
	static Handler playerModelRequest() {
		return new Handler() {
			private Response computePlayerModel() {
				try {
					MenuModel config = NetworkController.getInstance()
														.getModel()
														.getConfiguration();
					String name = MenuController.getInstance()
												.getModel()
												.getPlayerName();
					return Response.playerModel(
							PlayerModel.withLivesAndLevel(
									name,
									config.getLives()
							)
					);
				} catch(IOException | InterruptedException e) {
					throw new RuntimeException(e);
				}
			}

			@Override public CompletableFuture<Response> handle(Request request)
			throws IllegalArgumentException {
				if(request.getType() != Type.PlayerModel)
					throw new IllegalArgumentException(
							"Request must be of type PlayerModel");
				if(GameController.getInstance().isRunning())
					return CompletableFuture.completedFuture(
							Response.playerModel(
									GameController.getInstance()
												  .getPlayer()
												  .clone()
							)
					);
				return CompletableFuture.supplyAsync(this::computePlayerModel);
			}
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
			return CompletableFuture.completedFuture(
					Response.configuration(
							MenuController.getInstance()
										  .getModel()
										  .clone()
					)
			);
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
	 * @return A completable future of the response
	 * @throws IllegalArgumentException if the request is not handled
	 */
	CompletableFuture<Response> handle(Request request)
	throws IllegalArgumentException;
}
