package mtg.pong.modifier;

import mtg.pong.Global;
import mtg.pong.engine.GameObject;
import mtg.pong.engine.LaserShot;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Laser extends OnClickModifier {

	final LaserShot shot;

	public Laser() {
		this.id = Global.MODIFIER_ID.LASER;
		this.timeSpan = 20 * 1000;
		this.paint = new Paint();
		this.paint.setColor(Color.RED);
		this.shot = new LaserShot(false, this.paint);
		// TODO: Para poder usar os modifiers para montar o menu, refatorar!
		if (Global.gEngine != null) {
			Global.gEngine.addGameObject(this.shot);
		}
		this.totalUsage = 10;

		this.useDefaultLogoPaint();
		this.leftLogoOffset = -0.16f;
		this.bottonLogoOffset = 0.12f;
		this.logoSize = 0.87f;
		this.logoString = "|-";
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
	}

	@Override
	public void stop() {
		super.stop();
		Global.gEngine.restoreOriginalBoxes();
	}
}
