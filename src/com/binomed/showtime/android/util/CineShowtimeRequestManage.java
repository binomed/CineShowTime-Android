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

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Environment;
import android.util.Log;

import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.model.LocalisationBean;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.NearResp;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.parser.xml.ParserImdbResultXml;
import com.binomed.showtime.android.parser.xml.ParserNearResultXml;
import com.binomed.showtime.android.util.localisation.LocationUtils;
import com.binomed.showtime.cst.HttpParamsCst;
import com.binomed.showtime.cst.SpecialChars;
import com.binomed.showtime.util.AndShowtimeNumberFormat;

public abstract class CineShowtimeRequestManage {

	public static final String TAG = "RequestManager"; //$NON-NLS-1$

	public static NearResp searchTheatersOrMovies(Double latitude, Double longitude, String cityName, String movieName, String theaterId, int day, int start, String origin) throws Exception {

		URLBuilder andShowtimeUriBuilder = new URLBuilder(CineShowTimeEncodingUtil.convertLocaleToEncoding());
		andShowtimeUriBuilder.setProtocol(HttpParamsCst.BINOMED_APP_PROTOCOL);
		andShowtimeUriBuilder.setAdress(HttpParamsCst.BINOMED_APP_URL);
		andShowtimeUriBuilder.completePath(HttpParamsCst.BINOMED_APP_PATH);
		andShowtimeUriBuilder.completePath(((movieName != null) && (movieName.length() > 0)) ? HttpParamsCst.MOVIE_GET_METHODE : HttpParamsCst.NEAR_GET_METHODE);
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_LANG, Locale.getDefault().getLanguage());
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_OUTPUT, HttpParamsCst.VALUE_XML);
		// andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_ZIP, HttpParamsCst.VALUE_TRUE);
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_IE, CineShowTimeEncodingUtil.getEncoding());
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_OE, CineShowTimeEncodingUtil.getEncoding());
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_CURENT_TIME, String.valueOf(Calendar.getInstance().getTimeInMillis()));
		andShowtimeUriBuilder.addQueryParameter(HttpParamsCst.PARAM_TIME_ZONE, TimeZone.getDefault().getID());

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
				cityName = URLDecoder.decode(cityName, CineShowTimeEncodingUtil.getEncoding());
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
				cityName = URLDecoder.decode(cityName, CineShowTimeEncodingUtil.getEncoding());
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
			movieName = URLDecoder.decode(movieName, CineShowTimeEncodingUtil.getEncoding());
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

		List<Address> addressList = null;
		Address addressTheater = null;
		if ((geocoder != null)) {// && (originalPlace != null)) {
			for (TheaterBean theater : resultBean.getTheaterList()) {
				LocalisationBean localisation = theater.getPlace();
				// if (localisation != null && localisation.getLatitude() !=
				// null && localisation.getLongitude() != null) {
				// Location locaTheater = new Location("GPS");
				// locaTheater.setLatitude(localisation.getLatitude());
				// locaTheater.setLongitude(localisation.getLongitude());
				// theater.getPlace().setDistance(originalPlace.distanceTo(locaTheater)
				// / 1000);
				// } else
				if ((localisation != null) && (localisation.getSearchQuery() != null) && (localisation.getSearchQuery().length() > 0)) {
					try {
						LocationUtils.completeLocalisationBean(cityName, localisation);
						addressList = geocoder.getFromLocationName(localisation.getSearchQuery(), 1);
					} catch (Exception e) {
						Log.e(TAG, "error Searching cityName :" + localisation.getSearchQuery(), e);
					}
					if ((addressList != null) && (addressList.size() > 0)) {
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

	private static Location manageLocation(Geocoder geocoder, String cityName, URLBuilder andShowtimeUriBuilder, Double latitude, Double longitude) throws IOException {
		Location originalPlace = null;
		if (geocoder != null) {
			if (cityName != null) {
				cityName = URLDecoder.decode(cityName, CineShowTimeEncodingUtil.getEncoding());
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
				cityName = URLDecoder.decode(cityName, CineShowTimeEncodingUtil.getEncoding());
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

	public static void completeMovieDetail(MovieBean movie, String near) throws Exception {
		URLBuilder andShowtimeUriBuilder = new URLBuilder(CineShowTimeEncodingUtil.convertLocaleToEncoding());
		andShowtimeUriBuilder.setProtocol(HttpParamsCst.BINOMED_APP_PROTOCOL);
		andShowtimeUriBuilder.setAdress(HttpParamsCst.BINOMED_APP_URL);
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
