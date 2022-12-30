package project.views.menu;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import project.models.menu.MenuModel;

public class SelectGameModeView extends BorderPane {

	public SelectGameModeView(MenuModel model) {
		var container = initializeContainer(model);
		setCenter(container);

		Label title = new Label("Select game mode");
		title.setStyle("-fx-font-size: 20px;");
		setTop(title);

		setMaxSize(container.getMaxWidth(), container.getMaxHeight());
		setPadding(new Insets(10));
		setAlignment(title, Pos.CENTER);
		title.setAlignment(Pos.CENTER);
	}

	private HBox initializeContainer(MenuModel model) {
		var group = new ToggleGroup();
		var container = new HBox();
		int width = 0;
		for(var mode: MenuModel.GameMode.values()) {
			var button = new RadioButton(mode.toString());
			if(width == 0) button.setSelected(true);
			button.setPadding(new Insets(10));
			button.setToggleGroup(group);
			button.setOnAction(event -> model.setGameMode(mode));
			container.getChildren().add(button);
			width += button.getWidth() + 150;
		}

		container.setPadding(new Insets(10));
		container.setMaxSize(width, 50);
		container.setAlignment(Pos.CENTER);
		return container;
	}
}
