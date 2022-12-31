package project.models;

import project.views.View;
import project.views.game.GameView;

import java.util.HashSet;
import java.util.Set;

public abstract class Model {
	private final Set<View> viewers;

	public Model() {
		viewers = new HashSet<>();
	}

	public final void notifyViewers() {
		viewers.forEach(View::update);
	}

	public final void addViewer(View viewer) {
		viewers.add(viewer);
	}

	public final void removeViewer(GameView gameView){
		viewers.remove(gameView);
	}
}
