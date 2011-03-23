package com.binomed.showtime.android.adapter.view;

import java.util.List;

import android.content.Context;

import com.binomed.showtime.beans.TheaterBean;

public class TheaterFavListAdapter extends AbstractTheaterListAdapter {

	public TheaterFavListAdapter(Context context, List<TheaterBean> theaterList) {
		super(context, theaterList, false);
	}

}
