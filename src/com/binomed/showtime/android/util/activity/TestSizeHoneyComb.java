package com.binomed.showtime.android.util.activity;

import android.content.res.Configuration;

public final class TestSizeHoneyComb {

	public static boolean checkLargeScreen(int screenLayout) {
		return TestSizeOther.checkLargeScreen(screenLayout) //
				|| (screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

}
