package mtg.pong;

import mtg.pong.engine.GameEngine;
import mtg.pong.ui.GameCanvas;
import android.os.Bundle;
import android.view.Menu;


public class CanvasActivity extends OneInstanceActivity {

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Global.gameEnded = false;
		if (Global.gEngine == null) {
			Global.gEngine = new GameEngine(this);
		}
		Global.gCanvas = new GameCanvas(this);
		setContentView(Global.gCanvas);
	}

	@Override
	public void onResume() {
		super.onStart();
		Global.gEngine.loadAllControls();
		Global.resumeThreads();
	}

	@Override
	public void onPause() {
		super.onPause();
		Global.stopThreads(Global.gameEnded, true);
		if (Global.gameEnded) {
			finish();
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		Global.paused = !Global.paused;
		return false;
	}

}
