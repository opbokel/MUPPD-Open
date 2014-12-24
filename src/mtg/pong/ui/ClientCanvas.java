package mtg.pong.ui;

import mtg.pong.Global;
import android.content.Context;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ClientCanvas extends SurfaceView implements SurfaceHolder.Callback {
	public static final float HEIGHT = 100;

	public static final float WIDTH = 60;

	public static float SCALE = 1;

	public ClientCanvasThread thread;

	public SurfaceHolder sHolder;

	public boolean surfaceCreated = false;

	/**
	 * TEM QUE SER O ULTIMA A SER INSTANCIADO!
	 * 
	 * @param context
	 */
	public ClientCanvas(final Context context) {
		super(context);
		this.sHolder = getHolder();
		this.thread = new ClientCanvasThread();
		this.sHolder.addCallback(this);

		// TODO Auto-generated constructor stub
	}

	public void surfaceChanged(final SurfaceHolder arg0, final int arg1,
			final int arg2, final int arg3) {
		SCALE = Math.min(getHeight() / HEIGHT, getWidth() / WIDTH);
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
		return true;
	}

	private void callGameEnded() {
		// will call with the host commands
		// final Intent intent = new Intent(getContext(), WinnerActivity.class);
		// Global.scoreP1.prepareIntent(intent);
		// Global.scoreP2.prepareIntent(intent);
		// getContext().startActivity(intent);
	}

	public void recriateThread(final boolean force) {
		if (force || this.thread == null) {
			this.thread = new ClientCanvasThread();
		}
	}

	public class ClientCanvasThread extends Thread {
		public long lastTime = SystemClock.uptimeMillis();
		public long timeElapsed = Long.MAX_VALUE;
		long targetTime;

		@Override
		public void run() {
			Global.cUtil.bb.rewind();
			Canvas c = null;
			while (!isInterrupted()) {
				try {
					c = ClientCanvas.this.sHolder.lockCanvas(null);
					if (c == null) {
						return;
					}

					synchronized (ClientCanvas.this.sHolder) {
						while (true) {
							if (!Global.cUtil.bb.hasRemaining()) {
								return;
							}
							final char[] cmd = Global.cUtil.parseComand(c);
							if ('U' == cmd[0] && 'P' == cmd[1]) {
								break;
							}

						}
					}

				} finally {
					if (c != null) {
						ClientCanvas.this.sHolder.unlockCanvasAndPost(c);
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
