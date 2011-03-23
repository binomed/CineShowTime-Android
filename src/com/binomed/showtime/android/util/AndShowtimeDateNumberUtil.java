package com.binomed.showtime.android.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;

import com.binomed.showtime.android.R;
import com.binomed.showtime.android.cst.AndShowtimeCst;
import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.cst.SpecialChars;
import com.binomed.showtime.util.AndShowtimeNumberFormat;

public abstract class AndShowtimeDateNumberUtil {

	private static DecimalFormat decimalFormat = AndShowtimeNumberFormat.getFormat2Digit();

	private static final Float MILE_VALUE = new Float(0.621371192);

	private static Calendar time = Calendar.getInstance();

	private static HashMap<String, Long> mapMovieTime = new HashMap<String, Long>();
	private static HashMap<String, Spanned> mapMovieStr = new HashMap<String, Spanned>();

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

	public static String showMovieTime(Context context, Long time) {
		return DateUtils.formatDateTime(context, time, DateUtils.FORMAT_SHOW_TIME);
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
			break;
		case Calendar.MONDAY:
			spinnerValues.add(context.getResources().getString(R.string.spinnerTuesday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerWenesday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerThursday));
			break;
		case Calendar.TUESDAY:
			spinnerValues.add(context.getResources().getString(R.string.spinnerWenesday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerThursday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerFriday));
			break;
		case Calendar.WEDNESDAY:
			spinnerValues.add(context.getResources().getString(R.string.spinnerThursday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerFriday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerSaturday));
			break;
		case Calendar.THURSDAY:
			spinnerValues.add(context.getResources().getString(R.string.spinnerFriday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerSaturday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerSunday));
			break;
		case Calendar.FRIDAY:
			spinnerValues.add(context.getResources().getString(R.string.spinnerSaturday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerSunday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerMonday));
			break;
		case Calendar.SATURDAY:
			spinnerValues.add(context.getResources().getString(R.string.spinnerSunday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerMonday));
			spinnerValues.add(context.getResources().getString(R.string.spinnerTuesday));
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

	public static long getMinTime(List<Long> timeList) {
		long currentTime = Calendar.getInstance().getTimeInMillis();

		long minDiff0 = -1;
		long minDiffTemp = 0;
		long minTime = -1;

		Calendar cal = Calendar.getInstance();
		if (timeList != null) {
			for (Long timeTmp : timeList) {
				cal.setTimeInMillis(timeTmp);
				minDiffTemp = (timeTmp - currentTime);
				if ((minDiffTemp < (minDiff0) || (minDiff0 == -1)) && (minDiffTemp > 0)) {
					minDiff0 = minDiffTemp;
					minTime = timeTmp;
				}
			}
		}

		return minTime;
	}

	public static Spanned getMovieViewStr(String movieId, List<Long> projectionList, Context context) {
		Spanned spanned = null;
		Long curTime = Calendar.getInstance().getTimeInMillis();

		synchronized (mapMovieStr) {
			spanned = mapMovieStr.get(movieId);
			if (spanned != null) {
				Long pastTime = mapMovieTime.get(movieId);
				if ((curTime - pastTime) > 3600000) {
					spanned = null;
				}
			}
		}

		if (spanned == null) {
			StringBuilder movieListStr = null;
			movieListStr = new StringBuilder();
			boolean first = true;
			int passedShowtime;
			long minTime = AndShowtimeDateNumberUtil.getMinTime(projectionList);
			for (long projectionTime : projectionList) {
				if (!first) {
					movieListStr.append(" | ");
				} else {
					first = false;
				}
				passedShowtime = AndShowtimeDateNumberUtil.getPositionTime(projectionTime, minTime);
				switch (passedShowtime) {
				case 0:
					movieListStr.append("<FONT COLOR=\"").append(AndShowtimeCst.COLOR_WHITE).append("\">") //$NON-NLS-1$ //$NON-NLS-2$
							.append("<b>").append(AndShowtimeDateNumberUtil.showMovieTime(context, projectionTime)).append("</b>") //$NON-NLS-1$//$NON-NLS-2$
							.append("</FONT>"); //$NON-NLS-1$
					break;
				case 1:
					movieListStr.append(AndShowtimeDateNumberUtil.showMovieTime(context, projectionTime));
					break;
				case -1:
					movieListStr.append("<FONT COLOR=\"").append(AndShowtimeCst.COLOR_GREY).append("\">") //$NON-NLS-1$//$NON-NLS-2$
							.append("<i>").append(AndShowtimeDateNumberUtil.showMovieTime(context, projectionTime)).append("</i>") //$NON-NLS-1$ //$NON-NLS-2$
							.append("</FONT>"); //$NON-NLS-1$
					break;
				default:
					break;
				}
			}
			movieListStr.append("\n");
			spanned = Html.fromHtml(movieListStr.toString());

			synchronized (mapMovieStr) {
				mapMovieStr.put(movieId, spanned);
				mapMovieTime.put(movieId, curTime);
			}
		}

		return spanned;
	}

}
