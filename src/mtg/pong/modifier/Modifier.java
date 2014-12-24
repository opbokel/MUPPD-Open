package mtg.pong.modifier;

import mtg.pong.Global;
import mtg.pong.engine.GameObject;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public abstract class Modifier {

	public boolean enabled = false;

	public long timeSpan = 0;

	public long killTime;

	public long timeElapsed = 0;

	public String logoString = null;

	public Paint logoPaint = null;

	public float logoSize = 1.0f;

	public float leftLogoOffset = 0.2f;

	public float bottonLogoOffset = 0.065f;

	/**
	 * Must be setted
	 */
	public Paint paint;

	public int player;

	public int chanse = 75;

	public Global.MODIFIER_ID id = Global.MODIFIER_ID.DEFAULT_MOD_ID;

	public void start() {
		this.enabled = true;
		this.timeElapsed = 0;
		this.killTime = this.timeSpan;
	}

	public void useDefaultLogoPaint() {
		this.logoPaint = new Paint();
		final int mColor = this.paint.getColor();
		this.logoPaint.setColor(Color.rgb(255 - Color.red(mColor),
				255 - Color.green(mColor), 255 - Color.blue(mColor)));
		this.logoPaint.setTypeface(Global.getPixelatedFont(null));
	}

	public abstract void stop();

	public void evolve(final long timeInterval) {
		this.timeElapsed += timeInterval;
		this.killTime -= timeInterval;
		checkEnable(timeInterval);
	}

	public void drawLogo(final Canvas canvas, final RectF box) {
		if (this.paint != null) {
			Global.cUtil.drawRect(canvas, box, this.paint);
		}
		if (this.logoString != null) {
			final float size = (box.bottom - box.top) * this.logoSize;
			this.logoPaint.setTextSize(size);
			Global.cUtil.drawText(canvas, this.logoString, box.left
					+ (this.leftLogoOffset * size), box.bottom
					- (this.bottonLogoOffset * size), this.logoPaint);
		}
	}

	protected void checkEnable(final long timeInterval) {
		this.enabled = this.killTime >= 0;
		if (!this.enabled) {
			stop();
		}
	}

	public abstract void apply(GameObject ball, Global.REGION enterRegion);

	public abstract void draw(final Canvas canvas);

}
