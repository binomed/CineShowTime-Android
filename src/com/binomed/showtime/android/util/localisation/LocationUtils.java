package com.binomed.showtime.android.util.localisation;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.binomed.showtime.R;
import com.binomed.showtime.android.util.AndShowtimeFactory;
import com.binomed.showtime.beans.LocalisationBean;
import com.binomed.showtime.cst.GoogleKeys;
import com.skyhookwireless.wps.WPSAuthentication;
import com.skyhookwireless.wps.WPSStreetAddressLookup;
import com.skyhookwireless.wps.XPS;

public final class LocationUtils {

	public enum ProviderEnum {

		GPS_PROVIDER(LocationManager.GPS_PROVIDER, "GPS") //
		, GSM_PROVIDER(LocationManager.NETWORK_PROVIDER, "GSM") //
		, XPS_PROVIDER("XPS", "XPS") // GPS, WIFI, GPRS
		, WIFI_PROVIDER("WIFI", "WIFI")// WPS
		, IP_PROVIDER("IP", "IP")// WPS
		;

		private ProviderEnum(String androidProvider, String preferencesCode) {
			this.androidProvider = androidProvider;
			this.preferencesCode = preferencesCode;
		}

		private String androidProvider;

		private String preferencesCode;

		public String getAndroidProvider() {
			return androidProvider;
		}

		public String getPreferencesCode() {
			return preferencesCode;
		}

	}

	private static final String ANDROID_SETTINGS_LOCATION_SOURCE_SETTINGS = "android.settings.LOCATION_SOURCE_SETTINGS"; //$NON-NLS-1$

	private static final String TAG = "LocationUtils"; //$NON-NLS-1$

	private static LocationManager getLocationManager(Context context) {
		return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}

	public static boolean isLocalisationEnabled(Context context, ProviderEnum provider) {
		boolean result = false;
		switch (provider) {
		case GPS_PROVIDER:
		case GSM_PROVIDER: {
			LocationManager locationManager = getLocationManager(context);
			if (locationManager != null) {
				result = locationManager.isProviderEnabled(provider.getAndroidProvider());
			}
			break;
		}
		case XPS_PROVIDER: {
			LocationManager locationManager = getLocationManager(context);
			WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			if ((locationManager != null) && (wm != null)) {
				result = (locationManager.isProviderEnabled(ProviderEnum.GPS_PROVIDER.getAndroidProvider()) // 
						|| locationManager.isProviderEnabled(ProviderEnum.GSM_PROVIDER.getAndroidProvider())) //
						&& wm.isWifiEnabled();
			}
			break;
		}
		case WIFI_PROVIDER: {
			WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			if (wm != null) {
				result = wm.isWifiEnabled();
			}
			break;
		}
		case IP_PROVIDER: {
			result = true;
			break;
		}
		default:
			break;
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

	public static void registerLocalisationListener(Context context, ProviderEnum provider, LocalisationManagement listener) {
		switch (provider) {
		case GPS_PROVIDER:
		case GSM_PROVIDER: {
			LocationManager locationManager = getLocationManager(context);
			if (locationManager != null) {
				locationManager.requestLocationUpdates(provider.getAndroidProvider(), 10000, 10, listener);
			} else {
				Log.d(TAG, "No listener Put"); //$NON-NLS-1$
			}
			break;
		}
		case IP_PROVIDER:
		case WIFI_PROVIDER:
		case XPS_PROVIDER: {
			XPS xps = listener.getXps();
			if (xps == null) {
				xps = new XPS(context);
				listener.setXps(xps);
			}
			WPSAuthentication auth = new WPSAuthentication(GoogleKeys.SKYHOOK_USER_NAME, GoogleKeys.SKYHOOK_REALM);
			switch (provider) {
			case IP_PROVIDER: {
				xps.getIPLocation(auth //
						, WPSStreetAddressLookup.WPS_LIMITED_STREET_ADDRESS_LOOKUP // 
						, listener//
						);
				break;
			}
			case WIFI_PROVIDER: {
				xps.getLocation(auth //
						, WPSStreetAddressLookup.WPS_LIMITED_STREET_ADDRESS_LOOKUP // 
						, listener//
						);
				break;
			}
			case XPS_PROVIDER: {
				xps.getXPSLocation(auth,
				// note we convert _period to seconds
						(int) (5000 / 1000) //
						, 30 //
						, listener//
						);

			}
			default:
				break;
			}
			break;
		}
		default:
			break;
		}
	}

	public static void unRegisterListener(Context context, ProviderEnum provider, LocalisationManagement listener) {
		switch (provider) {
		case GPS_PROVIDER:
		case GSM_PROVIDER: {
			LocationManager locationManager = getLocationManager(context);
			if (locationManager != null) {
				locationManager.removeUpdates(listener);
			} else {
				Log.d(TAG, "No listener Put"); //$NON-NLS-1$
			}
			break;
		}
		case WIFI_PROVIDER:
		case XPS_PROVIDER: {
			XPS xps = listener.getXps();
			if (xps != null) {
				xps.abort();
			}
			break;
		}
		default:
			break;
		}
	}

	public static Location getLastLocation(Context context, ProviderEnum provider) {
		Location result = null;
		switch (provider) {
		case GPS_PROVIDER:
		case GSM_PROVIDER: {
			LocationManager locationManager = getLocationManager(context);
			if (locationManager != null) {
				result = locationManager.getLastKnownLocation(provider.getAndroidProvider());
			}
			break;
		}
		default:
			break;
		}
		return result;
	}

	public static ProviderEnum getProvider(SharedPreferences prefs, Context context) {
		String provider = prefs.getString(context.getResources().getString(R.string.preference_loc_key_localisation_provider) //
				, context.getResources().getString(R.string.preference_loc_default_localisation_provider));
		ProviderEnum providerValue = ProviderEnum.GPS_PROVIDER;
		if (ProviderEnum.GPS_PROVIDER.getAndroidProvider().equals(provider)) {
			providerValue = ProviderEnum.GPS_PROVIDER;
		} else if (ProviderEnum.GPS_PROVIDER.getAndroidProvider().equals(provider)) {
			providerValue = ProviderEnum.GSM_PROVIDER;
		} else if (ProviderEnum.WIFI_PROVIDER.getAndroidProvider().equals(provider)) {
			providerValue = ProviderEnum.WIFI_PROVIDER;
		} else if (ProviderEnum.IP_PROVIDER.getAndroidProvider().equals(provider)) {
			providerValue = ProviderEnum.IP_PROVIDER;
		} else if (ProviderEnum.XPS_PROVIDER.getAndroidProvider().equals(provider)) {
			providerValue = ProviderEnum.XPS_PROVIDER;
		}

		return providerValue;
	}

	public static boolean isEmptyLocation(LocalisationBean localisation) {
		return (localisation == null) // 
				|| ((localisation.getCityName() == null || localisation.getCityName().length() == 0) //
				&& ((localisation.getLatitude() == null || localisation.getLatitude() == 0)//
				|| (localisation.getLongitude() == null || localisation.getLongitude() == 0))//
				);
	}

	public static String getLocationString(Location coordiante) {
		String cityName = "";
		Geocoder geocoder = AndShowtimeFactory.getGeocoder();
		Double latitude = coordiante != null ? coordiante.getLatitude() : 0;
		Double longitude = coordiante != null ? coordiante.getLongitude() : 0;
		if (geocoder != null) {
			if (latitude != null && longitude != null && (latitude != 0 && longitude != 0)) {
				List<Address> addressList = null;
				try {
					addressList = geocoder.getFromLocation(latitude, longitude, 1);
				} catch (Exception e) {
					Log.e(TAG, "error Searching latitude, longitude :" + latitude + "," + longitude, e);
				}
				if ((addressList != null) && !addressList.isEmpty()) {
					if (addressList.get(0).getLocality() != null) {
						cityName = addressList.get(0).getLocality();
					}
					if (addressList.get(0).getLocality() != null && addressList.get(0).getPostalCode() != null) {
						cityName += " " + addressList.get(0).getPostalCode();
					}
					if (addressList.get(0).getLocality() != null && addressList.get(0).getCountryCode() != null) {
						cityName += ", " + addressList.get(0).getCountryCode();
					}
				}
			}
		}
		return cityName;
	}

}