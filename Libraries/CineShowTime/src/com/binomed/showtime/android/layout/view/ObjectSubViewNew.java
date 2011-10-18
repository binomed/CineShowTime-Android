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

	private Paint paintMainInfoDark = new Paint();
	private Paint paintSubInfoDark = new Paint();
	private Paint paintPassedDark = new Paint();
	private Paint paintNearestDark = new Paint();
	private Paint paintNextDark = new Paint();
	private Paint paintMainInfoLight = new Paint();
	private Paint paintSubInfoLight = new Paint();
	private Paint paintPassedLight = new Paint();
	private Paint paintNearestLight = new Paint();
	private Paint paintNextLight = new Paint();

	private MovieBean movieBean;

	private TheaterBean theaterBean;

	private List<ProjectionBean> projectionList;

	private String mainInfo, subMainInfo;
	private String[] splitMainInfo;

	private boolean kmUnit, lightFormat, distanceTime, movieView, blackTheme, format24;
	private int color = -1;

	private int specSizeWidth, mAscentMain, mAscentShowTime;

	private static final String PIPE = " | ";
	private static final String EMPTY = "";
	private static final String DB_DOT = " : ";
	private static final String DB_DOT_SINGLE_SPACE = ": ";
	private static final String SPACE = " ";

	public MovieBean getMovieBean() {
		return movieBean;
	}

	public TheaterBean getTheaterBean() {
		return theaterBean;
	}

	public ObjectSubViewNew(Context context, boolean kmUnit) {
		super(context);

		paintMainInfoDark.setTextSize(context.getResources().getDimension(R.dimen.cstSubMainInfo));
		paintMainInfoDark.setColor(getContext().getResources().getColor(R.color.sub_main_info_dark));
		paintMainInfoDark.setAntiAlias(true);
		paintSubInfoDark.setTextSize(context.getResources().getDimension(R.dimen.cstSubMainInfo));
		paintSubInfoDark.setColor(getContext().getResources().getColor(R.color.sub_sub_info_dark));
		paintSubInfoDark.setAntiAlias(true);

		paintPassedDark.setTextSize(context.getResources().getDimension(R.dimen.cstShowtime));
		paintPassedDark.setColor(getContext().getResources().getColor(R.color.showtime_passed_dark));
		paintPassedDark.setAntiAlias(true);
		paintNearestDark.setTextSize(context.getResources().getDimension(R.dimen.cstShowtime));
		paintNearestDark.setColor(getContext().getResources().getColor(R.color.showtime_nearest_dark));
		paintNearestDark.setTypeface(Typeface.DEFAULT_BOLD);
		paintNearestDark.setAntiAlias(true);
		paintNextDark.setTextSize(context.getResources().getDimension(R.dimen.cstShowtime));
		paintNextDark.setColor(getContext().getResources().getColor(R.color.showtime_next_dark));
		paintNextDark.setAntiAlias(true);

		paintMainInfoLight.setTextSize(context.getResources().getDimension(R.dimen.cstSubMainInfo));
		paintMainInfoLight.setColor(getContext().getResources().getColor(R.color.sub_main_info_light));
		paintMainInfoLight.setAntiAlias(true);
		paintSubInfoLight.setTextSize(context.getResources().getDimension(R.dimen.cstSubMainInfo));
		paintSubInfoLight.setColor(getContext().getResources().getColor(R.color.sub_sub_info_light));
		paintSubInfoLight.setAntiAlias(true);

		paintPassedLight.setTextSize(context.getResources().getDimension(R.dimen.cstShowtime));
		paintPassedLight.setColor(getContext().getResources().getColor(R.color.showtime_passed_light));
		paintPassedLight.setAntiAlias(true);
		paintNearestLight.setTextSize(context.getResources().getDimension(R.dimen.cstShowtime));
		paintNearestLight.setColor(getContext().getResources().getColor(R.color.showtime_nearest_light));
		paintNearestLight.setTypeface(Typeface.DEFAULT_BOLD);
		paintNearestLight.setAntiAlias(true);
		paintNextLight.setTextSize(context.getResources().getDimension(R.dimen.cstShowtime));
		paintNextLight.setColor(getContext().getResources().getColor(R.color.showtime_next_light));
		paintNextLight.setAntiAlias(true);

		mAscentMain = (int) paintMainInfoDark.ascent();
		mAscentShowTime = (int) paintNearestDark.ascent();

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
			color = blackTheme ? 1 : 0;
		}
		if ((movieBean != null) && (theaterBean != null)) {
			if (!movieView) {
				mainInfo = movieBean.getMovieName();
				subMainInfo = CineShowtimeDateNumberUtil.showMovieTimeLength(getContext(), movieBean);
			} else {
				mainInfo = theaterBean.getTheaterName();
				if ((theaterBean != null) && (theaterBean.getPlace() != null) && (theaterBean.getPlace().getDistance() != null)) {
					subMainInfo = CineShowtimeDateNumberUtil.showDistance(theaterBean.getPlace().getDistance(), !kmUnit);
				} else {
					subMainInfo = null;
				}

			}

			if (mainInfo != null) {
				splitMainInfo = mainInfo.split(SPACE);
			}
			projectionList = theaterBean.getMovieMap().get(movieBean.getId());

		} else {
			splitMainInfo = null;
			mainInfo = null;
			subMainInfo = null;
			projectionList = null;
		}

		requestLayout();
		invalidate();

	}

	@Override
	public int getPaddingLeft() {
		return 7 + super.getPaddingLeft();
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
				widthMainInfo = (int) paintMainInfoDark.measureText(mainInfo);
			}
			if (subMainInfo != null) {
				widthMainInfo += (int) paintSubInfoDark.measureText(DB_DOT + subMainInfo);
			}

			if (!lightFormat && (projectionList != null)) {
				boolean first = true;
				for (ProjectionBean projection : projectionList) {
					result += (int) paintNextDark.measureText((!first ? PIPE : EMPTY) + (format24 ? projection.getFormat24() : projection.getFormat12()));
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

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {

			String curLang = null;
			int width = getPaddingLeft() + getPaddingRight();
			int nbLines = 1, nbLinesMain = 1;
			if (!lightFormat && (projectionList != null)) {
				boolean first = true;
				boolean firstLine = true;
				for (ProjectionBean projection : projectionList) {
					if ((projection.getLang() != null) && !projection.getLang().equals(curLang)) {
						curLang = projection.getLang();
						if (!firstLine) {
							nbLines++;
						}
						firstLine = false;
						width = getPaddingLeft() + getPaddingRight();
						if (curLang != null) {
							width += (int) paintNextDark.measureText(curLang + DB_DOT);
						}
					}
					width += (int) paintNextDark.measureText((!first ? PIPE : EMPTY) + (format24 ? projection.getFormat24() : projection.getFormat12()));
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
				// String[] split = (mainInfo + (subMainInfo != null ? " : " + subMainInfo : "")).split(" ");
				width = getPaddingLeft() + getPaddingRight();
				for (String splitText : splitMainInfo) {
					width += (int) paintMainInfoDark.measureText(splitText + SPACE);
					if (width > specSizeWidth) {
						nbLinesMain++;
						width = getPaddingLeft() + getPaddingRight();
					}

				}
				width += (int) paintMainInfoDark.measureText(DB_DOT_SINGLE_SPACE);
				if (width > specSizeWidth) {
					nbLinesMain++;
					width = getPaddingLeft() + getPaddingRight();
				}
				if (subMainInfo != null) {
					width += (int) paintMainInfoDark.measureText(subMainInfo);
					if (width > specSizeWidth) {
						nbLinesMain++;
						width = getPaddingLeft() + getPaddingRight();
					}
				}
			} else {
				nbLinesMain = 0;
			}

			result = (nbLines * (int) (-mAscentShowTime + paintNearestDark.descent())) + (nbLinesMain * (int) (-mAscentMain + paintMainInfoDark.descent())) + 10;

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
		int measure = 0;
		Paint paintTmp = null;
		width = getPaddingLeft() + getPaddingRight();
		if (mainInfo != null) {
			// String[] split = (mainInfo + " : ").split(" ");
			paintTmp = blackTheme ? paintMainInfoDark : paintMainInfoLight;
			width = getPaddingLeft() + getPaddingRight();
			for (String splitText : splitMainInfo) {
				measure = (int) paintTmp.measureText(splitText + SPACE);
				width += measure;
				if (width > (specSizeWidth - getPaddingRight())) {
					width = 0;
					;
					posX = getPaddingLeft();
					posY += (int) (-mAscentMain + paintTmp.descent());
				}
				canvas.drawText(splitText + SPACE, posX, posY, paintTmp);
				posX += measure;
			}
			measure = (int) paintTmp.measureText(DB_DOT_SINGLE_SPACE);
			width += measure;
			if (width > (specSizeWidth - getPaddingRight())) {
				width = 0;
				;
				posX = getPaddingLeft();
				posY += (int) (-mAscentMain + paintTmp.descent());
			}
			canvas.drawText(DB_DOT_SINGLE_SPACE, posX, posY, paintTmp);
			posX += measure;
			if (subMainInfo != null) {
				paintTmp = blackTheme ? paintSubInfoDark : paintSubInfoLight;
				measure = (int) paintTmp.measureText(subMainInfo);
				width += measure;
				if (width > (specSizeWidth - getPaddingRight())) {
					width = 0;
					;
					posX = getPaddingLeft();
					posY += (int) (-mAscentMain + paintTmp.descent());
				}
				canvas.drawText(subMainInfo, posX, posY, paintTmp);
			}
		}
		posY += 10 - mAscentShowTime;
		width = getPaddingLeft() + getPaddingRight();
		posX = getPaddingLeft();
		if (!lightFormat && (projectionList != null)) {
			boolean first = true;
			boolean firstLine = true;
			boolean near = true;
			String curLang = null;
			long currentTime = System.currentTimeMillis();
			String timeStr = null;
			for (ProjectionBean projection : projectionList) {
				paintTmp = blackTheme ? paintNearestDark : paintNearestLight;

				if ((projection.getLang() != null) && !projection.getLang().equals(curLang)) {
					curLang = projection.getLang();
					width = 0;
					posX = getPaddingLeft();
					if (!firstLine) {
						posY += (int) (-mAscentShowTime + paintTmp.descent());
					}
					firstLine = false;
					if (curLang != null) {
						measure = (int) paintTmp.measureText(curLang + DB_DOT);
						width += measure;
						canvas.drawText(curLang + DB_DOT, posX, posY, paintTmp);
						posX += measure;
					}
				}

				paintTmp = blackTheme ? paintNextDark : paintNextLight;
				timeStr = (format24 ? projection.getFormat24() : projection.getFormat12());
				if (!first) {
					measure = (int) paintTmp.measureText(PIPE);
					width += measure;
					if (width > (specSizeWidth - getPaddingRight())) {
						width = 0;
						posX = getPaddingLeft();
						posY += (int) (-mAscentShowTime + paintTmp.descent());
					}
					canvas.drawText(PIPE, posX, posY, paintTmp);
					posX += measure;
				}
				first = false;

				if (currentTime > projection.getShowtime()) {
					paintTmp = blackTheme ? paintPassedDark : paintPassedLight;
				} else if (near) {
					paintTmp = blackTheme ? paintNearestDark : paintNearestLight;
					near = false;
				} else {
					paintTmp = blackTheme ? paintNextDark : paintNextLight;
				}

				measure = (int) paintTmp.measureText(timeStr);
				width += measure;
				if (width > (specSizeWidth - getPaddingRight())) {
					width = 0;
					posX = getPaddingLeft();
					posY += (int) (-mAscentShowTime + paintTmp.descent());
				}
				canvas.drawText(timeStr, posX, posY, paintTmp);
				posX += measure;
			}
		}
	}

}
