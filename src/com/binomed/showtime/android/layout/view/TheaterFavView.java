package com.binomed.showtime.android.layout.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.model.LocalisationBean;
import com.binomed.showtime.android.model.TheaterBean;

public class TheaterFavView extends RelativeLayout {

	private TextView theaterView = null;
	private TextView cityView = null;
	private ImageView deleteView = null;
	private TheaterBean theaterBean;

	public TheaterBean getTheaterBean() {
		return theaterBean;
	}

	public TheaterFavView(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_fav_item, this);

		theaterView = (TextView) this.findViewById(R.id.favItemTheater);
		deleteView = (ImageView) this.findViewById(R.id.favItemDelete);
		cityView = (TextView) this.findViewById(R.id.favItemCity);
	}

	public void setTheater(TheaterBean theaterBean) {
		this.theaterBean = theaterBean;

		String cityName = "";
		int resourceDrawable = 0;

		if ("0".equals(theaterBean.getId())) {
			resourceDrawable = R.drawable.vide;
		} else {
			resourceDrawable = R.drawable.ic_delete;
			LocalisationBean place = (LocalisationBean) theaterBean.getPlace();
			if (place != null && place.getCityName() != null) {
				cityName = place.getCityName();
			}
		}

		theaterView.setText(theaterBean.getTheaterName());
		cityView.setText(cityName);
		deleteView.setBackgroundResource(resourceDrawable);
	}

	@Override
	public void setOnClickListener(OnClickListener listener) {
		deleteView.setOnClickListener(listener);
	}
}
