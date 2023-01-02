package project.models;

import project.views.View;

import java.util.HashSet;
import java.util.Set;

public abstract class Model {
	/**
	 * Set of View for every viewer
	 */
	private final Set<View> viewers;

	/**
	 * Model constructor
	 */
	public Model() {
		viewers = new HashSet<>();
	}

	/**
	 * Notify all viewers of the game
	 */
	public final void notifyViewers() {
		viewers.forEach(View::update);
	}

	/**
	 * Add a view to the list of viewers
	 * @param viewer viewer to add
	 */
	public final void addViewer(View viewer) {
		viewers.add(viewer);
	}

	/**
	 * Remove a viewer from the list
	 * @param view to remove
	 */
	public final void removeViewer(View view){
		viewers.remove(view);
	}
}
