package com.binomed.showtime.android.adapter.view;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.binomed.showtime.R;
import com.binomed.showtime.beans.TheaterBean;

public class TheaterFavListAdapter extends BaseAdapter {

	private List<TheaterBean> theaterList;
	private Context mainContext;

	public TheaterFavListAdapter(Context context, List<TheaterBean> theaterList) {
		super();
		mainContext = context;
		this.theaterList = theaterList;
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

		TextView theaterView = null;
		if (convertView == null) {
			LayoutInflater inflator = (LayoutInflater) mainContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			theaterView = (TextView) inflator.inflate(R.layout.and_showtime_expandable_group_item, null);
		} else {
			theaterView = (TextView) convertView;
		}

		TheaterBean theater = (TheaterBean) getItem(groupPosition);

		if (theater != null) {
			theaterView.setText(theater.getTheaterName());
		}

		return theaterView;
	}

}
