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
import project.controllers.menu.MenuController;
import project.models.menu.MenuModel;
import project.views.View;

public class OptionsView extends BorderPane implements View {
	private final MenuModel model;
	private final Label title;
	private final FlowPane container;
	private MenuModel.GameMode currentMode;

	public OptionsView(MenuModel model) {
		this.model = model;
		this.title = new Label("Game Options");
		this.container = new FlowPane(Orientation.VERTICAL);
		this.currentMode = null;
		this.model.addViewer(this);

		this.container.setAlignment(Pos.CENTER);
		this.container.setColumnHalignment(HPos.CENTER);
		this.container.setHgap(10);
		this.container.setVgap(10);

		Button start = new Button("START");
		start.setOnMouseClicked(event -> MenuController.getInstance().startGame());

		setTop(title);
		setCenter(container);
		setBottom(start);

		setAlignment(title, Pos.CENTER);
		setAlignment(start, Pos.CENTER);
		setAlignment(container, Pos.CENTER);
		update();
	}

	@Override public void update() {
		if(currentMode != model.getGameMode()) {
			container.getChildren().clear();
			switch(currentMode = model.getGameMode()) {
				case Solo -> switchToSoloMode();
				case Host -> switchToHostMode();
				case Join -> switchToJoinMode();
				default -> throw new IllegalStateException(
						"Unexpected value: " + model.getGameMode());
			}
			container.getChildren().forEach(Node::autosize);
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

	private void switchToSoloMode() {
		title.setText("Solo Mode");

		var nbWords = new SelectNumberView(
				"Number of words",
				5,
				100,
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

	private void switchToHostMode() {
		switchToSoloMode();
		title.setText("Host Mode");

		var host = new TextField();
		host.setPromptText("Host");
		host.setPrefWidth(200);
		host.setText(model.getHost());
		host.textProperty().addListener((observable, oldValue, newValue) -> model.setHost(newValue));

		var port = new TextField();
		port.setPromptText("Port");
		port.setPrefWidth(200);
		port.setText(model.getPort());
		port.textProperty().addListener((observable, oldValue, newValue) -> model.setPort(newValue));

		var startServer = new Button("Start Server");
		startServer.setOnMouseClicked(event -> MenuController.getInstance().startServer());
		startServer.setPadding(new Insets(10));

		var stopServer = new Button("Stop Server");
		stopServer.setOnMouseClicked(event -> MenuController.getInstance().stopServer());
		stopServer.setPadding(new Insets(10));

		container.getChildren().addAll(
				makeLine(host, port),
				makeLine(startServer, stopServer)
		);

		container.setAlignment(Pos.CENTER);
	}


	private void switchToJoinMode() {
		switchToHostMode();
		title.setText("Join Mode");
	}
}
