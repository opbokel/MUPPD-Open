package mtg.pong.menu;

import mtg.pong.engine.GameObject;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;


public class GODrawable extends Drawable {

	public GameObject gameObject;

	public GODrawable(final GameObject gameObject) {
		this.gameObject = gameObject;
	}

	@Override
	public void draw(final Canvas arg0) {
		this.gameObject.draw(arg0);
	}

	@Override
	public int getOpacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAlpha(final int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setColorFilter(final ColorFilter arg0) {
		// TODO Auto-generated method stub

	}

}
