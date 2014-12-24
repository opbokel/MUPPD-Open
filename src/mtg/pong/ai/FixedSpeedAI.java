package mtg.pong.ai;

import mtg.pong.Global;
import mtg.pong.engine.Ball;
import mtg.pong.engine.Paddle;


public class FixedSpeedAI extends ArtificialInteligence {

	protected long speedChangeInterval = 100;

	protected long targetSpeedChange = this.speedChangeInterval;

	@Override
	public void evolve(final long timeInterval) {
		super.evolve(timeInterval);
		if (this.timeElapsed >= this.targetSpeedChange) {
			final Ball ball = getClosestBall(true);
			final Paddle paddle = Global.paddles[this.player];
			if (ball != null) {
				this.targetSpeedChange += this.speedChangeInterval;
				paddle.speed = this.random.nextFloat() * this.maxSpeed;
				final float signum = Math.signum(ball.box.centerX()
						- paddle.box.centerX());
				paddle.xDir = signum;
			} else {
				// Faz nada por emquantoj
			}

		}

	}

}
