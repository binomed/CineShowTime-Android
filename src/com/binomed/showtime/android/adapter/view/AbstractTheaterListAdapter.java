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

abstract class AbstractTheaterListAdapter extends BaseAdapter {

	private List<TheaterBean> theaterList;
	private Context mainContext;
	private boolean hasMoreTheater;

	public AbstractTheaterListAdapter(Context context, List<TheaterBean> theaterList, boolean hasMoreTheater) {
		super();
		mainContext = context;
		this.theaterList = theaterList;
		this.hasMoreTheater = hasMoreTheater;
	}

	@Override
	public int getCount() {
		int result = (theaterList != null) ? theaterList.size() : 0;
		if (hasMoreTheater) {
			result++;
		}
		return result;
	}

	@Override
	public Object getItem(int position) {
		TheaterBean theater = null;
		if ((theaterList != null) && (theaterList.size() > position)) {
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

		if (hasMoreTheater && theater == null) {
			theaterView.setText(mainContext.getResources().getString(R.string.itemMoreTheaters));
		} else if (theater != null) {
			theaterView.setText(theater.getTheaterName());
		}

		return theaterView;
	}

}
