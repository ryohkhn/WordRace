package project.controllers.game;

import project.models.game.network.NetworkModel;
import project.views.NetworkView;

import java.io.IOException;
import java.net.InetAddress;

public final class NetworkController {
	private static final NetworkController instance = new NetworkController();
	private NetworkModel model;
	private NetworkView view;

	private NetworkController() {}

	public static NetworkController getInstance() {
		return instance;
	}

	public void host(int port) throws IOException, InterruptedException {
		if(model != null) model.stop();
		model = NetworkModel.host(port);
		model.start();
		view = new NetworkView(model);
	}

	public void join(InetAddress address, int port)
	throws IOException, InterruptedException {
		if(model != null) model.stop();
		model = NetworkModel.join(address, port);
		model.start();
		view = new NetworkView(model);
	}

	public NetworkModel getModel() {
		return model;
	}

	public NetworkView getView() {
		return view;
	}
}
