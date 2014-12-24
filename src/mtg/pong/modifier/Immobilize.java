package mtg.pong.modifier;

import mtg.pong.Global;
import mtg.pong.engine.GameObject;
import mtg.pong.engine.Paddle;
import mtg.pong.engine.Shot;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Immobilize extends OnClickModifier {

	final Shot shot;

	public static final int HOLD_TIME = 1 * 1000;

	private int holdTimeRemaining = 0;

	public Immobilize() {
		this.id = Global.MODIFIER_ID.IMMOBILIZE;
		this.timeSpan = 20 * 1000;
		this.paint = new Paint();
		this.paint.setColor(Color.rgb(123, 22, 200));
		this.shot = new Shot(false, this.paint) {

			@Override
			public void reactColision(final GameObject gameObj) {
				if (this.player == gameObj.player) {
					return;
				}
				if (gameObj instanceof Paddle) {
					this.inGame = false;
					Immobilize.this.holdTimeRemaining = Immobilize.HOLD_TIME;
					Global.paddles[gameObj.player].discardInput = true;
				}

			}

		};
		// TODO: Para poder usar os modifiers para montar o menu, refatorar!
		if (Global.gEngine != null) {
			Global.gEngine.addGameObject(this.shot);
		}
		this.totalUsage = 5;

		this.useDefaultLogoPaint();
		this.logoString = "X";
	}

	@Override
	public void evolve(final long timeInterval) {
		super.evolve(timeInterval);
		if (this.enabled) {
			final int opPaddleId = this.player == 1 ? 2 : 1;
			final Paddle opPadle = Global.paddles[opPaddleId];
			if (this.holdTimeRemaining > 0) {
				opPadle.discardInput = true;
				this.holdTimeRemaining -= timeInterval;
			} else {
				opPadle.discardInput = false;
			}
		}
	}

	@Override
	public void stop() {
		super.stop();
		final int opPaddleId = this.player == 1 ? 2 : 1;
		final Paddle opPadle = Global.paddles[opPaddleId];
		opPadle.discardInput = false;
	}

	@Override
	protected void checkEnable(final long timeElapsed) {
		if (this.shot.inGame) {
			return;
		}
		super.checkEnable(timeElapsed);
	}

	@Override
	public void draw(final Canvas canvas) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(final GameObject goSource) {
		if (this.shot.inGame || this.usageCount >= this.totalUsage) {
			return;
		}
		this.usageCount++;
		final float left = goSource.box.centerX() - (this.shot.box.width() / 2);
		this.shot.box.offsetTo(left, goSource.box.top);
		if (goSource.player % 2 > 0) {
			this.shot.yDir = -1f;
		} else {
			this.shot.yDir = 1f;
		}
		this.shot.player = goSource.player;
		this.shot.inGame = true;
		if (this.usageCount >= this.totalUsage) {
			this.killTime = (int) (HOLD_TIME * 2.5);
		}
	}

}
