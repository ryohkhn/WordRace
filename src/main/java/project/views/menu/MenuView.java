package project.views.menu;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import project.models.menu.MenuModel;
import project.views.View;


public class MenuView extends Application implements View {
	private static final double width = 900;
	private static final double height = 600;
	private final MenuModel model;
	private Stage stage;

	public MenuView(MenuModel model) {
		this.model = model;
	}

	@Override
	public void start(Stage primaryStage) {
		this.stage = primaryStage;

		VBox root = new VBox();
		root.setSpacing(10);
		root.setPadding(new Insets(10));
		root.setAlignment(Pos.CENTER);
		root.setMaxSize(width, height);

		var selectGameMode = new SelectGameModeView(model);
		root.getChildren().add(selectGameMode);

		var options = new OptionsView(model);
		root.getChildren().add(options);

		root.setAlignment(Pos.CENTER);

		Scene scene = new Scene(root, width, height);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Configuration");
		primaryStage.show();
	}

	public Stage getStage() {
		return stage;
	}

	@Override
	public void update() {}

	@Override
	public void setVisible(boolean visible) {
		if(visible) stage.show();
		else stage.hide();
	}
}