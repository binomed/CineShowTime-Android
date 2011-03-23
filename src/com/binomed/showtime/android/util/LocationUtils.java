package com.binomed.showtime.android.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import com.binomed.showtime.android.R;

public final class LocationUtils {

	private static final String GPS = "gps"; //$NON-NLS-1$
	private static final String ANDROID_SETTINGS_LOCATION_SOURCE_SETTINGS = "android.settings.LOCATION_SOURCE_SETTINGS"; //$NON-NLS-1$

	private static final String TAG = "LocationListener"; //$NON-NLS-1$

	private static LocationManager getLocationManager(Context context) {
		return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}

	public static boolean isGPSEnabled(Context context) {
		boolean result = false;
		LocationManager locationManager = getLocationManager(context);
		if (locationManager != null) {
			result = locationManager.isProviderEnabled(GPS);
		}
		return result;
	}

	public static void checkGPSLocation(final Context context) {
		if (!isGPSEnabled(context)) {
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

	public static void registerListener(Context context, LocationListener listener) {
		LocationManager locationManager = getLocationManager(context);
		if (locationManager != null) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, listener);
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

	public static Location getLastLocation(Context context) {
		Location result = null;
		LocationManager locationManager = getLocationManager(context);
		if (locationManager != null) {
			result = locationManager.getLastKnownLocation(GPS);
		}
		return result;
	}

}
