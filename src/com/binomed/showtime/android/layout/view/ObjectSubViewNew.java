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
package com.binomed.showtime.android.layout.view;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;

import com.binomed.showtime.R;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.ProjectionBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.util.CineShowtimeDateNumberUtil;

public class ObjectSubViewNew extends View {

	private Paint paintMainInfo = new Paint();
	private Paint paintSubInfo = new Paint();
	private Paint paintPassed = new Paint();
	private Paint paintNearest = new Paint();
	private Paint paintNext = new Paint();

	private MovieBean movieBean;

	private TheaterBean theaterBean;

	private List<ProjectionBean> projectionList;

	private String mainInfo, subMainInfo;

	private boolean kmUnit, lightFormat, distanceTime, movieView, blackTheme, format24;
	private int color = -1;

	private int specSizeWidth, mAscentMain, mAscentShowTime;

	public MovieBean getMovieBean() {
		return movieBean;
	}

	public TheaterBean getTheaterBean() {
		return theaterBean;
	}

	public ObjectSubViewNew(Context context, boolean kmUnit) {
		super(context);

		paintMainInfo.setTextSize(context.getResources().getDimension(R.dimen.cstSubMainInfo));
		paintMainInfo.setAntiAlias(true);
		paintSubInfo.setTextSize(context.getResources().getDimension(R.dimen.cstSubMainInfo));
		paintSubInfo.setAntiAlias(true);

		paintPassed.setTextSize(context.getResources().getDimension(R.dimen.cstShowtime));
		paintPassed.setAntiAlias(true);
		paintNearest.setTextSize(context.getResources().getDimension(R.dimen.cstShowtime));
		paintNearest.setTypeface(Typeface.DEFAULT_BOLD);
		paintNearest.setAntiAlias(true);
		paintNext.setTextSize(context.getResources().getDimension(R.dimen.cstShowtime));
		paintNext.setAntiAlias(true);

		// this.setOrientation(VERTICAL);
		this.kmUnit = kmUnit;
	}

	public void setMovie(MovieBean movieBean, TheaterBean theaterBean, boolean distanceTime, boolean movieView, boolean blackTheme, boolean format24, boolean lightFormat) {
		this.movieBean = movieBean;
		this.theaterBean = theaterBean;
		this.lightFormat = lightFormat;
		this.distanceTime = distanceTime;
		this.movieView = movieView;
		this.blackTheme = blackTheme;
		this.format24 = format24;
		if ((color == -1) || ((color == 1) && blackTheme) || ((color == 0) && !blackTheme)) {
			paintMainInfo.setColor(getContext().getResources().getColor(blackTheme ? R.color.sub_main_info_dark : R.color.sub_main_info_light));
			paintSubInfo.setColor(getContext().getResources().getColor(blackTheme ? R.color.sub_sub_info_dark : R.color.sub_sub_info_light));
			paintPassed.setColor(getContext().getResources().getColor(blackTheme ? R.color.showtime_passed_dark : R.color.showtime_passed_light));
			paintNearest.setColor(getContext().getResources().getColor(blackTheme ? R.color.showtime_nearest_dark : R.color.showtime_nearest_light));
			paintNext.setColor(getContext().getResources().getColor(blackTheme ? R.color.showtime_next_dark : R.color.showtime_next_light));
			color = blackTheme ? 1 : 0;
		}
		if ((movieBean != null) && (theaterBean != null)) {
			if (!movieView) {
				// movieTitle.setText(new StringBuilder(movieBean.getMovieName()) //
				// .append(" : ").append(AndShowtimeDateNumberUtil.showMovieTimeLength(getContext(), movieBean))//
				// .toString()//
				// );
				mainInfo = movieBean.getMovieName();
				subMainInfo = CineShowtimeDateNumberUtil.showMovieTimeLength(getContext(), movieBean);
			} else {
				// StringBuilder strTheater = new StringBuilder(theaterBean.getTheaterName()); //
				// if ((theaterBean != null) && (theaterBean.getPlace() != null) && theaterBean.getPlace().getDistance() != null) {
				// strTheater.append(" : ").append(AndShowtimeDateNumberUtil.showDistance(theaterBean.getPlace().getDistance(), !kmUnit));//
				// }
				// movieTitle.setText(strTheater.toString()//
				// );
				mainInfo = theaterBean.getTheaterName();
				if ((theaterBean != null) && (theaterBean.getPlace() != null) && (theaterBean.getPlace().getDistance() != null)) {
					subMainInfo = CineShowtimeDateNumberUtil.showDistance(theaterBean.getPlace().getDistance(), !kmUnit);
				} else {
					subMainInfo = null;
				}

			}

			projectionList = theaterBean.getMovieMap().get(movieBean.getId());
			Long distanceTimeLong = null;
			if (distanceTime && (theaterBean != null) && (theaterBean.getPlace() != null)) {
				distanceTimeLong = theaterBean.getPlace().getDistanceTime();
			}
			// Spanned movieListStr = CineShowtimeDateNumberUtil.getMovieViewStr(movieBean.getId(), theaterBean.getId(), projectionList, getContext(), distanceTimeLong, blackTheme, format24);

		} else {
			mainInfo = null;
			subMainInfo = null;
			projectionList = null;
		}

		requestLayout();
		invalidate();

	}

	/**
	 * @see android.view.View#measure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
	}

	/**
	 * Determines the width of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The width of the view, honoring constraints from measureSpec
	 */
	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		this.specSizeWidth = specSize;

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text
			result = 0;
			int widthMainInfo = 0;
			if (mainInfo != null) {
				widthMainInfo = (int) paintMainInfo.measureText(mainInfo);
			}
			if (subMainInfo != null) {
				widthMainInfo += (int) paintSubInfo.measureText(" : " + subMainInfo);
			}

			if (!lightFormat && (projectionList != null)) {
				boolean first = true;
				for (ProjectionBean projection : projectionList) {
					result += (int) paintNext.measureText((!first ? " | " : "") + (format24 ? projection.getFormat24() : projection.getFormat12()));
					first = false;
				}
			}
			result += getPaddingLeft() + getPaddingRight();
			result = Math.max(result, widthMainInfo);
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by measureSpec
				result = Math.min(result, specSize);
			}
		}

		return result;
	}

	/**
	 * Determines the height of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The height of the view, honoring constraints from measureSpec
	 */
	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		mAscentMain = (int) paintMainInfo.ascent();
		mAscentShowTime = (int) paintNearest.ascent();
		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {

			int width = getPaddingLeft() + getPaddingRight();
			int nbLines = 1, nbLinesMain = 1;
			if (!lightFormat && (projectionList != null)) {
				boolean first = true;
				for (ProjectionBean projection : projectionList) {
					width += (int) paintNext.measureText((!first ? " | " : "") + (format24 ? projection.getFormat24() : projection.getFormat12()));
					first = false;
					if (width > specSizeWidth) {
						nbLines++;
						width = getPaddingLeft() + getPaddingRight();
					}
				}
			} else {
				nbLines = 0;
			}
			if (mainInfo != null) {
				String[] split = (mainInfo + (subMainInfo != null ? " : " + subMainInfo : "")).split(" ");
				width = getPaddingLeft() + getPaddingRight();
				for (String splitText : split) {
					width += (int) paintMainInfo.measureText(splitText + " ");
					if (width > specSizeWidth) {
						nbLinesMain++;
						width = getPaddingLeft() + getPaddingRight();
					}

				}
			} else {
				nbLinesMain = 0;
			}

			result = (nbLines * (int) (-mAscentShowTime + paintNearest.descent())) + (nbLinesMain * (int) (-mAscentMain + paintMainInfo.descent())) + 10;

			// Measure the text (beware: ascent is a negative number)
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by measureSpec
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int width = 0;
		int posY = getPaddingTop() - mAscentMain;
		int posX = getPaddingLeft();
		width = getPaddingLeft() + getPaddingRight();
		if (mainInfo != null) {
			String[] split = (mainInfo + " : ").split(" ");
			width = getPaddingLeft() + getPaddingRight();
			for (String splitText : split) {
				width += (int) paintMainInfo.measureText(splitText + " ");
				if (width > (specSizeWidth - getPaddingRight())) {
					width = 0;
					;
					posX = getPaddingLeft();
					posY += (int) (-mAscentMain + paintMainInfo.descent());
				}
				canvas.drawText(splitText + " ", posX, posY, paintMainInfo);
				posX += paintMainInfo.measureText(splitText + " ");
			}
			if (subMainInfo != null) {
				width += (int) paintSubInfo.measureText(subMainInfo);
				if (width > (specSizeWidth - getPaddingRight())) {
					width = 0;
					;
					posX = getPaddingLeft();
					posY += (int) (-mAscentMain + paintMainInfo.descent());
				}
				canvas.drawText(subMainInfo, posX, posY, paintSubInfo);
			}
		}
		posY += 10 - mAscentShowTime;
		width = getPaddingLeft() + getPaddingRight();
		posX = getPaddingLeft();
		Paint paintTmp = null;
		if (!lightFormat && (projectionList != null)) {
			boolean first = true;
			boolean near = true;
			long currentTime = System.currentTimeMillis();
			String timeStr = null;
			for (ProjectionBean projection : projectionList) {
				timeStr = (format24 ? projection.getFormat24() : projection.getFormat12());
				if (!first) {
					width += (int) paintNext.measureText(" | ");
					if (width > (specSizeWidth - getPaddingRight())) {
						width = 0;
						posX = getPaddingLeft();
						posY += (int) (-mAscentShowTime + paintNext.descent());
					}
					canvas.drawText(" | ", posX, posY, paintNext);
					posX += paintNext.measureText(" | ");
				}
				first = false;

				if (currentTime > projection.getShowtime()) {
					paintTmp = paintPassed;
				} else if (near) {
					paintTmp = paintNearest;
					near = false;
				} else {
					paintTmp = paintNext;
				}

				width += (int) paintTmp.measureText(timeStr);
				if (width > (specSizeWidth - getPaddingRight())) {
					width = 0;
					posX = getPaddingLeft();
					posY += (int) (-mAscentShowTime + paintTmp.descent());
				}
				canvas.drawText(timeStr, posX, posY, paintTmp);
				posX += paintTmp.measureText(timeStr);
			}
		}

	}

}
