package project.models.game.network;

import project.models.game.Word;

import java.io.Serializable;

public sealed abstract class Request implements Serializable {
	private final long created;
	private final Type type;

	protected Request(Type type) {
		this.created = System.currentTimeMillis();
		this.type = type;
	}

	public static Request word(Word word) {
		return new WordRequest(word);
	}

	public static Request playersList() {
		return new PlayersListRequest();
	}

	public static Request playerModel() {
		return new PlayerModelRequest();
	}

	public static Request configuration() {
		return new ConfigurationRequest();
	}

	public static Request gameStart() {
		return new GameStartRequest();
	}

	public final Type getType() {
		return type;
	}

	public long getCreated() {
		return created;
	}

	public static final class WordRequest extends Request {
		private final Word word;

		private WordRequest(Word word) {
			super(Type.Word);
			this.word = word;
		}

		public Word getWord() {
			return word;
		}
	}

	public static final class PlayersListRequest extends Request {
		private PlayersListRequest() {
			super(Type.PlayersList);
		}
	}

	public static final class PlayerModelRequest extends Request {
		private PlayerModelRequest() {
			super(Type.PlayerModel);
		}
	}

	public static final class ConfigurationRequest extends Request {
		private ConfigurationRequest() {
			super(Type.Configuration);
		}
	}

	public static final class GameStartRequest extends Request {
		private GameStartRequest() {
			super(Type.GameStart);
		}
	}
}
