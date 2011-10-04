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