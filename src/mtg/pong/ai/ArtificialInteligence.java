package mtg.pong.ai;

import java.util.Random;

import mtg.pong.Global;
import mtg.pong.engine.Ball;

public abstract class ArtificialInteligence {

	public int player = 0;

	public long timeElapsed = 0;

	public Random random;

	public float minSpeed;

	public float maxSpeed;

	public int difficultLevel = 0;

	public int shootChanse;

	public long shootInterval;

	public long shootTarget;

	public void initDefault() {
		this.minSpeed = 0.0f;

		this.maxSpeed = 0.14f;

		this.shootChanse = 10;

		this.shootInterval = 180;

		this.shootTarget = 0;
	}

	public void setDifficultLevel(final int difficultLevel) {
		this.difficultLevel = difficultLevel;
	}

	public ArtificialInteligence() {
		this.initDefault();
		this.random = new Random();
	}

	public void evolve(final long timeInterval) {
		this.timeElapsed += timeInterval;
	}

	public int getNextRandom(final int min, final int max) {
		return min + this.random.nextInt(max - min);
	}

	protected Ball getClosestBall(final boolean approchingOnly) {
		Ball closestBall = null;
		float closestBallDist = Float.MAX_VALUE;
		for (int n = 0; n < Global.balls.length; n++) {
			final Ball ball = Global.balls[n];
			if (!ball.inGame) {
				continue;
			}
			float distance = Global.paddles[this.player].box.centerY()
					- ball.box.centerY();
			distance = Math.abs(distance);
			boolean approching = false;
			if (approchingOnly) {
				if (this.player == 1) {
					approching = Global.balls[n].yDir > 0;
				} else {
					approching = Global.balls[n].yDir < 0;
				}
			}
			if (approchingOnly && !approching) {
				continue;
			}
			if (distance < closestBallDist) {
				closestBall = ball;
				closestBallDist = distance;
			}
		}
		return closestBall;
	}

}
