package mtg.pong.engine;

import mtg.pong.Global;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.MotionEvent;

public class Score extends GameObject {

	public int score = 0;

	private static final int EXTRA_STRING_ALLOC = 5;

	// Avoid string allocation during runtime
	private final String[] scoreStrings = new String[Global.maxScore
			+ EXTRA_STRING_ALLOC];

	public static final float SIZE = 5.0f;

	public static final String WINNER_PLAYER_KEY = "mtg.pong.engine.score.WINNER_PLAYER_KEY";

	public static final String LOSER_PLAYER_KEY = "mtg.pong.engine.score.LOSER_PLAYER_KEY";

	public static final String P1_SCORE_KEY = "mtg.pong.engine.score.P1_SCORE_KEY";

	public static final String P2_SCORE_KEY = "mtg.pong.engine.score.P2_SCORE_KEY";

	public Score(final int player, final RectF box, final boolean inGame,
			final Typeface font) {
		super(player, box, inGame);
		this.paint = new Paint();
		this.paint.setColor(Color.BLUE);
		this.paint.setTextSize(scale(SIZE));
		this.paint.setTypeface(font);
		this.colision = false;
		// Extra balls or a power up thar could give more points
		for (int n = 0; n < Global.maxScore + EXTRA_STRING_ALLOC; n++) {
			this.scoreStrings[n] = String.valueOf(n);
		}
	}

	public void incScore() {
		this.score++;
	}

	@Override
	public void draw(final Canvas canvas) {
		Global.cUtil.drawText(canvas, this.scoreStrings[this.score],
				this.box.left, this.box.top, this.paint);
	}

	@Override
	public void evolve(final long timeInterval) {
		if (Global.maxScore == this.score) {
			Global.gameEnded = true;
		}

	}

	public void prepareIntent(final Intent intent) {
		if (this.score == Global.maxScore) {
			intent.putExtra(WINNER_PLAYER_KEY, "" + this.player);
		} else {
			intent.putExtra(LOSER_PLAYER_KEY, "" + this.player);
		}
		if (this.player == 1) {
			intent.putExtra(P1_SCORE_KEY, this.scoreStrings[this.score]);
		} else {
			intent.putExtra(P2_SCORE_KEY, this.scoreStrings[this.score]);
		}
	}

	@Override
	public void reactColision(final GameObject gameObj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processInput(final MotionEvent event, final int pointerIndex) {
		// TODO Auto-generated method stub

	}

}
