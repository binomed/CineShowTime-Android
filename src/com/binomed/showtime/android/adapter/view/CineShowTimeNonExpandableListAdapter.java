package com.binomed.showtime.android.adapter.view;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.binomed.showtime.R;
import com.binomed.showtime.android.layout.view.ObjectMasterView;
import com.binomed.showtime.android.model.NearResp;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.util.comparator.CineShowtimeComparator;

public class CineShowTimeNonExpandableListAdapter extends BaseAdapter {

	private NearResp nearRespBean;
	private Map<String, TheaterBean> theatherFavList;
	private List<TheaterBean> theatherList;
	private Context mainContext;
	private boolean kmUnit;
	private boolean distanceTime;
	private OnClickListener onClickListener;

	private static final String TAG = "CineShowTimeExpandableListAdapter";

	public CineShowTimeNonExpandableListAdapter(Context context, OnClickListener listener) {
		// super();
		mainContext = context;
		this.onClickListener = listener;
		changePreferences();

	}

	public void changePreferences() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mainContext);
		String measure = prefs.getString(mainContext.getResources().getString(R.string.preference_loc_key_measure)//
				, mainContext.getResources().getString(R.string.preference_loc_default_measure));
		distanceTime = prefs.getBoolean(mainContext.getResources().getString(R.string.preference_loc_key_time_direction)//
				, false);
		kmUnit = mainContext.getResources().getString(R.string.preference_loc_default_measure).equals(measure);
	}

	public void setTheaterList(NearResp nearRespBean, Map<String, TheaterBean> theaterFavList, CineShowtimeComparator<?> comparator) {
		this.setNearRespList(nearRespBean, theaterFavList, comparator);
	}

	private void setNearRespList(NearResp nearRespBean, Map<String, TheaterBean> theaterFavList, CineShowtimeComparator<?> comparator) {
		this.nearRespBean = nearRespBean;
		this.theatherFavList = theaterFavList;
		if (this.nearRespBean != null) {
			this.theatherList = this.nearRespBean.getTheaterList();
		}
		if (comparator != null) {
			Collections.sort(theatherList, (Comparator<TheaterBean>) comparator);
		}

	}

	@Override
	public int getCount() {
		int result = (theatherList != null) ? theatherList.size() : 0;
		if ((nearRespBean != null) && nearRespBean.isHasMoreResults()) {
			result++;
		}
		return result;
	}

	@Override
	public Object getItem(int position) {
		Object result = null;
		if ((theatherList != null) && (position < theatherList.size())) {
			result = theatherList.get(position);
		}
		return result;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ObjectMasterView objectMasterView = null;
		if (convertView == null) {
			objectMasterView = new ObjectMasterView(mainContext, onClickListener);
		} else {
			objectMasterView = (ObjectMasterView) convertView;
		}

		TheaterBean theater = (TheaterBean) getItem(position);
		if (nearRespBean != null && nearRespBean.isHasMoreResults() && theater == null) {
			objectMasterView.setTheater(null, false);
		} else if (theater != null && theater.getTheaterName() != null) {
			objectMasterView.setTheater(theater, theatherFavList != null && theatherFavList.containsKey(theater.getId()));
		}
		return objectMasterView;
	}

}
