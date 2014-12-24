package mtg.pong.engine;

import java.util.ArrayList;
import java.util.List;

import mtg.pong.Global;
import mtg.pong.MainActivity;
import mtg.pong.menu.ModifierMenuActivity;
import mtg.pong.modifier.BallMultiplier;
import mtg.pong.modifier.ModifierPool;
import mtg.pong.ui.GameCanvas;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.view.MotionEvent;

//TODO: criar interface!
public class GameEngine {

	public EngineThread thread;

	public GameObject[] objects;

	public int objectsCount = 0;

	public GameObject[] colisionObjects;

	public int colisionObjectsCount = 0;

	public long totalTimeElapsed = 0;

	public static final long MIN_SIM_STEP = 33;

	public static final long MAX_SIM_STEP = 66;

	public static final float MODIFIER_SIZE = Ball.BALL_DEFAULT_SIZE * 2.0f;

	private List<GameObject> tempObjects = new ArrayList<GameObject>();

	public ModifierPool goPool;

	public Context context;

	public GameEngine(final Context context) {
		this.context = context;
		// Init the font using the context so it is avaliable to others
		Global.getPixelatedFont(context);
	}

	public void init() {
		if (this.objectsCount > 0) {
			return;
		}
		createGameObjects();
		this.thread = new EngineThread();
		if (Global.TWO_THREADS) {
			this.thread.start();
		}
		loadAllControls();
	}

	public int addGameObject(final GameObject gObj) {
		if (this.objects != null) {
			return -1;
		}
		this.tempObjects.add(gObj);
		return this.tempObjects.size() - 1;
	}

	public void commitGameObjects() {
		this.objectsCount = this.tempObjects.size();
		this.objects = new GameObject[this.objectsCount];
		this.objects = this.tempObjects.toArray(this.objects);
		this.colisionObjects = new GameObject[this.objectsCount];
		this.tempObjects = null;
		this.goPool.addModifiers(this.objects);
		this.goPool.commit();
	}

	public void createGameObjects() {
		if (this.objectsCount > 0) {
			return;
		}
		createDefaultObjects();
		createModifiers();
		createScores();
		commitGameObjects();
	}

	private void createDefaultObjects() {
		final Paint whitePaint = new Paint();
		whitePaint.setColor(Color.WHITE);
		// Cria palheta
		RectF box = new RectF(25f, 87f, 35f, 90f);
		final Paddle p1Padle = new Paddle(1, box, true, 0f, GameCanvas.WIDTH);
		Global.p1Paddle = p1Padle;
		addGameObject(p1Padle);
		box = new RectF(25f, 10f, 35f, 13f);
		final Paddle p2Padle = new Paddle(2, box, true, 0f, GameCanvas.WIDTH);
		Global.p2Paddle = p2Padle;
		addGameObject(p2Padle);
		Global.paddles = new Paddle[] { null, p1Padle, p2Padle };
		box = new RectF(GameCanvas.WIDTH - 0.5f, 0, GameCanvas.WIDTH,
				GameCanvas.HEIGHT);
		// Cria bordas
		final Border rightBorder = new Border(0, box, true,
				Border.POSITION.RIGHT);
		addGameObject(rightBorder);
		Global.rightBorder = rightBorder;
		box = new RectF(0, 0, 0.5f, GameCanvas.HEIGHT);
		final Border leftBorder = new Border(0, box, true, Border.POSITION.LEFT);
		addGameObject(leftBorder);
		Global.leftBorder = leftBorder;
		box = new RectF(0, (GameCanvas.HEIGHT / 2) - 0.2f, GameCanvas.WIDTH,
				(GameCanvas.HEIGHT / 2) + 0.2f);
		final Border fieldMiddle = new Border(0, box, true,
				Border.POSITION.MIDDLE);
		fieldMiddle.colision = false;
		addGameObject(fieldMiddle);
		// Cria a bola
		box = new RectF(0, 0, Ball.BALL_DEFAULT_SIZE, Ball.BALL_DEFAULT_SIZE);
		box.offsetTo((GameCanvas.WIDTH / 2) - (box.width() / 2),
				(GameCanvas.HEIGHT / 2) - (box.height() / 2));
		final Ball ball = new Ball(box, true);
		Global.ball = ball;
		Global.balls = new Ball[BallMultiplier.EXTRA_BALLS_COUNT + 1];
		Global.balls[0] = ball;
		addGameObject(ball);

	}

	private void createScores() {
		final Typeface scoreFont = Global.getPixelatedFont(this.context);
		RectF box = new RectF((GameCanvas.WIDTH - 7f),
				(GameCanvas.HEIGHT / 2f) + 20, 0, 0);
		Global.scoreP1 = new Score(1, box, true, scoreFont);
		addGameObject(Global.scoreP1);
		box = new RectF((GameCanvas.WIDTH - 7f), (GameCanvas.HEIGHT / 2f) - 20,
				0, 0);
		Global.scoreP2 = new Score(2, box, true, scoreFont);
		addGameObject(Global.scoreP2);
	}

	private void createModifiers() {
		this.goPool = new ModifierPool();
		final List<GameObject> allModifiers = this.goPool.getAllModifiers();
		final SharedPreferences settings = this.context.getSharedPreferences(
				ModifierMenuActivity.MODIFIER_PREFS, 0);
		for (final GameObject modifier : allModifiers) {
			if (settings.getBoolean(modifier.getGOKey(), true)) {
				addGameObject(modifier);
			}
		}
	}

	public void restoreOriginalBoxes() {
		for (int n = 0; n < this.objectsCount; n++) {
			final GameObject currentObj = this.objects[n];
			currentObj.restoreOriginalBox(true);
		}
	}

	public boolean processInput(final MotionEvent event,
			final int pointerIndex, final int player) {
		if (this.objectsCount == 0) {
			return false;
		}
		for (int n = 0; n < this.objectsCount; n++) {
			final GameObject currentObj = this.objects[n];
			if (currentObj.player == player) {
				currentObj.processInput(event, pointerIndex);
			}
		}
		return true;
	}

	public void prepareEngine() {
		this.colisionObjectsCount = 0;
		for (int n = 0; n < this.objectsCount; n++) {
			final GameObject currentObj = this.objects[n];
			if (currentObj.inGame && currentObj.colision) {
				this.colisionObjects[this.colisionObjectsCount] = currentObj;
				this.colisionObjectsCount++;
			}
		}

	}

	public void checkColision() {
		for (int n = 0; n < this.colisionObjectsCount; n++) {
			final GameObject currentObj = this.colisionObjects[n];
			for (int j = 0; j < this.colisionObjectsCount; j++) {
				final GameObject toCheckColision = this.colisionObjects[j];
				if (toCheckColision == currentObj) {
					continue;
				}
				if (currentObj.checkColision(toCheckColision, true)) {
					toCheckColision.reactColision(currentObj);
				}

			}
		}
	}

	public void evolve(final long timeInterval, final long currentTime) {
		this.totalTimeElapsed += timeInterval;
		for (int n = 0; n < this.objectsCount; n++) {
			final GameObject currentObj = this.objects[n];
			if (!currentObj.inGame) {
				continue;
			}
			currentObj.evolve(timeInterval);
		}
		if (this.goPool != null) {
			this.goPool.enableNextGOMOdifier(timeInterval);
		}
	}

	public void recriateThread(final boolean force) {
		if (force || this.thread == null) {
			this.thread = new EngineThread();
		}
	}

	public class EngineThread extends Thread {
		public long lastTime = SystemClock.uptimeMillis();
		public long timeElapsed;
		public long realTimeElapsed;

		@Override
		public void run() {
			try {
				while (!isInterrupted()) {
					final long currentTime = SystemClock.uptimeMillis();
					this.timeElapsed = currentTime - this.lastTime;
					this.lastTime = currentTime;
					// Gera slowdown mas simula corretamente
					if (this.timeElapsed > MAX_SIM_STEP) {
						this.timeElapsed = MAX_SIM_STEP;
					}
					if (Global.paused) {
						this.timeElapsed = 0;
					}
					execNextLoop(this.timeElapsed, currentTime);
					this.realTimeElapsed = SystemClock.uptimeMillis()
							- this.lastTime;
					if (this.realTimeElapsed < MIN_SIM_STEP) {
						sleep(MIN_SIM_STEP - this.realTimeElapsed);
					}
				}
			} catch (final InterruptedException e) {
				interrupt();
				return;
			}
		}

		public void execNextLoop(final long timeElapsed, final long currentTime) {
			if (timeElapsed == 0) {
				return;
			}
			prepareEngine();
			checkColision();
			evolve(timeElapsed, currentTime);
		}
	}

	public void loadControls(final int playerNumber) {
		if (Global.paddles == null || Global.paddles[playerNumber] == null) {
			return;
		}
		final SharedPreferences settings = this.context.getSharedPreferences(
				MainActivity.MAIN_PREFS, 0);
		final int controlNumber = settings.getInt(
				MainActivity.getPlayerControllerKey(playerNumber), 0);
		Global.paddles[playerNumber].setControl(controlNumber);
	}

	public void loadAllControls() {
		loadControls(1);
		loadControls(2);
	}
}
