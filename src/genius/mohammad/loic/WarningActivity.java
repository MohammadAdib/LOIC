package genius.mohammad.loic;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.CheckBox;

public class WarningActivity extends Activity implements OnClickListener {
	
	private boolean clicked = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Make us non-modal, so that others can receive touch events.
	    getWindow().setFlags(LayoutParams.FLAG_NOT_TOUCH_MODAL, LayoutParams.FLAG_NOT_TOUCH_MODAL);

	    // ...but notify us that it happened.
	    getWindow().setFlags(LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

	    // Note that flag changes must happen *before* the content view is set.
		setContentView(R.layout.activity_warning);
		findViewById(R.id.continueTV).setOnClickListener(this);
	}
	
	@Override
	  public boolean onTouchEvent(MotionEvent event) {
	    // If we've received a touch notification that the user has touched
	    // outside the app, finish the activity.
	    if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
	      return true;
	    }

	    // Delegate everything else to Activity.
	    return super.onTouchEvent(event);
	  }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		CheckBox cb = (CheckBox) findViewById(R.id.warningCB);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("warning", cb.isChecked() || !clicked).commit();
		editor.putBoolean("clicked", clicked).commit();
	}

	public void onClick(View v) {
		clicked = true;
		finish();
	}
}
