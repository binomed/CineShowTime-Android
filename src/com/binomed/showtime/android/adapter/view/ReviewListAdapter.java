package com.binomed.showtime.android.adapter.view;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.layout.view.ReviewView;
import com.binomed.showtime.android.model.ReviewBean;

public class ReviewListAdapter extends BaseAdapter {

	private List<ReviewBean> reviewList;
	private Context mainContext;

	public ReviewListAdapter(Context context, List<ReviewBean> reviewList) {
		super();
		mainContext = context;
		this.reviewList = reviewList;

	}

	@Override
	public int getCount() {
		return (reviewList != null) ? (reviewList.size() != 0 ? reviewList.size() : 1) : 1;
	}

	@Override
	public Object getItem(int position) {
		ReviewBean review = null;
		if ((reviewList != null) && (reviewList.size() >= position)) {
			review = reviewList.get(position);
		}
		return review;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int groupPosition, View convertView, ViewGroup parent) {
		ReviewView reviewView = null;
		ReviewBean review = (ReviewBean) getItem(groupPosition);
		if (review != null) {
			if (convertView == null) {
				reviewView = new ReviewView(mainContext);
			} else {
				reviewView = (ReviewView) convertView;
			}

			reviewView.setReviewBean(review);
			return reviewView;
		} else {
			TextView noResultView = new TextView(mainContext);
			noResultView.setText(R.string.noReview);
			return noResultView;
		}
	}

}
