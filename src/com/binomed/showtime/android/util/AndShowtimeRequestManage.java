package com.binomed.showtime.android.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Environment;
import android.util.Log;

import com.binomed.showtime.android.parser.xml.ParserImdbResultXml;
import com.binomed.showtime.android.parser.xml.ParserMovieResultXml;
import com.binomed.showtime.android.parser.xml.ParserNearResultXml;
import com.binomed.showtime.beans.LocalisationBean;
import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.MovieResp;
import com.binomed.showtime.beans.NearResp;
import com.binomed.showtime.beans.TheaterBean;
import com.binomed.showtime.cst.HttpParamsCst;
import com.binomed.showtime.util.AndShowtimeNumberFormat;

public abstract class AndShowtimeRequestManage {

	public static final String TAG = "RequestManager"; //$NON-NLS-1$

	public static NearResp searchTheaters(Double latitude, Double longitude, String cityName, String theaterId, int day, int start) throws Exception {

		URLBuilder andShowtimeUriBuilder = new URLBuilder(AndShowTimeEncodingUtil.convertLocaleToEncoding());
		andShowtimeUriBuilder.setProtocol(HttpParamsCst.BINOMED_APP_PROTOCOL);
		andShowtimeUriBuilder.setAdress(HttpParamsCst.BINOMED_APP_URL);
		andShowtimeUriBuilder.completePath(HttpParamsCst.BINOMED_APP_PATH);
		andShowtimeUriBuilder.completePath(HttpParamsCst.NEAR_GET_METHODE);
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_LANG, Locale.getDefault().getCountry());
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_OUTPUT, HttpParamsCst.VALUE_XML);
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_ZIP, HttpParamsCst.VALUE_TRUE);
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_IE, AndShowTimeEncodingUtil.getEncoding());
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_OE, AndShowTimeEncodingUtil.getEncoding());
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_CURENT_TIME, String.valueOf(Calendar.getInstance().getTimeInMillis()));
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_TIME_ZONE, TimeZone.getDefault().getID());

		// Builder androidUriBuilder = new Builder();
		// androidUriBuilder.scheme(HttpParamsCst.BINOMED_APP_PROTOCOL);
		// androidUriBuilder.authority(HttpParamsCst.BINOMED_APP_URL);
		// androidUriBuilder.appendPath(HttpParamsCst.BINOMED_APP_PATH);
		// androidUriBuilder.appendPath(HttpParamsCst.NEAR_GET_METHODE);
		// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_LANG, Locale.getDefault().getCountry());
		// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_OUTPUT, HttpParamsCst.VALUE_XML);
		// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_ZIP, HttpParamsCst.VALUE_TRUE);
		// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_IE, AndShowTimeEncodingUtil.getEncoding());
		// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_OE, AndShowTimeEncodingUtil.getEncoding());
		// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_CURENT_TIME, String.valueOf(Calendar.getInstance().getTimeInMillis()));
		// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_TIME_ZONE, TimeZone.getDefault().getID());
		if (theaterId != null) {
			// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_THEATER_ID //
			// , theaterId);
			andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_THEATER_ID //
					, theaterId);
		}
		if (day > 0) {
			// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_DAY //
			// , String.valueOf(day));
			andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_DAY //
					, String.valueOf(day));
		}
		if (start > 0) {
			// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_START //
			// , String.valueOf(start));
			andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_START //
					, String.valueOf(start));
		}

		Geocoder geocoder = AndShowtimeFactory.getGeocoder();
		Location originalPlace = null;
		if (geocoder != null) {
			if (cityName != null) {
				cityName = URLDecoder.decode(cityName, AndShowTimeEncodingUtil.getEncoding());
				List<Address> addressList = geocoder.getFromLocationName(cityName, 1);
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

					originalPlace = new Location("GPS");
					originalPlace.setLongitude(addressList.get(0).getLongitude());
					originalPlace.setLatitude(addressList.get(0).getLatitude());
				}
				if (cityName != null) {
					// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_PLACE, cityName);
					andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_PLACE, cityName);
				}
			}
			if (latitude != null && longitude != null && (latitude != 0 && longitude != 0)) {
				List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
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
					originalPlace = new Location("GPS");
					originalPlace.setLongitude(addressList.get(0).getLongitude());
					originalPlace.setLatitude(addressList.get(0).getLatitude());
				}
				// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_LAT //
				// , AndShowtimeNumberFormat.getFormatGeoCoord().format(latitude));
				// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_LONG//
				// , AndShowtimeNumberFormat.getFormatGeoCoord().format(longitude));
				andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_LAT //
						, AndShowtimeNumberFormat.getFormatGeoCoord().format(latitude));
				andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_LONG//
						, AndShowtimeNumberFormat.getFormatGeoCoord().format(longitude));
				if (cityName != null) {
					// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_PLACE, cityName);
					andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_PLACE, cityName);
				}
			}
		} else {
			if (cityName != null) {
				cityName = URLDecoder.decode(cityName, AndShowTimeEncodingUtil.getEncoding());
				// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_PLACE, cityName);
				andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_PLACE, cityName);
			}
			if (latitude != null && longitude != null && (latitude != 0 && longitude != 0)) {
				// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_LAT //
				// , AndShowtimeNumberFormat.getFormatGeoCoord().format(latitude));
				// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_LONG//
				// , AndShowtimeNumberFormat.getFormatGeoCoord().format(longitude));
				andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_LAT //
						, AndShowtimeNumberFormat.getFormatGeoCoord().format(latitude));
				andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_LONG//
						, AndShowtimeNumberFormat.getFormatGeoCoord().format(longitude));
			}
		}

		// String uri = androidUriBuilder.toString();
		String uri = andShowtimeUriBuilder.toUri();
		Log.i(TAG, "send request : " + uri); //$NON-NLS-1$
		HttpGet getMethod = AndShowtimeFactory.getHttpGet();
		getMethod.setURI(new URI(uri));
		HttpResponse res = AndShowtimeFactory.getHttpClient().execute(getMethod);

		// TODO � voir s'il faut pas mettre �a ailleurs
		if (start == 0) {
			BeanManagerFactory.cleanCentralMovieMap();
			BeanManagerFactory.cleanCentralTheaterMap();
		}
		XMLReader reader = AndShowtimeFactory.getXmlReader();
		ParserNearResultXml parser = AndShowtimeFactory.getParserNearResultXml();
		reader.setContentHandler(parser);
		InputSource inputSource = AndShowtimeFactory.getInputSource();
		inputSource.setByteStream(new GZIPInputStream(res.getEntity().getContent()));

		reader.parse(inputSource);

		NearResp resultBean = parser.getNearRespBean();
		if (start > 0) {
			// We have to complete the result with previous entries
			NearResp lastNearResp = BeanManagerFactory.getNearResp();
			lastNearResp.setHasMoreResults(resultBean.isHasMoreResults());
			lastNearResp.getTheaterList().addAll(resultBean.getTheaterList());
			for (String movieId : resultBean.getMapMovies().keySet()) {
				if (!lastNearResp.getMapMovies().containsKey(movieId)) {
					lastNearResp.getMapMovies().put(movieId, resultBean.getMapMovies().get(movieId));
				}
			}
			resultBean = lastNearResp;
		}

		List<Address> addressList = null;
		Address addressTheater = null;
		if ((geocoder != null) && (originalPlace != null)) {
			for (TheaterBean theater : resultBean.getTheaterList()) {
				LocalisationBean localisation = theater.getPlace();
				if (localisation != null && localisation.getLatitude() != null && localisation.getLongitude() != null) {
					Location locaTheater = new Location("GPS");
					locaTheater.setLatitude(localisation.getLatitude());
					locaTheater.setLongitude(localisation.getLongitude());
					theater.getPlace().setDistance(originalPlace.distanceTo(locaTheater) / 1000);
				} else if (localisation != null && localisation.getSearchQuery() != null && localisation.getSearchQuery().length() > 0) {
					addressList = geocoder.getFromLocationName(localisation.getSearchQuery(), 1);
					if (addressList != null && addressList.size() > 0) {
						addressTheater = addressList.get(0);
						localisation.setCityName(addressTheater.getLocality());
						localisation.setCountryName(addressTheater.getCountryName());
						localisation.setCountryNameCode(addressTheater.getCountryCode());
						localisation.setPostalCityNumber(addressTheater.getPostalCode());
						localisation.setLatitude(addressTheater.getLatitude());
						localisation.setLongitude(addressTheater.getLongitude());
						Location locaTheater = new Location("GPS");
						locaTheater.setLatitude(localisation.getLatitude());
						locaTheater.setLongitude(localisation.getLongitude());
						theater.getPlace().setDistance(originalPlace.distanceTo(locaTheater) / 1000);
					}
				}
			}
		}

		return resultBean;
	}

	public static MovieResp searchMovies(Double latitude, Double longitude, String cityName, String movieName, String theaterId, int day) throws Exception {

		URLBuilder andShowtimeUriBuilder = new URLBuilder(AndShowTimeEncodingUtil.convertLocaleToEncoding());
		andShowtimeUriBuilder.setProtocol(HttpParamsCst.BINOMED_APP_PROTOCOL);
		andShowtimeUriBuilder.setAdress(HttpParamsCst.BINOMED_APP_URL);
		andShowtimeUriBuilder.completePath(HttpParamsCst.BINOMED_APP_PATH);
		andShowtimeUriBuilder.completePath(HttpParamsCst.MOVIE_GET_METHODE);
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_LANG, Locale.getDefault().getCountry());
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_OUTPUT, HttpParamsCst.VALUE_XML);
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_ZIP, HttpParamsCst.VALUE_TRUE);
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_IE, AndShowTimeEncodingUtil.getEncoding());
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_OE, AndShowTimeEncodingUtil.getEncoding());
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_CURENT_TIME, String.valueOf(Calendar.getInstance().getTimeInMillis()));
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_TIME_ZONE, TimeZone.getDefault().getID());

		// Builder androidUriBuilder = new Builder();
		// androidUriBuilder.scheme(HttpParamsCst.BINOMED_APP_PROTOCOL);
		// androidUriBuilder.authority(HttpParamsCst.BINOMED_APP_URL);
		// androidUriBuilder.appendPath(HttpParamsCst.BINOMED_APP_PATH);
		// androidUriBuilder.appendPath(HttpParamsCst.MOVIE_GET_METHODE);
		// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_LANG, Locale.getDefault().getCountry());
		// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_OUTPUT, HttpParamsCst.VALUE_XML);
		// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_ZIP, HttpParamsCst.VALUE_TRUE);
		// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_IE, AndShowTimeEncodingUtil.getEncoding());
		// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_OE, AndShowTimeEncodingUtil.getEncoding());
		// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_CURENT_TIME, String.valueOf(Calendar.getInstance().getTimeInMillis()));
		// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_TIME_ZONE, TimeZone.getDefault().getID());
		if (theaterId != null) {
			// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_THEATER_ID //
			// , theaterId);
			andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_THEATER_ID //
					, theaterId);
		}
		if (day > 0) {
			// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_DAY //
			// , String.valueOf(day));
			andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_DAY //
					, String.valueOf(day));
		}

		Geocoder geocoder = AndShowtimeFactory.getGeocoder();
		Location originalPlace = null;
		if (geocoder != null) {
			if (cityName != null) {
				cityName = URLDecoder.decode(cityName, AndShowTimeEncodingUtil.getEncoding());
				List<Address> addressList = geocoder.getFromLocationName(cityName, 1);
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
					originalPlace = new Location("GPS");
					originalPlace.setLongitude(addressList.get(0).getLongitude());
					originalPlace.setLatitude(addressList.get(0).getLatitude());
				}
				// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_PLACE, cityName);
				andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_PLACE, cityName);
			}
			if (latitude != null && longitude != null && (latitude != 0 && longitude != 0)) {
				List<Address> addressList = geocoder.getFromLocation(longitude, latitude, 1);
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
					originalPlace = new Location("GPS");
					originalPlace.setLongitude(addressList.get(0).getLongitude());
					originalPlace.setLatitude(addressList.get(0).getLatitude());
				}
				// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_LAT //
				// , AndShowtimeNumberFormat.getFormatGeoCoord().format(latitude));
				// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_LONG//
				// , AndShowtimeNumberFormat.getFormatGeoCoord().format(longitude));
				andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_LAT //
						, AndShowtimeNumberFormat.getFormatGeoCoord().format(latitude));
				andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_LONG//
						, AndShowtimeNumberFormat.getFormatGeoCoord().format(longitude));
			}
		} else {
			if (cityName != null) {
				cityName = URLDecoder.decode(cityName, AndShowTimeEncodingUtil.getEncoding());
				// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_PLACE, cityName);
				andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_PLACE, cityName);
			}
			if (latitude != null && longitude != null && (latitude != 0 && longitude != 0)) {
				// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_LAT //
				// , AndShowtimeNumberFormat.getFormatGeoCoord().format(latitude));
				// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_LONG//
				// , AndShowtimeNumberFormat.getFormatGeoCoord().format(longitude));
				andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_LAT //
						, AndShowtimeNumberFormat.getFormatGeoCoord().format(latitude));
				andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_LONG//
						, AndShowtimeNumberFormat.getFormatGeoCoord().format(longitude));
			}
		}
		if (movieName != null) {
			movieName = URLDecoder.decode(movieName, AndShowTimeEncodingUtil.getEncoding());
			// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_MOVIE_NAME, movieName);
			andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_MOVIE_NAME, movieName);
		}

		// String uri = androidUriBuilder.toString();
		String uri = andShowtimeUriBuilder.toUri();
		Log.i(TAG, "send request : " + uri); //$NON-NLS-1$
		HttpGet getMethod = AndShowtimeFactory.getHttpGet();
		getMethod.setURI(new URI(uri));
		HttpResponse res = AndShowtimeFactory.getHttpClient().execute(getMethod);

		// TODO � voir s'il faut pas mettre �a ailleurs
		BeanManagerFactory.cleanCentralMovieMap();
		BeanManagerFactory.cleanCentralTheaterMap();

		XMLReader reader = AndShowtimeFactory.getXmlReader();
		ParserMovieResultXml parser = AndShowtimeFactory.getParserMovieResultXml();
		reader.setContentHandler(parser);
		InputSource inputSource = AndShowtimeFactory.getInputSource();
		inputSource.setByteStream(new GZIPInputStream(res.getEntity().getContent()));

		reader.parse(inputSource);

		MovieResp resultBean = parser.getMovieRespBean();
		List<Address> addressList = null;
		Address addressTheater = null;
		if ((geocoder != null) && (originalPlace != null)) {
			for (TheaterBean theater : resultBean.getTheaterList()) {
				LocalisationBean localisation = theater.getPlace();
				if (localisation != null && localisation.getLatitude() != null && localisation.getLongitude() != null) {
					Location locaTheater = new Location("GPS");
					locaTheater.setLatitude(localisation.getLatitude());
					locaTheater.setLongitude(localisation.getLongitude());
					theater.getPlace().setDistance(originalPlace.distanceTo(locaTheater) / 1000);
				} else if (localisation != null && localisation.getSearchQuery() != null && localisation.getSearchQuery().length() > 0) {
					addressList = geocoder.getFromLocationName(localisation.getSearchQuery(), 1);
					if (addressList != null && addressList.size() > 0) {
						addressTheater = addressList.get(0);
						localisation.setCityName(addressTheater.getLocality());
						localisation.setCountryName(addressTheater.getCountryName());
						localisation.setCountryNameCode(addressTheater.getCountryCode());
						localisation.setPostalCityNumber(addressTheater.getPostalCode());
						localisation.setLatitude(addressTheater.getLatitude());
						localisation.setLongitude(addressTheater.getLongitude());
						Location locaTheater = new Location("GPS");
						locaTheater.setLatitude(localisation.getLatitude());
						locaTheater.setLongitude(localisation.getLongitude());
						theater.getPlace().setDistance(originalPlace.distanceTo(locaTheater) / 1000);
					}
				}
			}
		}

		return resultBean;
	}

	public static void completeMovieDetail(MovieBean movie) throws Exception {
		URLBuilder andShowtimeUriBuilder = new URLBuilder(AndShowTimeEncodingUtil.convertLocaleToEncoding());
		andShowtimeUriBuilder.setProtocol(HttpParamsCst.BINOMED_APP_PROTOCOL);
		andShowtimeUriBuilder.setAdress(HttpParamsCst.BINOMED_APP_URL);
		andShowtimeUriBuilder.completePath(HttpParamsCst.IMDB_GET_METHODE);
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_LANG, Locale.getDefault().getCountry());
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_OUTPUT, HttpParamsCst.VALUE_XML);
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_ZIP, HttpParamsCst.VALUE_TRUE);
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_IE, AndShowTimeEncodingUtil.getEncoding());
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_OE, AndShowTimeEncodingUtil.getEncoding());
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_MOVIE_ID, movie.getId());
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_MOVIE_NAME, movie.getEnglishMovieName());
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_MOVIE_CUR_LANG_NAME, movie.getMovieName());

		// Builder androidUriBuilder = new Builder();
		// androidUriBuilder.scheme(HttpParamsCst.BINOMED_APP_PROTOCOL);
		// androidUriBuilder.authority(HttpParamsCst.BINOMED_APP_URL);
		// androidUriBuilder.appendPath(HttpParamsCst.IMDB_GET_METHODE);
		// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_LANG, Locale.getDefault().getCountry());
		// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_OUTPUT, HttpParamsCst.VALUE_XML);
		// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_ZIP, HttpParamsCst.VALUE_TRUE);
		// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_IE, AndShowTimeEncodingUtil.getEncoding());
		// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_OE, AndShowTimeEncodingUtil.getEncoding());
		// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_MOVIE_ID, movie.getId());
		// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_MOVIE_NAME, movie.getEnglishMovieName());
		// androidUriBuilder.appendQueryParameter(HttpParamsCst.PARAM_MOVIE_CUR_LANG_NAME, movie.getMovieName());

		// String uri = androidUriBuilder.toString();
		String uri = andShowtimeUriBuilder.toUri();
		Log.i(TAG, "send request : " + uri); //$NON-NLS-1$
		HttpGet getMethod = AndShowtimeFactory.getHttpGet();
		getMethod.setURI(new URI(uri));
		HttpResponse res = AndShowtimeFactory.getHttpClient().execute(getMethod);

		XMLReader reader = AndShowtimeFactory.getXmlReader();
		ParserImdbResultXml parser = AndShowtimeFactory.getParserImdbResultXml();

		reader.setContentHandler(parser);
		InputSource inputSource = AndShowtimeFactory.getInputSource();
		inputSource.setByteStream(new GZIPInputStream(res.getEntity().getContent()));

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

	}

	public static void completeMovieDetailStream(MovieBean movie) throws Exception {

		try {
			File root = Environment.getExternalStorageDirectory();
			if (root.canWrite()) {
				File posterFile = new File(root, "dcim/andshowtime/" + movie.getId() + ".jpg");
				posterFile.getParentFile().mkdirs();
				if (posterFile.exists()) {
					Log.i(TAG, "img existe");
					movie.setImgStream(new FileInputStream(posterFile));
				} else {
					Log.i(TAG, "img existe pas : lancement de la requete");
					HttpGet getMethod = AndShowtimeFactory.getHttpGet();
					getMethod.setURI(new URI(movie.getUrlImg()));
					HttpResponse res = AndShowtimeFactory.getHttpClient().execute(getMethod);

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
				HttpGet getMethod = AndShowtimeFactory.getHttpGet();
				getMethod.setURI(new URI(movie.getUrlImg()));
				HttpResponse res = AndShowtimeFactory.getHttpClient().execute(getMethod);

				InputStream inputStream = res.getEntity().getContent();
				movie.setImgStream(inputStream);
			}
		} catch (IOException e) {
			Log.e(TAG, "Could not write file " + e.getMessage()); //$NON-NLS-1$
		}

	}

}
