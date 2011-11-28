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
package com.binomed.showtime.android.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.NearResp;
import com.binomed.showtime.android.parser.xml.ParserImdbResultXml;
import com.binomed.showtime.android.parser.xml.ParserNearResultXml;
import com.binomed.showtime.android.parser.xml.ParserSimpleResultXml;
import com.binomed.showtime.cst.HttpParamsCst;
import com.binomed.showtime.cst.SpecialChars;
import com.binomed.showtime.util.AndShowtimeNumberFormat;

public abstract class CineShowtimeRequestManage {

	public static final String TAG = "RequestManager"; //$NON-NLS-1$

	public static NearResp searchTheatersOrMovies(Context context, Double latitude, Double longitude, String cityName, String movieName, String theaterId, int day, int start, String origin, String hourLocalized, String minutesLocalized) throws Exception {

		URLBuilder andShowtimeUriBuilder = new URLBuilder(CineShowTimeEncodingUtil.convertLocaleToEncoding());
		andShowtimeUriBuilder.setProtocol(HttpParamsCst.BINOMED_APP_PROTOCOL);
		andShowtimeUriBuilder.setAdress(getAppEngineUrl(context));
		andShowtimeUriBuilder.completePath(HttpParamsCst.BINOMED_APP_PATH);
		andShowtimeUriBuilder.completePath(((movieName != null) && (movieName.length() > 0)) ? HttpParamsCst.MOVIE_GET_METHODE : HttpParamsCst.NEAR_GET_METHODE);
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_LANG, Locale.getDefault().getLanguage());
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_OUTPUT, HttpParamsCst.VALUE_XML);
		// andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_ZIP, HttpParamsCst.VALUE_TRUE);
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_IE, CineShowTimeEncodingUtil.getEncoding());
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_OE, CineShowTimeEncodingUtil.getEncoding());
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_CURENT_TIME, String.valueOf(Calendar.getInstance().getTimeInMillis()));
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_TIME_ZONE, TimeZone.getDefault().getID());
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_HOUR_LOCALIZE, hourLocalized);
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_MIN_LOCALIZE, minutesLocalized);

		if (theaterId != null) {
			andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_THEATER_ID //
					, theaterId);
		}
		if (day > 0) {
			andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_DAY //
					, String.valueOf(day));
		}
		if (start > 0) {
			andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_START //
					, String.valueOf(start));
		}
		String countryCode = Locale.getDefault().getCountry();

		Geocoder geocoder = CineShowtimeFactory.getGeocoder();
		Location originalPlace = null;
		if (geocoder != null) {
			if (cityName != null) {
				try {
					cityName = URLDecoder.decode(cityName, CineShowTimeEncodingUtil.getEncoding());
				} catch (Exception e) {
					Log.e(TAG, "error during decoding", e);
				}
				List<Address> addressList = null;
				try {
					addressList = geocoder.getFromLocationName(cityName, 1);
				} catch (Exception e) {
					Log.e(TAG, "error Searching cityName :" + cityName, e);
				}
				if ((addressList != null) && !addressList.isEmpty()) {
					if (addressList.get(0).getLocality() != null) {
						cityName = addressList.get(0).getLocality();
					}
					// if (addressList.get(0).getLocality() != null &&
					// addressList.get(0).getPostalCode() != null) {
					// cityName += " " + addressList.get(0).getPostalCode();
					// }
					if ((addressList.get(0).getLocality() != null) && (addressList.get(0).getCountryCode() != null)) {
						cityName += ", " + addressList.get(0).getCountryCode();
					}

					originalPlace = new Location("GPS");
					originalPlace.setLongitude(addressList.get(0).getLongitude());
					originalPlace.setLatitude(addressList.get(0).getLatitude());

					countryCode = addressList.get(0).getCountryCode();
				}
				if (cityName != null) {
					andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_PLACE, cityName);
				}
			}
			if ((latitude != null) && (longitude != null) && ((latitude != 0) && (longitude != 0))) {
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
					if ((addressList.get(0).getLocality() != null) && (addressList.get(0).getPostalCode() != null)) {
						cityName += " " + addressList.get(0).getPostalCode();
					}
					if ((addressList.get(0).getLocality() != null) && (addressList.get(0).getCountryCode() != null)) {
						cityName += ", " + addressList.get(0).getCountryCode();
					}
					originalPlace = new Location("GPS");
					originalPlace.setLongitude(addressList.get(0).getLongitude());
					originalPlace.setLatitude(addressList.get(0).getLatitude());

					countryCode = addressList.get(0).getCountryCode();
				}
				andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_LAT //
						, AndShowtimeNumberFormat.getFormatGeoCoord().format(latitude));
				andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_LONG//
						, AndShowtimeNumberFormat.getFormatGeoCoord().format(longitude));
				if (cityName != null) {
					andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_PLACE, cityName);
				}
			}
		} else {
			if (cityName != null) {
				try {
					cityName = URLDecoder.decode(cityName, CineShowTimeEncodingUtil.getEncoding());
				} catch (Exception e) {
					Log.e(TAG, "error during decoding", e);
				}
				andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_PLACE, cityName);
			}
			if ((latitude != null) && (longitude != null) && ((latitude != 0) && (longitude != 0))) {
				andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_LAT //
						, AndShowtimeNumberFormat.getFormatGeoCoord().format(latitude));
				andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_LONG//
						, AndShowtimeNumberFormat.getFormatGeoCoord().format(longitude));
			}
		}
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_COUNTRY_CODE, countryCode);

		if (movieName != null) {
			try {
				movieName = URLDecoder.decode(movieName, CineShowTimeEncodingUtil.getEncoding());
			} catch (Exception e) {
				Log.e(TAG, "error during decoding", e);
			}
			andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_MOVIE_NAME, movieName);
		}

		String uri = andShowtimeUriBuilder.toUri();
		Log.i(TAG, "send request : " + uri); //$NON-NLS-1$
		HttpGet getMethod = CineShowtimeFactory.getHttpGet();
		getMethod.setURI(new URI(uri));
		HttpResponse res = CineShowtimeFactory.getHttpClient().execute(getMethod);

		XMLReader reader = CineShowtimeFactory.getXmlReader();
		ParserNearResultXml parser = CineShowtimeFactory.getParserNearResultXml();
		reader.setContentHandler(parser);
		InputSource inputSource = CineShowtimeFactory.getInputSource();
		// inputSource.setByteStream(new GZIPInputStream(res.getEntity().getContent()));
		inputSource.setByteStream(res.getEntity().getContent());

		reader.parse(inputSource);

		NearResp resultBean = parser.getNearRespBean();
		resultBean.setCityName(cityName);

		return resultBean;
	}

	private static String getAppEngineUrl(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String appEngineUrl = prefs.getString(CineShowtimeCst.PREF_KEY_APP_ENGINE, null);
		if (appEngineUrl == null) {
			try {
				URLBuilder andShowtimeUriBuilder = new URLBuilder(CineShowTimeEncodingUtil.convertLocaleToEncoding());
				andShowtimeUriBuilder.setProtocol(HttpParamsCst.BINOMED_APP_PROTOCOL);
				andShowtimeUriBuilder.setAdress(HttpParamsCst.BINOMED_APP_URL);
				andShowtimeUriBuilder.completePath(HttpParamsCst.BINOMED_APP_PATH);
				andShowtimeUriBuilder.completePath(HttpParamsCst.SEVER_GET_METHODE);
				String uri = andShowtimeUriBuilder.toUri();
				Log.i(TAG, "send request : " + uri); //$NON-NLS-1$
				HttpGet getMethod = CineShowtimeFactory.getHttpGet();
				getMethod.setURI(new URI(uri));
				HttpResponse res = CineShowtimeFactory.getHttpClient().execute(getMethod);
				XMLReader reader = CineShowtimeFactory.getXmlReader();
				ParserSimpleResultXml parser = CineShowtimeFactory.getParserSimpleResultXml();
				reader.setContentHandler(parser);
				InputSource inputSource = CineShowtimeFactory.getInputSource();
				inputSource.setByteStream(res.getEntity().getContent());

				reader.parse(inputSource);

				appEngineUrl = parser.getUrl();
				if ((appEngineUrl == null) || (appEngineUrl.length() == 0)) {
					appEngineUrl = HttpParamsCst.BINOMED_APP_URL;
				}

				try {
					Editor editor = prefs.edit();
					editor.putString(CineShowtimeCst.PREF_KEY_APP_ENGINE, appEngineUrl);
					editor.commit();
				} catch (Exception e) {
				}
			} catch (Exception e) {
				appEngineUrl = HttpParamsCst.BINOMED_APP_URL;
			}
			Log.i(TAG, "result : " + appEngineUrl); //$NON-NLS-1$
		}

		return appEngineUrl;

	}

	private static Location manageLocation(Geocoder geocoder, String cityName, URLBuilder andShowtimeUriBuilder, Double latitude, Double longitude) throws IOException {
		Location originalPlace = null;
		if (geocoder != null) {
			if (cityName != null) {
				try {
					cityName = URLDecoder.decode(cityName, CineShowTimeEncodingUtil.getEncoding());
				} catch (Exception e) {
					Log.e(TAG, "Error during encode", e);
				}
				List<Address> addressList = geocoder.getFromLocationName(cityName, 1);
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
					originalPlace = new Location("GPS");
					originalPlace.setLongitude(addressList.get(0).getLongitude());
					originalPlace.setLatitude(addressList.get(0).getLatitude());
				}
				andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_PLACE, cityName);
			}
			if ((latitude != null) && (longitude != null) && ((latitude != 0) && (longitude != 0))) {
				List<Address> addressList = geocoder.getFromLocation(longitude, latitude, 1);
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
					originalPlace = new Location("GPS");
					originalPlace.setLongitude(addressList.get(0).getLongitude());
					originalPlace.setLatitude(addressList.get(0).getLatitude());
				}
				andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_LAT //
						, AndShowtimeNumberFormat.getFormatGeoCoord().format(latitude));
				andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_LONG//
						, AndShowtimeNumberFormat.getFormatGeoCoord().format(longitude));
			}
		} else {
			if (cityName != null) {
				try {
					cityName = URLDecoder.decode(cityName, CineShowTimeEncodingUtil.getEncoding());
				} catch (Exception e) {
					Log.e(TAG, "error during decoding", e);
				}
				andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_PLACE, cityName);
			}
			if ((latitude != null) && (longitude != null) && ((latitude != 0) && (longitude != 0))) {
				andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_LAT //
						, AndShowtimeNumberFormat.getFormatGeoCoord().format(latitude));
				andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_LONG//
						, AndShowtimeNumberFormat.getFormatGeoCoord().format(longitude));
			}
		}

		return originalPlace;
	}

	public static void completeMovieDetail(Context context, MovieBean movie, String near) throws Exception {
		URLBuilder andShowtimeUriBuilder = new URLBuilder(CineShowTimeEncodingUtil.convertLocaleToEncoding());
		andShowtimeUriBuilder.setProtocol(HttpParamsCst.BINOMED_APP_PROTOCOL);
		andShowtimeUriBuilder.setAdress(getAppEngineUrl(context));
		andShowtimeUriBuilder.completePath(HttpParamsCst.IMDB_GET_METHODE);
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_LANG, Locale.getDefault().getLanguage());
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_OUTPUT, HttpParamsCst.VALUE_XML);
		// andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_ZIP, HttpParamsCst.VALUE_TRUE);
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_IE, CineShowTimeEncodingUtil.getEncoding());
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_OE, CineShowTimeEncodingUtil.getEncoding());
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_MOVIE_ID, movie.getId());
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_TRAILER, String.valueOf(true));
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_MOVIE_NAME, movie.getEnglishMovieName());
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_MOVIE_CUR_LANG_NAME, movie.getMovieName());
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_PLACE, URLEncoder.encode((near != null) ? near : SpecialChars.EMPTY, CineShowTimeEncodingUtil.getEncoding()));

		String uri = andShowtimeUriBuilder.toUri();
		Log.i(TAG, "send request : " + uri); //$NON-NLS-1$
		HttpGet getMethod = CineShowtimeFactory.getHttpGet();
		getMethod.setURI(new URI(uri));
		HttpResponse res = CineShowtimeFactory.getHttpClient().execute(getMethod);

		XMLReader reader = CineShowtimeFactory.getXmlReader();
		ParserImdbResultXml parser = CineShowtimeFactory.getParserImdbResultXml();

		reader.setContentHandler(parser);
		InputSource inputSource = CineShowtimeFactory.getInputSource();
		// inputSource.setByteStream(new GZIPInputStream(res.getEntity().getContent()));
		inputSource.setByteStream(res.getEntity().getContent());

		reader.parse(inputSource);

		MovieBean movieResult = parser.getMovieBean();

		movie.setImdbDesrciption(movieResult.isImdbDesrciption());
		movie.setDescription(movieResult.getDescription());
		movie.setUrlImg(movieResult.getUrlImg());
		movie.setUrlWikipedia(movieResult.getUrlWikipedia());
		movie.setImdbId(movieResult.getImdbId());
		movie.setRate(movieResult.getRate());
		movie.setStyle(movieResult.getStyle());
		movie.setDirectorList(movieResult.getDirectorList());
		movie.setActorList(movieResult.getActorList());
		movie.setReviews(movieResult.getReviews());
		movie.setYoutubeVideos(movieResult.getYoutubeVideos());

	}

	public static void completeMovieDetailStream(MovieBean movie) throws Exception {

		try {
			File root = Environment.getExternalStorageDirectory();
			if (root.canWrite()) {
				File posterFile = new File(root, new StringBuilder(CineShowtimeCst.FOLDER_POSTER).append(movie.getId()).append(".jpg").toString());
				posterFile.getParentFile().mkdirs();
				if (posterFile.exists()) {
					Log.i(TAG, "img existe");
					movie.setImgStream(new FileInputStream(posterFile));
				} else {
					Log.i(TAG, "img existe pas : lancement de la requete");
					HttpGet getMethod = CineShowtimeFactory.getHttpGet();
					getMethod.setURI(new URI(movie.getUrlImg()));
					HttpResponse res = CineShowtimeFactory.getHttpClient().execute(getMethod);

					FileOutputStream fileOutPut = new FileOutputStream(posterFile);
					InputStream inputStream = res.getEntity().getContent();
					byte[] tempon = new byte[10240];

					while (true) {
						int nRead = inputStream.read(tempon, 0, tempon.length);
						if (nRead <= 0) {
							break;
						}
						fileOutPut.write(tempon, 0, nRead);
					}
					fileOutPut.close();

					movie.setImgStream(new FileInputStream(posterFile));
				}

			} else {
				Log.i(TAG, "SD not accessible : ");
				HttpGet getMethod = CineShowtimeFactory.getHttpGet();
				getMethod.setURI(new URI(movie.getUrlImg()));
				HttpResponse res = CineShowtimeFactory.getHttpClient().execute(getMethod);

				InputStream inputStream = res.getEntity().getContent();
				movie.setImgStream(inputStream);
			}
		} catch (IOException e) {
			Log.e(TAG, "Could not write file " + e.getMessage()); //$NON-NLS-1$
		}

	}

}
