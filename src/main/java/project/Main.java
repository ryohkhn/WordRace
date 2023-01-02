package project;

import javafx.application.Application;
import javafx.stage.Stage;
import project.controllers.NetworkController;
import project.controllers.MenuController;

import java.io.IOException;

public class Main extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override public void start(Stage primaryStage) {
		primaryStage.setOnCloseRequest(e -> {
			try {
				NetworkController.getInstance().stop();
			} catch(IOException | InterruptedException ignored) {}
			System.exit(0);
		});
		MenuController.getInstance().getView().start(primaryStage);
	}
}
