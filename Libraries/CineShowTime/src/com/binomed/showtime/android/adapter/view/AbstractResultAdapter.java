package com.binomed.showtime.android.adapter.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.binomed.showtime.R;
import com.binomed.showtime.android.layout.view.ObjectMasterView;
import com.binomed.showtime.android.layout.view.ObjectSubViewNew;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.NearResp;
import com.binomed.showtime.android.model.ProjectionBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.util.CineShowtimeDateNumberUtil;
import com.binomed.showtime.android.util.CineShowtimeFactory;
import com.binomed.showtime.android.util.comparator.CineShowtimeComparator;
import com.binomed.showtime.android.util.comparator.TheaterShowtimeComparator;

public class AbstractResultAdapter {

	private static final String TAG = "AbstractResultAdapter";

	private NearResp nearRespBean;
	private Map<String, TheaterBean> theatherFavList;
	private Map<String, TheaterBean> theatherMap;
	private HashMap<String, List<Entry<String, List<ProjectionBean>>>> projectionsThMap;
	// private HashMap<String, List<String>> moviesForTheater;
	private List<String>[] moviesForTheater;
	private List<TheaterBean> theatherList;
	private List<MovieBean> movieList;
	private Context mainContext;
	private boolean kmUnit;
	private boolean movieView;
	private boolean distanceTime;
	private boolean blackTheme;
	private boolean format24;
	private CineShowtimeComparator<?> comparator;
	private OnClickListener onClickListener;

	private HashMap<String, ObjectMasterView> theaterMapMasterView;
	private HashMap<String, ObjectSubViewNew> theaterMapSubView;

	public AbstractResultAdapter(Context context, OnClickListener listener) {
		// super();
		mainContext = context;
		this.onClickListener = listener;
		this.theaterMapMasterView = new HashMap<String, ObjectMasterView>();
		this.theaterMapSubView = new HashMap<String, ObjectSubViewNew>();
		changePreferences();
	}

	public List<MovieBean> getMovieList() {
		return movieList;
	}

	public void changePreferences() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mainContext);
		String measure = prefs.getString(mainContext.getResources().getString(R.string.preference_loc_key_measure)//
				, mainContext.getResources().getString(R.string.preference_loc_default_measure));
		String defaultTheme = prefs.getString(mainContext.getResources().getString(R.string.preference_gen_key_theme)//
				, mainContext.getResources().getString(R.string.preference_gen_default_theme));
		distanceTime = prefs.getBoolean(mainContext.getResources().getString(R.string.preference_loc_key_time_direction)//
				, false);
		kmUnit = mainContext.getResources().getString(R.string.preference_loc_default_measure).equals(measure);
		blackTheme = mainContext.getResources().getString(R.string.preference_gen_default_theme).equals(defaultTheme);
		format24 = CineShowtimeDateNumberUtil.isFormat24(mainContext);
	}

	public void setTheaterList(NearResp nearRespBean, Map<String, TheaterBean> theaterFavList, CineShowtimeComparator<?> comparator) {
		this.setNearRespList(nearRespBean, theaterFavList, comparator, comparator != null ? comparator.getType() == comparator.COMPARATOR_MOVIE_NAME : false);
	}

	public void changeSort(CineShowtimeComparator<?> comparator) {
		this.theaterMapMasterView.clear();
		this.theaterMapSubView.clear();
		this.movieView = comparator.getType() == comparator.COMPARATOR_MOVIE_NAME;
		this.comparator = comparator;
		switch (comparator.getType()) {
		case CineShowtimeComparator.COMPARATOR_MOVIE_ID: {
			break;
		}
		case CineShowtimeComparator.COMPARATOR_MOVIE_NAME: {
			if (movieList != null) {
				Collections.sort(movieList, (Comparator<MovieBean>) comparator);
			}
		}
			break;
		case CineShowtimeComparator.COMPARATOR_THEATER_NAME:
		case CineShowtimeComparator.COMPARATOR_THEATER_DISTANCE:
		case CineShowtimeComparator.COMPARATOR_THEATER_SHOWTIME: {
			if (theatherList != null) {
				Collections.sort(theatherList, (Comparator<TheaterBean>) comparator);
			}
		}
			break;
		default:
			break;
		}
	}

	public void refreshTheater(String theaterId) {
		if (theatherMap.containsKey(theaterId)) {
			TheaterBean theater = theatherMap.get(theaterId);
			ObjectMasterView masterView = theaterMapMasterView.get(theaterId);
			ObjectSubViewNew subView = theaterMapSubView.get(theaterId);
			if (!movieView && (masterView != null)) {
				masterView.setTheater(theater, theatherFavList.containsKey(theaterId), false, blackTheme);
			}
			if (movieView && (subView != null)) {
				subView.setMovie(subView.getMovieBean(), theater, distanceTime, movieView, blackTheme, format24, false);

			}
		}
	}

	private void setNearRespList(NearResp nearRespBean, Map<String, TheaterBean> theaterFavList, CineShowtimeComparator<?> comparator, boolean movieView) {
		this.movieView = movieView;
		this.nearRespBean = nearRespBean;
		this.theatherFavList = theaterFavList;
		this.projectionsThMap = new HashMap<String, List<Entry<String, List<ProjectionBean>>>>();
		this.moviesForTheater = new List[nearRespBean.getTheaterList().size()];
		this.theaterMapMasterView.clear();
		this.theaterMapSubView.clear();
		// this.projectionsMovMap = new HashMap<String, List<Entry<String, List<ProjectionBean>>>>();
		this.theatherMap = new HashMap<String, TheaterBean>();
		if (this.nearRespBean != null) {
			this.theatherList = this.nearRespBean.getTheaterList();
			this.movieList = this.nearRespBean.getMapMovies() != null ? new ArrayList<MovieBean>(this.nearRespBean.getMapMovies().values()) : null;
			int i = 0;
			for (TheaterBean theaterTmp : this.nearRespBean.getTheaterList()) {
				// this.moviesForTheater.put(theaterTmp.getId(), new ArrayList<String>(theaterTmp.getMovieMap().keySet()));
				this.moviesForTheater[i] = new ArrayList<String>(theaterTmp.getMovieMap().keySet());
				i++;
			}
		}
		if (comparator != null) {
			switch (comparator.getType()) {
			case CineShowtimeComparator.COMPARATOR_MOVIE_ID: {
				break;
			}
			case CineShowtimeComparator.COMPARATOR_MOVIE_NAME: {
				if (this.movieList != null) {
					Collections.sort(movieList, (Comparator<MovieBean>) comparator);
				}
			}
				break;
			case CineShowtimeComparator.COMPARATOR_THEATER_SHOWTIME: {
				if (theatherList != null) {
					List<Entry<String, List<ProjectionBean>>> entries = null;
					List<TheaterBean> tempTheaterList = new ArrayList<TheaterBean>(theatherList);
					for (TheaterBean theater : tempTheaterList) {
						entries = projectionsThMap.get(theater.getId());

						if ((entries == null) && (theater.getMovieMap() != null)) {

							entries = new ArrayList<Entry<String, List<ProjectionBean>>>(theater.getMovieMap().entrySet());
							Collections.sort(entries, CineShowtimeFactory.getTheaterShowtimeInnerListComparator());
							projectionsThMap.put(theater.getId(), entries);

						}
					}
				}
			}
			case CineShowtimeComparator.COMPARATOR_THEATER_NAME:
			case CineShowtimeComparator.COMPARATOR_THEATER_DISTANCE: {
				if (theatherList != null) {
					Collections.sort(theatherList, (Comparator<TheaterBean>) comparator);
				}
			}
				break;
			default:
				break;
			}
		}
		if (theatherList != null) {
			// On doit initialiser avant tout cette map
			for (TheaterBean theater : theatherList) {
				theatherMap.put(theater.getId(), theater);
			}
		}

	}

	public Object getChild(int groupPosition, int childPosition) {
		Object result = null;
		if (theatherList != null) {
			if (!movieView) {
				TheaterBean theater = theatherList.get(groupPosition);
				if (theater.getMovieMap().size() >= childPosition) {
					if ((comparator != null) && (comparator.getClass().equals(TheaterShowtimeComparator.class))) {

						List<Entry<String, List<ProjectionBean>>> entries = projectionsThMap.get(theater.getId());
						if (entries == null) {
							entries = new ArrayList<Entry<String, List<ProjectionBean>>>(theater.getMovieMap().entrySet());
							Collections.sort(entries, CineShowtimeFactory.getTheaterShowtimeInnerListComparator());
							projectionsThMap.put(theater.getId(), entries);
						}
						result = nearRespBean.getMapMovies().get(entries.get(childPosition).getKey());
					} else {
						// String movieId = moviesForTheater.get(theater.getId()).get(childPosition);
						String movieId = moviesForTheater[groupPosition].get(childPosition);
						result = nearRespBean.getMapMovies().get(movieId);
					}
				}
			} else {
				MovieBean movie = movieList.get(groupPosition);
				if (movie.getTheaterList().size() >= childPosition) {
					String theaterId = movie.getTheaterList().get(childPosition);
					result = theatherMap.get(theaterId);
				}
			}
		}
		return result;
	}

	public int getChildrenCount(int groupPosition) {
		int result = 0;
		try {
			if (!movieView) {
				if ((theatherList != null) && (groupPosition < theatherList.size())) {
					result = theatherList.get(groupPosition).getMovieMap().size();
				}
			} else {
				if ((movieList != null) && (groupPosition < movieList.size())) {
					result = movieList.get(groupPosition).getTheaterList().size();
				}

			}
		} catch (Exception e) {
			Log.e(TAG, "Error getting childCount", e);
		}
		return result;
	}

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

		ObjectSubViewNew subView = null;
		if (convertView == null) {
			subView = new ObjectSubViewNew(mainContext, kmUnit);
		} else {
			subView = (ObjectSubViewNew) convertView;
		}
		MovieBean movieBean = null;
		TheaterBean theaterBean = null;

		if (!movieView) {
			movieBean = (MovieBean) getChild(groupPosition, childPosition);
			theaterBean = (TheaterBean) getGroup(groupPosition);
			if (!theaterMapSubView.containsKey(theaterBean.getId())) {
				theaterMapSubView.put(theaterBean.getId(), subView);
			}
		} else {
			movieBean = (MovieBean) getGroup(groupPosition);
			theaterBean = (TheaterBean) getChild(groupPosition, childPosition);

		}

		subView.setMovie(movieBean//
				, theaterBean//
				, distanceTime//
				, movieView //
				, blackTheme//
				, format24//
				, false //
		);
		return subView;
	}

	public Object getGroup(int groupPosition) {
		Object result = null;
		if (!movieView) {
			if ((theatherList != null) && (groupPosition < theatherList.size())) {
				result = theatherList.get(groupPosition);
			}
		} else {
			if ((movieList != null) && (groupPosition < movieList.size())) {
				result = movieList.get(groupPosition);
			}

		}
		return result;
	}

	public int getGroupCount() {
		int result = !movieView ? ((theatherList != null) ? theatherList.size() : 0) : ((movieList != null) ? movieList.size() : 0);
		if ((nearRespBean != null) && nearRespBean.isHasMoreResults()) {
			result++;
		}
		return result;
	}

	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

		ObjectMasterView objectMasterView = null;
		if (convertView == null) {
			objectMasterView = new ObjectMasterView(mainContext, onClickListener);
		} else {
			objectMasterView = (ObjectMasterView) convertView;
		}

		if (!movieView) {
			TheaterBean theater = (TheaterBean) getGroup(groupPosition);
			if ((nearRespBean != null) && nearRespBean.isHasMoreResults() && (theater == null)) {
				objectMasterView.setTheater(null, false, false, blackTheme);
			} else if ((theater != null) && (theater.getTheaterName() != null)) {
				if (!theaterMapMasterView.containsKey(theater.getId())) {
					theaterMapMasterView.put(theater.getId(), objectMasterView);
				}
				objectMasterView.setTheater(theater, (theatherFavList != null) && theatherFavList.containsKey(theater.getId()), false, blackTheme);
			}
		} else {
			MovieBean movie = (MovieBean) getGroup(groupPosition);
			objectMasterView.setMovie(movie, false, false);
		}
		return objectMasterView;
	}

}
