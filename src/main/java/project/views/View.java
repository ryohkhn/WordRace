package project.views;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

@FunctionalInterface
public interface View {
	void update();

	default void setVisible(boolean visible) {}

	/**
	 * Create the Menu Bar with an exit button and add it to the root Pane
	 */
	default MenuBar initMenuBar(){
		MenuItem exit=new MenuItem("Exit");
		exit.setOnAction(e -> System.exit(0));

		Menu file=new Menu("File");
		file.getItems().add(exit);

		MenuBar menu=new MenuBar();
		menu.getMenus().add(file);

		return menu;
	}
}
