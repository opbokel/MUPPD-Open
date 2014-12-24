package mtg.pong;

import mtg.pong.engine.Ball;
import mtg.pong.engine.Border;
import mtg.pong.engine.GameEngine;
import mtg.pong.engine.Paddle;
import mtg.pong.engine.Score;
import mtg.pong.ui.CanvasUtil;
import mtg.pong.ui.ClientCanvas;
import mtg.pong.ui.GameCanvas;
import android.content.Context;
import android.graphics.Typeface;
import android.widget.Toast;

public class Global {

	public enum REGION {
		TOP, BOTTOM, LEFT, RIGHT
	}

	public enum MODIFIER_ID {
		DEFAULT_MOD_ID(0), BALL_MULTIPLIER(1), BALL_SIN(2), LASER(3), PADDLE_SIZE(
				4), SOLID_BLOCK(5), SPEED_MOD(6), INVISIBLE_BALL(7), BLOCK_WALL(
				8), EXTRA_PADDLE(9), TRIP(10), IMMOBILIZE(11), REMOTE_CONTROL(
				12);

		public int id;

		public String modLogo;

		private MODIFIER_ID(final int id) {
			this.id = id;
		}
	}

	public static GameEngine gEngine;

	public static GameCanvas gCanvas;

	public static ClientCanvas cCanvas;

	public static Score scoreP1;

	public static Score scoreP2;

	public static Ball ball;

	public static Ball[] balls;

	public static Border leftBorder;

	public static Border rightBorder;

	public static Paddle p1Paddle;

	public static Paddle p2Paddle;

	public static Paddle[] paddles;

	public static CanvasUtil cUtil = new CanvasUtil();

	public static boolean DEBUG = false;

	public static final boolean TWO_THREADS = false;

	public static final boolean USE_MIN_FRAME_RATE = false;

	public static final boolean ONE_CLICK_MODIFIER = true;

	private static Typeface pixelFont;

	public static boolean paused = true;

	public static boolean gameEnded = false;

	public static boolean processInput = true;

	public static boolean isHost = true;

	public static int maxScore = 12;

	public static boolean leaveTrail = false;

	public static void stopThreads(final boolean destroyGlobalResources,
			final boolean pauseGame) {
		processInput = false;
		if (gCanvas != null) {
			Global.gCanvas.thread.interrupt();
			gCanvas.thread = null;
		}
		if (cCanvas != null) {
			Global.cCanvas.thread.interrupt();
			cCanvas.thread = null;
		}

		if (Global.gEngine != null) {
			if (TWO_THREADS) {
				Global.gEngine.thread.interrupt();
				gEngine.thread = null;
			}
		}
		if (destroyGlobalResources) {
			Global.gCanvas = null;
			Global.cCanvas = null;
			Global.gEngine = null;
			scoreP1 = null;
			scoreP2 = null;
			paddles = null;
			ball = null;
			balls = null;
		}
		if (pauseGame) {
			paused = true;
		}
	}

	public static Typeface getPixelatedFont(final Context context) {
		if (pixelFont == null) {
			pixelFont = Typeface.createFromAsset(context.getAssets(),
					"LCD_Solid.ttf");
		}
		return pixelFont;
	}

	public static void toastLong(final Context context, final String text) {
		final Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
		toast.show();
	}

	public static void toastShort(final Context context, final String text) {
		final Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		toast.show();
	}

	public static void resumeThreads() {
		if (gCanvas != null) {
			if (!gCanvas.surfaceCreated) {
				return;
			}
			// Bug bizzaro, a thread volta como não interrompida, mas tambem não
			// esta viva e não é possivel dar start. Somente quando minimiza o
			// sistema
			gCanvas.recriateThread(false);
			if (!gCanvas.thread.isAlive() || gCanvas.thread.isInterrupted()) {
				gCanvas.thread.start();
			}
		}
		if (cCanvas != null) {
			if (!cCanvas.surfaceCreated) {
				return;
			}
			cCanvas.recriateThread(false);
			if (!cCanvas.thread.isAlive() || cCanvas.thread.isInterrupted()) {
				cCanvas.thread.start();
			}
		}

		if (Global.gEngine != null) {
			if (TWO_THREADS) {
				gCanvas.recriateThread(false);
				if (!gEngine.thread.isAlive() || gEngine.thread.isInterrupted()) {
					Global.gEngine.thread.start();
				}
			}
		}
		processInput = true;
	}
}
