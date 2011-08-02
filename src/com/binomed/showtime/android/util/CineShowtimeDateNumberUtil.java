package com.binomed.showtime.android.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

import com.binomed.showtime.R;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.ProjectionBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.cst.SpecialChars;
import com.binomed.showtime.util.AndShowtimeNumberFormat;

public abstract class CineShowtimeDateNumberUtil {

	private static DecimalFormat decimalFormat = AndShowtimeNumberFormat.getFormat2Digit();

	private static final Float MILE_VALUE = new Float(0.621371192);

	private static Calendar time = Calendar.getInstance();

	private static HashMap<String, Long> mapMovieTime = new HashMap<String, Long>();
	private static HashMap<String, Spanned> mapMovieStr = new HashMap<String, Spanned>();
	private static HashMap<String, Spanned> mapMovieNameStr = new HashMap<String, Spanned>();
	private static HashMap<String, Spanned> mapTheaterNameStr = new HashMap<String, Spanned>();

	public static String showMovieTimeLength(Context context, MovieBean movieBean) {
		StringBuilder result = new StringBuilder("");
		if ((movieBean != null) && (movieBean.getMovieTime() != null)) {
			time.setTimeInMillis(movieBean.getMovieTime());
			result.append(decimalFormat.format(time.get(Calendar.HOUR_OF_DAY)));
			result.append(context.getResources().getString(R.string.hour));
			result.append(decimalFormat.format(time.get(Calendar.MINUTE)));
			result.append(context.getResources().getString(R.string.min));
		}
		return result.toString();
	}

	public static String showMovieTime(Context context, Long time, boolean format24) {
		return DateUtils.formatDateTime(context, time, (format24 ? DateUtils.FORMAT_24HOUR : DateUtils.FORMAT_12HOUR) | DateUtils.FORMAT_SHOW_TIME);
	}

	public static String showDistance(Float distance, boolean mileDst) {
		if (distance != null) {
			if (mileDst) {
				return AndShowtimeNumberFormat.getFormatGeoDist().format(distance * MILE_VALUE) + " ml"; //$NON-NLS-1$
			} else {
				return AndShowtimeNumberFormat.getFormatGeoDist().format(distance) + " km"; //$NON-NLS-1$
			}
		} else {
			return null;
		}
	}

	public static ArrayList<String> getSpinnerDaysValues(Context context) {
		ArrayList<String> spinnerValues = new ArrayList<String>();
		spinnerValues.add(context.getResources().getString(R.string.spinnerToday));
		Calendar dateToday = Calendar.getInstance();
		switch (dateToday.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.SUNDAY:
			spinnerValues.add(context.getResources().getString(R.string.spinnerMonday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerTuesday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerWenesday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerThursday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerFriday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerSaturday));
			break;
		case Calendar.MONDAY:
			spinnerValues.add(context.getResources().getString(R.string.spinnerTuesday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerWenesday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerThursday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerFriday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerSaturday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerSunday));
			break;
		case Calendar.TUESDAY:
			spinnerValues.add(context.getResources().getString(R.string.spinnerWenesday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerThursday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerFriday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerSaturday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerSunday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerMonday));
			break;
		case Calendar.WEDNESDAY:
			spinnerValues.add(context.getResources().getString(R.string.spinnerThursday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerFriday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerSaturday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerSunday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerMonday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerTuesday));
			break;
		case Calendar.THURSDAY:
			spinnerValues.add(context.getResources().getString(R.string.spinnerFriday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerSaturday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerSunday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerMonday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerTuesday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerWenesday));
			break;
		case Calendar.FRIDAY:
			spinnerValues.add(context.getResources().getString(R.string.spinnerSaturday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerSunday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerMonday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerTuesday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerWenesday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerThursday));
			break;
		case Calendar.SATURDAY:
			spinnerValues.add(context.getResources().getString(R.string.spinnerSunday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerMonday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerTuesday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerWenesday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerThursday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerFriday));
			break;
		default:
			break;
		}
		return spinnerValues;
	}

	public static String getDayString(Context context, long time) {
		String day = SpecialChars.EMPTY;
		Calendar today = Calendar.getInstance();
		Calendar showtime = Calendar.getInstance();
		showtime.setTimeInMillis(time);
		if (showtime.get(Calendar.DAY_OF_WEEK) == today.get(Calendar.DAY_OF_WEEK)) {
			day = context.getResources().getString(R.string.spinnerToday);
		} else {
			switch (showtime.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.SUNDAY:
				day = context.getResources().getString(R.string.spinnerSunday);
				break;
			case Calendar.MONDAY:
				day = context.getResources().getString(R.string.spinnerMonday);
				break;
			case Calendar.TUESDAY:
				day = context.getResources().getString(R.string.spinnerTuesday);
				break;
			case Calendar.WEDNESDAY:
				day = context.getResources().getString(R.string.spinnerWenesday);
				break;
			case Calendar.THURSDAY:
				day = context.getResources().getString(R.string.spinnerThursday);
				break;
			case Calendar.FRIDAY:
				day = context.getResources().getString(R.string.spinnerFriday);
				break;
			case Calendar.SATURDAY:
				day = context.getResources().getString(R.string.spinnerSaturday);
				break;
			default:
				break;
			}
		}
		return day;
	}

	public static int getValueDay(Context context, String string) {
		int day = 0;
		String today = context.getResources().getString(R.string.spinnerToday);
		String monday = context.getResources().getString(R.string.spinnerMonday);
		String tuesday = context.getResources().getString(R.string.spinnerTuesday);
		String wenesday = context.getResources().getString(R.string.spinnerWenesday);
		String thursday = context.getResources().getString(R.string.spinnerThursday);
		String friday = context.getResources().getString(R.string.spinnerFriday);
		String saturday = context.getResources().getString(R.string.spinnerSaturday);
		String sunday = context.getResources().getString(R.string.spinnerSunday);
		if (today.equals(string)) {
			day = 0;
		} else {
			Calendar dateToday = Calendar.getInstance();
			switch (dateToday.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.SUNDAY:
				if (monday.equals(string)) {
					day = 1;
				} else if (tuesday.equals(string)) {
					day = 2;
				} else if (wenesday.equals(string)) {
					day = 3;
				}
				break;
			case Calendar.MONDAY:
				if (tuesday.equals(string)) {
					day = 1;
				} else if (wenesday.equals(string)) {
					day = 2;
				} else if (thursday.equals(string)) {
					day = 3;
				}
				break;
			case Calendar.TUESDAY:
				if (wenesday.equals(string)) {
					day = 1;
				} else if (thursday.equals(string)) {
					day = 2;
				} else if (friday.equals(string)) {
					day = 3;
				}
				break;
			case Calendar.WEDNESDAY:
				if (thursday.equals(string)) {
					day = 1;
				} else if (friday.equals(string)) {
					day = 2;
				} else if (saturday.equals(string)) {
					day = 3;
				}
				break;
			case Calendar.THURSDAY:
				if (friday.equals(string)) {
					day = 1;
				} else if (saturday.equals(string)) {
					day = 2;
				} else if (sunday.equals(string)) {
					day = 3;
				}
				break;
			case Calendar.FRIDAY:
				if (saturday.equals(string)) {
					day = 1;
				} else if (sunday.equals(string)) {
					day = 2;
				} else if (monday.equals(string)) {
					day = 3;
				}
				break;
			case Calendar.SATURDAY:
				if (sunday.equals(string)) {
					day = 1;
				} else if (monday.equals(string)) {
					day = 2;
				} else if (tuesday.equals(string)) {
					day = 3;
				}
				break;
			default:
				break;
			}
		}
		return day;
	}

	public static int getPositionTime(long time, long minTime) {
		int result = -1;

		if (minTime == -1) {
			result = -1;
		} else if (minTime == time) {
			result = 0;
		} else if (minTime < time) {
			result = 1;
		}

		return result;
	}

	public static ProjectionBean getMinTime(List<ProjectionBean> timeList, Long distanceTime) {
		long currentTime = Calendar.getInstance().getTimeInMillis() + ((distanceTime != null) ? distanceTime : 0);

		long minDiff0 = -1;
		long minDiffTemp = 0;
		long minTime = -1;

		ProjectionBean result = null;

		// Calendar cal = Calendar.getInstance();
		if (timeList != null) {
			for (ProjectionBean timeTmp : timeList) {
				// cal.setTimeInMillis(timeTmp);
				minDiffTemp = (timeTmp.getShowtime() - currentTime);
				if (((minDiffTemp < (minDiff0)) || (minDiff0 == -1)) && (minDiffTemp > 0)) {
					minDiff0 = minDiffTemp;
					minTime = timeTmp.getShowtime();
					result = timeTmp;
				}
			}
		}

		return result;
	}

	public static List<ProjectionBean>[] getTimeOrder(List<ProjectionBean> timeList, long currentTime, Long distanceTime) {

		long curTime = currentTime + ((distanceTime != null) ? distanceTime : 0);
		ArrayList<ProjectionBean> beforeList = new ArrayList<ProjectionBean>();
		ArrayList<ProjectionBean> minList = new ArrayList<ProjectionBean>();
		ArrayList<ProjectionBean> afterList = new ArrayList<ProjectionBean>();
		ArrayList<ProjectionBean>[] timeOrderArray = new ArrayList[] { beforeList, minList, afterList };

		long minDiff0 = -1;
		long minDiffTemp = 0;
		ProjectionBean minTime = null;

		// Calendar cal = Calendar.getInstance();
		if (timeList != null) {
			boolean nearShowTime = true;
			for (ProjectionBean timeTmp : timeList) {
				if (timeTmp.getShowtime() < curTime) {

					beforeList.add(timeTmp);
				} else if (nearShowTime) {
					minList.add(timeTmp);
					nearShowTime = false;
				} else {
					afterList.add(timeTmp);

				}
				// cal.setTimeInMillis(timeTmp);
				// minDiffTemp = (timeTmp.getShowtime() - curTime);
				// if (((minDiffTemp < (minDiff0)) || (minDiff0 == -1)) && (minDiffTemp > 0)) {
				// minDiff0 = minDiffTemp;
				// minTime = timeTmp;
				// if (minList.size() > 0) {
				// beforeList.add(minList.remove(0));
				// }
				// minList.add(minTime);
				// } else if (minDiffTemp < 0) {
				// beforeList.add(timeTmp);
				// } else if (minDiffTemp > 0) {
				// afterList.add(timeTmp);
				//
				// }
			}
		}

		return timeOrderArray;
	}

	public static Spanned getMovieViewStr(String movieId, String theaterId, List<ProjectionBean> projectionList, Context context, Long distanceTime, boolean blackTheme, boolean format24) {
		Spanned spanned = null;
		Long curTime = Calendar.getInstance().getTimeInMillis();
		String key = new StringBuilder(movieId).append(theaterId).append(blackTheme).append(format24).toString();

		synchronized (mapMovieStr) {
			spanned = mapMovieStr.get(key);
			Long pastTime = mapMovieTime.get(key);
			if ((spanned != null) && (pastTime != null)) {
				if ((curTime - pastTime) > 600000) {
					spanned = null;
				}
			}
		}

		if (spanned == null) {
			StringBuilder movieListStr = null;
			movieListStr = new StringBuilder();
			boolean first = true;
			// TODO Gérer les liens et le coup VO VF
			Map<String, List<ProjectionBean>> splitProjectionBeanMap = CineShowtimeDateNumberUtil.splitProjectionList(projectionList);
			List<ProjectionBean>[] orderTimeListArray = null;
			for (Entry<String, List<ProjectionBean>> entryProjectionBeanList : splitProjectionBeanMap.entrySet()) {
				orderTimeListArray = CineShowtimeDateNumberUtil.getTimeOrder(entryProjectionBeanList.getValue(), curTime, distanceTime);

				if (entryProjectionBeanList.getKey() != null) {
					movieListStr.append("<FONT COLOR=\"").append(CineShowTimeLayoutUtils.getColorLang(blackTheme)).append("\">") //$NON-NLS-1$ //$NON-NLS-2$
							.append(entryProjectionBeanList.getKey()) //$NON-NLS-1$//$NON-NLS-2$
							.append(" : </FONT>"); //$NON-NLS-1$
				}
				first = true;

				for (ProjectionBean projectionTime : orderTimeListArray[0]) {
					if (!first) {
						movieListStr.append(" | ");
					} else {
						first = false;
					}
					movieListStr.append("<FONT COLOR=\"").append(CineShowTimeLayoutUtils.getColorPassedShowTime(blackTheme)).append("\">") //$NON-NLS-1$//$NON-NLS-2$
							.append("<i>").append(CineShowtimeDateNumberUtil.showMovieTime(context, projectionTime.getShowtime(), format24)).append("</i>") //$NON-NLS-1$ //$NON-NLS-2$
							.append("</FONT>"); //$NON-NLS-1$
				}
				for (ProjectionBean projectionTime : orderTimeListArray[1]) {
					if (!first) {
						movieListStr.append(" | ");
					} else {
						first = false;
					}
					movieListStr.append("<FONT COLOR=\"").append(CineShowTimeLayoutUtils.getColorNearestShowTime(blackTheme)).append("\">") //$NON-NLS-1$ //$NON-NLS-2$
							.append("<b>").append(CineShowtimeDateNumberUtil.showMovieTime(context, projectionTime.getShowtime(), format24)).append("</b>") //$NON-NLS-1$//$NON-NLS-2$
							.append("</FONT>"); //$NON-NLS-1$
				}
				for (ProjectionBean projectionTime : orderTimeListArray[2]) {
					if (!first) {
						movieListStr.append(" | ");
					} else {
						first = false;
					}
					movieListStr.append("<FONT COLOR=\"").append(CineShowTimeLayoutUtils.getColorNextShowTime(blackTheme)).append("\">") //$NON-NLS-1$ //$NON-NLS-2$
							.append(CineShowtimeDateNumberUtil.showMovieTime(context, projectionTime.getShowtime(), format24)) //$NON-NLS-1$//$NON-NLS-2$
							.append("</FONT>"); //$NON-NLS-1$
					// movieListStr.append(AndShowtimeDateNumberUtil.showMovieTime(context, projectionTime.getShowtime()));
				}
				movieListStr.append("<BR>");
			}
			spanned = Html.fromHtml(movieListStr.toString());

			synchronized (mapMovieStr) {
				mapMovieStr.put(key, spanned);
				mapMovieTime.put(key, curTime);
			}
		}

		return spanned;
	}

	public static Spanned getMovieNameViewStr(MovieBean movieBean, Context context, boolean blackTheme) {
		Spanned spanned = null;

		synchronized (mapMovieNameStr) {
			spanned = mapMovieNameStr.get(movieBean.getId() + blackTheme);
		}

		if (spanned == null) {
			StringBuilder movieNameStr = null;
			movieNameStr = new StringBuilder();
			movieNameStr.append("<FONT COLOR=\"").append(CineShowTimeLayoutUtils.getColorSubMainInfo(blackTheme)).append("\">") //$NON-NLS-1$ //$NON-NLS-2$
					.append(movieBean.getMovieName()) //$NON-NLS-1$//$NON-NLS-2$
					.append(" : </FONT>"); //$NON-NLS-1$
			movieNameStr.append("<FONT COLOR=\"").append(CineShowTimeLayoutUtils.getColorTimeOrDistance(blackTheme)).append("\">") //$NON-NLS-1$ //$NON-NLS-2$
					.append(CineShowtimeDateNumberUtil.showMovieTimeLength(context, movieBean)) //$NON-NLS-1$//$NON-NLS-2$
					.append(" </FONT>"); //$NON-NLS-1$
			movieNameStr.append("<BR>");
			spanned = Html.fromHtml(movieNameStr.toString());

			synchronized (mapMovieStr) {
				mapMovieNameStr.put(movieBean.getId() + blackTheme, spanned);
			}
		}

		return spanned;
	}

	public static Spanned getTheaterNameViewStr(TheaterBean theaterBean, boolean kmUnit, boolean blackTheme) {
		Spanned spanned = null;

		synchronized (mapTheaterNameStr) {
			spanned = mapTheaterNameStr.get(theaterBean.getId() + blackTheme);
		}

		if (spanned == null) {
			StringBuilder movieNameStr = null;
			movieNameStr = new StringBuilder();
			movieNameStr.append("<FONT COLOR=\"").append(CineShowTimeLayoutUtils.getColorSubMainInfo(blackTheme)).append("\">") //$NON-NLS-1$ //$NON-NLS-2$
					.append(theaterBean.getTheaterName()) //$NON-NLS-1$//$NON-NLS-2$
					.append("</FONT>"); //$NON-NLS-1$
			if ((theaterBean != null) && (theaterBean.getPlace() != null) && (theaterBean.getPlace().getDistance() != null)) {
				movieNameStr.append(" : <FONT COLOR=\"").append(CineShowTimeLayoutUtils.getColorTimeOrDistance(blackTheme)).append("\">") //$NON-NLS-1$ //$NON-NLS-2$
						.append(CineShowtimeDateNumberUtil.showDistance(theaterBean.getPlace().getDistance(), !kmUnit)) //$NON-NLS-1$//$NON-NLS-2$
						.append(" </FONT>"); //$NON-NLS-1$
			}
			movieNameStr.append("<BR>");
			spanned = Html.fromHtml(movieNameStr.toString());

			synchronized (mapMovieStr) {
				mapTheaterNameStr.put(theaterBean.getId() + blackTheme, spanned);
			}
		}

		return spanned;
	}

	public static boolean isFormat24(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String defaultTimeFormat = context.getResources().getString(R.string.preference_gen_default_time_format);//
		try {
			defaultTimeFormat = prefs.getString(context.getResources().getString(R.string.preference_gen_key_time_format)//
					, context.getResources().getString(R.string.preference_gen_default_time_format));
		} catch (ClassCastException e) {
			// We manage evolution of preference with version 23 of CineShowTime
			Editor editor = prefs.edit().putString(context.getResources().getString(R.string.preference_gen_key_time_format)//
					, context.getResources().getString(R.string.preference_gen_default_time_format));
			editor.commit();

		}
		boolean format24 = false;
		if (defaultTimeFormat.matches("\\d+")) {
			format24 = Integer.valueOf(defaultTimeFormat) == 24;
		} else {
			format24 = DateFormat.is24HourFormat(context);
		}

		return format24;
	}

	public static Map<String, List<ProjectionBean>> splitProjectionList(List<ProjectionBean> projectionList) {
		Map<String, List<ProjectionBean>> splitProjectionMap = new HashMap<String, List<ProjectionBean>>();
		List<ProjectionBean> listProjectionLangage = null;
		if (projectionList != null) {
			for (ProjectionBean projectionBean : projectionList) {
				listProjectionLangage = splitProjectionMap.get(projectionBean.getLang());
				if (listProjectionLangage == null) {
					listProjectionLangage = new ArrayList<ProjectionBean>();
					splitProjectionMap.put(projectionBean.getLang(), listProjectionLangage);
				}
				listProjectionLangage.add(projectionBean);
			}
		}
		return splitProjectionMap;
	}

	public static synchronized void clearMaps() {
		if (mapMovieStr != null) {
			mapMovieStr.clear();
		}
		if (mapMovieTime != null) {
			mapMovieTime.clear();
		}
	}

}
