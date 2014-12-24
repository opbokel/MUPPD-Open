package mtg.pong.ui;

import java.nio.ByteBuffer;

import mtg.pong.Global;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.SurfaceHolder;

public class CanvasUtil {

	public RectF box = new RectF();

	public Paint paint = new Paint();

	static final char PAR_DELIMITER = '\t';

	static final char FUNC_DELIMITER = 0;

	// Conservative (text has a variable size)
	static final int MAX_COMMAND_SIZE = 200;

	static final int BUFFER_SIZE = 1024 * 1024 * 8;

	// direct has better performance
	public ByteBuffer bb = ByteBuffer.allocate(BUFFER_SIZE);

	private final char[] tmpCharArray = new char[512];

	public enum C_CODE {
		DRAW_RECT(new char[] { 'D', 'R' }), DRAW_COLOR(new char[] { 'D', 'C' }), DRAW_TEXT(
				new char[] { 'D', 'T' }), UNLOCK_POST(new char[] { 'U', 'P' }), NULL_CMD(
				new char[] { 0, 0 });

		public char[] code;

		private C_CODE(final char[] code) {
			this.code = code;
		}
	}

	public int bufferIndex = 0;

	public void drawRect(final Canvas canvas, final RectF box, final Paint paint) {
		if (Global.isHost && (this.bb.remaining() >= MAX_COMMAND_SIZE)) {
			this.bb.putChar(C_CODE.DRAW_RECT.code[0]); // 2
			this.bb.putChar(C_CODE.DRAW_RECT.code[1]); // 2
			this.bb.putLong(Global.gEngine.totalTimeElapsed); // 8
			this.bb.putFloat(box.bottom / GameCanvas.SCALE); // 4
			this.bb.putFloat(box.left / GameCanvas.SCALE); // 4
			this.bb.putFloat(box.right / GameCanvas.SCALE); // 4
			this.bb.putFloat(box.top / GameCanvas.SCALE); // 4
			this.bb.putInt(paint.getColor()); // 4
		}
		canvas.drawRect(box, paint);
	}

	public void drawText(final Canvas canvas, final String text, final float x,
			final float y, final Paint paint) {
		if (Global.isHost && (this.bb.remaining() >= MAX_COMMAND_SIZE)) {
			this.bb.putChar(C_CODE.DRAW_TEXT.code[0]); // 2
			this.bb.putChar(C_CODE.DRAW_TEXT.code[1]); // 2
			this.bb.putLong(Global.gEngine.totalTimeElapsed); // 8
			this.bb.putFloat(x / GameCanvas.SCALE); // 4
			this.bb.putFloat(y / GameCanvas.SCALE); // 4
			this.bb.putInt(paint.getColor()); // 4
			this.bb.putFloat(paint.getTextSize() / GameCanvas.SCALE); // 4

			final int tlength = text.length();
			this.bb.putInt(tlength); // 4
			for (int n = 0; n < tlength; n++) {
				this.bb.putChar(text.charAt(n)); // 2
			}
		}
		canvas.drawText(text, x, y, paint);
	}

	public void drawRect(final Canvas canvas, final float left,
			final float top, final float right, final float bottom,
			final Paint paint) {
		this.box.set(left, top, right, bottom);
		drawRect(canvas, this.box, paint);
	}

	public void drawColor(final Canvas canvas, final int color) {
		if (Global.isHost && (this.bb.remaining() >= MAX_COMMAND_SIZE)) {
			this.bb.putChar(C_CODE.DRAW_COLOR.code[0]);
			this.bb.putChar(C_CODE.DRAW_COLOR.code[1]);
			this.bb.putLong(Global.gEngine.totalTimeElapsed);
			this.bb.putInt(this.paint.getColor());
		}
		canvas.drawColor(Integer.valueOf(color));
	}

	public void unlockCanvasAndPost(final Canvas canvas,
			final SurfaceHolder sHolder) {
		if (Global.isHost && (this.bb.remaining() >= MAX_COMMAND_SIZE)) {
			this.bb.putChar(C_CODE.UNLOCK_POST.code[0]);
			this.bb.putChar(C_CODE.UNLOCK_POST.code[1]);
			this.bb.putLong(Global.gEngine.totalTimeElapsed);
		}
		sHolder.unlockCanvasAndPost(canvas);
	}

	public char[] parseComand(final Canvas canvas) {
		// This will run only on client. Must find a way to not allocate string
		final char cmd0 = this.bb.getChar();
		final char cmd1 = this.bb.getChar();
		final long timeElapsed = this.bb.getLong();
		char[] cmd = C_CODE.NULL_CMD.code;
		if (cmd0 == 'D' && cmd1 == 'R') {
			final float bottom = this.bb.getFloat() * ClientCanvas.SCALE;
			final float left = this.bb.getFloat() * ClientCanvas.SCALE;
			final float right = this.bb.getFloat() * ClientCanvas.SCALE;
			final float top = this.bb.getFloat() * ClientCanvas.SCALE;
			this.box.set(left, top, right, bottom);

			final int color = this.bb.getInt();
			this.paint.setColor(color);
			canvas.drawRect(this.box, this.paint);
			cmd = C_CODE.DRAW_RECT.code;
		} else if (cmd0 == 'D' && cmd1 == 'C') {
			final int color = this.bb.getInt();
			canvas.drawColor(color);
			cmd = C_CODE.DRAW_COLOR.code;
		} else if (cmd0 == 'U' && cmd1 == 'P') {
			// must control outside with the surface holder, just return the
			// command code
			cmd = C_CODE.UNLOCK_POST.code;
		} else if (cmd0 == 'D' && cmd1 == 'T') {
			final float x = this.bb.getFloat() * ClientCanvas.SCALE;
			final float y = this.bb.getFloat() * ClientCanvas.SCALE;
			final int color = this.bb.getInt();
			this.paint.setColor(color);
			this.paint.setTextSize(this.bb.getFloat() * ClientCanvas.SCALE);

			final int tlength = this.bb.getInt();
			int n = 0;
			for (; n < tlength; n++) {
				this.tmpCharArray[n] = this.bb.getChar();
			}

			this.tmpCharArray[n + 1] = 0;
			this.paint.setTypeface(Global.getPixelatedFont(null));
			canvas.drawText(this.tmpCharArray, 0, n, x, y, this.paint);
			cmd = C_CODE.DRAW_TEXT.code;
		}
		return cmd;
	}
}
