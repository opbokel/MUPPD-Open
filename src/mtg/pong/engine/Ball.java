package mtg.pong.engine;

import java.util.Random;

import mtg.pong.Global;
import mtg.pong.ui.GameCanvas;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.FloatMath;
import android.view.MotionEvent;

public class Ball extends GameObject {

	public static final float MIN_SPEED = 0.03f;

	public static final float MAX_SPEED = 0.15f;

	public final static float MAX_X_DIR = 0.80f;

	public final static float INITAL_SPEED_PERCENTAGE = 0.5f;

	public static final float BALL_DEFAULT_SIZE = 3.0f;

	public final static float INCREASE_STEAP = 0.0000026f;

	public boolean drawPrediction = true;

	public final static float MIN_Y_DIR = FloatMath
			.sqrt((1 - (MAX_X_DIR * MAX_X_DIR)));

	public boolean original = true;

	public Random random = new Random();

	PointF predPoint = new PointF();

	public float predictionX;

	public float predictionY;

	public Ball(final RectF box, final boolean inGame) {
		super(0, box, inGame);
		this.paint = new Paint();
		this.paint.setColor(Color.WHITE);
		// TODO: paint padrao construtor
		this.originalPaint = new Paint();
		this.originalPaint.setColor(Color.WHITE);
		setRandomDirection();
		this.speed = MIN_SPEED;
		this.predictionX = this.box.centerX();
		this.predictionY = this.box.centerY();
	}

	public void setRandomDirection() {
		setUnitaryDirection((2f * this.random.nextFloat()) - 1f,
				(2f * this.random.nextFloat()) - 1f);
	}

	// TODO usar recursão
	public void predict(final Canvas canvas) {
		float x = this.box.centerX();
		float y = this.box.centerY();
		// Colisao passa tambem
		final float halfBallWidth = (this.box.width() / 2) * 0.9f;
		final float halfBallHeight = (this.box.height() / 2) * 0.9f;
		final float p1HitY = Global.paddles[1].box.top - halfBallHeight;
		final float p2HitY = Global.paddles[2].box.bottom + halfBallHeight;
		final float lBorderHitX = Global.leftBorder.box.right + (halfBallWidth);
		final float rBorderHitX = Global.rightBorder.box.left - (halfBallWidth);

		if (y > p1HitY || y < p2HitY) {
			return;
		}

		// Coeficiente angular de reta
		float a = this.yDir / this.xDir;

		if (a == Float.NaN || a == Float.NEGATIVE_INFINITY
				|| a == Float.POSITIVE_INFINITY) {
			if (this.yDir > 0) {
				if (Global.DEBUG) {
					canvas.drawLine(x, y, x, p1HitY, this.paint);
				}
				this.predictionX = x;
				this.predictionY = p1HitY;
			} else {
				if (Global.DEBUG) {
					canvas.drawLine(x, y, x, p2HitY, this.paint);
				}
				this.predictionX = x;
				this.predictionY = p2HitY;
			}
			return;
		}

		// y = ax + b
		float b = y - (a * x);

		float xFP;
		if (this.xDir < 0) {
			xFP = lBorderHitX;
		} else {
			xFP = rBorderHitX;
		}

		float yFP = b + a * xFP;

		if (yFP >= p1HitY || yFP <= p2HitY) {
			if (this.yDir > 0) {
				yFP = p1HitY;
			} else {
				yFP = p2HitY;
			}
			xFP = (yFP - b) / a;
			if (Global.DEBUG) {
				canvas.drawLine(x, y, xFP, yFP, this.paint);
			}
			this.predictionX = xFP;
			this.predictionY = yFP;
			return;
		}
		if (Global.DEBUG) {
			canvas.drawLine(x, y, xFP, yFP, this.paint);
		}
		x = xFP;
		y = yFP;
		a = -a;
		b = y - (a * x);
		if (this.yDir > 0) {
			yFP = p1HitY;
		} else {
			yFP = p2HitY;
		}
		xFP = (yFP - b) / a;
		if (xFP >= lBorderHitX && xFP <= rBorderHitX) {
			if (Global.DEBUG) {
				canvas.drawLine(x, y, xFP, yFP, this.paint);
			}
			this.predictionX = xFP;
			this.predictionY = yFP;
			return;
		}
		if (this.xDir < 0) {
			xFP = rBorderHitX;
		} else {
			xFP = lBorderHitX;
		}
		yFP = b + a * xFP;
		if (Global.DEBUG) {
			canvas.drawLine(x, y, xFP, yFP, this.paint);
		}
		x = xFP;
		y = yFP;
		a = -a;
		b = y - (a * x);
		if (this.yDir > 0) {
			yFP = p1HitY;
		} else {
			yFP = p2HitY;
		}
		xFP = (yFP - b) / a;
		if (Global.DEBUG) {
			canvas.drawLine(x, y, xFP, yFP, this.paint);
		}
		this.predictionX = xFP;
		this.predictionY = yFP;
	}

	@Override
	public void draw(final Canvas canvas) {
		Global.cUtil.drawRect(canvas, this.box, this.paint);
		predict(canvas);
	}

	@Override
	public void setUnitaryDirection(final float xDir, final float yDir) {
		super.setUnitaryDirection(xDir, yDir);
		boolean isMaxXDir = false;
		if (this.xDir > MAX_X_DIR) {
			this.xDir = MAX_X_DIR;
			isMaxXDir = true;
		} else if (this.xDir < -MAX_X_DIR) {
			this.xDir = -MAX_X_DIR;
			isMaxXDir = true;
		}
		if (isMaxXDir) {
			if (this.yDir > 0) {
				this.yDir = MIN_Y_DIR;
			} else if (this.yDir < 0) {
				this.yDir = -MIN_Y_DIR;
			}
		}

	}

	public void resetSpeed() {
		final float speedRange = MAX_SPEED - MIN_SPEED;
		this.speed = MIN_SPEED + (speedRange * this.random.nextFloat())
				* INITAL_SPEED_PERCENTAGE;
	}

	@Override
	// TODO: Garantir que não saia do espaço
	public void evolve(final long timeInterval) {
		move(timeInterval);
		if (this.box.top > scale(GameCanvas.HEIGHT)) {
			this.box.offsetTo(this.box.left, 0 - this.box.height());
			Global.scoreP2.incScore();
			if (!this.original) {
				this.inGame = false;
			} else {
				resetSpeed();
			}
		} else if (this.box.top < 0 - this.box.height()) {
			this.box.offsetTo(this.box.left, scale(GameCanvas.HEIGHT));
			Global.scoreP1.incScore();
			if (!this.original) {
				this.inGame = false;
			} else {
				resetSpeed();
			}
		}
		if (this.speed < MAX_SPEED) {
			this.speed += INCREASE_STEAP * timeInterval;
		} else {
			this.speed = MAX_SPEED;
		}
	}

	@Override
	public void reactColision(final GameObject gameObj) {
	}

	@Override
	public void processInput(final MotionEvent event, final int pointerIndex) {
		// TODO Auto-generated method stub

	}

}
