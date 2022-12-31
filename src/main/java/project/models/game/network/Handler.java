package project.models.game.network;

import project.controllers.game.GameController;
import project.controllers.menu.MenuController;
import project.models.game.PlayerModel;

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
				long current = System.currentTimeMillis() - 10000; // 10 seconds
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
				m = MenuController.getInstance().getModel().getPlayer();
			}
			return Response.playerModel(m);
		};
	}

	Response handle(Request request);
}
