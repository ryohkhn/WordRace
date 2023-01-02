package project.controllers;

import project.models.game.network.NetworkModel;
import project.views.network.NetworkView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Controller for managing the network connection for a game.
 */
public final class NetworkController {
	private static final NetworkController instance = new NetworkController();
	private final NetworkView view = new NetworkView();
	private NetworkModel model;

	private NetworkController() {}

	/**
	 * Returns the singleton instance of this class.
	 *
	 * @return the singleton instance of this class
	 */
	public static NetworkController getInstance() {
		return instance;
	}

	/**
	 * Returns the local host address.
	 *
	 * @return the local host address
	 */
	public static InetAddress getLocalHost() {
		try {
			var interfaces = NetworkInterface.getNetworkInterfaces();
			while(interfaces.hasMoreElements()) {
				var networkInterface = interfaces.nextElement();
				var addresses = networkInterface.getInetAddresses();
				while(addresses.hasMoreElements()) {
					var addr = addresses.nextElement();
					if(!addr.isAnyLocalAddress() && !addr.isLinkLocalAddress())
						return addr;
				}
			}
		} catch(SocketException ignored) {}
		try {
			return InetAddress.getByName("No address found");
		} catch(UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Hosts a game at the specified port.
	 *
	 * @param port the port to host the game at
	 * @throws IOException          if an I/O error occurs
	 * @throws InterruptedException if the thread is interrupted
	 */
	public void host(int port) throws IOException, InterruptedException {
		if(model != null) stop();
		model = NetworkModel.host(port);
		view.setModel(model);
		model.addViewer(view);
		model.addViewer(MenuController.getInstance().getView());
	}

	/**
	 * Joins a game hosted at the specified address and port.
	 *
	 * @param address the address of the game host
	 * @param port    the port of the game host
	 * @throws IOException          if an I/O error occurs
	 * @throws InterruptedException if the thread is interrupted
	 */
	public void join(InetAddress address, int port)
	throws IOException, InterruptedException {
		if(model != null) stop();
		model = NetworkModel.join(address, port);
		view.setModel(model);
	}

	/**
	 * Stops the current network connection.
	 *
	 * @throws IOException          if an I/O error occurs
	 * @throws InterruptedException if the thread is interrupted
	 */
	public void stop() throws IOException, InterruptedException {
		if(model != null) model.stop();
		model = null;
		view.setModel(null);
	}

	/**
	 * Returns the current network model or null if there is no network model.
	 *
	 * @return the current network model
	 */
	public NetworkModel getModel() {
		return model;
	}

	/**
	 * Returns the network view.
	 *
	 * @return the network view
	 */
	public NetworkView getView() {
		return view;
	}

	/**
	 * Return if a network model is currently running.
	 *
	 * @return if a network model is currently running
	 */
	public boolean isRunning() {
		return model != null;
	}

	/**
	 * Return the number of players in the current game.
	 *
	 * @return the number of players in the current game
	 */
	public int getNumberOfPlayers() {
		return model.getNumberOfPlayers();
	}

	/**
	 * Indicate to all the other clients that the game has started.
	 *
	 * @throws UnsupportedOperationException if the current client is not the host
	 */
	public void gameStarted() throws UnsupportedOperationException {
		model.gameStarted();
	}
}
