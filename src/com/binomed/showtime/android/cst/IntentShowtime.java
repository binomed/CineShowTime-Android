package com.binomed.showtime.android.cst;

import java.net.URLEncoder;

import android.content.Intent;
import android.net.Uri;
import android.net.Uri.Builder;

import com.binomed.showtime.android.util.AndShowTimeEncodingUtil;
import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.TheaterBean;
import com.binomed.showtime.cst.SpecialChars;
import com.binomed.showtime.util.AndShowtimeNumberFormat;

public class IntentShowtime {

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
			mapsUri.append(URLEncoder.encode(theater.getPlace().getSearchQuery(), AndShowTimeEncodingUtil.getEncoding())); //$NON-NLS-1$
			mapsUri.append("+("); //$NON-NLS-1$
			mapsUri.append(theater.getTheaterName()); //$NON-NLS-1$
			mapsUri.append(")"); //$NON-NLS-1$
		} catch (Exception e) {
			mapsUri.append(AndShowtimeNumberFormat.getFormatGeoCoord().format(theater.getPlace().getLongitude()));
			mapsUri.append(SpecialChars.COMMA);
			mapsUri.append(AndShowtimeNumberFormat.getFormatGeoCoord().format(theater.getPlace().getLatitude()));
			mapsUri.append("?z=22");
		}
		Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(mapsUri.toString()));
		return myIntent;
	}

	public static Intent createCallIntent(TheaterBean theater) {
		StringBuilder callUri = new StringBuilder("tel:");
		callUri.append(theater.getPhoneNumber());
		Intent myIntent = new Intent(android.content.Intent.ACTION_CALL, Uri.parse(callUri.toString()));
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

	public static Intent createImdbBrowserIntent(MovieBean movie) {
		StringBuilder imdbUriStr = new StringBuilder("http://www.imdb.com/title/tt"); //$NON-NLS-1$
		imdbUriStr.append(movie.getImdbId()).append("/"); //$NON-NLS-1$
		Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(imdbUriStr.toString()));
		return myIntent;
	}

	public static Intent createHelpAndShowTime() {
		StringBuilder imdbUriStr = new StringBuilder("http://code.google.com/p/binomed-android-project/wiki/ScreenShotAndManual"); //$NON-NLS-1$
		Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(imdbUriStr.toString()));
		return myIntent;
	}

}
