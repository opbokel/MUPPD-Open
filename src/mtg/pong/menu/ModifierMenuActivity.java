package mtg.pong.menu;

import java.util.List;

import mtg.pong.Global;
import mtg.pong.R;
import mtg.pong.engine.GameObject;
import mtg.pong.modifier.ModifierPool;
import android.app.ListActivity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

public class ModifierMenuActivity extends ListActivity {

	public static final String MODIFIER_PREFS = "modifierPreferences";

	private GOMenuAdapter adapter;

	private boolean sendDrawOld = Global.isHost;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.sendDrawOld = Global.isHost;
		Global.isHost = false;
		getWindow().getDecorView().getRootView()
				.setBackgroundColor(Color.BLACK);

		// Carrega os modifiers
		final List<GameObject> modifiers = new ModifierPool().getAllModifiers();
		final SharedPreferences settings = getSharedPreferences(MODIFIER_PREFS,
				0);
		for (final GameObject go : modifiers) {
			go.inGame = settings.getBoolean(go.getGOKey(), true);
		}
		this.adapter = new GOMenuAdapter(this, modifiers);
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		// Assign adapter to ListView
		this.setListAdapter(this.adapter);
	}

	@Override
	public void onStop() {
		super.onStop();
		Global.isHost = this.sendDrawOld;
		final SharedPreferences settings = getSharedPreferences(MODIFIER_PREFS,
				0);
		final SharedPreferences.Editor editor = settings.edit();
		for (final String key : this.adapter.checkBoxs.keySet()) {
			final CheckBox cb = this.adapter.checkBoxs.get(key);
			editor.putBoolean(key, cb.isChecked());
		}
		editor.commit();
		final String text = getString(R.string.configurations_saved);
		final int duration = Toast.LENGTH_SHORT;
		final Toast toast = Toast.makeText(this, text, duration);
		toast.show();
	}

}
