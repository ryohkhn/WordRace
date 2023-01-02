package project.controllers.game;

import project.controllers.menu.MenuController;
import project.models.game.network.NetworkModel;
import project.views.network.NetworkView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public final class NetworkController {
	private static final NetworkController instance = new NetworkController();
	private final NetworkView view = new NetworkView();
	private NetworkModel model;

	private NetworkController() {}

	public static NetworkController getInstance() {
		return instance;
	}

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

	public void host(int port) throws IOException, InterruptedException {
		if(model != null) stop();
		model = NetworkModel.host(port);
		view.setModel(model);
		model.addViewer(view);
		model.addViewer(MenuController.getInstance().getView());
	}

	public void join(InetAddress address, int port)
	throws IOException, InterruptedException {
		if(model != null) stop();
		model = NetworkModel.join(address, port);
		view.setModel(model);
	}

	public void stop() throws IOException, InterruptedException {
		if(model != null) model.stop();
		model = null;
		view.setModel(null);
	}

	public NetworkModel getModel() {
		return model;
	}

	public NetworkView getView() {
		return view;
	}

	public boolean isRunning() {
		return model != null;
	}

	public int getNumberOfPlayers() {
		return model.getNumberOfPlayers();
	}

	public void gameStarted() throws UnsupportedOperationException {
		model.gameStarted();
	}
}
