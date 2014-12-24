package mtg.pong.engine;

import mtg.pong.Global;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;

public class Border extends GameObject {

	public enum POSITION {
		RIGHT, LEFT, MIDDLE;
	}

	POSITION position;

	public Border(final int player, final RectF box, final boolean inGame,
			final POSITION position) {
		super(player, box, inGame);
		this.paint = new Paint();
		this.paint.setColor(Color.WHITE);
		this.position = position;
	}

	@Override
	public void draw(final Canvas canvas) {
		Global.cUtil.drawRect(canvas, this.box, this.paint);
	}

	@Override
	public void evolve(final long timeInterval) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reactColision(final GameObject gameObj) {
		if (gameObj instanceof Ball) {
			final Ball ball = (Ball) gameObj;
			if (this.position == Border.POSITION.RIGHT) {
				if (ball.xDir > 0) {
					ball.xDir = -ball.xDir;
				}
			} else if (this.position == Border.POSITION.LEFT) {
				if (ball.xDir < 0) {
					ball.xDir = -ball.xDir;
				}
			}
		}

	}

	@Override
	public void processInput(final MotionEvent event, final int pointerIndex) {
		// TODO Auto-generated method stub

	}

}
