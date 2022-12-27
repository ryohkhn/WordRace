package project.models.game.network;

import project.models.game.Word;

import java.io.Serializable;

public sealed abstract class Request implements Serializable {
	private final Type type;

	protected Request(Type type) {
		this.type = type;
	}

	public static Request word(Word word) {
		return new WordRequest(word);
	}

	public static Request playersList() {
		return new PlayersListRequest();
	}

	public final Type getType() {
		return type;
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
}
