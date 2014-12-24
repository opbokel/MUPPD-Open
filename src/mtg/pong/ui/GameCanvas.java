package mtg.pong.ui;

import mtg.pong.Global;
import mtg.pong.WinnerActivity;
import mtg.pong.engine.GameEngine;
import mtg.pong.engine.GameObject;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameCanvas extends SurfaceView implements SurfaceHolder.Callback {

	public static final float HEIGHT = 100;

	public static final float WIDTH = 60;

	public static float SCALE = 1;

	public CanvasThread thread;

	public SurfaceHolder sHolder;

	public static Paint backgroundPaint;

	public Paint textPaint;

	public boolean surfaceCreated = false;

	/**
	 * TEM QUE SER O ULTIMA A SER INSTANCIADO!
	 * 
	 * @param context
	 */
	public GameCanvas(final Context context) {
		super(context);
		this.sHolder = getHolder();
		this.thread = new CanvasThread();
		backgroundPaint = new Paint();
		backgroundPaint.setColor(Color.BLACK);
		this.textPaint = new Paint();
		this.textPaint.setColor(Color.WHITE);
		this.sHolder.addCallback(this);

		// TODO Auto-generated constructor stub
	}

	public void surfaceChanged(final SurfaceHolder arg0, final int arg1,
			final int arg2, final int arg3) {
		SCALE = Math.min(getHeight() / HEIGHT, getWidth() / WIDTH);
		Global.gEngine.init();
		Global.resumeThreads();
	}

	public void surfaceCreated(final SurfaceHolder arg0) {
		this.surfaceCreated = true;
	}

	public void surfaceDestroyed(final SurfaceHolder arg0) {
		this.surfaceCreated = false;

	}

	public int getPlayer(final MotionEvent event, final int pointerIndex) {
		// TODO: Botar sobre width total caso centralize
		if (event.getY(pointerIndex) > (HEIGHT * SCALE) / 2) {
			return 1;
		}
		return 2;

	}

	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		if (!Global.processInput) {
			return true;
		}
		if (Global.paused) {
			Global.paused = false;
		}
		final int pCount = event.getPointerCount();
		for (int n = 0; n < pCount; n++) {
			if (!Global.gEngine.processInput(event, n, getPlayer(event, n))) {
				return false;
			}
		}
		return true;
	}

	private void callGameEnded() {
		final Intent intent = new Intent(getContext(), WinnerActivity.class);
		Global.scoreP1.prepareIntent(intent);
		Global.scoreP2.prepareIntent(intent);
		getContext().startActivity(intent);
	}

	public void recriateThread(final boolean force) {
		if (force || this.thread == null) {
			this.thread = new CanvasThread();
		}
	}

	public class CanvasThread extends Thread {
		public long lastTime = SystemClock.uptimeMillis();
		public long timeElapsed = Long.MAX_VALUE;
		long targetTime;

		@Override
		public void run() {
			Canvas c = null;
			while (!isInterrupted() && !Global.gameEnded) {
				try {
					final long currentTime = SystemClock.uptimeMillis();
					this.timeElapsed = currentTime - this.lastTime;
					this.lastTime = currentTime;
					this.targetTime = currentTime + GameEngine.MIN_SIM_STEP;
					final long realTimeElapsed = this.timeElapsed;

					c = GameCanvas.this.sHolder.lockCanvas(null);
					if (c == null || Global.gEngine.objectsCount == 0) {
						return;
					}
					if (Global.paused) {
						this.timeElapsed = 0;
					}
					synchronized (GameCanvas.this.sHolder) {
						if (!Global.leaveTrail) {
							Global.cUtil.drawColor(c,
									GameCanvas.this.backgroundPaint.getColor());
						}
						for (int n = 0; n < Global.gEngine.objectsCount; n++) {
							final GameObject currentObj = Global.gEngine.objects[n];
							if (currentObj.inGame) {
								currentObj.draw(c);
							}

						}
					}

					if (!Global.TWO_THREADS) {
						if (this.timeElapsed > GameEngine.MAX_SIM_STEP) {
							this.timeElapsed = GameEngine.MAX_SIM_STEP;
						}
						Global.gEngine.thread.execNextLoop(this.timeElapsed,
								currentTime);
					}
					if (Global.paused) {
						InfoRender.drawPauseText(c, SCALE, getContext(),
								realTimeElapsed);
					}

				} finally {
					if (c != null) {
						// mantem framerate estavel
						if (Global.USE_MIN_FRAME_RATE) {
							while (SystemClock.uptimeMillis() < this.targetTime) {

							}
						}
						Global.cUtil.unlockCanvasAndPost(c,
								GameCanvas.this.sHolder);
					}
				}
			}
			if (Global.gameEnded) {
				callGameEnded();
				return;
			}
		}
	}

}