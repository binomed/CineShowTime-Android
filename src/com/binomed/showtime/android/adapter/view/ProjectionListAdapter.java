package com.binomed.showtime.android.adapter.view;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;

import com.binomed.showtime.R;
import com.binomed.showtime.android.layout.view.ProjectionView;
import com.binomed.showtime.android.util.AndShowTimeLayoutUtils;
import com.binomed.showtime.android.util.AndShowtimeDateNumberUtil;
import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.ProjectionBean;
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
		changePreferences();
	}

	public void changePreferences() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mainContext);
		String defaultTheme = prefs.getString(mainContext.getResources().getString(R.string.preference_gen_key_theme)//
				, mainContext.getResources().getString(R.string.preference_gen_default_theme));
		minuteToAdd = Integer.valueOf(prefs.getString(mainContext.getResources().getString(R.string.preference_gen_key_time_adds)//
				, mainContext.getResources().getString(R.string.preference_gen_default_time_adds)));
		blackTheme = mainContext.getResources().getString(R.string.preference_gen_default_theme).equals(defaultTheme);
		format24 = AndShowtimeDateNumberUtil.isFormat24(mainContext);
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
			passedShowtime = AndShowtimeDateNumberUtil.getPositionTime(projectionBean.getShowtime(), (minTime != null) ? minTime.getShowtime() : -1l);

			if (projectionBean.getLang() != null) {
				projectionBuilder.append("<FONT COLOR=\"").append(AndShowTimeLayoutUtils.getColorLang(blackTheme)).append("\">") //$NON-NLS-1$ //$NON-NLS-2$
						.append(projectionBean.getLang()) //$NON-NLS-1$//$NON-NLS-2$
						.append(" : </FONT>"); //$NON-NLS-1$
			}

			Calendar timeInMillis = this.timeInMillis;
			Calendar movieTime = this.movieTime;

			// StringBuilder projectionBuilder = new StringBuilder(context.getResources().getString(R.string.projectionTime));

			switch (passedShowtime) {
			case 0:
				projectionBuilder.append("<FONT COLOR=\"").append(AndShowTimeLayoutUtils.getColorNearestShowTime(blackTheme)).append("\">") //$NON-NLS-1$ //$NON-NLS-2$
						.append("<b>"); //$NON-NLS-1$
				break;
			case 1:
				projectionBuilder.append("<FONT COLOR=\"").append(AndShowTimeLayoutUtils.getColorNextShowTime(blackTheme)).append("\">"); //$NON-NLS-1$ //$NON-NLS-2$
				break;
			case -1:
				projectionBuilder.append("<FONT COLOR=\"").append(AndShowTimeLayoutUtils.getColorPassedShowTime(blackTheme)).append("\">") //$NON-NLS-1$//$NON-NLS-2$
						.append("<i>"); //$NON-NLS-1$
				break;
			default:
				break;
			}
			// projectionBuilder.append(context.getResources().getString(R.string.projectionTime));
			projectionBuilder.append(AndShowtimeDateNumberUtil.showMovieTime(context, projectionBean.getShowtime(), format24));

			timeInMillis.setTimeInMillis(projectionBean.getShowtime());
			if (movieBean.getMovieTime() != null) {
				movieTime.setTimeInMillis(movieBean.getMovieTime());

				timeInMillis.add(Calendar.HOUR_OF_DAY, movieTime.get(Calendar.HOUR_OF_DAY));
				timeInMillis.add(Calendar.MINUTE, movieTime.get(Calendar.MINUTE) + minuteToAdd);
				projectionBuilder.append("<br>")// //$NON-NLS-1$
						.append(context.getResources().getString(R.string.endHour));//

				projectionBuilder.append(AndShowtimeDateNumberUtil.showMovieTime(context, timeInMillis.getTimeInMillis(), format24));
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
