package mtg.pong.ai;

import mtg.pong.Global;
import mtg.pong.engine.Ball;
import mtg.pong.engine.GameObject;
import mtg.pong.engine.Paddle;

public class GradualAcelarationAI extends ArtificialInteligence {

	public int speedChangeInterval;

	public int speedChangeIntervalMin;

	public int speedChangeIntervalMax;

	public long speedChangeTarget;

	public int runWayInterval;

	public int runWayIntervalMin;

	public int runWayIntervalMax;

	public long runWayTarget;

	public int followInterval;

	public int followIntervalMin;

	public int followIntervalMax;

	public long followTarget;

	public int randomizeInterval;

	public long randomizeTarget;

	public float targetSpeed;

	public float increaseRatio;

	public boolean randomizeReactionTime;

	public boolean randomizePredicition;

	public boolean prediction;

	public boolean randomizeFollowAndRun;

	@Override
	public void initDefault() {
		super.initDefault();

		this.speedChangeInterval = 97;

		this.speedChangeIntervalMin = 50;

		this.speedChangeIntervalMax = 97;

		this.speedChangeTarget = 0;

		this.runWayInterval = 200;

		this.runWayIntervalMin = 50;

		this.runWayIntervalMax = 400;

		this.runWayTarget = 0;

		this.followInterval = 50;

		this.followIntervalMin = 50;

		this.followIntervalMax = 100;

		this.followTarget = 0;

		this.randomizeInterval = 4000;

		this.randomizeTarget = 0;

		this.targetSpeed = 0f;

		this.increaseRatio = 0f;

		this.randomizeReactionTime = false;

		this.randomizePredicition = false;

		this.prediction = false;

		this.randomizeFollowAndRun = false;
	}

	// Experimental
	private void randomizeParameters() {
		if (this.timeElapsed >= this.randomizeTarget) {
			this.randomizeTarget = this.timeElapsed + this.randomizeInterval;
			if (this.randomizeReactionTime) {
				this.speedChangeInterval = getNextRandom(
						this.speedChangeIntervalMin,
						this.speedChangeIntervalMax);
			}
			if (this.randomizeFollowAndRun) {
				this.runWayInterval = getNextRandom(this.runWayIntervalMin,
						this.runWayIntervalMax);
				this.followInterval = getNextRandom(this.followIntervalMin,
						this.followIntervalMax);
			}

			if (this.randomizePredicition) {
				this.prediction = 1 == this.random.nextInt(2);
			}
		}
	}

	private float getNextSpeed(final float maxPercentage,
			final GameObject goReference, final boolean runWay,
			final Paddle paddle) {
		float signum;
		if (runWay) {
			signum = Math.signum(paddle.box.centerX()
					- goReference.box.centerX());
			final boolean farFromEdge = paddle
					.canFitAnother(Global.REGION.LEFT)
					&& paddle.canFitAnother(Global.REGION.RIGHT);
			if (!farFromEdge) {
				signum = -signum;
			}
		} else {
			float xGORef;
			if (this.prediction && goReference instanceof Ball) {
				xGORef = ((Ball) goReference).predictionX;
			} else {
				xGORef = goReference.box.centerX();
			}
			if (paddle.inEdge) {
				signum = this.random.nextInt(2) == 1 ? -1 : 1;
			} else {
				signum = Math.signum(xGORef - paddle.box.centerX());
			}
		}
		return signum
				* (this.minSpeed + (this.random.nextFloat()
						* (this.maxSpeed - this.minSpeed) * maxPercentage));
	}

	private void setSpeedTarget(final float target, final float changeInterval,
			final Paddle paddle) {
		this.targetSpeed = target;
		this.increaseRatio = (this.targetSpeed - paddle.speed)
				/ this.speedChangeInterval;
	}

	@Override
	public void evolve(final long timeInterval) {
		super.evolve(timeInterval);
		final Paddle paddle = Global.paddles[this.player];
		final Ball ball = getClosestBall(true);
		paddle.xDir = 1;
		paddle.yDir = 0;
		if (ball != null) {
			if (this.timeElapsed >= this.speedChangeTarget) {
				this.speedChangeTarget = this.timeElapsed
						+ this.speedChangeInterval;
				setSpeedTarget(getNextSpeed(1f, ball, false, paddle),
						this.speedChangeInterval, paddle);
			}
		} else {
			final Paddle opositePaddle = Global.paddles[this.player == 2 ? 1
					: 2];
			if (opositePaddle.onClickModifier != null) {
				if (this.timeElapsed >= this.runWayTarget) {
					this.runWayTarget = this.timeElapsed + this.runWayInterval;
					setSpeedTarget(
							getNextSpeed(1f, opositePaddle, true, paddle),
							this.runWayInterval, paddle);
				}
			}
			if (paddle.onClickModifier != null) {
				if (this.timeElapsed >= this.followTarget) {
					this.followTarget = this.timeElapsed + this.followInterval;
					if (opositePaddle.onClickModifier == null) {
						setSpeedTarget(
								getNextSpeed(1f, opositePaddle, false, paddle),
								this.followInterval, paddle);
					}
				}
			}
			if (paddle.onClickModifier == null
					&& opositePaddle.onClickModifier == null) {
				setSpeedTarget(0f, this.speedChangeInterval, paddle);
			}
		}
		if (paddle.onClickModifier != null) {
			if (this.timeElapsed >= this.shootTarget) {
				this.shootTarget = this.timeElapsed + this.shootInterval;
				final int percentage = this.random.nextInt(100);
				if (percentage < this.shootChanse) {
					paddle.onClickModifier.onClick(paddle);
				}
			}
		}
		if (this.targetSpeed > 0 && paddle.speed > this.targetSpeed) {
			this.increaseRatio = 0;
		} else if (this.targetSpeed < 0 && paddle.speed < this.targetSpeed) {
			this.increaseRatio = 0;
		} else if (this.targetSpeed == 0
				&& Math.abs(paddle.speed) <= Math.abs(this.increaseRatio)) {
			this.increaseRatio = 0;
			paddle.speed = 0;
		}
		paddle.speed += this.increaseRatio * timeInterval;
		randomizeParameters();

	}

	@Override
	public void setDifficultLevel(final int difficultLevel) {
		super.setDifficultLevel(difficultLevel);
		initDefault();
		switch (difficultLevel) {
		case 0:
			this.randomizePredicition = false;
			this.prediction = false;
			this.randomizeReactionTime = true;
			break;
		case 1:
			this.randomizePredicition = true;
			this.prediction = true;
			this.randomizeReactionTime = true;
			break;
		case 2:
			this.randomizePredicition = false;
			this.prediction = true;
			this.randomizeReactionTime = true;
			break;
		case 3:
			this.randomizePredicition = false;
			this.prediction = true;
			this.randomizeReactionTime = true;
			this.speedChangeIntervalMax = 69;
			this.speedChangeIntervalMin = 30;
			break;
		}
	}
}
