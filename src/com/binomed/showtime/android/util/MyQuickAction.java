package com.binomed.showtime.android.util;

import greendroid.widget.QuickAction;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;

public class MyQuickAction extends QuickAction {

	private static final ColorFilter BLACK_CF = new LightingColorFilter(Color.BLACK, Color.BLACK);

	public MyQuickAction(Context ctx, int drawableId, int titleId, boolean applyFilter) {
		super(ctx, buildDrawable(ctx, drawableId, applyFilter), titleId);
	}

	private static Drawable buildDrawable(Context ctx, int drawableId, boolean applyFilter) {
		Drawable d = ctx.getResources().getDrawable(drawableId);
		if (applyFilter) {
			d.setColorFilter(BLACK_CF);
		}
		return d;
	}

}