package com.binomed.showtime.android.layout.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.util.AndShowtimeDateNumberUtil;
import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.TheaterBean;
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

	public void setTheater(TheaterBean theaterBean, boolean isFav) {
		this.theaterBean = theaterBean;
		this.isFav = isFav;

		StringBuilder strTheater = new StringBuilder();
		if ((theaterBean != null) && (theaterBean.getPlace() != null) && theaterBean.getPlace().getDistance() != null) {
			strTheater.append(" (");
			strTheater.append(AndShowtimeDateNumberUtil.showDistance(theaterBean.getPlace().getDistance(), !kmUnit));
			strTheater.append(")");
		}
		if (theaterBean != null) {
			objectName.setText(theaterBean.getTheaterName());
			if (!String.valueOf(HttpParamsCst.ERROR_NO_DATA).equals(theaterBean.getId()) //
					&& !String.valueOf(HttpParamsCst.ERROR_WRONG_DATE).equals(theaterBean.getId())// 
					&& !String.valueOf(HttpParamsCst.ERROR_WRONG_PLACE).equals(theaterBean.getId()) //
					&& !String.valueOf(HttpParamsCst.ERROR_CUSTOM_MESSAGE).equals(theaterBean.getId()) //
			) {
				expandFav.setImageDrawable(context.getResources().getDrawable(isFav ? R.drawable.btn_star_big_on : R.drawable.btn_star_big_off));
			} else {
				expandFav.setImageDrawable(context.getResources().getDrawable(R.drawable.vide));
			}
		} else {
			objectName.setText(context.getResources().getString(R.string.itemMoreTheaters));
			expandFav.setImageDrawable(context.getResources().getDrawable(R.drawable.vide));
		}
		objectSubContentName.setText(strTheater.toString());
	}

	public void setMovie(MovieBean movieBean, boolean isFav) {
		this.movieBean = movieBean;

		objectName.setText(movieBean.getMovieName());
		objectSubContentName.setText(AndShowtimeDateNumberUtil.showMovieTimeLength(context, movieBean));
		expandFav.setImageDrawable(context.getResources().getDrawable(R.drawable.vide));
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
