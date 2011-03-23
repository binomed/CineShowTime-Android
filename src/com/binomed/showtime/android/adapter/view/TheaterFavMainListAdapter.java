package com.binomed.showtime.android.adapter.view;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.binomed.showtime.android.layout.view.TheaterFavView;
import com.binomed.showtime.beans.TheaterBean;

public class TheaterFavMainListAdapter extends AbstractTheaterListAdapter {

	private OnClickListener listener;

	public TheaterFavMainListAdapter(Context context, List<TheaterBean> theaterList, OnClickListener clickListener) {
		super(context, theaterList, false);
		this.listener = clickListener;
	}

	@Override
	public View getView(int groupPosition, View convertView, ViewGroup parent) {

		TheaterFavView mainView = null;
		if (convertView == null) {
			mainView = new TheaterFavView(mainContext);
			mainView.setOnClickListener(listener);
		} else {
			mainView = (TheaterFavView) convertView;
		}

		TheaterBean theater = (TheaterBean) getItem(groupPosition);
		mainView.setTheater(theater);

		return mainView;
	}

}
