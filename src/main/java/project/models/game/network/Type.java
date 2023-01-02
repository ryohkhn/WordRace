package project.models.game.network;

/**
 * Enum representing the different types of network messages that can be sent or received.
 */
public enum Type {
	/**
	 * A word message.
	 */
	Word,
	/**
	 * A message containing a list of players.
	 */
	PlayersList,
	/**
	 * A message containing information about a single player.
	 */
	PlayerModel,
	/**
	 * A message containing configuration information.
	 */
	Configuration,
	/**
	 * A message indicating that the game has started.
	 */
	GameStart
}

