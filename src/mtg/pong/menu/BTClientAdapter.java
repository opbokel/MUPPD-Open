package mtg.pong.menu;

import java.util.ArrayList;
import java.util.List;

import mtg.pong.Global;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BTClientAdapter extends ArrayAdapter<String> {

	public BTClientAdapter(final Context context) {
		super(context, android.R.layout.simple_list_item_1);
	}

	public void updateItem(final int position, final String value) {
		final int count = this.getCount();
		final List<String> items = new ArrayList<String>();
		for (int n = 0; n < count; n++) {
			if (n == position) {
				items.add(value);
			} else {
				items.add(this.getItem(n));
			}
		}
		this.clear();
		for (final String item : items) {
			this.add(item);
		}
	}

	@Override
	public View getView(final int position, final View convertView,
			final ViewGroup parent) {
		super.getView(position, convertView, parent);
		final TextView text = new TextView(getContext());
		final Typeface font = Global.getPixelatedFont(getContext());
		text.setTypeface(font);
		text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
		text.setTextColor(Color.WHITE);
		text.setPadding(20, 15, 0, 15);

		final String txtString = this.getItem(position);

		text.setText(txtString);
		return text;
	}
}
