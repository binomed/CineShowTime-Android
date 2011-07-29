package com.binomed.showtime.android.adapter.view;

import greendroid.widget.PagedAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.binomed.showtime.android.model.MovieBean;

public class MoviePagedAdapter extends PagedAdapter {

	private int NB_PAGE = 3;

	@Override
	public int getCount() {
		return NB_PAGE;
	}

	private MovieBean movie;
	private Context context;

	public MoviePagedAdapter(MovieBean movie, Context context) {
		super();
		this.movie = movie;
		this.context = context;
	}

	@Override
	public Object getItem(int position) {
		return movie;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

}
