package mtg.pong.engine;

import mtg.pong.Global;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;

public abstract class Shot extends GameObject {

	public static final float SHOT_HEIGHT = 3.0f;

	public static final float SHOT_WIDTH = SHOT_HEIGHT * 0.33f;

	public Shot(final boolean inGame, final Paint paint) {
		super(0, new RectF(0f, 0f, SHOT_WIDTH, SHOT_HEIGHT), inGame);
		this.paint = paint;
		this.speed = Ball.MAX_SPEED * 1.5f;
		this.xDir = 0f;
	}

	@Override
	public void draw(final Canvas canvas) {
		Global.cUtil.drawRect(canvas, this.box, this.paint);
	}

	@Override
	public void evolve(final long timeInterval) {
		move(timeInterval);
		outOfCanvas(true);
	}

	@Override
	public abstract void reactColision(final GameObject gameObj);

	@Override
	public void processInput(final MotionEvent event, final int pointerIndex) {
		// TODO Auto-generated method stub

	}

}
