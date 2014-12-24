package mtg.pong.modifier;

import java.util.Random;

import mtg.pong.Global;
import mtg.pong.Global.REGION;
import mtg.pong.engine.Ball;
import mtg.pong.engine.GameObject;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class BallMultiplier extends Modifier {

	public static final int EXTRA_BALLS_COUNT = 2;

	public Random random = new Random();

	public Ball[] balls;

	public BallMultiplier() {
		this.id = Global.MODIFIER_ID.BALL_MULTIPLIER;
		this.balls = new Ball[EXTRA_BALLS_COUNT];
		for (int n = 0; n < EXTRA_BALLS_COUNT; n++) {
			final RectF box = new RectF(0, 0, Ball.BALL_DEFAULT_SIZE,
					Ball.BALL_DEFAULT_SIZE);
			final Ball ball = new Ball(box, false);
			ball.original = false;
			ball.paint.setColor(Color.YELLOW);
			this.paint = new Paint();
			this.paint.setColor(Color.YELLOW);
			// TODO: Para poder usar os modifiers para montar o menu, refatorar!
			if (Global.gEngine != null) {
				Global.gEngine.addGameObject(ball);
				Global.balls[n + 1] = ball;
			}
			this.balls[n] = ball;
		}
		this.useDefaultLogoPaint();
		this.leftLogoOffset = -0.02f;
		this.bottonLogoOffset = 0.18f;
		this.logoSize = 0.83f;
		this.logoString = "+2";
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void checkEnable(final long timeInterval) {
		for (int n = 0; n < EXTRA_BALLS_COUNT; n++) {
			if (this.balls[n].inGame) {
				this.enabled = true;
				return;
			}
		}
		this.enabled = false;
		stop();
	}

	@Override
	public void apply(final GameObject ball, final REGION enterRegion) {
		for (int n = 0; n < EXTRA_BALLS_COUNT; n++) {
			final Ball newBall = this.balls[n];
			newBall.box.set(ball.box);
			if (n % 2 == 0) {
				newBall.setRandomDirection();
			} else {
				newBall.yDir = -this.balls[n - 1].yDir;
				newBall.xDir = -this.balls[n - 1].xDir;
			}
			newBall.speed = Ball.MIN_SPEED;
			newBall.inGame = true;
		}

	}

	@Override
	public void draw(final Canvas canvas) {
		// TODO Auto-generated method stub

	}

}
