package project.controllers.game;

import project.controllers.menu.MenuController;
import project.models.game.network.NetworkModel;
import project.views.network.NetworkView;

import java.io.IOException;
import java.net.InetAddress;

public final class NetworkController {
	private static final NetworkController instance = new NetworkController();
	private NetworkModel model;
	private final NetworkView view = new NetworkView();

	private NetworkController() {}

	public static NetworkController getInstance() {
		return instance;
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
}
