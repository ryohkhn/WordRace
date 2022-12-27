package project.models.game.network;

import project.models.game.Word;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.List;

public sealed abstract class Response implements Serializable {
	private final Type type;

	protected Response(Type type) {
		this.type = type;
	}

	public static Response word(Word word) {
		return new WordResponse(word);
	}

	public static Response playersList(List<PlayersList.Player> players) {
		return new PlayersList(players);
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
		private final List<Player> players;

		private PlayersList(List<Player> players) {
			super(Type.PlayersList);
			this.players = players;
		}

		public List<Player> getPlayers() {
			return players;
		}

		public record Player(InetAddress address) {}
	}
}
