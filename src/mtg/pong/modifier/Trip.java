package mtg.pong.modifier;

import mtg.pong.Global;
import mtg.pong.Global.REGION;
import mtg.pong.engine.GameObject;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Trip extends Modifier {

	public Trip() {
		this.id = Global.MODIFIER_ID.TRIP;
		this.timeSpan = 8 * 1000;
		this.paint = new Paint();
		this.paint.setColor(Color.rgb(90, 150, 122));
		this.useDefaultLogoPaint();
		final int lColor = this.logoPaint.getColor();
		this.logoPaint.setColor(Color.rgb(255, Color.green(lColor),
				Color.blue(lColor)));
		this.logoString = "T";
	}

	@Override
	public void stop() {
		Global.leaveTrail = false;
	}

	@Override
	public void apply(final GameObject ball, final REGION enterRegion) {
		Global.leaveTrail = true;

	}

	@Override
	public void draw(final Canvas canvas) {
		// TODO Auto-generated method stub

	}

}
