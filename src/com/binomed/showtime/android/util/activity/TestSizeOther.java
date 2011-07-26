package com.binomed.showtime.android.util.activity;

import android.content.res.Configuration;

public final class TestSizeOther {

	public static boolean checkLargeScreen(int screenLayout) {
		return (screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

}
