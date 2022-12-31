package project.models.game.network;

import project.models.game.PlayerModel;
import project.models.game.Word;
import project.models.menu.MenuModel;

import java.io.Serializable;
import java.util.List;

public sealed abstract class Response implements Serializable {
	private final long created;
	private final Type type;

	protected Response(Type type) {
		this.created = System.currentTimeMillis();
		this.type = type;
	}

	public static Response word(Word word) {
		return new WordResponse(word);
	}

	public static Response playersList(List<PlayerModel> players) {
		return new PlayersListResponse(players);
	}

	public static Response playerModel(PlayerModel player) {
		return new PlayerModelResponse(player);
	}

	public static Response configuration(MenuModel model) {
		return new ConfigurationResponse(model);
	}

	public long getCreated() {
		return created;
	}

	public Type getType() {
		return type;
	}

	public static final class WordResponse extends Response {
		private final Word word;

		private WordResponse(Word word) {
			super(Type.Word);
			this.word = word;
		}

		public Word getWord() {
			return word;
		}
	}

	public static final class PlayersListResponse extends Response {
		private final List<PlayerModel> players;

		private PlayersListResponse(List<PlayerModel> players) {
			super(Type.PlayersList);
			this.players = players;
		}

		public List<PlayerModel> getPlayers() {
			return players;
		}
	}

	public static final class PlayerModelResponse extends Response {
		private final PlayerModel player;

		private PlayerModelResponse(PlayerModel player) {
			super(Type.PlayerModel);
			this.player = player;
		}

		public PlayerModel getPlayer() {
			return player;
		}
	}

	public static final class ConfigurationResponse extends Response {
		private final MenuModel configuration;

		public ConfigurationResponse(MenuModel configuration) {
			super(Type.Configuration);
			this.configuration = configuration;
		}

		public MenuModel getConfiguration() {
			return configuration;
		}
	}
}
