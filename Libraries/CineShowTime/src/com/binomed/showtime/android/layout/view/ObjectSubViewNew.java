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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;

import com.binomed.showtime.R;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.PaintInstruction;
import com.binomed.showtime.android.model.PaintInstructionPool;
import com.binomed.showtime.android.model.ProjectionBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.util.CineShowtimeDateNumberUtil;

public class ObjectSubViewNew extends View {

	private final Paint paintMainInfoDark = new Paint();
	private final Paint paintSubInfoDark = new Paint();
	private final Paint paintPassedDark = new Paint();
	private final Paint paintNearestDark = new Paint();
	private final Paint paintNextDark = new Paint();
	private final Paint paintMainInfoLight = new Paint();
	private final Paint paintSubInfoLight = new Paint();
	private final Paint paintPassedLight = new Paint();
	private final Paint paintNearestLight = new Paint();
	private final Paint paintNextLight = new Paint();
	private final int measureSubInfo_DB_DOT;
	private final int measureNext_DB_DOT;
	private final int measureNearest_DB_DOT;
	private final int measureNext_PIPE;
	private final int measureNext_EMPTY;
	private final int measureMainInfo_SPACE;
	private final int measureMainInfo_DB_DOT_SINGLE_SPCACE;

	private MovieBean movieBean;

	private TheaterBean theaterBean;

	private List<ProjectionBean> projectionList;

	private String mainInfo, subMainInfo;
	private String[] splitMainInfo;

	private boolean kmUnit, lightFormat, distanceTime, movieView, blackTheme, format24;
	private int color = -1;

	private int specSizeWidth, mAscentMain, mAscentShowTime;
	private int heightView;

	private final int paddingLeft, paddingTop;

	private static final String PIPE = " | ";
	private static final String EMPTY = "";
	private static final String DB_DOT = " : ";
	private static final String DB_DOT_SINGLE_SPACE = ": ";
	private static final String SPACE = " ";

	private final ArrayList<PaintInstruction> painInstructionList = new ArrayList<PaintInstruction>();

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
		paintMainInfoDark.setTypeface(Typeface.DEFAULT_BOLD);
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
		paintMainInfoLight.setTypeface(Typeface.DEFAULT_BOLD);
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

		this.kmUnit = kmUnit;

		this.paddingLeft = context.getResources().getDimensionPixelOffset(R.dimen.cstPaddingLeftSubView);
		this.paddingTop = context.getResources().getDimensionPixelOffset(R.dimen.cstPaddingTopListItems);

		measureSubInfo_DB_DOT = (int) paintSubInfoDark.measureText(DB_DOT);
		measureNext_DB_DOT = (int) paintNextDark.measureText(DB_DOT);
		measureNearest_DB_DOT = (int) paintNearestDark.measureText(DB_DOT);
		measureNext_PIPE = (int) paintNextDark.measureText(PIPE);
		measureNext_EMPTY = (int) paintNextDark.measureText(EMPTY);
		measureMainInfo_SPACE = (int) paintMainInfoDark.measureText(SPACE);
		measureMainInfo_DB_DOT_SINGLE_SPCACE = (int) paintMainInfoDark.measureText(DB_DOT_SINGLE_SPACE);
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
				splitMainInfo = movieBean.getDecomposedName();
				subMainInfo = movieBean.getMovieTimeFormat();
			} else {
				mainInfo = theaterBean.getTheaterName();
				splitMainInfo = theaterBean.getDecomposedName();
				if ((theaterBean != null) && (theaterBean.getPlace() != null) && (theaterBean.getPlace().getDistance() != null)) {
					subMainInfo = CineShowtimeDateNumberUtil.showDistance(theaterBean.getPlace().getDistance(), !kmUnit);
				} else {
					subMainInfo = null;
				}

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

	public void manageHeightAndPaintInstructions() {
		heightView = getPaddingTop();
		for (int i = 0; i < painInstructionList.size(); i++) {
			PaintInstructionPool.getInstance().checkIn(painInstructionList.get(i));
		}
		painInstructionList.clear();

		String curLang = null;
		int width = 0;
		int nbLines = 1, nbLinesMain = 1;
		Paint paintTmp = null;
		int posY = getPaddingTop() - mAscentMain;
		int posX = getPaddingLeft();
		int measure = 0;

		if (splitMainInfo != null) {
			paintTmp = blackTheme ? paintMainInfoDark : paintMainInfoLight;
			width = getPaddingLeft() + getPaddingRight();
			String splitText = null;
			for (int i = 0; i < splitMainInfo.length; i++) {
				splitText = splitMainInfo[i];
				measure = (int) paintMainInfoDark.measureText(splitText);
				width += measure;
				width += measureMainInfo_SPACE;
				if (width > (specSizeWidth - getPaddingRight())) {
					nbLinesMain++;
					width = getPaddingLeft() + getPaddingRight();

					posX = getPaddingLeft();
					posY += (int) (-mAscentMain + paintTmp.descent());
				}

				painInstructionList.add(PaintInstructionPool.getInstance().newInstance(splitText, paintTmp, posX, posY));
				posX += measure;
				painInstructionList.add(PaintInstructionPool.getInstance().newInstance(SPACE, paintTmp, posX, posY));
				posX += measureMainInfo_SPACE;
			}
			// We manage the " : " after the name of theater or movie
			width += measureMainInfo_DB_DOT_SINGLE_SPCACE;
			if (width > (specSizeWidth - getPaddingRight())) {
				nbLinesMain++;
				width = getPaddingLeft() + getPaddingRight();
				// width = 0;

				posX = getPaddingLeft();
				posY += (int) (-mAscentMain + paintTmp.descent());
			}
			painInstructionList.add(PaintInstructionPool.getInstance().newInstance(DB_DOT_SINGLE_SPACE, paintTmp, posX, posY));
			posX += measureMainInfo_DB_DOT_SINGLE_SPCACE;
			// We manage the sub info (distance or time)
			if (subMainInfo != null) {
				paintTmp = blackTheme ? paintSubInfoDark : paintSubInfoLight;
				measure = (int) paintMainInfoDark.measureText(subMainInfo);
				width += measure;
				if (width > (specSizeWidth - getPaddingRight())) {
					nbLinesMain++;
					width = getPaddingLeft() + getPaddingRight();
					// width = 0;

					posX = getPaddingLeft();
					posY += (int) (-mAscentMain + paintTmp.descent());
				}
				painInstructionList.add(PaintInstructionPool.getInstance().newInstance(subMainInfo, paintTmp, posX, posY));
			}
		} else {
			nbLinesMain = 0;
		}

		// We now manage the projections we reset the posY and posX
		posY += 10 - mAscentShowTime;
		posX = getPaddingLeft();
		width = getPaddingLeft() + getPaddingRight();
		// width = 0;
		if (!lightFormat && (projectionList != null)) {
			boolean first = true;
			boolean firstLine = true;
			boolean near = true;
			long currentTime = System.currentTimeMillis();
			String timeStr = null;
			ProjectionBean projection = null;
			for (int i = 0; i < projectionList.size(); i++) {
				projection = projectionList.get(i);
				paintTmp = blackTheme ? paintNearestDark : paintNearestLight;

				// If the string of language is shown then we start a new line
				if ((projection.getLang() != null) && !projection.getLang().equals(curLang)) {
					curLang = projection.getLang();
					posX = getPaddingLeft();
					if (!firstLine) {
						nbLines++;
						posY += (int) (-mAscentShowTime + paintTmp.descent());
					}
					firstLine = false;
					width = getPaddingLeft() + getPaddingRight();
					// width = 0;
					if ((curLang != null) && (curLang.length() > 0)) {
						width += (int) paintNextDark.measureText(curLang);
						width += measureNext_DB_DOT;

						measure = (int) paintTmp.measureText(curLang);
						width += measure + measureNearest_DB_DOT;
						painInstructionList.add(PaintInstructionPool.getInstance().newInstance(curLang, paintTmp, posX, posY));
						posX += measure;
						painInstructionList.add(PaintInstructionPool.getInstance().newInstance(DB_DOT, paintTmp, posX, posY));
						posX += measureNearest_DB_DOT;
					}
				}

				paintTmp = blackTheme ? paintNextDark : paintNextLight;
				timeStr = (format24 ? projection.getFormat24() : projection.getFormat12());
				if (!first) {
					width += measureNext_PIPE;
					if (width > (specSizeWidth - getPaddingRight())) {
						nbLines++;
						width = getPaddingLeft() + getPaddingRight();
						// width = 0;
						posX = getPaddingLeft();
						posY += (int) (-mAscentShowTime + paintTmp.descent());
					}
					painInstructionList.add(PaintInstructionPool.getInstance().newInstance(PIPE, paintTmp, posX, posY));
					posX += measureNext_PIPE;
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
					nbLines++;
					width = getPaddingLeft() + getPaddingRight();
					// width = 0;
					posX = getPaddingLeft();
					posY += (int) (-mAscentShowTime + paintTmp.descent());
				}
				painInstructionList.add(PaintInstructionPool.getInstance().newInstance(timeStr, paintTmp, posX, posY));
				posX += measure;

			}
		} else {
			nbLines = 0;
		}

		heightView = getPaddingTop() + (nbLines * (int) (-mAscentShowTime + paintNearestDark.descent())) + (nbLinesMain * (int) (-mAscentMain + paintMainInfoDark.descent())) + 10;
	}

	@Override
	public int getPaddingTop() {
		return paddingTop + super.getPaddingTop();
	}

	@Override
	public int getPaddingLeft() {
		return paddingLeft + super.getPaddingLeft();
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
				widthMainInfo += measureSubInfo_DB_DOT;
				widthMainInfo += (int) paintSubInfoDark.measureText(subMainInfo);
			}

			if (!lightFormat && (projectionList != null)) {
				boolean first = true;
				ProjectionBean projection = null;
				for (int i = 0; i < projectionList.size(); i++) {
					projection = projectionList.get(i);
					result += (!first ? measureNext_PIPE : measureNext_EMPTY);
					result += (int) paintNextDark.measureText(format24 ? projection.getFormat24() : projection.getFormat12());
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
		int result = getPaddingTop();
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {

			// We calculate directly the height and all elements to draw in order to optimize allocations
			manageHeightAndPaintInstructions();
			result = heightView;

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
		PaintInstruction instruction = null;
		for (int i = 0; i < painInstructionList.size(); i++) {
			instruction = painInstructionList.get(i);
			canvas.drawText(instruction.getTextToPrint(), instruction.getPosX(), instruction.getPosY(), instruction.getPainter());
		}
	}

}
