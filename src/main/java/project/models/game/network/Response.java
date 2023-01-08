package project.models.game.network;

import project.models.game.PlayerModel;
import project.models.game.words.Word;
import project.models.menu.MenuModel;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a network response
 */
public sealed abstract class Response implements Serializable {
	private final long created;
	private final Type type;

	protected Response(Type type) {
		this.created = System.currentTimeMillis();
		this.type = type;
	}

	/**
	 * Creates a new WordResponse with the specified word.
	 *
	 * @param word the word for the response
	 * @return a new WordResponse
	 * @see WordResponse
	 */
	public static Response word(Word word) {
		return new WordResponse(word);
	}

	/**
	 * Creates a new PlayersListResponse with the specified list of players.
	 *
	 * @param players the list of players for the response
	 * @return a new PlayersListResponse
	 * @see PlayersListResponse
	 */
	public static Response playersList(List<PlayerModel> players) {
		return new PlayersListResponse(players);
	}

	/**
	 * Creates a new PlayerModelResponse with the specified player.
	 *
	 * @param player the player for the response
	 * @return a new PlayerModelResponse
	 * @see PlayerModelResponse
	 */
	public static Response playerModel(PlayerModel player) {
		return new PlayerModelResponse(player);
	}

	/**
	 * Creates a new ConfigurationResponse with the specified configuration model.
	 *
	 * @param model the configuration model for the response
	 * @return a new ConfigurationResponse
	 * @see ConfigurationResponse
	 */
	public static Response configuration(MenuModel model) {
		return new ConfigurationResponse(model);
	}

	/**
	 * Returns the time when this response was created.
	 *
	 * @return the time when this response was created
	 */
	public long getCreated() {
		return created;
	}

	/**
	 * Returns the type of this response.
	 *
	 * @return the type of this response
	 * @see Type
	 */
	public Type getType() {
		return type;
	}


	/**
	 * Represents a response containing a word.
	 */
	public static final class WordResponse extends Response {
		private final Word word;

		private WordResponse(Word word) {
			super(Type.Word);
			this.word = word;
		}

		/**
		 * Returns the word for this response.
		 *
		 * @return the word for this response
		 */
		public Word getWord() {
			return word;
		}
	}

	/**
	 * Represents a response containing a list of players.
	 */
	public static final class PlayersListResponse extends Response {
		private final List<PlayerModel> players;

		private PlayersListResponse(List<PlayerModel> players) {
			super(Type.PlayersList);
			this.players = players;
		}

		/**
		 * Returns the list of players for this response.
		 *
		 * @return the list of players for this response
		 */
		public List<PlayerModel> getPlayers() {
			return players;
		}
	}

	/**
	 * Represents a response containing a player model.
	 */
	public static final class PlayerModelResponse extends Response {
		private final PlayerModel player;

		private PlayerModelResponse(PlayerModel player) {
			super(Type.PlayerModel);
			this.player = player;
		}

		/**
		 * Returns the player model for this response.
		 *
		 * @return the player model for this response
		 */
		public PlayerModel getPlayer() {
			return player;
		}
	}

	/**
	 * Represents a response containing a configuration model.
	 */
	public static final class ConfigurationResponse extends Response {
		private final MenuModel configuration;

		private ConfigurationResponse(MenuModel configuration) {
			super(Type.Configuration);
			this.configuration = configuration;
		}

		/**
		 * Returns the configuration model for this response.
		 *
		 * @return the configuration model for this response
		 */
		public MenuModel getConfiguration() {
			return configuration;
		}
	}
}
