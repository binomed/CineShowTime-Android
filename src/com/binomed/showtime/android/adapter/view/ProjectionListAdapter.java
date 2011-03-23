package com.binomed.showtime.android.adapter.view;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.binomed.showtime.android.R;
import com.binomed.showtime.android.cst.AndShowtimeCst;
import com.binomed.showtime.android.util.AndShowtimeDateNumberUtil;
import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.cst.SpecialChars;

public class ProjectionListAdapter extends BaseAdapter {

	private MovieBean movieBean;
	private List<Long> projectionList;
	private Context mainContext;

	private Calendar timeInMillis;
	private Calendar movieTime;
	private long minTime;

	private HashMap<Integer, Long> mapMovieTime;
	private HashMap<Integer, StringBuilder> mapMovieStr;

	public ProjectionListAdapter(Context context, MovieBean movieBean, List<Long> projectionList, Long minTime) {
		super();
		timeInMillis = Calendar.getInstance();
		movieTime = Calendar.getInstance();
		mainContext = context;
		this.movieBean = movieBean;
		this.projectionList = projectionList;
		this.minTime = minTime;
		mapMovieTime = new HashMap<Integer, Long>();
		mapMovieStr = new HashMap<Integer, StringBuilder>();
	}

	@Override
	public int getCount() {
		return (projectionList != null) ? projectionList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		Long projectionTime = null;
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
		TextView projectionView = null;
		if (convertView == null) {
			projectionView = getGenericView();
		} else {
			projectionView = (TextView) convertView;
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

		if (projectionBuilder == null) {
			projectionBuilder = new StringBuilder(SpecialChars.EMPTY);
			Long projectionTime = (Long) getItem(groupPosition);
			int passedShowtime;
			passedShowtime = AndShowtimeDateNumberUtil.getPositionTime(projectionTime, minTime);

			Calendar timeInMillis = this.timeInMillis;
			Calendar movieTime = this.movieTime;

			// StringBuilder projectionBuilder = new StringBuilder(context.getResources().getString(R.string.projectionTime));

			switch (passedShowtime) {
			case 0:
				projectionBuilder.append("<FONT COLOR=\"").append(AndShowtimeCst.COLOR_WHITE).append("\">") //$NON-NLS-1$ //$NON-NLS-2$
						.append("<b>"); //$NON-NLS-1$
				break;
			case 1:
				break;
			case -1:
				projectionBuilder.append("<FONT COLOR=\"").append(AndShowtimeCst.COLOR_GREY).append("\">") //$NON-NLS-1$//$NON-NLS-2$
						.append("<i>"); //$NON-NLS-1$
				break;
			default:
				break;
			}
			projectionBuilder.append(context.getResources().getString(R.string.projectionTime));
			projectionBuilder.append(AndShowtimeDateNumberUtil.showMovieTime(context, projectionTime));

			timeInMillis.setTimeInMillis(projectionTime);
			if (movieBean.getMovieTime() != null) {
				movieTime.setTimeInMillis(movieBean.getMovieTime());

				timeInMillis.add(Calendar.HOUR_OF_DAY, movieTime.get(Calendar.HOUR_OF_DAY));
				timeInMillis.add(Calendar.MINUTE, movieTime.get(Calendar.MINUTE) + 10);

				projectionBuilder.append("<br>")// //$NON-NLS-1$
						.append(context.getResources().getString(R.string.endHour));//

				projectionBuilder.append(AndShowtimeDateNumberUtil.showMovieTime(context, timeInMillis.getTimeInMillis()));
			}

			switch (passedShowtime) {
			case 0:
				projectionBuilder.append("</b>") //$NON-NLS-1$
						.append("</FONT>"); //$NON-NLS-1$
				break;
			case 1:
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

		projectionView.setText(Html.fromHtml(projectionBuilder.toString()));

		return projectionView;
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

}
