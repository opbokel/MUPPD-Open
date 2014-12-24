package mtg.pong.menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mtg.pong.Global;
import mtg.pong.R;
import mtg.pong.engine.GOModifier;
import mtg.pong.engine.GameObject;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GOMenuAdapter extends ArrayAdapter<GameObject> {

	public Map<String, CheckBox> checkBoxs;

	public GOMenuAdapter(final Context context, final List<GameObject> objects) {
		super(context, android.R.layout.simple_list_item_multiple_choice,
				objects);
		this.checkBoxs = new HashMap<String, CheckBox>();

	}

	private CheckBox createCheckBox(final GameObject go) {

		final CheckBox cBox = new CheckBox(getContext());

		cBox.setMinWidth((int) getRealSize(48));
		cBox.setMinHeight((int) getRealSize(60));
		this.checkBoxs.put(go.getGOKey(), cBox);
		cBox.setChecked(go.inGame);

		cBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(final CompoundButton arg0,
					final boolean isChecked) {
				go.inGame = isChecked;
			}
		});

		return cBox;

	}

	private String getGOName(final GameObject go) {
		if (go instanceof GOModifier) {
			final GOModifier mod = (GOModifier) go;
			final String[] modNames = getContext().getResources()
					.getStringArray(R.array.modifier_names);
			final int index = mod.modifier.id.id;
			return modNames[index];
		} else {
			return go.getGOKey();
		}
	}

	private float getRealSize(final float sizeDP) {
		final float scale = getContext().getResources().getDisplayMetrics().density;
		return (sizeDP * scale);
	}

	@Override
	public View getView(final int position, final View convertView,
			final ViewGroup parent) {
		final LinearLayout row = new LinearLayout(getContext());
		row.setOrientation(LinearLayout.HORIZONTAL);
		final ImageView image = new ImageView(getContext());

		final TextView cText = new TextView(getContext());
		final Typeface font = Global.getPixelatedFont(getContext());
		cText.setTypeface(font);
		// cText.setTextSize(getRealSize(16));
		cText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
		cText.setTextColor(Color.WHITE);

		final GameObject go = this.getItem(position);

		final String name = getGOName(go);
		cText.setText(name);

		final GODrawable goDrawable = new GODrawable(go);
		go.box.left = 0;
		go.box.top = getRealSize(5);
		go.box.right = getRealSize(60);
		go.box.bottom = getRealSize(60);
		go.colision = true;

		image.setMinimumHeight((int) getRealSize(70));
		image.setMinimumWidth((int) getRealSize(70));
		image.setImageDrawable(goDrawable);

		row.addView(createCheckBox(go));
		row.addView(image);
		row.addView(cText);
		// image.bringToFront();
		return row;
	}
}
