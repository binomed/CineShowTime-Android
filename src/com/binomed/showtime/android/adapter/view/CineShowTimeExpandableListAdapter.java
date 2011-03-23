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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;

import com.binomed.showtime.R;
import com.binomed.showtime.android.layout.view.ObjectMasterView;
import com.binomed.showtime.android.layout.view.ObjectSubView;
import com.binomed.showtime.android.util.AndShowtimeDateNumberUtil;
import com.binomed.showtime.android.util.AndShowtimeFactory;
import com.binomed.showtime.android.util.comparator.AndShowtimeComparator;
import com.binomed.showtime.android.util.comparator.TheaterShowtimeComparator;
import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.MovieResp;
import com.binomed.showtime.beans.NearResp;
import com.binomed.showtime.beans.ProjectionBean;
import com.binomed.showtime.beans.TheaterBean;

public class CineShowTimeExpandableListAdapter extends BaseExpandableListAdapter {

	private NearResp nearRespBean;
	private Map<String, TheaterBean> theatherFavList;
	private Map<String, TheaterBean> theatherMap;
	private List<TheaterBean> theatherList;
	private List<MovieBean> movieList;
	private Context mainContext;
	private AndShowtimeComparator<?> comparator;
	private boolean kmUnit;
	private boolean distanceTime;
	private boolean movieView;
	private boolean blackTheme;
	private boolean format24;
	private HashMap<String, List<Entry<String, List<ProjectionBean>>>> projectionsThMap;
	// private HashMap<String, List<Entry<String, List<ProjectionBean>>>> projectionsMovMap;
	private OnClickListener onClickListener;

	private static final String TAG = "CineShowTimeExpandableListAdapter";

	public CineShowTimeExpandableListAdapter(Context context, OnClickListener listener) {
		// super();
		mainContext = context;
		this.onClickListener = listener;
		changePreferences();

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
		format24 = AndShowtimeDateNumberUtil.isFormat24(mainContext);
	}

	public void setMovieList(MovieResp movieRespBean, AndShowtimeComparator<?> comparator) {
		this.movieView = true;
		NearResp transformNearResp = new NearResp();
		transformNearResp.setTheaterList(new ArrayList<TheaterBean>());
		transformNearResp.setHasMoreResults(false);
		transformNearResp.setMapMovies(new HashMap<String, MovieBean>());
		transformNearResp.setCityName(movieRespBean.getCityName());
		MovieBean movie = movieRespBean.getMovie();
		transformNearResp.getMapMovies().put(movie.getId(), movie);
		for (TheaterBean theaterBean : movieRespBean.getTheaterList()) {
			transformNearResp.getTheaterList().add(theaterBean);
		}
		this.setNearRespList(transformNearResp, null, comparator, true);
	}

	public void setTheaterList(NearResp nearRespBean, Map<String, TheaterBean> theaterFavList, AndShowtimeComparator<?> comparator) {
		this.setNearRespList(nearRespBean, theaterFavList, comparator, comparator.getType() == comparator.COMPARATOR_MOVIE_NAME);
	}

	public void changeSort(AndShowtimeComparator<?> comparator) {
		this.movieView = comparator.getType() == comparator.COMPARATOR_MOVIE_NAME;
		this.comparator = comparator;
		switch (comparator.getType()) {
		case AndShowtimeComparator.COMPARATOR_MOVIE_ID: {
			break;
		}
		case AndShowtimeComparator.COMPARATOR_MOVIE_NAME: {
			Collections.sort(movieList, (Comparator<MovieBean>) comparator);
		}
			break;
		case AndShowtimeComparator.COMPARATOR_THEATER_NAME:
		case AndShowtimeComparator.COMPARATOR_THEATER_DISTANCE:
		case AndShowtimeComparator.COMPARATOR_THEATER_SHOWTIME: {
			Collections.sort(theatherList, (Comparator<TheaterBean>) comparator);
		}
			break;
		default:
			break;
		}
	}

	private void setNearRespList(NearResp nearRespBean, Map<String, TheaterBean> theaterFavList, AndShowtimeComparator<?> comparator, boolean movieView) {
		this.movieView = movieView;
		this.nearRespBean = nearRespBean;
		this.theatherFavList = theaterFavList;
		this.comparator = comparator;
		this.projectionsThMap = new HashMap<String, List<Entry<String, List<ProjectionBean>>>>();
		// this.projectionsMovMap = new HashMap<String, List<Entry<String, List<ProjectionBean>>>>();
		this.theatherMap = new HashMap<String, TheaterBean>();
		if (this.nearRespBean != null) {
			this.theatherList = this.nearRespBean.getTheaterList();
			this.movieList = this.nearRespBean.getMapMovies() != null ? new ArrayList<MovieBean>(this.nearRespBean.getMapMovies().values()) : null;
		}
		if (comparator != null) {
			switch (comparator.getType()) {
			case AndShowtimeComparator.COMPARATOR_MOVIE_ID: {
				break;
			}
			case AndShowtimeComparator.COMPARATOR_MOVIE_NAME: {
				if (this.movieList != null) {
					Collections.sort(movieList, (Comparator<MovieBean>) comparator);
				}
			}
				break;
			case AndShowtimeComparator.COMPARATOR_THEATER_NAME:
			case AndShowtimeComparator.COMPARATOR_THEATER_DISTANCE:
			case AndShowtimeComparator.COMPARATOR_THEATER_SHOWTIME: {
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

			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					AndShowtimeDateNumberUtil.clearMaps();
					Long distanceTimeLong = null;
					// En fonction du type d'affichage on
					List<Entry<String, List<ProjectionBean>>> entries = null;
					for (TheaterBean theater : theatherList) {
						entries = projectionsThMap.get(theater.getId());

						if (entries == null && theater.getMovieMap() != null) {

							entries = new ArrayList<Entry<String, List<ProjectionBean>>>(theater.getMovieMap().entrySet());
							Collections.sort(entries, AndShowtimeFactory.getTheaterShowtimeInnerListComparator());
							projectionsThMap.put(theater.getId(), entries);

							for (Entry<String, List<ProjectionBean>> entryMovieIdListProjection : theater.getMovieMap().entrySet()) {
								if (distanceTime && theater != null && theater.getPlace() != null) {
									distanceTimeLong = theater.getPlace().getDistanceTime();
								}
								AndShowtimeDateNumberUtil.getMovieViewStr(entryMovieIdListProjection.getKey(), theater.getId(), entryMovieIdListProjection.getValue(), mainContext, distanceTimeLong, true, true);
								AndShowtimeDateNumberUtil.getMovieViewStr(entryMovieIdListProjection.getKey(), theater.getId(), entryMovieIdListProjection.getValue(), mainContext, distanceTimeLong, true, false);
								AndShowtimeDateNumberUtil.getMovieViewStr(entryMovieIdListProjection.getKey(), theater.getId(), entryMovieIdListProjection.getValue(), mainContext, distanceTimeLong, false, true);
								AndShowtimeDateNumberUtil.getMovieViewStr(entryMovieIdListProjection.getKey(), theater.getId(), entryMovieIdListProjection.getValue(), mainContext, distanceTimeLong, false, false);
							}
						}
					}

					// TheaterBean theaterTmp = null;
					// Map<String, List<ProjectionBean>> mapThProjection = null;
					// for (MovieBean movie : movieList) {
					// entries = projectionsMovMap.get(movie.getId());
					// if (entries == null) {
					// mapThProjection = new HashMap<String, List<ProjectionBean>>();
					// for (String theaterId : movie.getTheaterList()) {
					// theaterTmp = theatherMap.get(theaterId);
					// mapThProjection.put(theaterId, theaterTmp.getMovieMap().get(movie.getId()));
					// }
					// entries = new ArrayList<Entry<String, List<ProjectionBean>>>(mapThProjection.entrySet());
					// Collections.sort(entries, AndShowtimeFactory.getTheaterShowtimeInnerListComparator());
					// projectionsMovMap.put(movie.getId(), entries);
					// }
					// }

				}
			});
			thread.start();
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
							Collections.sort(entries, AndShowtimeFactory.getTheaterShowtimeInnerListComparator());
							projectionsThMap.put(theater.getId(), entries);
						}
						result = nearRespBean.getMapMovies().get(entries.get(childPosition).getKey());
					} else {
						String movieId = theater.getMovieMap().keySet().toArray()[childPosition].toString();
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

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
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

		ObjectSubView subView = null;
		if (convertView == null) {
			subView = new ObjectSubView(mainContext, kmUnit);
		} else {
			subView = (ObjectSubView) convertView;
		}
		MovieBean movieBean = null;
		TheaterBean theaterBean = null;

		if (!movieView) {
			movieBean = (MovieBean) getChild(groupPosition, childPosition);
			theaterBean = (TheaterBean) getGroup(groupPosition);
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

	public long getGroupId(int groupPosition) {
		return groupPosition;
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
			if (nearRespBean != null && nearRespBean.isHasMoreResults() && theater == null) {
				objectMasterView.setTheater(null, false);
			} else if (theater != null && theater.getTheaterName() != null) {
				objectMasterView.setTheater(theater, theatherFavList != null && theatherFavList.containsKey(theater.getId()));
			}
		} else {
			MovieBean movie = (MovieBean) getGroup(groupPosition);
			objectMasterView.setMovie(movie, false);
		}
		return objectMasterView;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public boolean hasStableIds() {
		return true;
	}

}
