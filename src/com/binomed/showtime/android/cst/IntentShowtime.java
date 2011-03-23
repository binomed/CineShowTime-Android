package com.binomed.showtime.android.cst;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

import com.binomed.showtime.R;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.model.YoutubeBean;
import com.binomed.showtime.android.util.CineShowTimeEncodingUtil;
import com.binomed.showtime.android.util.CineShowtimeFactory;
import com.binomed.showtime.cst.SpecialChars;
import com.binomed.showtime.util.AndShowtimeNumberFormat;

public class IntentShowtime {

	private static final String TAG = "IntentShowTime"; //$NON-NLS-1$

	public static Intent createMapsIntent(TheaterBean theater) {
		StringBuilder mapsUri = new StringBuilder("geo:");
		// geo:latitude,longitude
		// geo:latitude,longitude?z=zoom
		// geo:0,0?q=my+street+address
		// geo:0,0?q=business+near+city
		// mapsUri.append(AndShowtimeNumberFormat.getFormatGeoCoord().format(theater.getPlace().getLongitude()));
		// mapsUri.append(SpecialChars.COMMA);
		// mapsUri.append(AndShowtimeNumberFormat.getFormatGeoCoord().format(theater.getPlace().getLatitude()));
		// mapsUri.append("?z=22");

		try {
			mapsUri.append(0);
			mapsUri.append(SpecialChars.COMMA);
			mapsUri.append(0);
			mapsUri.append("?q="); //$NON-NLS-1$
			//			mapsUri.append("?q=("); //$NON-NLS-1$
			//			mapsUri.append(URLEncoder.encode(theater.getTheaterName(), AndShowTimeEncodingUtil.getEncoding())); //$NON-NLS-1$
			//			mapsUri.append(")+"); //$NON-NLS-1$
			mapsUri.append(URLEncoder.encode(theater.getPlace().getSearchQuery(), CineShowTimeEncodingUtil.getEncoding())); //$NON-NLS-1$
			mapsUri.append("+("); //$NON-NLS-1$
			mapsUri.append(theater.getTheaterName()); //$NON-NLS-1$
			mapsUri.append(")"); //$NON-NLS-1$
		} catch (Exception e) {
			if (theater.getPlace().getLatitude() != null && theater.getPlace().getLongitude() != null) {
				mapsUri.append(AndShowtimeNumberFormat.getFormatGeoCoord().format(theater.getPlace().getLongitude()));
				mapsUri.append(SpecialChars.COMMA);
				mapsUri.append(AndShowtimeNumberFormat.getFormatGeoCoord().format(theater.getPlace().getLatitude()));
				mapsUri.append("?z=22");
			}
		}
		Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(mapsUri.toString()));
		return myIntent;
	}

	public static Intent createMapsIntentBrowser(TheaterBean theater) {
		StringBuilder mapsUri = new StringBuilder("http://maps.google.com/maps");

		try {
			mapsUri.append("?q="); //$NON-NLS-1$
			//			mapsUri.append(URLEncoder.encode(theater.getPlace().getSearchQuery(), AndShowTimeEncodingUtil.getEncoding())); //$NON-NLS-1$
			mapsUri.append(theater.getPlace().getSearchQuery()); //$NON-NLS-1$
			mapsUri.append("+("); //$NON-NLS-1$
			mapsUri.append(theater.getTheaterName()); //$NON-NLS-1$
			mapsUri.append(")"); //$NON-NLS-1$
		} catch (Exception e) {
			if (theater.getPlace().getLatitude() != null && theater.getPlace().getLongitude() != null) {
				mapsUri.append(AndShowtimeNumberFormat.getFormatGeoCoord().format(theater.getPlace().getLongitude()));
				mapsUri.append(SpecialChars.COMMA);
				mapsUri.append(AndShowtimeNumberFormat.getFormatGeoCoord().format(theater.getPlace().getLatitude()));
				mapsUri.append("?z=22");
			}
		}
		Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(mapsUri.toString()));
		return myIntent;
	}

	public static Intent createMapsWithDrivingDirectionIntent(TheaterBean theater, Location source) {
		Geocoder geocoder = CineShowtimeFactory.getGeocoder();
		if (geocoder != null) {
			List<Address> addressList = null;
			try {
				addressList = geocoder.getFromLocation(source.getLatitude(), source.getLongitude(), 1);
			} catch (Exception e) {
				Log.e(TAG, "error Searching latitude, longitude :" + source.getLatitude() + "," + source.getLongitude(), e);
			}
			if (addressList != null && addressList.size() > 0) {
				try {
					StringBuilder mapsUri = new StringBuilder("http://maps.google.com/maps?saddr="); //$NON-NLS-1$
					mapsUri.append(addressList.get(0).getAddressLine(0));

					mapsUri.append("&daddr="); //$NON-NLS-1$
					mapsUri.append(URLEncoder.encode(theater.getPlace().getSearchQuery(), CineShowTimeEncodingUtil.getEncoding()));
					mapsUri.append("+("); //$NON-NLS-1$
					mapsUri.append(theater.getTheaterName()); //$NON-NLS-1$
					mapsUri.append(")"); //$NON-NLS-1$
					Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(mapsUri.toString()));
					return myIntent;
				} catch (UnsupportedEncodingException e) {
					Log.e(TAG, "error encoding :" + theater.getPlace().getSearchQuery(), e);
				}
			}
		}
		return null;

	}

	public static Intent createCallIntent(TheaterBean theater) {
		StringBuilder callUri = new StringBuilder("tel:");
		callUri.append(theater.getPhoneNumber());
		Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(callUri.toString()));
		return myIntent;
	}

	public static Intent createYoutubeIntent(MovieBean movie) {
		Builder youtubeUri = new Builder();
		youtubeUri.scheme("http");
		youtubeUri.authority("m.youtube.com");
		youtubeUri.path("results");
		youtubeUri.appendQueryParameter("q", new StringBuilder("\"").append(movie.getMovieName()).append("\" trailer").toString());

		StringBuilder youtubeUriStr = new StringBuilder("http://m.youtube.com/results?q=");
		youtubeUriStr.append(new StringBuilder("\"").append(Uri.encode(movie.getMovieName())).append("\"+trailer").toString());
		Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(youtubeUriStr.toString()));
		return myIntent;
	}

	public static Intent createTrailerIntent(YoutubeBean trailer) {
		Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(trailer.getUrlVideo()));
		return myIntent;
	}

	public static Intent createImdbBrowserIntent(MovieBean movie) {
		StringBuilder imdbUriStr = new StringBuilder("http://www.imdb.com/title/tt"); //$NON-NLS-1$
		imdbUriStr.append(movie.getImdbId()).append("/"); //$NON-NLS-1$
		Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(imdbUriStr.toString()));
		return myIntent;
	}

	public static Intent createHelpAndShowTime(Context context) {
		Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.urlHelp)));
		return myIntent;
	}

}
