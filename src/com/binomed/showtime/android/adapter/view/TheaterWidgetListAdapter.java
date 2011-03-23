package com.binomed.showtime.android.adapter.view;

import java.util.List;

import android.content.Context;

import com.binomed.showtime.beans.TheaterBean;

public class TheaterWidgetListAdapter extends AbstractTheaterListAdapter {

	public TheaterWidgetListAdapter(Context context, List<TheaterBean> theaterList, boolean hasMoreTheater) {
		super(context, theaterList, hasMoreTheater);
	}

}
