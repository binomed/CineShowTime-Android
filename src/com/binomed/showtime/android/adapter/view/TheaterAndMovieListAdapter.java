package com.binomed.showtime.android.adapter.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.binomed.showtime.android.R;
import com.binomed.showtime.android.layout.view.MovieView;
import com.binomed.showtime.android.util.AndShowtimeDateNumberUtil;
import com.binomed.showtime.android.util.AndShowtimeFactory;
import com.binomed.showtime.android.util.comparator.TheaterShowtimeComparator;
import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.NearResp;
import com.binomed.showtime.beans.TheaterBean;

public class TheaterAndMovieListAdapter extends BaseExpandableListAdapter {

	private NearResp nearRespBean;
	private List<TheaterBean> theatherList;
	private Context mainContext;
	private Comparator<TheaterBean> comparator;
	private boolean kmUnit;
	private HashMap<String, List<Entry<String, List<Long>>>> projectionsMap;

	public TheaterAndMovieListAdapter(Context context, NearResp nearRespBean, Comparator<TheaterBean> comparator) {
		super();
		mainContext = context;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String measure = prefs.getString(context.getResources().getString(R.string.preference_loc_key_measure)//
				, context.getResources().getString(R.string.preference_loc_default_measure));
		kmUnit = context.getResources().getString(R.string.preference_loc_default_measure).equals(measure);
		this.nearRespBean = nearRespBean;
		this.comparator = comparator;
		this.projectionsMap = new HashMap<String, List<Entry<String, List<Long>>>>();
		if (this.nearRespBean != null) {
			this.theatherList = this.nearRespBean.getTheaterList();
		}
		if (comparator != null) {
			Collections.sort(theatherList, comparator);
		}
	}

	public Object getChild(int groupPosition, int childPosition) {
		Object result = null;
		if (theatherList != null) {
			TheaterBean theater = theatherList.get(groupPosition);
			if (theater.getMovieMap().size() >= childPosition) {
				if ((comparator != null) && (comparator.getClass().equals(TheaterShowtimeComparator.class))) {

					List<Entry<String, List<Long>>> entries = projectionsMap.get(theater.getId());
					if (entries == null) {
						entries = new ArrayList<Entry<String, List<Long>>>(theater.getMovieMap().entrySet());
						Collections.sort(entries, AndShowtimeFactory.getTheaterShowtimeInnerListComparator());
						projectionsMap.put(theater.getId(), entries);
					}
					result = nearRespBean.getMapMovies().get(entries.get(childPosition).getKey());
				} else {
					String movieId = theater.getMovieMap().keySet().toArray()[childPosition].toString();
					result = nearRespBean.getMapMovies().get(movieId);
				}
			}
		}
		return result;
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public int getChildrenCount(int groupPosition) {
		int result = 0;
		if ((theatherList != null) && (groupPosition < theatherList.size())) {
			result = theatherList.get(groupPosition).getMovieMap().size();
		}
		return result;
	}

	public TextView getGenericView() {
		// Layout parameters for the ExpandableListView
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 64);

		TextView textView = new TextView(mainContext);
		textView.setLayoutParams(lp);
		// Center the text vertically
		textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		// Set the text starting position
		textView.setPadding(36, 0, 0, 0);
		return textView;
	}

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		MovieView movieView = null;
		if (convertView == null) {
			movieView = new MovieView(mainContext);
		} else {
			movieView = (MovieView) convertView;
		}
		movieView.setMovie((MovieBean) getChild(groupPosition, childPosition)//
				, (TheaterBean) getGroup(groupPosition)//
				);
		return movieView;
	}

	public Object getGroup(int groupPosition) {
		Object result = null;
		if ((theatherList != null) && (groupPosition < theatherList.size())) {
			result = theatherList.get(groupPosition);
		}
		return result;
	}

	public int getGroupCount() {
		int result = (theatherList != null) ? theatherList.size() : 0;
		if ((nearRespBean != null) && nearRespBean.isHasMoreResults()) {
			result++;
		}
		return result;
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

		TextView textView = null;
		if (convertView == null) {
			textView = getGenericView();
		} else {
			textView = (TextView) convertView;
		}

		TheaterBean theater = (TheaterBean) getGroup(groupPosition);
		if (nearRespBean != null && nearRespBean.isHasMoreResults() && theater == null) {
			textView.setText(mainContext.getResources().getString(R.string.itemMoreTheaters));
		} else if (theater != null) {
			StringBuilder strBuilder = new StringBuilder(theater.getTheaterName());
			if ((theater.getPlace() != null) && theater.getPlace().getDistance() != null) {
				strBuilder.append("\n");
				strBuilder.append(AndShowtimeDateNumberUtil.showDistance(theater.getPlace().getDistance(), !kmUnit));
			}
			textView.setText(strBuilder.toString());
		}
		return textView;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public boolean hasStableIds() {
		return true;
	}

}
