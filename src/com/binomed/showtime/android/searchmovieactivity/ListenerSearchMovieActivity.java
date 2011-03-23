package com.binomed.showtime.android.searchmovieactivity;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.binomed.showtime.R;
import com.binomed.showtime.android.cst.AndShowtimeCst;
import com.binomed.showtime.android.layout.dialogs.sort.ListSelectionListener;
import com.binomed.showtime.android.util.AndShowtimeFactory;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.beans.MovieResp;
import com.binomed.showtime.beans.TheaterBean;

public class ListenerSearchMovieActivity implements OnClickListener, OnItemClickListener, LocationListener, OnItemSelectedListener, ListSelectionListener {

	private AndShowTimeSearchMovieActivity movieActivity;
	private ControlerSearchMovieActivity controler;
	private ModelSearchMovieActivity model;

	private static final String TAG = "ListenerNearActivity"; //$NON-NLS-1$

	public ListenerSearchMovieActivity(AndShowTimeSearchMovieActivity nearActivity, ControlerSearchMovieActivity controlerNearActivity, ModelSearchMovieActivity model) {
		super();
		this.movieActivity = nearActivity;
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
		case R.id.searchMovieBtnSearch: {
			String cityName = null;
			if (movieActivity.fieldNearName.getText().toString().length() > 0) {
				cityName = movieActivity.fieldNearName.getText().toString();
			}
			model.setCityName(cityName);
			String movieName = null;
			if (movieActivity.fieldMovieName.getText().toString().length() > 0) {
				movieName = movieActivity.fieldMovieName.getText().toString();
			}
			model.setMovieName(movieName);

			model.setFavTheaterId(null);

			try {

				boolean canLaunch = true;
				boolean btnCheck = movieActivity.checkLocationButton.isChecked();
				if (movieName == null) {
					Toast.makeText(movieActivity //
							, R.string.msgNoMovieName //
							, Toast.LENGTH_LONG)//
							.show();
					canLaunch = false;
				}
				if (canLaunch && btnCheck && model.getGpsLocalisation() == null) {
					Toast.makeText(movieActivity //
							, R.string.msgNoGps //
							, Toast.LENGTH_LONG) //
							.show();
					canLaunch = false;
				} else if (canLaunch && !btnCheck && (cityName == null)) {
					Toast.makeText(movieActivity //
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
					movieActivity.launchMovieService();
				}

			} catch (Exception e) {
				Log.e(TAG, "erreur au lancement du service", e); //$NON-NLS-1$
			}
			break;
		}
		case R.id.searchMovieBtnSpeechCity: {
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			intent.putExtra(RecognizerIntent.EXTRA_PROMPT, movieActivity.getResources().getString(R.string.msgSpeecCity));
			movieActivity.startActivityForResult(intent, AndShowTimeSearchMovieActivity.VOICE_RECOGNITION_CITY_REQUEST_CODE);
		}
		case R.id.searchMovieBtnSpeechMovie: {
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			intent.putExtra(RecognizerIntent.EXTRA_PROMPT, movieActivity.getResources().getString(R.string.msgSpeecMovie));
			movieActivity.startActivityForResult(intent, AndShowTimeSearchMovieActivity.VOICE_RECOGNITION_MOVIE_REQUEST_CODE);
		}
		case R.id.searchMovieLocation: {
			movieActivity.fieldNearName.setEnabled(!movieActivity.checkLocationButton.isChecked());
			if (!movieActivity.checkLocationButton.isChecked()) {
				movieActivity.gpsImgView.setImageBitmap(movieActivity.bitmapGpsOff);
				movieActivity.removeListenersLocation();
			} else {
				movieActivity.gpsImgView.setImageBitmap(movieActivity.bitmapGpsOn);
				movieActivity.initListenersLocation();
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
		movieActivity.checkLocationButton.setEnabled(false);
		if (!movieActivity.checkLocationButton.isChecked()) {
			movieActivity.checkLocationButton.setChecked(false);
			movieActivity.fieldNearName.setEnabled(true);
			movieActivity.gpsImgView.setImageBitmap(movieActivity.bitmapGpsOff);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	@Override
	public void onProviderEnabled(String arg0) {
		movieActivity.checkLocationButton.setEnabled(true);
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
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> adpater, View view, int groupPosition, long id) {
		MovieResp movieResp = BeanManagerFactory.getMovieResp();
		if (movieResp != null) {
			TheaterBean theater = movieResp.getTheaterList().get(groupPosition);
			controler.openMovieActivity(movieResp.getMovie(), theater);
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
		case R.id.searchMovieSpinner: {
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

	@Override
	public void sortSelected(int sourceID, int sortKey) {
		switch (sourceID) {
		case AndShowTimeSearchMovieActivity.ID_SORT: {
			switch (sortKey) {
			case AndShowtimeCst.SORT_THEATER_NAME:
				movieActivity.comparator = AndShowtimeFactory.getTheaterNameComparator();
				break;
			case AndShowtimeCst.SORT_THEATER_DISTANCE:
				movieActivity.comparator = AndShowtimeFactory.getTheaterDistanceComparator();
				break;
			case AndShowtimeCst.SORT_SHOWTIME:
				movieActivity.comparator = AndShowtimeFactory.getTheaterShowtimeComparator();
				break;

			default:
				movieActivity.comparator = null;
				break;
			}

			movieActivity.display();
			break;
		}
		case AndShowTimeSearchMovieActivity.ID_VOICE_CITY: {
			movieActivity.fieldNearName.setText(model.getVoiceCityList().get(0));
			break;
		}
		case AndShowTimeSearchMovieActivity.ID_VOICE_MOVIE: {
			movieActivity.fieldMovieName.setText(model.getVoiceMovieList().get(0));
			break;
		}
		default:
			break;
		}

	}

}
