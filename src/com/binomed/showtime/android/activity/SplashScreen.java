package com.binomed.showtime.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.cst.ParamIntent;

public class SplashScreen extends Activity {

	private final Handler mHandler = new Handler();
	private static final int SPLASH_SCREEN_DURATION = 2500;
	private static final int SPLASH_SCREEN_FIRST_DURATION = 2000;
	private ImageView splashImage, splashImage2;

	private final Runnable mPendingLauncherRunnable = new Runnable() {
		@Override
		public void run() {
			Intent intent = new Intent(SplashScreen.this, CineShowTimeMainActivity.class);
			intent.putExtra(ParamIntent.ACTIVITY_LARGE_SCREEN, ((SplashScreen.this.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE //
					)
					|| ((SplashScreen.this.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE //
					));
			startActivity(intent);
			finish();
		}
	};

	private final Runnable mFadeOutRunnable = new Runnable() {
		@Override
		public void run() {
			// Animation fadeIn = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.appear);
			// splashImage.setImageResource(R.drawable.splash_front_0);
			Animation fadeOut = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.disappear);
			splashImage.startAnimation(fadeOut);
			splashImage2.startAnimation(fadeOut);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		splashImage = (ImageView) findViewById(R.id.SplashImage);
		splashImage2 = (ImageView) findViewById(R.id.SplashImage2);
		Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.appear);
		// splashImage.setImageResource(R.drawable.splash_front_0);
		Animation fadeOut = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.disappear);
		splashImage.startAnimation(fadeOut);
		splashImage2.startAnimation(fadeIn);

		mHandler.postDelayed(mFadeOutRunnable, SPLASH_SCREEN_FIRST_DURATION);
		mHandler.postDelayed(mPendingLauncherRunnable, SPLASH_SCREEN_DURATION);

	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacks(mPendingLauncherRunnable);
	}

}
