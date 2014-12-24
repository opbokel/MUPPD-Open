package mtg.pong.modifier;

import mtg.pong.Global;
import mtg.pong.Global.REGION;
import mtg.pong.engine.GameObject;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class PaddleSize extends Modifier {

	protected float changePercentage = 0.55f;

	public PaddleSize() {
		this.id = Global.MODIFIER_ID.PADDLE_SIZE;
		this.timeSpan = 20 * 1000;
		this.paint = new Paint();
		this.paint.setColor(Color.GRAY);
		this.useDefaultLogoPaint();
		this.logoPaint.setColor(Color.WHITE);
		this.leftLogoOffset = -0.16f;
		// this.bottonLogoOffset = 0.12f;
		this.logoSize = 0.87f;
		this.logoString = "|^";
	}

	@Override
	public void stop() {
		Global.paddles[this.player].restoreOriginalBox(true);
	}

	@Override
	public void apply(final GameObject ball, final REGION enterRegion) {
		final RectF box = Global.paddles[this.player].box;
		box.right = box.right + (box.width() * this.changePercentage);
	}

	@Override
	public void draw(final Canvas canvas) {
		// TODO Auto-generated method stub

	}

}
