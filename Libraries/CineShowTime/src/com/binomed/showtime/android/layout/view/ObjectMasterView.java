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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.util.CineShowtimeDateNumberUtil;
import com.binomed.showtime.cst.HttpParamsCst;

public class ObjectMasterView extends LinearLayout {

	private Context context;
	private ImageView expandImg;
	private ImageView expandFav;
	private TextView objectName;
	private TextView objectSubContentName;
	private TheaterBean theaterBean;
	private MovieBean movieBean;
	private boolean kmUnit;
	private boolean distanceTime;
	private boolean isFav;

	public TheaterBean getTheaterBean() {
		return theaterBean;
	}

	public MovieBean getMovieBean() {
		return movieBean;
	}

	public boolean isFav() {
		return isFav;
	}

	public ObjectMasterView(Context context, OnClickListener listener) {
		super(context);
		this.setOrientation(VERTICAL);

		// Here we build the child views in code. They could also have
		// been specified in an XML file.
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String measure = prefs.getString(context.getResources().getString(R.string.preference_loc_key_measure) //
				, context.getResources().getString(R.string.preference_loc_default_measure));
		distanceTime = prefs.getBoolean(context.getResources().getString(R.string.preference_loc_key_time_direction) //
				, false);
		kmUnit = context.getResources().getString(R.string.preference_loc_default_measure).equals(measure);

		this.context = context;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_expandable_group_item, this);

		// expandImg = (ImageView) this.findViewById(R.id.expand_img);
		expandFav = (ImageView) this.findViewById(R.id.expand_fav);
		expandFav.setOnClickListener(listener);
		objectName = (TextView) this.findViewById(R.id.object_name);
		objectSubContentName = (TextView) this.findViewById(R.id.object_subcontent_name);
	}

	public void setTheater(TheaterBean theaterBean, boolean isFav, boolean lightFormat, boolean blackTheme) {
		this.theaterBean = theaterBean;
		this.isFav = isFav;

		StringBuilder strTheater = new StringBuilder();
		if ((theaterBean != null) && (theaterBean.getPlace() != null) && (theaterBean.getPlace().getDistance() != null)) {
			strTheater.append(" (");
			strTheater.append(CineShowtimeDateNumberUtil.showDistance(theaterBean.getPlace().getDistance(), !kmUnit));
			strTheater.append(")");
		}
		if (theaterBean != null) {
			objectName.setText(theaterBean.getTheaterName());
			if (!String.valueOf(HttpParamsCst.ERROR_NO_DATA).equals(theaterBean.getId()) //
					&& !String.valueOf(HttpParamsCst.ERROR_WRONG_DATE).equals(theaterBean.getId())//
					&& !String.valueOf(HttpParamsCst.ERROR_WRONG_PLACE).equals(theaterBean.getId()) //
					&& !String.valueOf(HttpParamsCst.ERROR_CUSTOM_MESSAGE).equals(theaterBean.getId()) //
			) {
				expandFav.setImageDrawable(context.getResources().getDrawable(isFav ? R.drawable.btn_star_big_on : blackTheme ? R.drawable.btn_star_big_off_disable : R.drawable.btn_star_big_off));
			} else {
				expandFav.setImageDrawable(context.getResources().getDrawable(R.drawable.vide));
			}
		} else {
			objectName.setText(context.getResources().getString(R.string.itemMoreTheaters));
			expandFav.setImageDrawable(context.getResources().getDrawable(R.drawable.vide));
		}

		objectSubContentName.setText(strTheater.toString());

		objectSubContentName.setVisibility(lightFormat ? View.GONE : View.VISIBLE);
	}

	public void setMovie(MovieBean movieBean, boolean isFav, boolean lightFormat) {
		this.movieBean = movieBean;

		if (movieBean != null) {
			objectName.setText(movieBean.getMovieName());
			objectSubContentName.setText(CineShowtimeDateNumberUtil.showMovieTimeLength(context, movieBean));
		} else {
			objectName.setText("");
			objectSubContentName.setText("");

		}
		expandFav.setImageDrawable(context.getResources().getDrawable(R.drawable.vide));

		objectSubContentName.setVisibility(lightFormat ? View.GONE : View.VISIBLE);
	}

	public void toggleFav() {
		isFav = !isFav;
		expandFav.setImageDrawable(context.getResources().getDrawable(isFav ? R.drawable.btn_star_big_on : R.drawable.btn_star_big_off));
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		expandFav.setOnClickListener(l);
	}

}
