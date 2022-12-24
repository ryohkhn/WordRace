package project;

import javafx.application.Application;
import javafx.stage.Stage;
import project.controllers.game.GameController;

public class Main extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override public void start(Stage primaryStage) throws Exception {
		GameController.getInstance().start(10, 15);
	}
}
