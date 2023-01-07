package project.views.menu;

import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;

import java.util.function.Consumer;

public class SelectNumberView extends BorderPane {

	/**
	 * Creates a spinner with a title and minimum/maximum values
	 * @param title the title of the spinner
	 * @param min minimum value
	 * @param max maximum value
	 * @param initial initial value
	 * @param update the Consumer to accept when the spinner changes
	 */
	public SelectNumberView(String title, int min, int max, int initial, Consumer<Integer> update) {
		var spinner = new Spinner<Integer>(min, max, min);
		spinner.valueProperty()
			   .addListener((observable, oldValue, newValue) -> update.accept(newValue));
		spinner.setMaxWidth(75);
		spinner.getValueFactory()
			   .setValue(initial);

		setCenter(spinner);
		setTop(new Label(title));
		setPadding(new javafx.geometry.Insets(10));
	}
}
