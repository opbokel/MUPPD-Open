package mtg.pong.modifier;

import mtg.pong.Global;
import mtg.pong.engine.Ball;
import mtg.pong.engine.GameObject;
import mtg.pong.engine.Paddle;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class RemoteControl extends OnClickModifier {

	public RemoteControl() {
		this.id = Global.MODIFIER_ID.REMOTE_CONTROL;
		this.paint = new Paint();
		this.paint.setColor(Color.rgb(180, 110, 100));
		this.timeSpan = 4 * 1000;
		this.useDefaultLogoPaint();
		this.logoString = "R";
	}

	@Override
	public void evolve(final long timeInterval) {
		super.evolve(timeInterval);
		if (this.enabled) {
			final Paddle paddle = Global.paddles[this.player];
			final Ball ball = Global.ball;
			final float speedDirection = paddle.box.centerX()
					- ball.box.centerX();
			if (speedDirection > 0) {
				ball.setUnitaryDirection(0.7f, ball.yDir);
			} else {
				ball.setUnitaryDirection(-0.7f, ball.yDir);
			}
		}
	}

	@Override
	public void draw(final Canvas canvas) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(final GameObject goSource) {
		// TODO Auto-generated method stub

	}

}
