package mtg.pong.engine;

import mtg.pong.Global;
import mtg.pong.ui.GameCanvas;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.FloatMath;
import android.view.MotionEvent;

public abstract class GameObject {

	public float xDir = 0;

	public float yDir = 0;

	public float speed = 0;

	public int order = 0;

	public Paint paint;

	public int player;

	public RectF box;

	public RectF originalBox;

	public Paint originalPaint;

	public RectF tempBox;

	public RectF parseBox = new RectF();

	public boolean colision = true;

	public boolean inGame;

	public boolean inEdge = false;

	public abstract void draw(final Canvas canvas);

	public abstract void evolve(final long timeInterval);

	public abstract void reactColision(final GameObject gameObj);

	public abstract void processInput(MotionEvent event, int pointerIndex);

	public float maxX;

	public float minX;

	public GameObject(final int player, final RectF box, final boolean inGame) {
		this.player = player;
		setBox(box, true);
		this.inGame = inGame;
		this.maxX = scale(GameCanvas.WIDTH);
		this.minX = scale(0f);
	}

	public void setBox(final RectF box, final boolean scale) {
		box.bottom = scale(box.bottom);
		box.left = scale(box.left);
		box.right = scale(box.right);
		box.top = scale(box.top);
		this.box = box;
		this.tempBox = new RectF(box);
		this.originalBox = new RectF(box);
	}

	public void restoreOriginalBox(final boolean sizeOnly) {
		final float oldLeft = this.box.left;
		final float oldTop = this.box.top;
		this.box.set(this.originalBox);
		if (sizeOnly) {
			this.box.offsetTo(oldLeft, oldTop);
		}
	}

	public Global.REGION getColisionRegion(final GameObject gObjEnteredRegion) {
		final RectF box1 = this.box;
		final RectF box2 = gObjEnteredRegion.box;
		this.tempBox.setIntersect(box1, box2);
		if (this.tempBox.width() > this.tempBox.height()) {
			if (this.tempBox.top == box1.top) {
				return Global.REGION.TOP;
			} else {
				return Global.REGION.BOTTOM;
			}
		} else {
			if (this.tempBox.left == box1.left) {
				return Global.REGION.LEFT;
			} else {
				return Global.REGION.RIGHT;
			}
		}
	}

	public void move(final float dx, final float dy, final boolean scale) {
		if (scale) {
			this.box.offset(scale(dx), scale(dy));
		} else {
			this.box.offset(dx, dy);
		}
		ajustToInsideField();
	}

	public void ajustToInsideField() {
		if (this.box.left < this.minX) {
			this.box.offsetTo(this.minX, this.box.top);
			this.inEdge = true;
			return;
		} else if (this.box.right > this.maxX) {
			this.box.offsetTo(this.maxX - this.box.width(), this.box.top);
			this.inEdge = true;
			return;
		}
		this.inEdge = false;
	}

	public boolean canFitAnother(final Global.REGION region) {
		switch (region) {
		case TOP:
			return this.box.top - this.box.height() > 0;
		case BOTTOM:
			return this.box.bottom + this.box.height() < GameCanvas.HEIGHT;
		case LEFT:
			return this.box.left - this.box.width() > this.minX;
		case RIGHT:
			return this.box.right + this.box.width() < this.maxX;
		default:
			return true;

		}
	}

	public boolean outOfCanvas(final boolean removeFromGame) {
		boolean result = false;
		if (this.box.top > scale(GameCanvas.HEIGHT)
				|| (this.box.top < -this.box.height())) {
			result = true;
		}
		if (result && removeFromGame) {
			this.inGame = false;
		}
		return result;
	}

	/**
	 * Move utilizando a velocidade e direção pré definados
	 */
	public void move(final long timeInterval) {
		move(this.xDir * this.speed * timeInterval, this.yDir * this.speed
				* timeInterval, true);
	}

	public void setMoveAngle(final float angle) {
		this.xDir = FloatMath.cos(angle);
		this.yDir = FloatMath.sin(angle);
	}

	public void setPosition(final float x, final float y, final boolean scale,
			final boolean ajustToInsideField) {
		if (scale) {
			this.box.offsetTo(scale(x), scale(y));
		} else {
			this.box.offsetTo(x, y);
		}
		if (ajustToInsideField) {
			ajustToInsideField();
		}
	}

	public void setUnitaryDirection(final float xDir, final float yDir) {
		final float vetMod = FloatMath.sqrt((xDir * xDir) + (yDir * yDir));
		this.xDir = xDir / vetMod;
		this.yDir = yDir / vetMod;
	}

	public void move(final float angle, final long timeInterval) {
		setMoveAngle(angle);
		move(timeInterval);
	}

	public float scale(final float number) {
		return number * GameCanvas.SCALE;
	}

	public float unScale(final float number) {
		return number / GameCanvas.SCALE;
	}

	public boolean checkColision(final GameObject gameObj, final boolean react) {
		if (gameObj == null || !gameObj.inGame || !this.inGame) {
			return false;
		}
		final boolean result = RectF.intersects(gameObj.box, this.box);

		if (result && react) {
			reactColision(gameObj);
		}

		return result;
	}

	public String getGOKey() {
		return this.getClass().getName();
	}

}
