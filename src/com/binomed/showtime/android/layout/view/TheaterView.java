package com.binomed.showtime.android.layout.view;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.cst.AndShowtimeCst;
import com.binomed.showtime.android.util.AndShowtimeDateNumberUtil;
import com.binomed.showtime.beans.TheaterBean;

public class TheaterView extends LinearLayout {

	private TextView theaterName;
	private TextView movieList;
	private TheaterBean theaterBean;
	private boolean kmUnit;
	private boolean distanceTime;

	public TheaterBean getTheaterBean() {
		return theaterBean;
	}

	public TheaterView(Context context) {
		super(context);
		this.setOrientation(VERTICAL);

		// Here we build the child views in code. They could also have
		// been specified in an XML file.
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String measure = prefs.getString(context.getResources().getString(R.string.preference_loc_key_measure) //
				, context.getResources().getString(R.string.preference_loc_default_measure));
		distanceTime = prefs.getBoolean(context.getResources().getString(R.string.preference_loc_key_time_direction) //
				, false);
		kmUnit = context.getResources().getString(R.string.preference_loc_default_measure).equals(measure);

		theaterName = new TextView(context);
		addView(theaterName, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		movieList = new TextView(context);
		addView(movieList, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	}

	public void setTheater(TheaterBean theaterBean) {
		this.theaterBean = theaterBean;

		StringBuilder strTheater = new StringBuilder(theaterBean.getTheaterName());
		if ((theaterBean.getPlace() != null) && theaterBean.getPlace().getDistance() != null) {
			strTheater.append(" (");
			strTheater.append(AndShowtimeDateNumberUtil.showDistance(theaterBean.getPlace().getDistance(), !kmUnit));
			strTheater.append(")");
		}
		theaterName.setText(strTheater.toString());
		StringBuilder movieListStr = new StringBuilder("");

		boolean first = true;
		Set<String> movieIdSet = theaterBean.getMovieMap().keySet();
		List<Long> projectionList = theaterBean.getMovieMap().get(movieIdSet.toArray()[0]);
		int passedShowtime;
		Long distanceTimeLong = null;
		if (distanceTime && theaterBean.getPlace() != null) {
			distanceTimeLong = theaterBean.getPlace().getDistanceTime();
		}
		// long minTime = AndShowtimeDateNumberUtil.getMinTime(projectionList);
		List<Long>[] orderTimeListArray = AndShowtimeDateNumberUtil.getTimeOrder(projectionList, Calendar.getInstance().getTimeInMillis(), distanceTimeLong);
		for (long projectionTime : orderTimeListArray[0]) {
			if (!first) {
				movieListStr.append(" | ");
			} else {
				first = false;
			}

			movieListStr.append("<FONT COLOR=\"").append(AndShowtimeCst.COLOR_GREY).append("\">") //$NON-NLS-1$//$NON-NLS-2$
					.append("<i>").append(AndShowtimeDateNumberUtil.showMovieTime(getContext(), projectionTime)).append("</i>") //$NON-NLS-1$ //$NON-NLS-2$
					.append("</FONT>"); //$NON-NLS-1$
		}
		for (long projectionTime : orderTimeListArray[1]) {
			if (!first) {
				movieListStr.append(" | ");
			} else {
				first = false;
			}

			movieListStr.append("<FONT COLOR=\"").append(AndShowtimeCst.COLOR_WHITE).append("\">") //$NON-NLS-1$ //$NON-NLS-2$
					.append("<b>").append(AndShowtimeDateNumberUtil.showMovieTime(getContext(), projectionTime)).append("</b>") //$NON-NLS-1$//$NON-NLS-2$
					.append("</FONT>"); //$NON-NLS-1$
		}
		for (long projectionTime : orderTimeListArray[2]) {
			if (!first) {
				movieListStr.append(" | ");
			} else {
				first = false;
			}

			movieListStr.append(AndShowtimeDateNumberUtil.showMovieTime(getContext(), projectionTime));

		}
		movieListStr.append("\n");

		movieList.setText(Html.fromHtml(movieListStr.toString()));
	}
}
