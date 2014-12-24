package mtg.pong;

import mtg.pong.menu.ModifierMenuActivity;
import mtg.pong.network.BluetoothActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends OneInstanceActivity {

	public static final String AI_KEY = "mtg.pong.AI_KEY";

	public static final String MAIN_PREFS = "mainPrefs";

	private static final String PLAYER_CONTROlLER_KEY = "playerControllerKey";

	private ColorStateList oldResumeColors;

	private int debugCount = 0;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().getDecorView().getRootView()
				.setBackgroundColor(Color.BLACK);
		prepareFonts();
		loadPreferences();
		final TextView resumeText = (TextView) findViewById(R.id.resumeGameText);
		this.oldResumeColors = resumeText.getTextColors();
	}

	@Override
	public void onResume() {
		super.onStart();
		final TextView resumeText = (TextView) findViewById(R.id.resumeGameText);
		if (Global.gEngine == null) {
			resumeText.setTextColor(Color.GRAY);
		} else if (this.oldResumeColors != null) {
			resumeText.setTextColor(this.oldResumeColors);
		}
		this.debugCount = 0;

	}

	public void loadPreferences() {
		final String p1Control = getPlayerControlText(1, false);
		final String p2Control = getPlayerControlText(2, false);
		setControllerText(1, p1Control,
				(TextView) findViewById(R.id.p1ControlText));
		setControllerText(2, p2Control,
				(TextView) findViewById(R.id.p2ControlText));
	}

	public static String getPlayerControllerKey(final int playerNumber) {
		return PLAYER_CONTROlLER_KEY + "-" + playerNumber;
	}

	public String getPlayerControlText(final int playerNumber,
			final boolean getNextControl) {
		final String[] controlArray = getResources().getStringArray(
				R.array.controller_array);
		final SharedPreferences settings = getSharedPreferences(
				MainActivity.MAIN_PREFS, 0);
		int currentControl = settings.getInt(
				MainActivity.getPlayerControllerKey(playerNumber),
				playerNumber == 2 ? 3 : 0);
		if (getNextControl) {
			currentControl++;
			if (currentControl >= controlArray.length) {
				currentControl = 0;
			}
			final SharedPreferences.Editor editor = settings.edit();
			editor.putInt(getPlayerControllerKey(playerNumber), currentControl);
			editor.commit();
		}
		return controlArray[currentControl];
	}

	public void setControllerText(final int player, final String control,
			final TextView textView) {
		final String fullText = getString(R.string.player) + player + ": "
				+ control;
		textView.setText(fullText);
	}

	private void prepareFonts() {
		final Typeface font = Global.getPixelatedFont(this);

		TextView txt = (TextView) findViewById(R.id.newGameText);
		txt.setTypeface(font);

		txt = (TextView) findViewById(R.id.resumeGameText);
		txt.setTypeface(font);

		txt = (TextView) findViewById(R.id.p1ControlText);
		txt.setTypeface(font);

		txt = (TextView) findViewById(R.id.p2ControlText);
		txt.setTypeface(font);

		txt = (TextView) findViewById(R.id.optionsText);
		txt.setTypeface(font);

		txt = (TextView) findViewById(R.id.nextMain);
		txt.setTypeface(font);
	}

	public void newGame(final View view) {
		Global.gEngine = null;
		Global.leaveTrail = false;
		// global.getgetPixelatedFont
		startGame(view);
	}

	public void resumeGame(final View view) {
		if (Global.gEngine == null) {
			return;
		}
		startGame(view);
	}

	public void next(final View view) {
		final Intent intent = new Intent(this, BluetoothActivity.class);
		startActivity(intent);
	}

	public void startGame(final View view) {
		final Intent intent = new Intent(this, CanvasActivity.class);
		startActivity(intent);
	}

	public void setDebugEnable(final View view) {
		this.debugCount++;
		if (this.debugCount >= 5) {
			this.debugCount = 0;
			Global.DEBUG = !Global.DEBUG;
			final String text = "DEBUG " + Global.DEBUG;
			final int duration = Toast.LENGTH_SHORT;
			final Toast toast = Toast.makeText(this, text, duration);
			toast.show();
		}
	}

	public void callOptionsMenu(final View view) {
		final Intent intent = new Intent(this, ModifierMenuActivity.class);
		startActivity(intent);
	}

	public void changeP1Control(final View view) {
		final String p1Control = getPlayerControlText(1, true);
		setControllerText(1, p1Control,
				(TextView) findViewById(R.id.p1ControlText));
	}

	public void changeP2Control(final View view) {
		final String p2Control = getPlayerControlText(2, true);
		setControllerText(2, p2Control,
				(TextView) findViewById(R.id.p2ControlText));
	}

}
