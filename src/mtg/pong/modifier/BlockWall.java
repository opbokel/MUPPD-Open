package mtg.pong.modifier;

import mtg.pong.Global;
import mtg.pong.Global.REGION;
import mtg.pong.engine.GOModifier;
import mtg.pong.engine.GameObject;
import mtg.pong.ui.GameCanvas;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class BlockWall extends Modifier {

	public GOModifier[] wall;

	public static final float BLOCK_SIZE = ModifierPool.MODIFIER_SIZE;

	int color1 = Color.rgb(212, 14, 80);

	int color2 = Color.rgb(212, 100, 14);

	public BlockWall() {
		this.id = Global.MODIFIER_ID.BLOCK_WALL;
		this.paint = new Paint();
		this.paint.setColor(this.color2);
		this.wall = new GOModifier[Math.round(GameCanvas.WIDTH / BLOCK_SIZE)];
		for (int n = 0; n < this.wall.length; n++) {
			final Modifier sbm = new SolidBlockMod();
			// if (n % 2 == 0) {
			// sbm.paint.setColor(this.color2);
			// } else {
			// sbm.paint.setColor(this.color1);
			// }
			sbm.useDefaultLogoPaint();

			final RectF box = new RectF(0, 0, BLOCK_SIZE, BLOCK_SIZE);
			this.wall[n] = new GOModifier(0, box, false, sbm);
			this.wall[n].destroyAfterColision = true;
			if (Global.gEngine != null) {
				Global.gEngine.addGameObject(this.wall[n]);
			}
		}
		this.useDefaultLogoPaint();
		this.leftLogoOffset = 0.03f;
		this.bottonLogoOffset = -0.04f;
		this.logoSize = 1.45f;
		this.logoString = "\u2509";
	}

	@Override
	public void stop() {
		for (int n = 0; n < this.wall.length; n++) {
			this.wall[n].inGame = false;
			this.wall[n].colision = false;
		}

	}

	@Override
	protected void checkEnable(final long timeInterval) {
		for (int n = 0; n < this.wall.length; n++) {
			final boolean inGame = this.wall[n].inGame;
			if (inGame) {
				return;
			}
		}
		this.enabled = false;
		stop();
	}

	@Override
	public void apply(final GameObject ball, final REGION enterRegion) {
		float yTop;
		final float wallWitdh = this.wall[0].box.width();
		if (ball.yDir > 0) {
			yTop = ball.box.centerY() - (wallWitdh * 2);
		} else {
			yTop = ball.box.centerY() + wallWitdh;
		}

		int x = 0;
		for (int n = 0; n < this.wall.length; n++) {
			this.wall[n].setPosition(x, yTop, false, false);
			this.wall[n].inGame = true;
			this.wall[n].colision = true;
			x += wallWitdh;
		}

	}

	@Override
	public void draw(final Canvas canvas) {
		// TODO Auto-generated method stub

	}

}
