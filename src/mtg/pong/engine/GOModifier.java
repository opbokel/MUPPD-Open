package mtg.pong.engine;

import java.util.Random;

import mtg.pong.modifier.Modifier;
import mtg.pong.ui.GameCanvas;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.MotionEvent;

public class GOModifier extends GameObject {

	public Modifier modifier;

	public static final int RANDOM_TIME_MAX = 3000;

	public static final float DIST_FROM_CENTER_MAX = 20f;

	public boolean destroyAfterColision = false;

	public Random random;

	public GOModifier(final int player, final RectF box, final boolean inGame,
			final Modifier modifier) {
		super(player, box, inGame);
		this.modifier = modifier;
		this.colision = false;
		this.random = new Random();
		getNextPosition();
	}

	@Override
	public void draw(final Canvas canvas) {
		if (this.modifier.enabled) {
			this.modifier.draw(canvas);
			return;
		}
		if (!this.colision) {
			return;
		}
		this.modifier.drawLogo(canvas, this.box);

	}

	public void getNextPosition() {
		float heightMiddleOffset = this.random.nextFloat()
				* ((DIST_FROM_CENTER_MAX) * 2f);
		heightMiddleOffset = heightMiddleOffset - DIST_FROM_CENTER_MAX
				- unScale(this.box.height() * 0.5f);
		final float y = (GameCanvas.HEIGHT * 0.5f) + heightMiddleOffset;
		final float widthOffset = this.random.nextFloat()
				* (GameCanvas.WIDTH - unScale(this.box.width()));
		setPosition(widthOffset, y, true, false);
		// TODO, medir distancia da bola e repetir se muito perto
	}

	@Override
	public void evolve(final long timeInterval) {
		if (this.modifier.enabled) {
			this.modifier.evolve(timeInterval);
			return;
		}
	}

	@Override
	public void reactColision(final GameObject gameObj) {
		if (gameObj instanceof Ball) {
			if (!((Ball) gameObj).original) {
				return;
			}
			this.colision = false;
			this.modifier.player = gameObj.player;
			this.modifier.start();
			this.modifier.apply(gameObj, getColisionRegion(gameObj));
			getNextPosition();
			if (this.destroyAfterColision) {
				this.inGame = false;
				this.colision = false;
			}
		}
	}

	@Override
	public void processInput(final MotionEvent event, final int pointerIndex) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getGOKey() {
		return this.modifier.getClass().getName();
	}

}
