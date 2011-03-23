package com.binomed.showtime.android.searchnearactivity;

import java.io.UnsupportedEncodingException;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.binomed.showtime.R;
import com.binomed.showtime.android.cst.AndShowtimeCst;
import com.binomed.showtime.android.layout.dialogs.fav.TheaterFavSelectionListener;
import com.binomed.showtime.android.layout.dialogs.sort.ListSelectionListener;
import com.binomed.showtime.android.layout.view.MovieView;
import com.binomed.showtime.android.util.AndShowtimeFactory;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.beans.LocalisationBean;
import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.TheaterBean;

public class ListenerSearchNearActivity implements OnClickListener, OnChildClickListener, LocationListener, TheaterFavSelectionListener, OnItemSelectedListener, OnGroupClickListener, ListSelectionListener {

	private AndShowTimeSearchNearActivity nearActivity;
	private ControlerSearchNearActivity controler;
	private ModelSearchNearActivity model;

	private static final String TAG = "ListenerNearActivity"; //$NON-NLS-1$

	public ListenerSearchNearActivity(AndShowTimeSearchNearActivity nearActivity, ControlerSearchNearActivity controlerNearActivity, ModelSearchNearActivity model) {
		super();
		this.nearActivity = nearActivity;
		this.controler = controlerNearActivity;
		this.model = model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.searchNearBtnSearch: {
			String cityName = null;
			if (nearActivity.fieldCityName.getText().toString().length() > 0) {
				cityName = nearActivity.fieldCityName.getText().toString();
			}
			model.setCityName(cityName);
			model.setFavTheaterId(null);
			model.setStart(0);

			try {

				boolean canLaunch = true;
				boolean btnCheck = nearActivity.checkButtonLocalisation.isChecked();
				if (btnCheck && model.getGpsLocalisation() == null) {
					Toast.makeText(nearActivity //
							, R.string.msgNoGps //
							, Toast.LENGTH_LONG) //
							.show();
					canLaunch = false;
				} else if (!btnCheck && (cityName == null)) {
					Toast.makeText(nearActivity //
							, R.string.msgNoCityName //
							, Toast.LENGTH_LONG) //
							.show();
					canLaunch = false;
				}

				if (canLaunch) {
					if (btnCheck) {
						model.setCityName(null);
						model.setLocalisationSearch(model.getGpsLocalisation());
					} else {
						model.setLocalisationSearch(null);
					}
					nearActivity.launchNearService();
				}

			} catch (Exception e) {
				Log.e(TAG, "erreur au lancement du service", e); //$NON-NLS-1$
			}
			break;
		}
		case R.id.searchNearBtnSpeech: {
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			intent.putExtra(RecognizerIntent.EXTRA_PROMPT, nearActivity.getResources().getString(R.string.msgSpeecCity));
			nearActivity.startActivityForResult(intent, AndShowTimeSearchNearActivity.VOICE_RECOGNITION_REQUEST_CODE);
		}
		case R.id.searchNearLocation: {
			nearActivity.fieldCityName.setEnabled(!nearActivity.checkButtonLocalisation.isChecked());
			if (!nearActivity.checkButtonLocalisation.isChecked()) {
				nearActivity.gpsImgView.setImageBitmap(nearActivity.bitmapGpsOff);
				nearActivity.removeListenersLocation();
			} else {
				nearActivity.gpsImgView.setImageBitmap(nearActivity.bitmapGpsOn);
				nearActivity.initListenersLocation();
			}
			break;
		}
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListView.OnChildClickListener#onChildClick(android.widget.ExpandableListView, android.view.View, int, int, long)
	 */
	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		MovieView movieView = (MovieView) v;

		TheaterBean theater = movieView.getTheaterBean();
		MovieBean movie = movieView.getMovieBean();

		controler.openMovieActivity(movie, theater);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	@Override
	public void onLocationChanged(Location arg0) {
		Log.d(TAG, "Change location : lat : " + arg0.getLatitude() + " / lon : " + arg0.getLongitude());
		model.setGpsLocalisation(arg0);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
	 */
	@Override
	public void onProviderDisabled(String arg0) {
		nearActivity.checkButtonLocalisation.setEnabled(false);
		if (nearActivity.checkButtonLocalisation.isChecked()) {
			nearActivity.checkButtonLocalisation.setChecked(false);
			nearActivity.fieldCityName.setEnabled(true);
			nearActivity.gpsImgView.setImageBitmap(nearActivity.bitmapGpsOff);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	@Override
	public void onProviderEnabled(String arg0) {
		nearActivity.checkButtonLocalisation.setEnabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
	 */
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.nearactivity.view.TheaterFavSelectionListener#removeTheater(com.binomed.showtime.beans.TheaterBean)
	 */
	@Override
	public void removeTheater(TheaterBean theater) {
		controler.removeFavorite(theater);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.nearactivity.view.TheaterFavSelectionListener#theaterSelected(com.binomed.showtime.beans.TheaterBean)
	 */
	@Override
	public void theaterSelected(TheaterBean theater) {
		model.setFavTheaterId(theater.getId());
		model.setLocalisationSearch(null);
		model.setCityName(null);
		if (theater.getPlace() != null) {
			LocalisationBean localisation = theater.getPlace();
			if (localisation.getLatitude() != null && localisation.getLongitude() != null) {
				Location locationTheater = new Location("GPS");
				locationTheater.setLatitude(localisation.getLatitude());
				locationTheater.setLongitude(localisation.getLongitude());
				model.setLocalisationSearch(locationTheater);
			} else {
				if (theater.getPlace().getCityName() != null //
						&& theater.getPlace().getCityName().length() > 0) {
					model.setCityName(theater.getPlace().getCityName());
				}
				if (theater.getPlace().getCountryNameCode() != null //
						&& theater.getPlace().getCountryNameCode().length() > 0 //
						&& model.getCityName() != null) {
					model.setCityName(model.getCityName() + ", " + theater.getPlace().getCountryNameCode()); //$NON-NLS-1$
				}
			}
		}
		try {
			nearActivity.launchNearService();
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "erreur au lancement du service", e); //$NON-NLS-1$
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemSelected(AdapterView<?> adapter, View view, int groupPositon, long id) {
		switch (adapter.getId()) {
		case R.id.searchNearSpinner: {
			model.setDay(groupPositon);
			break;
		}
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
	 */
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListView.OnGroupClickListener#onGroupClick(android.widget.ExpandableListView, android.view.View, int, long)
	 */
	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
		switch (parent.getId()) {
		case R.id.searchNearListResult: {
			int theaterListSize = BeanManagerFactory.getNearResp().getTheaterList().size();
			if (theaterListSize == groupPosition) {
				model.setStart(model.getStart() + 10);
				try {
					nearActivity.launchNearService();
				} catch (UnsupportedEncodingException e) {
					// TODO
				}
			}
			break;
		}

		default:
			break;
		}
		return false;
	}

	@Override
	public void sortSelected(int sourceID, int sortKey) {
		switch (sourceID) {
		case AndShowTimeSearchNearActivity.ID_SORT: {
			switch (sortKey) {
			case AndShowtimeCst.SORT_THEATER_NAME:
				nearActivity.comparator = AndShowtimeFactory.getTheaterNameComparator();
				break;
			case AndShowtimeCst.SORT_THEATER_DISTANCE:
				nearActivity.comparator = AndShowtimeFactory.getTheaterDistanceComparator();
				break;
			case AndShowtimeCst.SORT_SHOWTIME:
				nearActivity.comparator = AndShowtimeFactory.getTheaterShowtimeComparator();
				break;

			default:
				nearActivity.comparator = null;
				break;
			}
			nearActivity.display();
			break;
		}
		case AndShowTimeSearchNearActivity.ID_VOICE: {
			nearActivity.fieldCityName.setText(model.getVoiceCityList().get(sortKey));
			break;
		}
		default:
			break;
		}

	}

}
