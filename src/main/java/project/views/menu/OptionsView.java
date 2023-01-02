package project.views.menu;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import project.controllers.NetworkController;
import project.controllers.MenuController;
import project.models.menu.MenuModel;
import project.views.View;
import project.views.network.NetworkView;

import java.io.IOException;

public class OptionsView extends BorderPane implements View {
	private final MenuModel model;
	private final Label title, error;
	private final FlowPane container;
	private final NetworkView networkView;
	private MenuModel.GameMode currentMode;

	public OptionsView(MenuModel model) {
		this.model = model;
		this.title = new Label("Game Options");
		this.error = new Label();
		this.container = new FlowPane(Orientation.VERTICAL);
		this.currentMode = null;
		this.networkView = NetworkController.getInstance().getView();
		this.model.addViewer(this);

		this.error.setStyle("-fx-text-fill: red");
		this.error.setFont(new Font(20));

		this.container.setAlignment(Pos.CENTER);
		this.container.setColumnHalignment(HPos.CENTER);
		this.container.setHgap(10);
		this.container.setVgap(10);

		FlowPane center = makeLine(container, networkView);

		Button start = new Button("START");
		start.setOnMouseClicked(event -> {
			error.setText("");
			try {
				MenuController.getInstance().startGame();
			} catch(Exception e) {
				error.setText(e.getMessage());
			}
		});
		start.setPrefSize(100, 50);
		VBox bottom = new VBox(start, error);
		bottom.setAlignment(Pos.CENTER);
		bottom.setSpacing(10);

		setTop(title);
		setCenter(center);
		setBottom(bottom);

		setAlignment(title, Pos.CENTER);
		setAlignment(center, Pos.CENTER);
		setAlignment(bottom, Pos.CENTER);
		update();
	}

	@Override public void update() {
		if(currentMode != model.getGameMode()) {
			container.getChildren().clear();
			switch(currentMode = model.getGameMode()) {
				case Normal -> switchToNormalMode();
				case Competitive -> switchToCompetitiveMode();
				case Host -> switchToHostMode();
				case Join -> switchToJoinMode();
				default -> throw new IllegalStateException(
						"Unexpected value: " + model.getGameMode());
			}
			container.getChildren().forEach(Node::autosize);
			networkView.update();
		}
	}

	private FlowPane makeLine(Node... nodes) {
		FlowPane line = new FlowPane(Orientation.HORIZONTAL);
		line.setAlignment(Pos.CENTER);
		line.getChildren().addAll(nodes);
		line.setHgap(10);
		line.setVgap(10);
		line.setColumnHalignment(HPos.CENTER);
		return line;
	}

	private void switchToNormalMode() {
		title.setText("Normal Mode");

		var nbWords = new SelectNumberView(
				"Number of words",
				5,
				100,
				model.getNbWord(),
				model::setNbWord
		);

		container.getChildren().add(makeLine(nbWords));
	}

	private void switchToCompetitiveMode() {
		title.setText("Competitive Mode");

		var nbWords = new SelectNumberView(
				"Number of words",
				5,
				50,
				model.getNbWord(),
				model::setNbWord
		);
		var nbLives = new SelectNumberView(
				"Number of lives",
				1,
				10,
				model.getLives(),
				model::setLives
		);

		container.getChildren().add(makeLine(nbWords, nbLives));
	}

	private TextField getPortField() {
		var port = new TextField();
		port.setPromptText("Port");
		port.setPrefWidth(75);
		port.setText(model.getPort());
		port.textProperty().addListener((ob, ov, nv) -> model.setPort(nv));
		return port;
	}

	private void switchToHostMode() {
		switchToCompetitiveMode();
		title.setText("Host Mode");

		var host = new Label(NetworkController.getLocalHost().getHostAddress());
		host.setPrefWidth(100);
		container.getChildren().add(makeLine(host, getPortField()));

		var startServer = new Button("Start Server");
		startServer.setOnMouseClicked(event -> {
			error.setText("");
			try {
				MenuController.getInstance().startServer();
			} catch(IOException | InterruptedException e) {
				error.setText(e.getMessage());
			} catch(NumberFormatException e) {
				error.setText("Port must be a number");
			}
		});
		startServer.setPadding(new Insets(10));

		var stopServer = new Button("Stop Server");
		stopServer.setOnMouseClicked(event -> MenuController.getInstance()
															.stopServer());
		stopServer.setPadding(new Insets(10));

		container.getChildren().add(makeLine(startServer, stopServer));
	}


	private void switchToJoinMode() {
		title.setText("Join Mode");

		var host = new TextField();
		host.setPromptText("Host");
		host.setPrefWidth(150);
		host.setText(model.getHost());
		host.textProperty().addListener((ob, ov, nv) -> model.setHost(nv));
		container.getChildren().add(makeLine(host, getPortField()));

		var joinServer = new Button("Join Server");
		joinServer.setOnMouseClicked(event -> {
			error.setText("");
			try {
				MenuController.getInstance().joinServer();
			} catch(IOException | InterruptedException e) {
				error.setText(e.getMessage());
			} catch(NumberFormatException e) {
				error.setText("Port must be a number");
			}
		});
		joinServer.setPadding(new Insets(10));

		container.getChildren().add(makeLine(joinServer));
	}
}
