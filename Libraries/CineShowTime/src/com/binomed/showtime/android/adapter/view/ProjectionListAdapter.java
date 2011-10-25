/*
 * Copyright (C) 2011 Binomed (http://blog.binomed.fr)
 *
 * Licensed under the Eclipse Public License - v 1.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 * THE ACCOMPANYING PROGRAM IS PROVIDED UNDER THE TERMS OF THIS ECLIPSE PUBLIC 
 * LICENSE ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM 
 * CONSTITUTES RECIPIENT'S ACCEPTANCE OF THIS AGREEMENT.
 */
package com.binomed.showtime.android.adapter.view;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.binomed.showtime.R;
import com.binomed.showtime.android.layout.view.ProjectionView;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.ProjectionBean;
import com.binomed.showtime.android.util.CineShowtimeDateNumberUtil;
import com.binomed.showtime.cst.SpecialChars;

public class ProjectionListAdapter extends BaseAdapter {

	private MovieBean movieBean;
	private List<ProjectionBean> projectionList;
	private Context mainContext;

	private Calendar timeInMillis;
	private Calendar movieTime;
	private ProjectionBean minTime;

	private HashMap<Integer, Long> mapMovieTime;
	private HashMap<Integer, StringBuilder> mapMovieStr;
	private int minuteToAdd;
	private boolean blackTheme;
	private boolean format24;
	private OnClickListener clickListener;
	private static String passedDark, passedLight, nearDark, nearLight, nextDark, nextLight;

	public ProjectionListAdapter(Context context, MovieBean movieBean, List<ProjectionBean> projectionList, ProjectionBean minTime, OnClickListener clickListener) {
		super();
		timeInMillis = Calendar.getInstance();
		movieTime = Calendar.getInstance();
		mainContext = context;
		this.movieBean = movieBean;
		this.projectionList = projectionList;
		this.minTime = minTime;
		this.clickListener = clickListener;
		mapMovieTime = new HashMap<Integer, Long>();
		mapMovieStr = new HashMap<Integer, StringBuilder>();

		passedDark = context.getResources().getString(R.color.showtime_passed_dark).substring(0, 1) + context.getResources().getString(R.color.showtime_passed_dark).substring(3);
		passedLight = context.getResources().getString(R.color.showtime_passed_light).substring(0, 1) + context.getResources().getString(R.color.showtime_passed_light).substring(3);
		nearDark = context.getResources().getString(R.color.showtime_nearest_dark).substring(0, 1) + context.getResources().getString(R.color.showtime_nearest_dark).substring(3);
		nearLight = context.getResources().getString(R.color.showtime_nearest_light).substring(0, 1) + context.getResources().getString(R.color.showtime_nearest_light).substring(3);
		nextDark = context.getResources().getString(R.color.showtime_next_dark).substring(0, 1) + context.getResources().getString(R.color.showtime_next_dark).substring(3);
		nextLight = context.getResources().getString(R.color.showtime_next_light).substring(0, 1) + context.getResources().getString(R.color.showtime_next_light).substring(3);
		changePreferences();
	}

	public void changePreferences() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mainContext);
		String defaultTheme = prefs.getString(mainContext.getResources().getString(R.string.preference_gen_key_theme)//
				, mainContext.getResources().getString(R.string.preference_gen_default_theme));
		minuteToAdd = Integer.valueOf(prefs.getString(mainContext.getResources().getString(R.string.preference_gen_key_time_adds)//
				, mainContext.getResources().getString(R.string.preference_gen_default_time_adds)));
		blackTheme = mainContext.getResources().getString(R.string.preference_gen_default_theme).equals(defaultTheme);
		format24 = CineShowtimeDateNumberUtil.isFormat24(mainContext);
	}

	@Override
	public int getCount() {
		return (projectionList != null) ? projectionList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		ProjectionBean projectionTime = null;
		if ((projectionList != null) && (projectionList.size() >= position)) {
			projectionTime = projectionList.get(position);
		}
		return projectionTime;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int groupPosition, View convertView, ViewGroup parent) {
		ProjectionView projectionView = null;
		if (convertView == null) {
			projectionView = new ProjectionView(mainContext, clickListener);
		} else {
			projectionView = (ProjectionView) convertView;
		}

		Context context = mainContext;

		StringBuilder projectionBuilder = null;
		Long curTime = Calendar.getInstance().getTimeInMillis();
		synchronized (mapMovieStr) {
			projectionBuilder = mapMovieStr.get(groupPosition);
			if (projectionBuilder != null) {
				Long pastTime = mapMovieTime.get(groupPosition);
				if ((curTime - pastTime) > 3600000) {
					projectionBuilder = null;
				}
			}
		}

		ProjectionBean projectionBean = (ProjectionBean) getItem(groupPosition);
		if (projectionBuilder == null) {
			projectionBuilder = new StringBuilder(SpecialChars.EMPTY);
			int passedShowtime;
			passedShowtime = CineShowtimeDateNumberUtil.getPositionTime(projectionBean.getShowtime(), (minTime != null) ? minTime.getShowtime() : -1l);

			if (projectionBean.getLang() != null && projectionBean.getLang().length() > 0) {
				projectionBuilder.append("<FONT COLOR=\"").append(blackTheme ? nearDark : nearLight).append("\">") //$NON-NLS-1$ //$NON-NLS-2$
						//				projectionBuilder.append("<FONT COLOR=\"").append(context.getResources().getString(R.color.showtime_passed_dark):context.getResources().getString(R.color.showtime_passed_light)CineShowTimeLayoutUtils.getColorLang(blackTheme)).append("\">") //$NON-NLS-1$ //$NON-NLS-2$
						.append(projectionBean.getLang()) //$NON-NLS-1$//$NON-NLS-2$
						.append(" : </FONT>"); //$NON-NLS-1$
			}

			Calendar timeInMillis = this.timeInMillis;
			Calendar movieTime = this.movieTime;

			// StringBuilder projectionBuilder = new StringBuilder(context.getResources().getString(R.string.projectionTime));

			switch (passedShowtime) {
			case 0:
				projectionBuilder.append("<FONT COLOR=\"").append(blackTheme ? nearDark : nearLight).append("\">") //$NON-NLS-1$ //$NON-NLS-2$
						.append("<b>"); //$NON-NLS-1$
				break;
			case 1:
				projectionBuilder.append("<FONT COLOR=\"").append(blackTheme ? nextDark : nextLight).append("\">"); //$NON-NLS-1$ //$NON-NLS-2$
				break;
			case -1:
				projectionBuilder.append("<FONT COLOR=\"").append(blackTheme ? passedDark : passedLight).append("\">") //$NON-NLS-1$//$NON-NLS-2$
						.append("<i>"); //$NON-NLS-1$
				break;
			default:
				break;
			}
			// projectionBuilder.append(context.getResources().getString(R.string.projectionTime));
			projectionBuilder.append(CineShowtimeDateNumberUtil.showMovieTime(context, projectionBean.getShowtime(), format24));

			timeInMillis.setTimeInMillis(projectionBean.getShowtime());
			if (movieBean.getMovieTime() != null) {
				movieTime.setTimeInMillis(movieBean.getMovieTime());

				timeInMillis.add(Calendar.HOUR_OF_DAY, movieTime.get(Calendar.HOUR_OF_DAY));
				timeInMillis.add(Calendar.MINUTE, movieTime.get(Calendar.MINUTE) + minuteToAdd);
				projectionBuilder.append("<br>")// //$NON-NLS-1$
						.append(context.getResources().getString(R.string.endHour));//

				projectionBuilder.append(CineShowtimeDateNumberUtil.showMovieTime(context, timeInMillis.getTimeInMillis(), format24));
			}

			switch (passedShowtime) {
			case 0:
				projectionBuilder.append("</b>") //$NON-NLS-1$
						.append("</FONT>"); //$NON-NLS-1$
				break;
			case 1:
				projectionBuilder.append("</FONT>"); //$NON-NLS-1$
				break;
			case -1:
				projectionBuilder.append("</i>") //$NON-NLS-1$ 
						.append("</FONT>"); //$NON-NLS-1$
				break;
			default:
				break;
			}

			synchronized (mapMovieStr) {
				mapMovieStr.put(groupPosition, projectionBuilder);
				mapMovieTime.put(groupPosition, curTime);
			}
		}

		projectionView.setProjectionBean(projectionBean, Html.fromHtml(projectionBuilder.toString()));

		return projectionView;
	}

}
