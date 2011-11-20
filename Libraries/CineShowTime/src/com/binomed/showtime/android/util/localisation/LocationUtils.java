/*
5 * Copyright (C) 2011 Binomed (http://blog.binomed.fr)
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

import java.net.URI;
import java.text.MessageFormat;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.db.CineShowtimeDbAdapter;
import com.binomed.showtime.android.model.LocalisationBean;
import com.binomed.showtime.android.util.CineShowtimeFactory;
import com.binomed.showtime.cst.GoogleKeys;
import com.skyhookwireless.wps.RegistrationCallback;
import com.skyhookwireless.wps.WPSAuthentication;
import com.skyhookwireless.wps.WPSContinuation;
import com.skyhookwireless.wps.WPSReturnCode;
import com.skyhookwireless.wps.WPSStreetAddressLookup;
import com.skyhookwireless.wps.XPS;

public final class LocationUtils {

	private static final String mapRequestDist = "http://maps.google.com/maps/nav?q=from:{0}%20to:{1}&ie=utf8&oe=utf8&sensor=false&key={2}"; //$NON-NLS-1$

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
				try {
					result = locationManager.isProviderEnabled(provider.getAndroidProvider());
				} catch (Exception e) {
					result = false;
				}
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
						@Override
						public void onClick(DialogInterface dialog, int which) {
							context.startActivity(new Intent(ANDROID_SETTINGS_LOCATION_SOURCE_SETTINGS));
						}
					})
					//
					.setNegativeButton(R.string.gpsInactiveBtnNo, null).show();
		}
	}

	public static void registerLocalisationListener(final Context context, final ProviderEnum provider, final LocalisationManagement listener) {
		switch (provider) {
		case GPS_PROVIDER:
		case GSM_PROVIDER: {
			LocationManager locationManager = getLocationManager(context);
			if (locationManager != null) {
				locationManager.requestLocationUpdates(provider.getAndroidProvider(), 10000, 10, listener);
			} else {
				if (Log.isLoggable(TAG, Log.DEBUG)) {
					Log.d(TAG, "No listener Put"); //$NON-NLS-1$
				}
			}
			break;
		}
		case IP_PROVIDER:
		case WIFI_PROVIDER:
		case XPS_PROVIDER: {
			final XPS xps = listener.getXps();
			// if (xps == null) {
			// xps = new XPS(context);
			// listener.setXps(xps);
			// }
			// WPS wps = listener.getWps();
			// if (wps == null) {
			// wps = new WPS(context);
			// listener.setWps(wps);
			// }

			// WPSAuthentication auth = listener.getWpsAuth();
			// if (auth == null) {
			// auth = new WPSAuthentication(GoogleKeys.SKYHOOK_USER_NAME_REGISTER, GoogleKeys.SKYHOOK_REALM);
			// listener.setWpsAuth(auth);
			// }
			final WPSAuthentication auth = new WPSAuthentication(GoogleKeys.SKYHOOK_USER_NAME_REGISTER, GoogleKeys.SKYHOOK_REALM);
			// if (!checkSkyHookRegistration(context)) {
			// WPSAuthentication authRegister = new WPSAuthentication(GoogleKeys.SKYHOOK_USER_NAME_REGISTER, GoogleKeys.SKYHOOK_REALM);
			// WPS wps = new WPS(context);
			xps.registerUser(auth, null, new RegistrationCallback() {

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

				@Override
				public void done() {
					// TODO Auto-generated method stub
					Log.i(TAG, "Registration Done ! ");

				}

				@Override
				public void handleSuccess() {
					// Intent intentNearFillDBService = new Intent(context, CineShowDBGlobalService.class);
					// intentNearFillDBService.putExtra(ParamIntent.SERVICE_DB_TYPE, CineShowtimeCst.DB_TYPE_SKYHOOK_REGISTRATION);
					// context.startService(intentNearFillDBService);

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
						xps.getXPSLocation(auth, (5000 / 1000) //
								, 30 //
								, listener//
						);

					}
					default:
						break;
					}
				}
			});
			// }
			// switch (provider) {
			// case IP_PROVIDER: {
			// xps.getIPLocation(auth //
			// , WPSStreetAddressLookup.WPS_LIMITED_STREET_ADDRESS_LOOKUP //
			// , listener//
			// );
			// break;
			// }
			// case WIFI_PROVIDER: {
			// xps.getLocation(auth //
			// , WPSStreetAddressLookup.WPS_LIMITED_STREET_ADDRESS_LOOKUP //
			// , listener//
			// );
			// break;
			// }
			// case XPS_PROVIDER: {
			// xps.getXPSLocation(auth, (5000 / 1000) //
			// , 30 //
			// , listener//
			// );
			//
			// }
			// default:
			// break;
			// }
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
				if (Log.isLoggable(TAG, Log.DEBUG)) {
					Log.d(TAG, "No listener Put"); //$NON-NLS-1$
				}
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

		String[] entriesInitial = context.getResources().getStringArray(R.array.mode_localisation);
		String[] entryValuesInitial = context.getResources().getStringArray(R.array.mode_localisation_code);

		int i = 0;
		for (String entrie : entriesInitial) {
			if (entrie.equals(provider)) {
				break;
			}
			i++;
		}
		if (i == entriesInitial.length) {
			i = 0;
		}
		provider = entryValuesInitial[i];

		ProviderEnum providerValue = ProviderEnum.GSM_PROVIDER;
		if (ProviderEnum.GPS_PROVIDER.getPreferencesCode().equals(provider)) {
			providerValue = ProviderEnum.GPS_PROVIDER;
		} else if (ProviderEnum.GSM_PROVIDER.getPreferencesCode().equals(provider)) {
			providerValue = ProviderEnum.GSM_PROVIDER;
		} else if (ProviderEnum.WIFI_PROVIDER.getPreferencesCode().equals(provider)) {
			providerValue = ProviderEnum.WIFI_PROVIDER;
		} else if (ProviderEnum.IP_PROVIDER.getPreferencesCode().equals(provider)) {
			providerValue = ProviderEnum.IP_PROVIDER;
		} else if (ProviderEnum.XPS_PROVIDER.getPreferencesCode().equals(provider)) {
			providerValue = ProviderEnum.XPS_PROVIDER;
		}

		return providerValue;
	}

	public static boolean isEmptyLocation(LocalisationBean localisation) {
		return (localisation == null) //
				|| (((localisation.getCityName() == null) || (localisation.getCityName().length() == 0)) //
				&& (((localisation.getLatitude() == null) || (localisation.getLatitude() == 0))//
				|| ((localisation.getLongitude() == null) || (localisation.getLongitude() == 0)))//
				);
	}

	public static String getLocationString(Location coordiante) throws Exception {
		String cityName = "";
		Geocoder geocoder = CineShowtimeFactory.getGeocoder();
		Double latitude = coordiante != null ? coordiante.getLatitude() : 0;
		Double longitude = coordiante != null ? coordiante.getLongitude() : 0;
		if (geocoder != null) {
			if ((latitude != null) && (longitude != null) && ((latitude != 0) && (longitude != 0))) {
				List<Address> addressList = null;
				try {
					addressList = geocoder.getFromLocation(latitude, longitude, 1);
				} catch (Exception e) {
					Log.e(TAG, "error Searching latitude, longitude :" + latitude + "," + longitude, e);
					throw new Exception("error Searching latitude, longitude :" + latitude + "," + longitude, e);
				}
				if ((addressList != null) && !addressList.isEmpty()) {
					if (addressList.get(0).getLocality() != null) {
						cityName = addressList.get(0).getLocality();
					}
					if ((addressList.get(0).getLocality() != null) && (addressList.get(0).getPostalCode() != null)) {
						cityName += " " + addressList.get(0).getPostalCode();
					}
					if ((addressList.get(0).getLocality() != null) && (addressList.get(0).getCountryCode() != null)) {
						cityName += ", " + addressList.get(0).getCountryCode();
					}
				}
			}
		}
		return cityName;
	}

	private static boolean checkSkyHookRegistration(Context context) {
		boolean result = false;
		CineShowtimeDbAdapter mDbHelper = new CineShowtimeDbAdapter(context);
		mDbHelper.open();
		Cursor cursorRegistration = mDbHelper.fetchSkyHookRegistration();

		if (cursorRegistration != null) {
			try {
				result = cursorRegistration.moveToFirst();
			} finally {
				cursorRegistration.close();
				if ((mDbHelper != null) && mDbHelper.isOpen()) {
					mDbHelper.close();
				}
			}
		}

		return result;
	}

	public static void completeLocalisationBean(String source, LocalisationBean localisationBean) {
		String uri = MessageFormat.format(mapRequestDist //
				, source.replaceAll(" ", "+") //
				, localisationBean.getSearchQuery().replace(" ", "+") //
				, GoogleKeys.GOOGLE_MAPS_KEY //
				);

		Log.i(TAG, "Send maps Query : " + uri);

		try {
			HttpGet getMethod = CineShowtimeFactory.getHttpGet();
			getMethod.setURI(new URI(uri));
			HttpResponse res = CineShowtimeFactory.getHttpClient().execute(getMethod);

			JSONObject jsonObj = new JSONObject(EntityUtils.toString(res.getEntity()));

			JSONObject statusJSON = jsonObj.getJSONObject("Status");
			if ((statusJSON != null) && statusJSON.has("code")) {
				Object code = statusJSON.get("code");
				if ((code != null) && "200".equals(code.toString())) {
					if (jsonObj.has("Directions")) {
						JSONObject directionJSON = jsonObj.getJSONObject("Directions");
						if (directionJSON.has("Distance")) {
							JSONObject distanceJSON = directionJSON.getJSONObject("Distance");
							localisationBean.setDistance(Double.valueOf((distanceJSON.get("meters") != null) ? distanceJSON.getDouble("meters") : 0).floatValue() / 1000);
						}
						if (directionJSON.has("Duration")) {
							JSONObject durationJSON = directionJSON.getJSONObject("Duration");
							localisationBean.setDistanceTime((durationJSON.get("seconds") != null) ? Long.valueOf(durationJSON.getString("seconds") + "000") : -1l);
						}
					}
					// get informations about destination :
					if (jsonObj.has("Placemark") && (jsonObj.getJSONArray("Placemark").length() == 2)) {
						JSONArray arrayDirections = jsonObj.getJSONArray("Placemark");
						JSONObject objDest = arrayDirections.getJSONObject(1);
						if (objDest.has("AddressDetails")) {
							JSONObject jsonDetail = objDest.getJSONObject("AddressDetails");
							if (jsonDetail.has("Country")) {
								JSONObject jsonCountry = jsonDetail.getJSONObject("Country");
								localisationBean.setCountryName(jsonCountry.getString("CountryName"));
								localisationBean.setCountryNameCode(jsonCountry.getString("CountryNameCode"));

								if (jsonCountry.has("AdministrativeArea")) {
									JSONObject jsonAdminArea = jsonCountry.getJSONObject("AdministrativeArea");
									if (jsonAdminArea.has("SubAdministrativeArea")) {
										JSONObject jsonSubAdminArea = jsonAdminArea.getJSONObject("SubAdministrativeArea");
										if (jsonSubAdminArea.has("Locality")) {
											JSONObject jsonLocality = jsonSubAdminArea.getJSONObject("Locality");
											localisationBean.setCityName(jsonLocality.getString("LocalityName"));

											if (jsonLocality.has("PostalCode")) {
												JSONObject jsonPostalCode = jsonLocality.getJSONObject("PostalCode");
												localisationBean.setPostalCityNumber(jsonPostalCode.getString("PostalCodeNumber"));
											}
										}
									}
								}
							}
						}
						if (objDest.has("Point")) {
							JSONObject jsonPoint = objDest.getJSONObject("Point");
							if (jsonPoint.has("coordinates")) {
								JSONArray coordinatesArray = jsonPoint.getJSONArray("coordinates");
								localisationBean.setLatitude(coordinatesArray.getDouble(0));
								localisationBean.setLatitude(coordinatesArray.getDouble(1));
							}
						}
					}
				}

			}

			if ((localisationBean.getCityName() == null) || (localisationBean.getCityName().length() == 0)) {
				localisationBean.setCityName(source);
			}
		} catch (Exception e) {
			Log.e(TAG, "Error during getting direction from " + source + " to " + localisationBean.getSearchQuery(), e);
		}
	}

}
