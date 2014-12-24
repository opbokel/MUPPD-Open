package mtg.pong;

import mtg.pong.engine.Score;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class WinnerActivity extends OneInstanceActivity {

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_winner);
		getWindow().getDecorView().getRootView()
				.setBackgroundColor(Color.BLACK);
		prepareTexts();
	}

	public void mainActivity(final View view) {
		final Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	public void replay(final View view) {
		final int duration = Toast.LENGTH_SHORT;
		final Toast toast = Toast.makeText(this,
				String.valueOf(Global.cUtil.bb.limit()), duration);
		toast.show();
		final Intent intent = new Intent(this, ClientCanvasActivity.class);
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		mainActivity(null);
	}

	private void prepareTexts() {
		final Typeface font = Global.getPixelatedFont(this);

		final Intent intent = getIntent();
		TextView txt = (TextView) findViewById(R.id.winnerPlayer);
		txt.setTypeface(font);
		txt.setText(getString(R.string.player) + " "
				+ intent.getStringExtra(Score.WINNER_PLAYER_KEY) + " "
				+ getString(R.string.wins));

		txt = (TextView) findViewById(R.id.scoreWinner);
		txt.setText(intent.getStringExtra(Score.P1_SCORE_KEY) + " X "
				+ intent.getStringExtra(Score.P2_SCORE_KEY));
		txt.setTypeface(font);

		txt = (TextView) findViewById(R.id.continueWinner);
		txt.setTypeface(font);
	}

	@Override
	public void onStop() {
		super.onStop();
		finish();
	}
}
