package mtg.pong.modifier;

import mtg.pong.Global;
import mtg.pong.Global.REGION;
import mtg.pong.engine.GameObject;
import mtg.pong.engine.Paddle;
import mtg.pong.ui.GameCanvas;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class ExtraPaddle extends Modifier {

	Paddle extraPaddle;

	public ExtraPaddle() {
		this.id = Global.MODIFIER_ID.EXTRA_PADDLE;
		this.paint = new Paint();
		this.paint.setColor(Color.WHITE);
		this.timeSpan = 10 * 1000;
		// The position is irrelevant, only the size
		final RectF box = new RectF(25f, 87f, 35f, 90f);
		this.extraPaddle = new Paddle(0, box, false, 0f, GameCanvas.WIDTH);
		this.extraPaddle.controlType = Paddle.CONTROL_TYPE.FAKE;
		if (Global.gEngine != null) {
			Global.gEngine.addGameObject(this.extraPaddle);
		}
		this.useDefaultLogoPaint();
		this.leftLogoOffset = -0.05f;
		this.logoSize = 0.87f;
		this.logoString = "||";
	}

	@Override
	public void stop() {
		this.extraPaddle.inGame = false;
	}

	@Override
	public void apply(final GameObject ball, final REGION enterRegion) {
		this.extraPaddle.player = ball.player;
		this.extraPaddle.inGame = true;

		this.extraPaddle.box.set(Global.paddles[this.extraPaddle.player].box);

		final float paddleHeight = this.extraPaddle.box.height();

		this.extraPaddle.box.top = ball.box.top;
		this.extraPaddle.box.bottom = this.extraPaddle.box.top + paddleHeight;

	}

	@Override
	public void evolve(final long timeInterval) {
		super.evolve(timeInterval);
		if (this.enabled) {
			this.extraPaddle.box.left = Global.paddles[this.extraPaddle.player].box.left;
			this.extraPaddle.box.right = Global.paddles[this.extraPaddle.player].box.right;
		}
	}

	@Override
	public void draw(final Canvas canvas) {
		// TODO Auto-generated method stub

	}

}
