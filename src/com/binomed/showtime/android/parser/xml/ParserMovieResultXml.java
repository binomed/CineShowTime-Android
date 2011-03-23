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

import com.binomed.showtime.android.model.LocalisationBean;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.MovieResp;
import com.binomed.showtime.android.model.ProjectionBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.util.CineShowTimeEncodingUtil;
import com.binomed.showtime.cst.XmlGramarMovieResult;

public class ParserMovieResultXml implements ContentHandler {

	private MovieResp movieRespBean = null;

	private boolean inTheater;

	private TheaterBean curentTheater;

	private String curentMovieId;

	public MovieResp getMovieRespBean() {
		return movieRespBean;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	@Override
	public void startDocument() throws SAXException {
		movieRespBean = new MovieResp();
		movieRespBean.setTheaterList(new ArrayList<TheaterBean>());

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
		if (XmlGramarMovieResult.NODE_MOVIE_RESP.equals(localName)) {
			if (atts.getValue(XmlGramarMovieResult.ATTR_CITY_NAME) != null) {
				try {
					movieRespBean.setCityName(URLDecoder.decode(atts.getValue(XmlGramarMovieResult.ATTR_CITY_NAME), CineShowTimeEncodingUtil.getEncoding()));
				} catch (UnsupportedEncodingException e) {
				}
			}
			if (atts.getValue(XmlGramarMovieResult.ATTR_MORE_RESULTS) != null) {
				movieRespBean.setHasMoreResults(Boolean.valueOf(atts.getValue(XmlGramarMovieResult.ATTR_MORE_RESULTS)));
			}
		} else if (!inTheater && XmlGramarMovieResult.NODE_MOVIE.equals(localName)) {
			MovieBean movie = new MovieBean();
			movie.setId(atts.getValue(XmlGramarMovieResult.ATTR_ID));
			try {
				if (atts.getValue(XmlGramarMovieResult.ATTR_ENGLISH_MOVIE_NAME) != null) {
					movie.setEnglishMovieName(URLDecoder.decode(atts.getValue(XmlGramarMovieResult.ATTR_ENGLISH_MOVIE_NAME), CineShowTimeEncodingUtil.getEncoding()));
				}
			} catch (UnsupportedEncodingException e1) {
			}
			try {
				if (atts.getValue(XmlGramarMovieResult.ATTR_MOVIE_NAME) != null) {
					movie.setMovieName(URLDecoder.decode(atts.getValue(XmlGramarMovieResult.ATTR_MOVIE_NAME), CineShowTimeEncodingUtil.getEncoding()));
				}
			} catch (UnsupportedEncodingException e1) {
			}
			movie.setLang(atts.getValue(XmlGramarMovieResult.ATTR_LANG));
			String time = atts.getValue(XmlGramarMovieResult.ATTR_TIME);
			if (time != null) {
				try {
					movie.setMovieTime(Long.valueOf(time));
				} catch (NumberFormatException e) {
				}
			}
			movieRespBean.setMovie(movie);
		} else if (XmlGramarMovieResult.NODE_THEATER.equals(localName)) {
			inTheater = true;
			curentTheater = new TheaterBean();
			curentTheater.setMovieMap(new HashMap<String, List<ProjectionBean>>());
			curentTheater.setId(atts.getValue(XmlGramarMovieResult.ATTR_ID));
			try {
				if (atts.getValue(XmlGramarMovieResult.ATTR_THEATER_NAME) != null) {
					curentTheater.setTheaterName(URLDecoder.decode(atts.getValue(XmlGramarMovieResult.ATTR_THEATER_NAME), CineShowTimeEncodingUtil.getEncoding()));
				}
			} catch (UnsupportedEncodingException e) {
			}
			curentTheater.setPhoneNumber(atts.getValue(XmlGramarMovieResult.ATTR_PHONE_NUMBER));
			movieRespBean.getTheaterList().add(curentTheater);
		} else if (inTheater && XmlGramarMovieResult.NODE_LOCALISATION.equals(localName)) {
			LocalisationBean localisationBean = new LocalisationBean();
			try {
				if (atts.getValue(XmlGramarMovieResult.ATTR_CITY_NAME) != null) {
					localisationBean.setCityName(URLDecoder.decode(atts.getValue(XmlGramarMovieResult.ATTR_CITY_NAME), CineShowTimeEncodingUtil.getEncoding()));
				}
			} catch (UnsupportedEncodingException e) {
			}
			try {
				if (atts.getValue(XmlGramarMovieResult.ATTR_COUNTRY_NAME) != null) {
					localisationBean.setCountryName(URLDecoder.decode(atts.getValue(XmlGramarMovieResult.ATTR_COUNTRY_NAME), CineShowTimeEncodingUtil.getEncoding()));
				}
			} catch (UnsupportedEncodingException e) {
			}
			localisationBean.setCountryNameCode(atts.getValue(XmlGramarMovieResult.ATTR_COUNTRY_CODE));
			localisationBean.setPostalCityNumber(atts.getValue(XmlGramarMovieResult.ATTR_POSTAL_CODE));
			try {
				if (atts.getValue(XmlGramarMovieResult.ATTR_COUNTRY_NAME) != null) {
					localisationBean.setSearchQuery(URLDecoder.decode(atts.getValue(XmlGramarMovieResult.ATTR_SEARCH_QUERY), CineShowTimeEncodingUtil.getEncoding()));
				}
			} catch (UnsupportedEncodingException e) {
			}
			String distance = atts.getValue(XmlGramarMovieResult.ATTR_DISTANCE);
			if (distance != null) {
				try {
					localisationBean.setDistance(Float.valueOf(distance));
				} catch (NumberFormatException e) {
				}
			}
			String distanceTime = atts.getValue(XmlGramarMovieResult.ATTR_DISTANCE_TIME);
			if (distanceTime != null) {
				try {
					localisationBean.setDistanceTime(Long.valueOf(distanceTime));
				} catch (NumberFormatException e) {
				}
			}
			String latitude = atts.getValue(XmlGramarMovieResult.ATTR_LATITUDE);
			if (latitude != null) {
				try {
					localisationBean.setLatitude(Double.valueOf(latitude));
				} catch (NumberFormatException e) {
				}
			}
			String longitude = atts.getValue(XmlGramarMovieResult.ATTR_LONGITUDE);
			if (longitude != null) {
				try {
					localisationBean.setLongitude(Double.valueOf(longitude));
				} catch (NumberFormatException e) {
				}
			}

			curentTheater.setPlace(localisationBean);
		} else if (inTheater && XmlGramarMovieResult.NODE_MOVIE.equals(localName)) {
			curentMovieId = atts.getValue(XmlGramarMovieResult.ATTR_ID);
		} else if (inTheater && XmlGramarMovieResult.NODE_PROJECTION.equals(localName)) {
			List<ProjectionBean> projectionList = curentTheater.getMovieMap().get(curentMovieId);
			if (projectionList == null) {
				projectionList = new ArrayList<ProjectionBean>();
				curentTheater.getMovieMap().put(curentMovieId, projectionList);
			}
			String time = atts.getValue(XmlGramarMovieResult.ATTR_TIME);
			if (time != null) {
				try {
					ProjectionBean projection = new ProjectionBean();
					projection.setShowtime(Long.valueOf(time));

					String lang = atts.getValue(XmlGramarMovieResult.ATTR_LANG);
					if (lang != null) {
						try {
							projection.setSubtitle(URLDecoder.decode(lang, CineShowTimeEncodingUtil.getEncoding()));
						} catch (UnsupportedEncodingException e) {
						}
					}

					String reservationLink = atts.getValue(XmlGramarMovieResult.ATTR_RESERVATION_URL);
					if (reservationLink != null) {
						projection.setReservationLink(reservationLink);
					}

					projectionList.add(projection);
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
		if (inTheater && XmlGramarMovieResult.NODE_THEATER.equals(localName)) {
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
