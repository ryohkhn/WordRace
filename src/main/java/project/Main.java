package project;

import javafx.application.Application;
import javafx.stage.Stage;
import project.controllers.game.GameController;
import project.controllers.menu.MenuController;

public class Main extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override public void start(Stage primaryStage) {
		MenuController.getInstance().getView().start(primaryStage);
	}
}
