package mtg.pong.engine;

import android.graphics.Paint;

public class LaserShot extends Shot {

	public LaserShot(final boolean inGame, final Paint paint) {
		super(inGame, paint);
	}

	@Override
	public void reactColision(final GameObject gameObj) {
		if (this.player == gameObj.player) {
			return;
		}
		float dist = this.box.centerX() - gameObj.box.centerX();
		if (dist > 0) {
			dist = gameObj.box.right - this.box.centerX();
			gameObj.box.set(gameObj.box.left, gameObj.box.top,
					gameObj.box.right - dist, gameObj.box.bottom);
		} else {
			dist = gameObj.box.left - this.box.centerX();
			gameObj.box.set(gameObj.box.left - dist, gameObj.box.top,
					gameObj.box.right, gameObj.box.bottom);
		}

	}

}
