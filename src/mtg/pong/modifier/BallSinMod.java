package mtg.pong.modifier;

import mtg.pong.Global;
import mtg.pong.engine.GameObject;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.FloatMath;

public class BallSinMod extends Modifier {

	public static final long PERIOD = 1000;

	public static final float MAX_VAL = 10;

	public float lastDistortion = 0;

	public BallSinMod() {
		this.id = Global.MODIFIER_ID.BALL_SIN;
		this.timeSpan = 4000;
		this.paint = new Paint();
		this.paint.setColor(Color.GREEN);
		this.useDefaultLogoPaint();
		this.leftLogoOffset = 0.03f;
		this.bottonLogoOffset = -0.07f;
		this.logoSize = 1.45f;
		this.logoString = "~";
	}

	@Override
	public void draw(final Canvas canvas) {
		// TODO Auto-generated method stub
	}

	@Override
	public void apply(final GameObject object, final Global.REGION enterRegion) {

	}

	@Override
	public void evolve(final long timeInterval) {
		super.evolve(timeInterval);
		if (this.enabled) {
			final float angle = ((this.timeElapsed % PERIOD) / (float) PERIOD)
					* 2f * (float) Math.PI;
			final float distortion = FloatMath.sin(angle) * MAX_VAL;
			Global.ball.move(distortion - this.lastDistortion, 0, true);
			this.lastDistortion = distortion;
		}
	}

	@Override
	public void stop() {
		this.lastDistortion = 0;

	}
}
