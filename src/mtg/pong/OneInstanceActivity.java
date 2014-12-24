package mtg.pong;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;

public class OneInstanceActivity extends Activity {

	private static Map<String, Activity> lastInstances = new HashMap<String, Activity>();

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final String className = this.getClass().getName();
		finishLastInstance(className);
		lastInstances.put(className, this);
	}

	public static void finishLastInstance(final String className) {
		final Activity lastInstance = lastInstances.get(className);
		if (lastInstance != null) {
			lastInstance.finish();
			lastInstances.remove(className);
		}

	}

}
