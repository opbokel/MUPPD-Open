package mtg.pong.engine;

import mtg.pong.Global;
import mtg.pong.ai.ArtificialInteligence;
import mtg.pong.ai.GradualAcelarationAI;
import mtg.pong.modifier.OnClickModifier;
import mtg.pong.ui.GameCanvas;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;

public class Paddle extends GameObject {

	public OnClickModifier onClickModifier;

	public float sensibility = 2.5f;

	public float lastEventX = 0;

	public long lastClickTime = 0;

	public boolean discardInput = false;

	public ArtificialInteligence ai = new GradualAcelarationAI();

	public static final float MAX_X_MODIFIER = Ball.MAX_X_DIR;

	public static final long TWO_CLICKS_MAX_INTERVAL = 400l;

	public CONTROL_TYPE controlType = CONTROL_TYPE.MOVE_EVENT;

	public float moveFactor = 0;

	public float movePosition = -1;

	public static enum CONTROL_TYPE {
		ONE_TO_ONE, MOVE_EVENT, CPU, FAKE,
	}

	public Paddle(final int player, final RectF box, final boolean inGame,
			final float minX, final float maxX) {
		super(player, box, inGame);
		this.paint = new Paint();
		this.paint.setColor(Color.WHITE);
		this.ai.player = this.player;
	}

	public void setControl(final int control) {
		switch (control) {
		case 0:
			this.controlType = CONTROL_TYPE.ONE_TO_ONE;
			break;
		case 1:
			this.controlType = CONTROL_TYPE.MOVE_EVENT;
			break;
		default:
			this.controlType = CONTROL_TYPE.CPU;
			this.ai.setDifficultLevel(control - 2);
		}

	}

	@Override
	public void draw(final Canvas canvas) {
		if (Global.leaveTrail) {
			if (this.controlType != CONTROL_TYPE.FAKE) {
				if (this.player == 1) {
					Global.cUtil.drawRect(canvas, 0, this.box.top,
							scale(GameCanvas.WIDTH), scale(GameCanvas.HEIGHT),
							GameCanvas.backgroundPaint);
				} else {
					Global.cUtil.drawRect(canvas, 0, 0,
							scale(GameCanvas.WIDTH), this.box.bottom,
							GameCanvas.backgroundPaint);
				}
			}
		}
		if (this.onClickModifier == null) {
			Global.cUtil.drawRect(canvas, this.box, this.paint);
		} else {
			Global.cUtil.drawRect(canvas, this.box, this.onClickModifier.paint);
		}

	}

	@Override
	public void evolve(final long timeInterval) {
		if (this.discardInput) {
			return;
		}
		switch (this.controlType) {
		case CPU:
			this.ai.evolve(timeInterval);
			move(timeInterval);
			break;
		case MOVE_EVENT:
			move((this.moveFactor), 0, false);
			this.moveFactor = 0;
		case ONE_TO_ONE:
			if (this.movePosition > 0) {
				setPosition(this.movePosition, this.box.top, false, true);
				this.movePosition = -1;
			}
		}

	}

	@Override
	public void reactColision(final GameObject gameObj) {
		if (gameObj instanceof Ball) {
			final Ball ball = (Ball) gameObj;
			if ((this.player == 1 && ball.yDir > 0)
					|| (this.player == 2 && ball.yDir < 0)) {
				final float colMod = getCollisionXModifier(ball);
				ball.setUnitaryDirection(ball.xDir + colMod, -ball.yDir
						* (1f - Math.abs(colMod / 2)));
				ball.player = this.player;
			}
		}

	}

	public float getCollisionXModifier(final Ball ball) {
		final Global.REGION region = getColisionRegion(ball);
		if (region == Global.REGION.LEFT) {
			return -MAX_X_MODIFIER;
		}
		if (region == Global.REGION.RIGHT) {
			return MAX_X_MODIFIER;
		}
		final float paddleCenter = this.box.centerX();
		final float ballCenter = ball.box.centerX();
		final float distance = ballCenter - paddleCenter;
		final float reflectPercentage = (distance / (((this.box.width()) / 2f) + (ball.box
				.width() / 2f)));
		return reflectPercentage * MAX_X_MODIFIER;
	}

	private boolean processTwoClicks(final MotionEvent event) {
		final long eventTime = event.getEventTime();
		final long elapsed = eventTime - this.lastClickTime;
		boolean result = false;
		if (elapsed != 0 && elapsed <= TWO_CLICKS_MAX_INTERVAL) {
			if (this.onClickModifier != null) {
				this.onClickModifier.onClick(this);
			}
			result = true;
		}
		this.lastClickTime = eventTime;
		return result;
	}

	public void processClick(final MotionEvent event, final int pointerIndex) {
		final int action = event.getActionMasked();
		if ((action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN)
				&& event.getActionIndex() == pointerIndex) {
			this.lastEventX = event.getX(pointerIndex);
		}
		if (Global.ONE_CLICK_MODIFIER) {
			if ((action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP)
					&& event.getActionIndex() == pointerIndex) {
				if (this.onClickModifier != null) {
					this.onClickModifier.onClick(this);
				}
			}
		} else if ((action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN)
				&& event.getActionIndex() == pointerIndex) {
			processTwoClicks(event);
		}

	}

	@Override
	public void processInput(final MotionEvent event, final int pointerIndex) {
		if (this.controlType.equals(CONTROL_TYPE.CPU)
				|| this.controlType.equals(CONTROL_TYPE.FAKE)
				|| this.discardInput) {
			return;
		}
		processClick(event, pointerIndex);

		final int action = event.getActionMasked();
		switch (this.controlType) {
		case MOVE_EVENT:
			if (action == MotionEvent.ACTION_MOVE) {
				final float eventX = event.getX(pointerIndex);
				final float dist = eventX - this.lastEventX;
				if (dist == 0) {
					return;
				}
				this.moveFactor += (dist * this.sensibility);
				// move((dist * this.sensibility), 0, false);
				this.lastEventX = eventX;
			}
			break;
		case ONE_TO_ONE:
			// esse codigo move imediatamente a palheta.
			final float eventX = event.getX(pointerIndex);
			final float paddleMiddleOffset = (this.box.right - this.box.left) / 2;
			// setPosition(eventX - paddleMiddleOffset, this.box.top, false);
			this.movePosition = eventX - paddleMiddleOffset;
			break;
		}

	}

}
