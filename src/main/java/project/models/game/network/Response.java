package project.models.game.network;

import project.models.game.PlayerModel;
import project.models.game.Word;

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
		return new PlayersList(players);
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

	public static final class PlayersList extends Response {
		private final List<PlayerModel> players;

		private PlayersList(List<PlayerModel> players) {
			super(Type.PlayersList);
			this.players = players;
		}

		public List<PlayerModel> getPlayers() {
			return players;
		}
	}
}
