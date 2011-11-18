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

import java.util.Locale;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.location.Geocoder;

import com.binomed.showtime.android.parser.xml.ParserImdbResultXml;
import com.binomed.showtime.android.parser.xml.ParserMovieResultXml;
import com.binomed.showtime.android.parser.xml.ParserNearResultXml;
import com.binomed.showtime.android.parser.xml.ParserSimpleResultXml;
import com.binomed.showtime.android.util.comparator.MovieNameComparator;
import com.binomed.showtime.android.util.comparator.MovieNameComparatorFromId;
import com.binomed.showtime.android.util.comparator.TheaterDistanceComparator;
import com.binomed.showtime.android.util.comparator.TheaterNameComparator;
import com.binomed.showtime.android.util.comparator.TheaterShowtimeComparator;
import com.binomed.showtime.android.util.comparator.TheaterShowtimeInnerListComparator;

public final class CineShowtimeFactory {

	private static CineShowtimeFactory instance;

	private ParserNearResultXml parserNearResultXml;
	private ParserSimpleResultXml parserSimpleResultXml;
	private ParserMovieResultXml parserMovieResultXml;
	private ParserImdbResultXml parserImdbResultXml;

	private MovieNameComparator movieNameComparator;
	private MovieNameComparatorFromId movieNameComparatorFromId;
	private TheaterDistanceComparator theaterDistanceComparator;
	private TheaterNameComparator theaterNameComparator;
	private TheaterShowtimeComparator theaterShowtimeComparator;
	private TheaterShowtimeInnerListComparator theaterShowtimeInnerListComparator;

	private Geocoder geoCoder;

	private HttpClient client;
	private HttpGet getMethod;
	private XMLReader reader;
	private InputSource xmlSource;

	private static CineShowtimeFactory getInstance() {
		if (instance == null) {
			instance = new CineShowtimeFactory();
		}
		return instance;
	}

	private void setGeocoder(Geocoder geocoder) {
		this.geoCoder = geocoder;
	}

	private Geocoder getPrivateGeocode() {
		return geoCoder;
	}

	private ParserNearResultXml getPrivateParserNearResultXml() {
		if (parserNearResultXml == null) {
			parserNearResultXml = new ParserNearResultXml();
		}
		return parserNearResultXml;
	}

	private ParserSimpleResultXml getPrivateParserSimpleResultXml() {
		if (parserSimpleResultXml == null) {
			parserSimpleResultXml = new ParserSimpleResultXml();
		}
		return parserSimpleResultXml;
	}

	private ParserMovieResultXml getPrivateParserMovieResultXml() {
		if (parserMovieResultXml == null) {
			parserMovieResultXml = new ParserMovieResultXml();
		}
		return parserMovieResultXml;
	}

	private ParserImdbResultXml getPrivateParserImdbResultXml() {
		if (parserImdbResultXml == null) {
			parserImdbResultXml = new ParserImdbResultXml();
		}
		return parserImdbResultXml;
	}

	private MovieNameComparator getPrivateMovieNameComparator() {
		if (movieNameComparator == null) {
			movieNameComparator = new MovieNameComparator();
		}
		return movieNameComparator;
	}

	private MovieNameComparatorFromId getPrivateMovieNameComparatorFromId() {
		if (movieNameComparatorFromId == null) {
			movieNameComparatorFromId = new MovieNameComparatorFromId();
		}
		return movieNameComparatorFromId;
	}

	private TheaterNameComparator getPrivateTheaterNameComparator() {
		if (theaterNameComparator == null) {
			theaterNameComparator = new TheaterNameComparator();
		}
		return theaterNameComparator;
	}

	private TheaterDistanceComparator getPrivateTheaterDistanceComparator() {
		if (theaterDistanceComparator == null) {
			theaterDistanceComparator = new TheaterDistanceComparator();
		}
		return theaterDistanceComparator;
	}

	private TheaterShowtimeComparator getPrivateTheaterShowtimeComparator() {
		if (theaterShowtimeComparator == null) {
			theaterShowtimeComparator = new TheaterShowtimeComparator();
		}
		return theaterShowtimeComparator;
	}

	private TheaterShowtimeInnerListComparator getPrivateTheaterShowtimeInnerListComparator() {
		if (theaterShowtimeInnerListComparator == null) {
			theaterShowtimeInnerListComparator = new TheaterShowtimeInnerListComparator();
		}
		return theaterShowtimeInnerListComparator;
	}

	private HttpClient getClient() {
		if (client == null) {
			client = new DefaultHttpClient();
		}
		return client;
	}

	private HttpGet getGetMethod() {
		if (getMethod == null) {
			getMethod = new HttpGet();
		}
		return getMethod;
	}

	private XMLReader getReader() throws Exception {
		if (reader == null) {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();

			reader = sp.getXMLReader();
		}
		return reader;
	}

	private InputSource getXmlSource() {
		if (xmlSource == null) {
			xmlSource = new InputSource();
		}
		return xmlSource;
	}

	public static ParserNearResultXml getParserNearResultXml() {
		return CineShowtimeFactory.getInstance().getPrivateParserNearResultXml();
	}

	public static ParserSimpleResultXml getParserSimpleResultXml() {
		return CineShowtimeFactory.getInstance().getPrivateParserSimpleResultXml();
	}

	public static ParserMovieResultXml getParserMovieResultXml() {
		return CineShowtimeFactory.getInstance().getPrivateParserMovieResultXml();
	}

	public static ParserImdbResultXml getParserImdbResultXml() {
		return CineShowtimeFactory.getInstance().getPrivateParserImdbResultXml();
	}

	public static HttpClient getHttpClient() {
		return CineShowtimeFactory.getInstance().getClient();
	}

	public static HttpGet getHttpGet() {
		return CineShowtimeFactory.getInstance().getGetMethod();
	}

	public static XMLReader getXmlReader() throws Exception {
		return CineShowtimeFactory.getInstance().getReader();
	}

	public static InputSource getInputSource() throws SAXException {
		return CineShowtimeFactory.getInstance().getXmlSource();
	}

	public static void initGeocoder(Context context) {
		CineShowtimeFactory.getInstance().setGeocoder(new Geocoder(context, Locale.getDefault()));
	}

	public static Geocoder getGeocoder() {
		return CineShowtimeFactory.getInstance().getPrivateGeocode();
	}

	public static MovieNameComparator getMovieNameComparator() {
		return CineShowtimeFactory.getInstance().getPrivateMovieNameComparator();
	}

	public static MovieNameComparatorFromId getMovieNameComparatorFromId() {
		return CineShowtimeFactory.getInstance().getPrivateMovieNameComparatorFromId();
	}

	public static TheaterNameComparator getTheaterNameComparator() {
		return CineShowtimeFactory.getInstance().getPrivateTheaterNameComparator();
	}

	public static TheaterDistanceComparator getTheaterDistanceComparator() {
		return CineShowtimeFactory.getInstance().getPrivateTheaterDistanceComparator();
	}

	public static TheaterShowtimeComparator getTheaterShowtimeComparator() {
		return CineShowtimeFactory.getInstance().getPrivateTheaterShowtimeComparator();
	}

	public static TheaterShowtimeInnerListComparator getTheaterShowtimeInnerListComparator() {
		return CineShowtimeFactory.getInstance().getPrivateTheaterShowtimeInnerListComparator();
	}

}
