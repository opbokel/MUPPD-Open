package mtg.pong.modifier;

import java.util.Random;

import mtg.pong.Global;
import mtg.pong.Global.REGION;
import mtg.pong.engine.Ball;
import mtg.pong.engine.GameObject;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class SpeedMod extends Modifier {

	public static final float MAX_SPEED_CHANGE = 0.08f;

	public static final float MIN_SPEED_CHANGE = 0.02f;

	public Random random;

	public SpeedMod() {
		this.id = Global.MODIFIER_ID.SPEED_MOD;
		this.paint = new Paint();
		this.paint.setColor(Color.CYAN);
		this.random = new Random();
		this.useDefaultLogoPaint();
		this.leftLogoOffset = 0.00f;
		this.bottonLogoOffset = 0.1f;
		this.logoSize = 0.87f;
		this.logoString = "->";
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void apply(final GameObject ball, final REGION enterRegion) {
		float speedChange = MIN_SPEED_CHANGE
				+ (this.random.nextFloat() * (MAX_SPEED_CHANGE - MIN_SPEED_CHANGE));
		final int multFactor = (this.random.nextInt(2) == 1) ? -1 : 1;
		speedChange = speedChange * multFactor;
		ball.speed += speedChange;
		if (ball.speed > Ball.MAX_SPEED) {
			ball.speed = Ball.MAX_SPEED - Math.abs(speedChange);
			ball.speed = Math.max(ball.speed, Ball.MIN_SPEED);
		} else if (ball.speed < Ball.MIN_SPEED) {
			ball.speed = Ball.MIN_SPEED + Math.abs(speedChange);
			ball.speed = Math.min(ball.speed, Ball.MAX_SPEED);
		}
	}

	@Override
	public void draw(final Canvas canvas) {
		// TODO Auto-generated method stub

	}

}
