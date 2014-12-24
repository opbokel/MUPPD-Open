package mtg.pong.ai;

import mtg.pong.Global;
import mtg.pong.engine.Paddle;


public class ImpossibleAI extends ArtificialInteligence {

	@Override
	public void evolve(final long timeInterval) {
		final float ballX = Global.ball.box.centerX();
		final Paddle paddle = Global.paddles[this.player];
		paddle.speed = 0;
		paddle.box.offsetTo(ballX - (paddle.box.width() / 2f), paddle.box.top);
	}
}
