package mtg.pong;

import mtg.pong.ui.ClientCanvas;
import android.os.Bundle;
import android.view.Menu;

public class ClientCanvasActivity extends OneInstanceActivity {
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Global.gCanvas = null;
		Global.cCanvas = new ClientCanvas(this);
		setContentView(Global.cCanvas);
	}

	@Override
	public void onResume() {
		super.onStart();
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
