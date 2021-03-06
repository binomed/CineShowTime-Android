/*
 * Copyright (C) 2011 Binomed (http://blog.binomed.fr)
 *
 * Licensed under the Eclipse Public License - v 1.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 * THE ACCOMPANYING PROGRAM IS PROVIDED UNDER THE TERMS OF THIS ECLIPSE PUBLIC 
 * LICENSE ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM 
 * CONSTITUTES RECIPIENT'S ACCEPTANCE OF THIS AGREEMENT.
 */
package com.binomed.showtime.android.screen.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.util.activity.TestSizeHoneyComb;
import com.binomed.showtime.android.util.activity.TestSizeOther;

public class SplashScreen extends Activity {

	private final Handler mHandler = new Handler();
	private static final int SPLASH_SCREEN_DURATION = 2500;
	private static final int SPLASH_SCREEN_FIRST_DURATION = 2000;
	private ImageView splashImage, splashImage2;

	private final Runnable mPendingLauncherRunnable = new Runnable() {
		@Override
		public void run() {
			Intent intent = new Intent(SplashScreen.this, CineShowTimeMainActivity.class);
			if (Integer.valueOf(Build.VERSION.SDK) <= 10) {
				intent.putExtra(ParamIntent.ACTIVITY_LARGE_SCREEN, TestSizeOther.checkLargeScreen(SplashScreen.this.getResources().getConfiguration().screenLayout));

			} else {
				intent.putExtra(ParamIntent.ACTIVITY_LARGE_SCREEN, TestSizeHoneyComb.checkLargeScreen(SplashScreen.this.getResources().getConfiguration().screenLayout));

			}
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

	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.appear);
			// splashImage.setImageResource(R.drawable.splash_front_0);
			Animation fadeOut = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.disappear);
			splashImage.startAnimation(fadeOut);
			splashImage2.startAnimation(fadeIn);

			mHandler.postDelayed(mFadeOutRunnable, SPLASH_SCREEN_FIRST_DURATION);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			mHandler.postDelayed(mPendingLauncherRunnable, SPLASH_SCREEN_DURATION);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacks(mFadeOutRunnable);
		mHandler.removeCallbacks(mPendingLauncherRunnable);
	}

}
