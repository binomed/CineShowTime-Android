package com.binomed.showtime.android.adapter.view;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.binomed.showtime.android.layout.view.TheaterView;
import com.binomed.showtime.beans.TheaterBean;

public class MovieListAdapter extends BaseAdapter {

	private List<TheaterBean> theaterList;
	private Context mainContext;

	public MovieListAdapter(Context context, List<TheaterBean> theaterList, Comparator<TheaterBean> comparator) {
		super();
		mainContext = context;
		this.theaterList = theaterList;
		if (comparator != null) {
			Collections.sort(this.theaterList, comparator);
		}
	}

	@Override
	public int getCount() {
		return (theaterList != null) ? theaterList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		TheaterBean theater = null;
		if ((theaterList != null) && (theaterList.size() >= position)) {
			theater = theaterList.get(position);
		}
		return theater;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int groupPosition, View convertView, ViewGroup parent) {

		TheaterView theaterView = null;
		if (convertView == null) {
			theaterView = new TheaterView(mainContext);

		} else {
			theaterView = (TheaterView) convertView;
		}

		TheaterBean theater = (TheaterBean) getItem(groupPosition);

		if (theater != null) {
			theaterView.setTheater(theater);
		}

		return theaterView;
	}

}
