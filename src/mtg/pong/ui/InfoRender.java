package mtg.pong.ui;

import mtg.pong.Global;
import mtg.pong.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

public class InfoRender {

	public static final Paint PAUSE_PAINT;

	public static final float PAUSE_TEXT_SIZE = 5.0f;

	public static final long PAUSE_BLINK_TIME_INTERVAL = 500;

	public static final float x = 3f;

	public static final float y = 47f;

	public static long totalTimeElapsed = 0;

	static {
		PAUSE_PAINT = new Paint();
		PAUSE_PAINT.setColor(Color.BLUE);
	}

	public static void drawPauseText(final Canvas c, final float scale,
			final Context context, final long timeElapsed) {
		final Typeface font = Global.getPixelatedFont(context);
		PAUSE_PAINT.setTypeface(font);
		if (totalTimeElapsed < PAUSE_BLINK_TIME_INTERVAL) {
			final float realTextSize = PAUSE_TEXT_SIZE * scale;
			PAUSE_PAINT.setTextSize(realTextSize);
			final String text = context.getString(R.string.pause_text);
			c.drawText(text, x * scale, y * scale, PAUSE_PAINT);
		} else if (totalTimeElapsed > (PAUSE_BLINK_TIME_INTERVAL * 2)) {
			totalTimeElapsed = 0;
		}
		totalTimeElapsed += timeElapsed;
	}
}
