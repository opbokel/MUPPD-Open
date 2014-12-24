package mtg.pong.modifier;

import mtg.pong.Global;
import mtg.pong.engine.GameObject;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class SolidBlockMod extends Modifier {

	private static float lastBallX = 0;

	private static float lastBallY = 0;

	public SolidBlockMod() {
		this.id = Global.MODIFIER_ID.SOLID_BLOCK;
		this.timeSpan = 0;
		this.paint = new Paint();
		this.paint.setColor(Color.BLUE);
		this.useDefaultLogoPaint();
		this.leftLogoOffset = -0.01f;
		this.bottonLogoOffset = -0.1f;
		this.logoSize = 1.8f;
		this.logoString = "\u25A3";
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void apply(final GameObject ball, final Global.REGION enterRegion) {
		if (isLastBall(ball)) {
			return;
		}
		switch (enterRegion) {
		case BOTTOM:
		case TOP:
			ball.yDir = -ball.yDir;
			break;
		case LEFT:
		case RIGHT:
			ball.xDir = -ball.xDir;
			break;
		}
		lastBallX = ball.box.centerX();
		lastBallY = ball.box.centerY();
	}

	private boolean isLastBall(final GameObject ball) {
		return lastBallX == ball.box.centerX()
				&& lastBallY == ball.box.centerY();
	}

	@Override
	public void draw(final Canvas canvas) {
		// TODO Auto-generated method stub

	}

}
