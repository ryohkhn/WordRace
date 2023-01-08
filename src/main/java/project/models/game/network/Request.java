package project.models.game.network;

import project.models.game.words.Word;

import java.io.Serializable;

/**
 * Represents a network request
 */
public sealed abstract class Request implements Serializable {
	private final long created;
	private final Type type;

	protected Request(Type type) {
		this.created = System.currentTimeMillis();
		this.type = type;
	}

	/**
	 * Creates a new WordRequest with the specified word.
	 *
	 * @param word the word for the request
	 * @return a new WordRequest
	 */
	public static Request word(Word word) {
		return new WordRequest(word);
	}

	/**
	 * Creates a new PlayersListRequest.
	 *
	 * @return a new PlayersListRequest
	 */
	public static Request playersList() {
		return new PlayersListRequest();
	}

	/**
	 * Creates a new PlayerModelRequest.
	 *
	 * @return a new PlayerModelRequest
	 */
	public static Request playerModel() {
		return new PlayerModelRequest();
	}

	/**
	 * Creates a new ConfigurationRequest.
	 *
	 * @return a new ConfigurationRequest
	 */
	public static Request configuration() {
		return new ConfigurationRequest();
	}

	/**
	 * Creates a new GameStartRequest.
	 *
	 * @return a new GameStartRequest
	 */
	public static Request gameStart() {
		return new GameStartRequest();
	}

	/**
	 * Returns the type of the request.
	 *
	 * @return the type of the request
	 * @see Type
	 */
	public final Type getType() {
		return type;
	}

	/**
	 * Returns the time when the request was created.
	 *
	 * @return the time when the request was created
	 */
	public long getCreated() {
		return created;
	}

	/**
	 * Represents a request containing a word.
	 *
	 * @see Request
	 */
	public static final class WordRequest extends Request {
		private final Word word;

		private WordRequest(Word word) {
			super(Type.Word);
			this.word = word;
		}

		/**
		 * Returns the word of the request.
		 *
		 * @return the word of the request
		 */
		public Word getWord() {
			return word;
		}
	}

	/**
	 * Represents a request for the list of players.
	 *
	 * @see Request
	 */
	public static final class PlayersListRequest extends Request {
		private PlayersListRequest() {
			super(Type.PlayersList);
		}
	}

	/**
	 * Represents a request for the player model.
	 *
	 * @see Request
	 */
	public static final class PlayerModelRequest extends Request {
		private PlayerModelRequest() {
			super(Type.PlayerModel);
		}
	}

	/**
	 * Represents a request for the configuration model.
	 *
	 * @see Request
	 */
	public static final class ConfigurationRequest extends Request {
		private ConfigurationRequest() {
			super(Type.Configuration);
		}
	}

	/**
	 * Represents a request for the game start.
	 *
	 * @see Request
	 */
	public static final class GameStartRequest extends Request {
		private GameStartRequest() {
			super(Type.GameStart);
		}
	}
}
