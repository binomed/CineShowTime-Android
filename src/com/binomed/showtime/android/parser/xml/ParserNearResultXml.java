package com.binomed.showtime.android.parser.xml;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.binomed.showtime.android.util.AndShowTimeEncodingUtil;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.beans.LocalisationBean;
import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.NearResp;
import com.binomed.showtime.beans.TheaterBean;
import com.binomed.showtime.cst.XmlGramarNearResult;

public class ParserNearResultXml implements ContentHandler {

	private NearResp nearRespBean = null;

	private boolean inTheater, inMovieList;

	private TheaterBean curentTheater;

	private String curentMovieId;

	public NearResp getNearRespBean() {
		return nearRespBean;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	@Override
	public void startDocument() throws SAXException {
		nearRespBean = new NearResp();
		nearRespBean.setMapMovies(new HashMap<String, MovieBean>());
		nearRespBean.setTheaterList(new ArrayList<TheaterBean>());

		inMovieList = false;
		inTheater = false;
		curentTheater = null;
		curentMovieId = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException {
		if (XmlGramarNearResult.NODE_NEAR_RESP.equals(localName)) {
			if (atts.getValue(XmlGramarNearResult.ATTR_CITY_NAME) != null) {
				try {
					nearRespBean.setCityName(URLDecoder.decode(atts.getValue(XmlGramarNearResult.ATTR_CITY_NAME), AndShowTimeEncodingUtil.getEncoding()));
				} catch (UnsupportedEncodingException e) {
				}
			}
			if (atts.getValue(XmlGramarNearResult.ATTR_MORE_RESULTS) != null) {
				nearRespBean.setHasMoreResults(Boolean.valueOf(atts.getValue(XmlGramarNearResult.ATTR_MORE_RESULTS)));
			}
		} else if (XmlGramarNearResult.NODE_MOVIE_LIST.equals(localName)) {
			inMovieList = true;
		} else if (inMovieList && XmlGramarNearResult.NODE_MOVIE.equals(localName)) {
			MovieBean movie = new MovieBean();
			movie.setId(atts.getValue(XmlGramarNearResult.ATTR_ID));
			try {
				if (atts.getValue(XmlGramarNearResult.ATTR_ENGLISH_MOVIE_NAME) != null) {
					movie.setEnglishMovieName(URLDecoder.decode(atts.getValue(XmlGramarNearResult.ATTR_ENGLISH_MOVIE_NAME), AndShowTimeEncodingUtil.getEncoding()));
				}
			} catch (UnsupportedEncodingException e1) {
			}
			try {
				if (atts.getValue(XmlGramarNearResult.ATTR_MOVIE_NAME) != null) {
					movie.setMovieName(URLDecoder.decode(atts.getValue(XmlGramarNearResult.ATTR_MOVIE_NAME), AndShowTimeEncodingUtil.getEncoding()));
				}
			} catch (UnsupportedEncodingException e1) {
			}
			movie.setLang(atts.getValue(XmlGramarNearResult.ATTR_LANG));
			String time = atts.getValue(XmlGramarNearResult.ATTR_TIME);
			if (time != null) {
				try {
					movie.setMovieTime(Long.valueOf(time));
				} catch (NumberFormatException e) {
				}
			}
			BeanManagerFactory.putMovie(movie);
			nearRespBean.getMapMovies().put(movie.getId(), movie);
		} else if (XmlGramarNearResult.NODE_THEATER.equals(localName)) {
			inTheater = true;
			curentTheater = new TheaterBean();
			curentTheater.setMovieMap(new HashMap<String, List<Long>>());
			curentTheater.setId(atts.getValue(XmlGramarNearResult.ATTR_ID));
			try {
				if (atts.getValue(XmlGramarNearResult.ATTR_THEATER_NAME) != null) {
					curentTheater.setTheaterName(URLDecoder.decode(atts.getValue(XmlGramarNearResult.ATTR_THEATER_NAME), AndShowTimeEncodingUtil.getEncoding()));
				}
			} catch (UnsupportedEncodingException e) {
			}
			curentTheater.setPhoneNumber(atts.getValue(XmlGramarNearResult.ATTR_PHONE_NUMBER));
			BeanManagerFactory.putTheater(curentTheater);
			nearRespBean.getTheaterList().add(curentTheater);
		} else if (inTheater && XmlGramarNearResult.NODE_LOCALISATION.equals(localName)) {
			LocalisationBean localisationBean = new LocalisationBean();
			try {
				if (atts.getValue(XmlGramarNearResult.ATTR_CITY_NAME) != null) {
					localisationBean.setCityName(URLDecoder.decode(atts.getValue(XmlGramarNearResult.ATTR_CITY_NAME), AndShowTimeEncodingUtil.getEncoding()));
				}
			} catch (UnsupportedEncodingException e) {
			}
			try {
				if (atts.getValue(XmlGramarNearResult.ATTR_COUNTRY_NAME) != null) {
					localisationBean.setCountryName(URLDecoder.decode(atts.getValue(XmlGramarNearResult.ATTR_COUNTRY_NAME), AndShowTimeEncodingUtil.getEncoding()));
				}
			} catch (UnsupportedEncodingException e) {
			}
			try {
				if (atts.getValue(XmlGramarNearResult.ATTR_SEARCH_QUERY) != null) {
					localisationBean.setSearchQuery(URLDecoder.decode(atts.getValue(XmlGramarNearResult.ATTR_SEARCH_QUERY), AndShowTimeEncodingUtil.getEncoding()));
				}
			} catch (UnsupportedEncodingException e) {
			}
			localisationBean.setCountryNameCode(atts.getValue(XmlGramarNearResult.ATTR_COUNTRY_CODE));
			localisationBean.setPostalCityNumber(atts.getValue(XmlGramarNearResult.ATTR_POSTAL_CODE));
			try {
				if (atts.getValue(XmlGramarNearResult.ATTR_COUNTRY_NAME) != null) {
					localisationBean.setSearchQuery(URLDecoder.decode(atts.getValue(XmlGramarNearResult.ATTR_SEARCH_QUERY), AndShowTimeEncodingUtil.getEncoding()));
				}
			} catch (UnsupportedEncodingException e) {
			}
			String distance = atts.getValue(XmlGramarNearResult.ATTR_DISTANCE);
			if (distance != null) {
				try {
					localisationBean.setDistance(Float.valueOf(distance));
				} catch (NumberFormatException e) {
				}
			}
			String distanceTime = atts.getValue(XmlGramarNearResult.ATTR_DISTANCE_TIME);
			if (distanceTime != null) {
				try {
					localisationBean.setDistanceTime(Long.valueOf(distanceTime));
				} catch (NumberFormatException e) {
				}
			}
			String latitude = atts.getValue(XmlGramarNearResult.ATTR_LATITUDE);
			if (latitude != null) {
				try {
					localisationBean.setLatitude(Double.valueOf(latitude));
				} catch (NumberFormatException e) {
				}
			}
			String longitude = atts.getValue(XmlGramarNearResult.ATTR_LONGITUDE);
			if (longitude != null) {
				try {
					localisationBean.setLongitude(Double.valueOf(longitude));
				} catch (NumberFormatException e) {
				}
			}

			curentTheater.setPlace(localisationBean);
		} else if (inTheater && XmlGramarNearResult.NODE_MOVIE.equals(localName)) {
			curentMovieId = atts.getValue(XmlGramarNearResult.ATTR_ID);
		} else if (inTheater && XmlGramarNearResult.NODE_PROJECTION.equals(localName)) {
			List<Long> projectionList = curentTheater.getMovieMap().get(curentMovieId);
			if (projectionList == null) {
				projectionList = new ArrayList<Long>();
				curentTheater.getMovieMap().put(curentMovieId, projectionList);
			}
			String time = atts.getValue(XmlGramarNearResult.ATTR_TIME);
			if (time != null) {
				try {
					projectionList.add(Long.valueOf(time));
				} catch (NumberFormatException e) {
				}
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {
		if (inMovieList && XmlGramarNearResult.NODE_MOVIE_LIST.equals(localName)) {
			inMovieList = false;
		} else if (inTheater && XmlGramarNearResult.NODE_THEATER.equals(localName)) {
			inTheater = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	@Override
	public void endDocument() throws SAXException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
	 */
	@Override
	public void endPrefixMapping(String arg0) throws SAXException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
	 */
	@Override
	public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
	 */
	@Override
	public void processingInstruction(String arg0, String arg1) throws SAXException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
	 */
	@Override
	public void setDocumentLocator(Locator arg0) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
	 */
	@Override
	public void skippedEntity(String arg0) throws SAXException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
	 */
	@Override
	public void startPrefixMapping(String arg0, String arg1) throws SAXException {
	}

}
