package mtg.pong.modifier;

import mtg.pong.Global;
import mtg.pong.Global.REGION;
import mtg.pong.engine.GameObject;
import mtg.pong.engine.Paddle;

public abstract class OnClickModifier extends Modifier {

	public int totalUsage = 1;

	public int usageCount = 0;

	@Override
	public void stop() {
		this.enabled = false;
		final Paddle paddle = Global.paddles[this.player];
		paddle.onClickModifier = null;
	}

	@Override
	public void apply(final GameObject ball, final REGION enterRegion) {
		final Paddle paddle = Global.paddles[this.player];
		if (paddle.onClickModifier != null) {
			paddle.onClickModifier.stop();
		}
		paddle.onClickModifier = this;
		this.usageCount = 0;
	}

	public abstract void onClick(GameObject goSource);

}
