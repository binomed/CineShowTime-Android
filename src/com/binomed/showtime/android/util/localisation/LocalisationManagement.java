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
package com.binomed.showtime.android.util.localisation;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.handler.TextCallBackFromLocation;
import com.binomed.showtime.android.layout.view.AutoCompleteTextWithSpeech;
import com.binomed.showtime.android.util.CineShowtimeFactory;
import com.binomed.showtime.android.util.localisation.LocationUtils.ProviderEnum;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
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
	// private Bitmap bitmapGpsOn;
	private AnimationDrawable bitmapGpsOn;
	private Bitmap bitmapGpsOff;
	private Bitmap bitmapGpsDisabled;
	private Context context;
	private ImageView imageGps;
	private AutoCompleteTextWithSpeech textSearch;
	private IModelLocalisation model;
	private boolean locationListener, checkedGps;
	private TextCallBackFromLocation handlerTextSearch;
	private GoogleAnalyticsTracker tracker;
	private XPS xps;

	protected XPS getXps() {
		return xps;
	}

	protected void setXps(XPS xps) {
		this.xps = xps;
	}

	public LocalisationManagement(Context context, GoogleAnalyticsTracker tracker, ImageView imageGps, AutoCompleteTextWithSpeech textSearch, IModelLocalisation model) {
		super();
		this.context = context;
		this.imageGps = imageGps;
		this.textSearch = textSearch;
		this.model = model;
		this.tracker = tracker;

		// Init handler
		handlerTextSearch = new TextCallBackFromLocation(this.textSearch);

		// Init preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(context);

		// Init Provider
		initProvider();

		// Init viewState
		checkedGps = false;
		// bitmapGpsOn = BitmapFactory.decodeResource(context.getResources(), R.drawable.gps_activ);

		// bitmapGpsOn = AnimationUtils.loadAnimation(context, R.drawable.gps_anim);
		bitmapGpsOff = BitmapFactory.decodeResource(context.getResources(), R.drawable.gps_not_activ);
		bitmapGpsDisabled = BitmapFactory.decodeResource(context.getResources(), R.drawable.gps_disable);
		if (LocationUtils.isLocalisationEnabled(context, provider)) {
			imageGps.setBackgroundDrawable(null);
			imageGps.setImageBitmap(bitmapGpsOff);
		} else {
			imageGps.setBackgroundDrawable(null);
			imageGps.setImageBitmap(bitmapGpsDisabled);
		}

		// Init Listeners
		imageGps.setOnClickListener(this);

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
		if (LocationUtils.isLocalisationEnabled(context, provider)) {
			if (bitmapGpsOn != null) {
				bitmapGpsOn.stop();
				bitmapGpsOn = null;
			}
			imageGps.setBackgroundDrawable(null);
			imageGps.setImageBitmap(bitmapGpsOff);
		} else {
			if (bitmapGpsOn != null) {
				bitmapGpsOn.stop();
				bitmapGpsOn = null;
			}
			imageGps.setBackgroundDrawable(null);
			imageGps.setImageBitmap(bitmapGpsDisabled);
		}
		if (checkboxPreference && checkedGps && !locationListener) {
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
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.util.localisation.IListenerLocalisationUtilCallBack#isGPSCheck()
	 */
	@Override
	public boolean isGPSCheck() {
		return checkedGps;
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
		tracker.trackEvent("Action", "GPS", "Use of gps", 0);
		checkedGps = !checkedGps;
		textSearch.setEnabled(!checkedGps);
		if (!checkedGps) {
			if (bitmapGpsOn != null) {
				bitmapGpsOn.stop();
				bitmapGpsOn = null;
			}
			imageGps.setBackgroundDrawable(null);
			imageGps.setImageBitmap(bitmapGpsOff);
			removeListenersLocation();
		} else {
			textSearch.setText("");
			// imageGps.setImageBitmap(bitmapGpsOn);
			// imageGps.startAnimation(bitmapGpsOn);
			imageGps.setImageBitmap(null);
			imageGps.setBackgroundResource(R.drawable.gps_anim);
			bitmapGpsOn = (AnimationDrawable) imageGps.getBackground();
			bitmapGpsOn.start();
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
		tracker.trackEvent("Action", "GPS", "Gps return", 0);
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, "Change location : lat : " + arg0.getLatitude() + " / lon : " + arg0.getLongitude());
		}
		model.setLocalisation(arg0);
		if (textSearch.getText().toString().length() == 0) {
			CineShowtimeFactory.initGeocoder(context);
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
		if (checkedGps) {
			checkedGps = false;
			textSearch.setEnabled(true);
			// imageGps.setImageBitmap(bitmapGpsOff);
			if (bitmapGpsOn != null) {
				bitmapGpsOn.stop();
				bitmapGpsOn = null;
			}
			imageGps.setBackgroundDrawable(null);
			imageGps.setImageBitmap(bitmapGpsDisabled);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	@Override
	public void onProviderEnabled(String arg0) {
		if (bitmapGpsOn != null) {
			bitmapGpsOn.stop();
			bitmapGpsOn = null;
		}
		imageGps.setBackgroundDrawable(null);
		imageGps.setImageBitmap(bitmapGpsOff);
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
			CineShowtimeFactory.initGeocoder(context);
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
			CineShowtimeFactory.initGeocoder(context);
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
			CineShowtimeFactory.initGeocoder(context);
			handlerTextSearch.sendInputRecieved(LocationUtils.getLocationString(location));
		}

	}

}