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
import com.binomed.showtime.android.util.comparator.MovieNameComparator;
import com.binomed.showtime.android.util.comparator.TheaterDistanceComparator;
import com.binomed.showtime.android.util.comparator.TheaterNameComparator;
import com.binomed.showtime.android.util.comparator.TheaterShowtimeComparator;
import com.binomed.showtime.android.util.comparator.TheaterShowtimeInnerListComparator;

public final class AndShowtimeFactory {

	private static AndShowtimeFactory instance;

	private ParserNearResultXml parserNearResultXml;
	private ParserMovieResultXml parserMovieResultXml;
	private ParserImdbResultXml parserImdbResultXml;

	private MovieNameComparator movieNameComparator;
	private TheaterDistanceComparator theaterDistanceComparator;
	private TheaterNameComparator theaterNameComparator;
	private TheaterShowtimeComparator theaterShowtimeComparator;
	private TheaterShowtimeInnerListComparator theaterShowtimeInnerListComparator;

	private Geocoder geoCoder;

	private HttpClient client;
	private HttpGet getMethod;
	private XMLReader reader;
	private InputSource xmlSource;

	private static AndShowtimeFactory getInstance() {
		if (instance == null) {
			instance = new AndShowtimeFactory();
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
		return AndShowtimeFactory.getInstance().getPrivateParserNearResultXml();
	}

	public static ParserMovieResultXml getParserMovieResultXml() {
		return AndShowtimeFactory.getInstance().getPrivateParserMovieResultXml();
	}

	public static ParserImdbResultXml getParserImdbResultXml() {
		return AndShowtimeFactory.getInstance().getPrivateParserImdbResultXml();
	}

	public static HttpClient getHttpClient() {
		return AndShowtimeFactory.getInstance().getClient();
	}

	public static HttpGet getHttpGet() {
		return AndShowtimeFactory.getInstance().getGetMethod();
	}

	public static XMLReader getXmlReader() throws Exception {
		return AndShowtimeFactory.getInstance().getReader();
	}

	public static InputSource getInputSource() throws SAXException {
		return AndShowtimeFactory.getInstance().getXmlSource();
	}

	public static void initGeocoder(Context context) {
		AndShowtimeFactory.getInstance().setGeocoder(new Geocoder(context, Locale.getDefault()));
	}

	public static Geocoder getGeocoder() {
		return AndShowtimeFactory.getInstance().getPrivateGeocode();
	}

	public static MovieNameComparator getMovieNameComparator() {
		return AndShowtimeFactory.getInstance().getPrivateMovieNameComparator();
	}

	public static TheaterNameComparator getTheaterNameComparator() {
		return AndShowtimeFactory.getInstance().getPrivateTheaterNameComparator();
	}

	public static TheaterDistanceComparator getTheaterDistanceComparator() {
		return AndShowtimeFactory.getInstance().getPrivateTheaterDistanceComparator();
	}

	public static TheaterShowtimeComparator getTheaterShowtimeComparator() {
		return AndShowtimeFactory.getInstance().getPrivateTheaterShowtimeComparator();
	}

	public static TheaterShowtimeInnerListComparator getTheaterShowtimeInnerListComparator() {
		return AndShowtimeFactory.getInstance().getPrivateTheaterShowtimeInnerListComparator();
	}

}
