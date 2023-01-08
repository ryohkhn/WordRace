package project.views.menu;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import project.controllers.MenuController;
import project.controllers.NetworkController;
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
			if(currentMode == MenuModel.GameMode.Join)
				error.setText("Only the host can start the game");
			else
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

	private TextField getPlayerNameField() {
		return new TextField() {
			{
				setPromptText("Enter your name");
				setText(model.getPlayerName());
				setMaxWidth(200);
				setTextFormatter(new TextFormatter<>(change -> {
					change.setText(change.getText()
										 .replaceAll("[^a-zA-Z0-9]", ""));
					return change;
				}));
				textProperty().addListener((ob, ov, nv) -> {
					error.setText("");
					try {
						model.setPlayerName(nv);
					} catch(Exception e) {
						error.setText(e.getMessage());
					}
				});
			}
		};
	}

	/**
	 * Change the view depending on the game mode
	 */
	@Override public void update() {
		if(currentMode != model.getGameMode()) {
			container.getChildren().clear();
			container.getChildren().add(getPlayerNameField());
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

	/**
	 * Make a line with all nodes
	 *
	 * @param nodes the nodes to align
	 * @return a FlowPane with a ligne of nodes
	 */
	private FlowPane makeLine(Node... nodes) {
		FlowPane line = new FlowPane(Orientation.HORIZONTAL);
		line.setAlignment(Pos.CENTER);
		line.getChildren().addAll(nodes);
		line.setHgap(10);
		line.setVgap(10);
		line.setColumnHalignment(HPos.CENTER);
		return line;
	}

	/**
	 * Creates a field for the number of words
	 *
	 * @return the SelectNumberView pane of words
	 */
	private SelectNumberView getNbWordsField() {
		return new SelectNumberView(
				"Number of words",
				5,
				100,
				model.getNbWord(),
				model::setNbWord
		);
	}

	/**
	 * Change display text to normal mode
	 */
	private void switchToNormalMode() {
		title.setText("Normal Mode");
		container.getChildren().add(makeLine(getNbWordsField()));
	}

	/**
	 * Creates a field for the number of lives
	 *
	 * @return the SelectNumberView pane of lives
	 */
	private SelectNumberView getLivesField() {
		return new SelectNumberView(
				"Number of lives",
				1,
				100,
				model.getLives(),
				model::setLives
		);
	}

	/**
	 * Change display text and add the fields for the mode configuration
	 */
	private void switchToCompetitiveMode() {
		title.setText("Competitive Mode");
		container.getChildren().add(makeLine(
				getNbWordsField(),
				getLivesField()
		));
	}

	/**
	 * Creates a text field for the port value
	 *
	 * @return a TextField instance
	 */
	private TextField getPortField() {
		var port = new TextField();
		port.setPromptText("Port");
		port.setPrefWidth(75);
		port.setText(model.getPort());
		port.textProperty().addListener((ob, ov, nv) -> model.setPort(nv));
		return port;
	}

	/**
	 * Creates a text field for the host IP address
	 *
	 * @return a TextField instance
	 */
	private TextField getHostField(boolean editable) {
		var host = new TextField();
		if(editable) host.setPromptText("Host");
		else host.setText(NetworkController.getLocalHost().getHostAddress());
		host.setPrefWidth(150);
		host.textProperty().addListener((ob, ov, nv) -> model.setHost(nv));
		host.setEditable(editable);
		return host;
	}

	/**
	 * Change title and add server buttons to the view
	 */
	private void switchToHostMode() {
		switchToCompetitiveMode();
		title.setText("Host Mode");

		container.getChildren().add(makeLine(
				getHostField(false),
				getPortField()
		));

		var startServer = new Button("Start Server");
		startServer.setOnMouseClicked(event -> {
			error.setText("");
			try {
				MenuController.getInstance().startServer();
			} catch(IOException | InterruptedException e) {
				error.setText(e.getMessage());
			} catch(NumberFormatException e) {
				error.setText("Port must be a positive integer");
			}
		});
		startServer.setPadding(new Insets(10));

		var stopServer = new Button("Stop Server");
		stopServer.setOnMouseClicked(event -> {
			error.setText("");
			MenuController.getInstance()
						  .stopServer();
		});
		stopServer.setPadding(new Insets(10));

		container.getChildren().add(makeLine(startServer, stopServer));
	}

	/**
	 * Change title and add server buttons to the view
	 */
	private void switchToJoinMode() {
		title.setText("Join Mode");
		container.getChildren().add(makeLine(
				getHostField(true),
				getPortField()
		));

		var joinServer = new Button("Join Server");
		joinServer.setOnMouseClicked(event -> {
			error.setText("");
			try {
				MenuController.getInstance().joinServer();
			} catch(IOException | InterruptedException e) {
				error.setText(e.getMessage());
			} catch(NumberFormatException e) {
				error.setText("Port must be a positive integer");
			}
		});
		joinServer.setPadding(new Insets(10));

		container.getChildren().add(makeLine(joinServer));
	}
}
