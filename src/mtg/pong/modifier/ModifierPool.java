package mtg.pong.modifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mtg.pong.engine.Ball;
import mtg.pong.engine.GOModifier;
import mtg.pong.engine.GameObject;
import android.graphics.RectF;

public class ModifierPool {

	public Random random;

	public GOModifier[] modPool;

	public int modPoolSize;

	public static int TIMER_INTERVAL = 5000;

	public static final float MODIFIER_SIZE = Ball.BALL_DEFAULT_SIZE * 2.0f;

	public long timer;

	private List<GameObject> tempObjects = new ArrayList<GameObject>();

	public ModifierPool() {
		this.random = new Random();
		this.timer = this.random.nextInt(TIMER_INTERVAL / 2);
	}

	public void addModifiers(final GameObject[] objects) {
		for (int n = 0; n < objects.length; n++) {
			final GameObject currentObj = objects[n];
			if (currentObj instanceof GOModifier) {
				if (currentObj.inGame) {
					this.tempObjects.add(currentObj);
				}
			}
		}
	}

	public void commit() {
		this.modPool = new GOModifier[this.tempObjects.size()];
		this.modPool = this.tempObjects.toArray(this.modPool);
		this.modPoolSize = this.modPool.length;
		this.tempObjects = null;
	}

	public void enableNextGOMOdifier(final long timeInterval) {
		if (this.modPoolSize == 0) {
			return;
		}
		this.timer += timeInterval;
		if (this.timer > TIMER_INTERVAL) {
			final int poolIndex = this.random.nextInt(this.modPoolSize);
			final GOModifier nextGOMod = this.modPool[poolIndex];
			if (!nextGOMod.modifier.enabled && !nextGOMod.colision) {
				final int percent = this.random.nextInt(100);
				if (percent < nextGOMod.modifier.chanse) {
					nextGOMod.colision = true;
				}
			}
			this.timer = this.random.nextInt(TIMER_INTERVAL / 2);
		}
	}

	public List<GameObject> getAllModifiers() {
		final List<GameObject> result = new ArrayList<GameObject>();
		RectF box;
		Modifier modifier;
		box = new RectF(0, 0, MODIFIER_SIZE, MODIFIER_SIZE);
		modifier = new BallSinMod();
		result.add(new GOModifier(0, box, true, modifier));
		box = new RectF(0, 0, MODIFIER_SIZE, MODIFIER_SIZE);
		modifier = new SolidBlockMod();
		result.add(new GOModifier(0, box, true, modifier));
		box = new RectF(0, 0, MODIFIER_SIZE, MODIFIER_SIZE);
		modifier = new SpeedMod();
		result.add(new GOModifier(0, box, true, modifier));
		box = new RectF(0, 0, MODIFIER_SIZE, MODIFIER_SIZE);
		modifier = new BallMultiplier();
		result.add(new GOModifier(0, box, true, modifier));
		box = new RectF(0, 0, MODIFIER_SIZE, MODIFIER_SIZE);
		modifier = new Laser();
		result.add(new GOModifier(0, box, true, modifier));
		box = new RectF(0, 0, MODIFIER_SIZE, MODIFIER_SIZE);
		modifier = new PaddleSize();
		result.add(new GOModifier(0, box, true, modifier));
		box = new RectF(0, 0, MODIFIER_SIZE, MODIFIER_SIZE);
		modifier = new InvisibleBall();
		result.add(new GOModifier(0, box, true, modifier));
		box = new RectF(0, 0, MODIFIER_SIZE, MODIFIER_SIZE);
		modifier = new BlockWall();
		result.add(new GOModifier(0, box, true, modifier));
		box = new RectF(0, 0, MODIFIER_SIZE, MODIFIER_SIZE);
		modifier = new ExtraPaddle();
		result.add(new GOModifier(0, box, true, modifier));
		box = new RectF(0, 0, MODIFIER_SIZE, MODIFIER_SIZE);
		modifier = new Trip();
		result.add(new GOModifier(0, box, true, modifier));
		box = new RectF(0, 0, MODIFIER_SIZE, MODIFIER_SIZE);
		modifier = new Immobilize();
		result.add(new GOModifier(0, box, true, modifier));
		box = new RectF(0, 0, MODIFIER_SIZE, MODIFIER_SIZE);
		modifier = new RemoteControl();
		result.add(new GOModifier(0, box, true, modifier));

		return result;
	}

}
