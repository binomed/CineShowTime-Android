package com.binomed.showtime.android.util.localisation;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.handler.TextCallBackFromLocation;
import com.binomed.showtime.android.util.AndShowtimeFactory;
import com.binomed.showtime.android.util.localisation.LocationUtils.ProviderEnum;
import com.skyhookwireless.wps.IPLocation;
import com.skyhookwireless.wps.IPLocationCallback;
import com.skyhookwireless.wps.WPSContinuation;
import com.skyhookwireless.wps.WPSLocation;
import com.skyhookwireless.wps.WPSLocationCallback;
import com.skyhookwireless.wps.WPSPeriodicLocationCallback;
import com.skyhookwireless.wps.WPSReturnCode;
import com.skyhookwireless.wps.XPS;

public class LocalisationManagement implements IListenerLocalisationUtilCallBack //
		, OnClickListener //
		, LocationListener // 
		, WPSPeriodicLocationCallback //
		, WPSLocationCallback //
		, IPLocationCallback //
{

	private static final String TAG = null;
	private SharedPreferences prefs;
	private ProviderEnum provider;
	private boolean checkboxPreference;
	private Bitmap bitmapGpsOn;
	private Bitmap bitmapGpsOff;
	private Context context;
	private ImageView imageGps;
	private CheckBox chckBoxGps;
	private AutoCompleteTextView textSearch;
	private IModelLocalisation model;
	private boolean locationListener;
	private TextCallBackFromLocation handlerTextSearch;
	private XPS xps;

	protected XPS getXps() {
		return xps;
	}

	protected void setXps(XPS xps) {
		this.xps = xps;
	}

	public LocalisationManagement(Context context, ImageView imageGps, CheckBox chckBoxGps, AutoCompleteTextView textSearch, IModelLocalisation model) {
		super();
		this.context = context;
		this.imageGps = imageGps;
		this.chckBoxGps = chckBoxGps;
		this.textSearch = textSearch;
		this.model = model;

		// Init handler
		handlerTextSearch = new TextCallBackFromLocation(this.textSearch);

		// Init preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(context);

		// Init Provider
		initProvider();

		// Init viewState
		bitmapGpsOn = BitmapFactory.decodeResource(context.getResources(), R.drawable.gps_activ);
		bitmapGpsOff = BitmapFactory.decodeResource(context.getResources(), R.drawable.gps_not_activ);
		imageGps.setImageBitmap(bitmapGpsOff);
		chckBoxGps.setChecked(false);
		chckBoxGps.setEnabled(LocationUtils.isLocalisationEnabled(context, provider));

		// Init Listeners
		chckBoxGps.setOnClickListener(this);

		// Init Localisation
		if (model.getLocalisation() == null) {
			model.setLocalisation(checkboxPreference ? LocationUtils.getLastLocation(context, provider) : null);
		}
	}

	private void initProvider() {
		provider = LocationUtils.getProvider(prefs, context);
		checkboxPreference = prefs.getBoolean(context.getResources().getString(R.string.preference_loc_key_enable_localisation), true);

	}

	private void initListenersLocation() {
		if (checkboxPreference) {
			locationListener = true;
			model.setLocalisation(null);
			LocationUtils.registerLocalisationListener(context, provider, this);
		}

	}

	private void removeListenersLocation() {
		locationListener = false;
		LocationUtils.unRegisterListener(context, provider, this);
	}

	/*
	 * 
	 * IListenerLocalisationUtilCallBack
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.util.localisation.IListenerLocalisationUtilCallBack#onPause()
	 */
	@Override
	public void onPause() {
		removeListenersLocation();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.util.localisation.IListenerLocalisationUtilCallBack#onPreferenceReturn()
	 */
	@Override
	public void onPreferenceReturn() {
		checkboxPreference = prefs.getBoolean(context.getResources().getString(R.string.preference_loc_key_enable_localisation), true);
		chckBoxGps.setEnabled(LocationUtils.isLocalisationEnabled(context, provider));
		if (checkboxPreference && chckBoxGps.isChecked() && !locationListener) {
			initListenersLocation();
		} else {
			removeListenersLocation();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.util.localisation.IListenerLocalisationUtilCallBack#onResume()
	 */
	@Override
	public void onResume() {
		initProvider();
		model.setLocalisation(checkboxPreference ? LocationUtils.getLastLocation(context, provider) : null);

	}

	/*
	 * 
	 * OnClickListener
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		textSearch.setEnabled(!chckBoxGps.isChecked());
		if (!chckBoxGps.isChecked()) {
			imageGps.setImageBitmap(bitmapGpsOff);
			removeListenersLocation();
		} else {
			textSearch.setText("");
			imageGps.setImageBitmap(bitmapGpsOn);
			initListenersLocation();
		}
	}

	/*
	 * 
	 * LocationListener
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	@Override
	public void onLocationChanged(Location arg0) {
		Log.d(TAG, "Change location : lat : " + arg0.getLatitude() + " / lon : " + arg0.getLongitude());
		model.setLocalisation(arg0);
		if (textSearch.getText().toString().length() == 0) {
			AndShowtimeFactory.initGeocoder(context);
			handlerTextSearch.sendInputRecieved(LocationUtils.getLocationString(arg0));
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
	 */
	@Override
	public void onProviderDisabled(String arg0) {
		chckBoxGps.setEnabled(false);
		if (chckBoxGps.isChecked()) {
			chckBoxGps.setChecked(false);
			textSearch.setEnabled(true);
			imageGps.setImageBitmap(bitmapGpsOff);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	@Override
	public void onProviderEnabled(String arg0) {
		chckBoxGps.setEnabled(true);
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
	 * Commons method XPS
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skyhookwireless.wps._sdkfc#done()
	 */
	@Override
	public void done() {
		// Nothing to do because localisation was done

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skyhookwireless.wps._sdkfc#handleError(com.skyhookwireless.wps.WPSReturnCode)
	 */
	@Override
	public WPSContinuation handleError(WPSReturnCode error) {
		switch (error) {
		case WPS_ERROR_LOCATION_CANNOT_BE_DETERMINED: {
			Log.e(TAG, error.toString());
			break;
		}
		case WPS_ERROR_WIFI_NOT_AVAILABLE: {
			Log.e(TAG, error.toString());
			break;
		}
		case WPS_ERROR_SERVER_UNAVAILABLE: {

			Log.e(TAG, error.toString());
			break;
		}
		case WPS_ERROR_NO_WIFI_IN_RANGE: {
			Log.e(TAG, error.toString());

			break;
		}
		case WPS_ERROR: {
			Log.e(TAG, error.name());

			break;
		}
		default:
			Log.e(TAG, error.name());
			break;
		}
		// TODO gérer les cas d'erreur
		// in all case, we'll stop
		return WPSContinuation.WPS_STOP;
	}

	/*
	 * Methods for XPS
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skyhookwireless.wps.WPSPeriodicLocationCallback#handleWPSPeriodicLocation(com.skyhookwireless.wps.WPSLocation)
	 */
	@Override
	public WPSContinuation handleWPSPeriodicLocation(WPSLocation wpsLocation) {

		Location location = model.getLocalisation();
		if (location == null) {
			location = new Location(ProviderEnum.GPS_PROVIDER.getAndroidProvider());
			model.setLocalisation(location);
		}
		location.setLatitude(wpsLocation.getLatitude());
		location.setLongitude(wpsLocation.getLongitude());
		if (textSearch.getText().toString().length() == 0) {
			AndShowtimeFactory.initGeocoder(context);
			handlerTextSearch.sendInputRecieved(LocationUtils.getLocationString(location));
		}
		// In all case we'ill continue after getting location only user would stop
		return WPSContinuation.WPS_CONTINUE;
	}

	/*
	 * Method Wifi
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skyhookwireless.wps.WPSLocationCallback#handleWPSLocation(com.skyhookwireless.wps.WPSLocation)
	 */
	@Override
	public void handleWPSLocation(WPSLocation wpsLocation) {

		Location location = model.getLocalisation();
		if (location == null) {
			location = new Location(ProviderEnum.GPS_PROVIDER.getAndroidProvider());
			model.setLocalisation(location);
		}
		location.setLatitude(wpsLocation.getLatitude());
		location.setLongitude(wpsLocation.getLongitude());
		if (textSearch.getText().toString().length() == 0) {
			AndShowtimeFactory.initGeocoder(context);
			handlerTextSearch.sendInputRecieved(LocationUtils.getLocationString(location));
		}
	}

	/*
	 * Method IP
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skyhookwireless.wps.IPLocationCallback#handleIPLocation(com.skyhookwireless.wps.IPLocation)
	 */
	@Override
	public void handleIPLocation(IPLocation ipLocation) {
		Location location = model.getLocalisation();
		if (location == null) {
			location = new Location(ProviderEnum.GPS_PROVIDER.getAndroidProvider());
			model.setLocalisation(location);
		}
		location.setLongitude(ipLocation.getLongitude());
		location.setLatitude(ipLocation.getLatitude());
		if (textSearch.getText().toString().length() == 0) {
			AndShowtimeFactory.initGeocoder(context);
			handlerTextSearch.sendInputRecieved(LocationUtils.getLocationString(location));
		}

	}

}