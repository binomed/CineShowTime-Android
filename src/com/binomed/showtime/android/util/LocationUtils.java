package com.binomed.showtime.android.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import com.binomed.showtime.R;

public final class LocationUtils {

	public enum ProviderEnum {

		GPS_PROVIDER(LocationManager.GPS_PROVIDER) //
		, GSM_PROVIDER(LocationManager.NETWORK_PROVIDER);

		private ProviderEnum(String androidProvider) {
			this.androidProvider = androidProvider;
		}

		private String androidProvider;

		public String getAndroidProvider() {
			return androidProvider;
		}

	}

	private static final String GPS = "gps"; //$NON-NLS-1$
	private static final String ANDROID_SETTINGS_LOCATION_SOURCE_SETTINGS = "android.settings.LOCATION_SOURCE_SETTINGS"; //$NON-NLS-1$

	private static final String TAG = "LocationListener"; //$NON-NLS-1$

	private static LocationManager getLocationManager(Context context) {
		return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}

	public static boolean isLocalisationEnabled(Context context, ProviderEnum provider) {
		boolean result = false;
		LocationManager locationManager = getLocationManager(context);
		if (locationManager != null) {
			result = locationManager.isProviderEnabled(provider.getAndroidProvider());
		}
		return result;
	}

	public static void checkProviderLocation(final Context context, ProviderEnum provider) {
		if (!isLocalisationEnabled(context, provider)) {
			new AlertDialog.Builder(context)
			// 
					.setTitle(R.string.gpsInactiveTitle)
					//
					.setMessage(R.string.gpsInactiveMsg)
					//
					.setNeutralButton(R.string.gpsInactiveBtnYes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							context.startActivity(new Intent(ANDROID_SETTINGS_LOCATION_SOURCE_SETTINGS));
						}
					})
					//
					.setNegativeButton(R.string.gpsInactiveBtnNo, null).show();
		}
	}

	public static void registerLocalisationListener(Context context, ProviderEnum provider, LocationListener listener) {
		LocationManager locationManager = getLocationManager(context);
		if (locationManager != null) {
			locationManager.requestLocationUpdates(provider.getAndroidProvider(), 10000, 10, listener);
		} else {
			Log.d(TAG, "No listener Put"); //$NON-NLS-1$
		}
	}

	public static void unRegisterListener(Context context, LocationListener listener) {
		LocationManager locationManager = getLocationManager(context);
		if (locationManager != null) {
			locationManager.removeUpdates(listener);
		} else {
			Log.d(TAG, "No listener Put"); //$NON-NLS-1$
		}
	}

	public static Location getLastLocation(Context context, ProviderEnum provider) {
		Location result = null;
		LocationManager locationManager = getLocationManager(context);
		if (locationManager != null) {
			result = locationManager.getLastKnownLocation(provider.getAndroidProvider());
		}
		return result;
	}

	public static ProviderEnum getProvider(SharedPreferences prefs, Context context) {
		String provider = prefs.getString(context.getResources().getString(R.string.preference_loc_key_localisation_provider) //
				, context.getResources().getString(R.string.preference_loc_default_localisation_provider));
		String[] providerCodes = context.getResources().getStringArray(R.array.mode_localisation_code);
		ProviderEnum providerValue = ProviderEnum.GPS_PROVIDER;
		if (providerCodes.length > 0 && providerCodes[0].equals(provider)) {
			providerValue = ProviderEnum.GPS_PROVIDER;
		} else if (providerCodes.length > 1 && providerCodes[1].equals(provider)) {
			providerValue = ProviderEnum.GSM_PROVIDER;
		}

		return providerValue;
	}

}
