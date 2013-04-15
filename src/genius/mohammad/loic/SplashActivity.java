package genius.mohammad.loic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		Runnable r = new Runnable() {
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (Exception e) {

				}
				Intent i = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(i);
				SplashActivity.this.finish();
			}
		};
		new Thread(r).start();
	}
}
