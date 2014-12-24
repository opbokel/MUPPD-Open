package mtg.pong.modifier;

import mtg.pong.Global;
import mtg.pong.Global.REGION;
import mtg.pong.engine.GameObject;
import mtg.pong.ui.GameCanvas;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class InvisibleBall extends Modifier {

	private int scoreP1;

	private int scoreP2;

	private int p1Difficult;

	private int p2Difficult;

	private int originalBallColor;

	private int targetlBallColor;

	private final int transitionTime = 1000;

	private int colorToAdd;

	public InvisibleBall() {
		this.id = Global.MODIFIER_ID.INVISIBLE_BALL;
		this.timeSpan = 3000;
		this.paint = new Paint();
		this.paint.setColor(Color.MAGENTA);
		this.useDefaultLogoPaint();
		this.leftLogoOffset = -0.01f;
		this.bottonLogoOffset = -0.1f;
		this.logoSize = 1.8f;
		this.logoString = "\u25A1";
	}

	@Override
	public void stop() {
		Global.ball.paint.setColor(this.originalBallColor);
		Global.p1Paddle.ai.setDifficultLevel(this.p1Difficult);
		Global.p2Paddle.ai.setDifficultLevel(this.p2Difficult);

	}

	@Override
	public void apply(final GameObject ball, final REGION enterRegion) {
		this.scoreP1 = Global.scoreP1.score;
		this.scoreP2 = Global.scoreP2.score;
		this.p1Difficult = Global.p1Paddle.ai.difficultLevel;
		this.p2Difficult = Global.p2Paddle.ai.difficultLevel;
		Global.p1Paddle.ai.setDifficultLevel(0);
		Global.p2Paddle.ai.setDifficultLevel(0);
		this.originalBallColor = Global.ball.originalPaint.getColor();
		this.targetlBallColor = GameCanvas.backgroundPaint.getColor();
		this.colorToAdd = (this.targetlBallColor - this.originalBallColor)
				/ this.transitionTime;
	}

	@Override
	protected void checkEnable(final long timeInterval) {
		super.checkEnable(timeInterval);
		if (!this.enabled) {
			return;
		}
		if (this.scoreP1 != Global.scoreP1.score
				|| this.scoreP2 != Global.scoreP2.score) {
			this.enabled = false;
			stop();
		}
	}

	@Override
	public void evolve(final long timeInterval) {
		super.evolve(timeInterval);
		if (this.enabled) {
			final int ballColor = Global.ball.paint.getColor();
			if (this.timeElapsed <= this.transitionTime) {
				Global.ball.paint.setColor(ballColor
						+ (int) (this.colorToAdd * timeInterval));
			} else {
				Global.ball.paint.setColor(this.targetlBallColor);
			}
		}
	}

	@Override
	public void draw(final Canvas canvas) {
		// TODO Auto-generated method stub

	}

}
