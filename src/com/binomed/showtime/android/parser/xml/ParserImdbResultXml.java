package com.binomed.showtime.android.parser.xml;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.ReviewBean;
import com.binomed.showtime.android.model.YoutubeBean;
import com.binomed.showtime.android.util.CineShowTimeEncodingUtil;
import com.binomed.showtime.cst.XmlGramarImdbResult;

public class ParserImdbResultXml implements ContentHandler {

	private MovieBean movieBean = null;
	private ReviewBean reviewBean = null;

	private StringBuilder description;

	private boolean inDescription, inReview;

	public MovieBean getMovieBean() {
		return movieBean;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	@Override
	public void startDocument() throws SAXException {
		inDescription = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException {
		if (XmlGramarImdbResult.NODE_MOVIE.equals(localName)) {
			movieBean = new MovieBean();

			movieBean.setId(atts.getValue(XmlGramarImdbResult.ATTR_ID));
			movieBean.setImdbId(atts.getValue(XmlGramarImdbResult.ATTR_IMDB_ID));
			try {
				if (atts.getValue(XmlGramarImdbResult.ATTR_MOVIE_NAME) != null) {
					movieBean.setMovieName(URLDecoder.decode(atts.getValue(XmlGramarImdbResult.ATTR_MOVIE_NAME), CineShowTimeEncodingUtil.getEncoding()));
				}
			} catch (UnsupportedEncodingException e) {
			}
			movieBean.setUrlImg(atts.getValue(XmlGramarImdbResult.ATTR_URL_IMG));
			try {
				if (atts.getValue(XmlGramarImdbResult.ATTR_URL_WIKIPEDIA) != null) {
					movieBean.setUrlWikipedia(URLDecoder.decode(atts.getValue(XmlGramarImdbResult.ATTR_URL_WIKIPEDIA), CineShowTimeEncodingUtil.getEncoding()));
				}
			} catch (UnsupportedEncodingException e) {
			}

			String rate = atts.getValue(XmlGramarImdbResult.ATTR_RATE);
			if (rate != null) {
				movieBean.setRate(Double.valueOf(rate));
			}
			String genre = atts.getValue(XmlGramarImdbResult.ATTR_STYLE);
			if (genre != null) {
				movieBean.setStyle(genre);
			}

			String directors = atts.getValue(XmlGramarImdbResult.ATTR_DIRECTORS);
			if (directors != null) {
				try {
					movieBean.setDirectorList(URLDecoder.decode(directors, CineShowTimeEncodingUtil.getEncoding()));
				} catch (UnsupportedEncodingException e) {
				}
			}

			String actors = atts.getValue(XmlGramarImdbResult.ATTR_ACTORS);
			if (actors != null) {
				try {
					movieBean.setActorList(URLDecoder.decode(actors, CineShowTimeEncodingUtil.getEncoding()));
				} catch (UnsupportedEncodingException e) {
				}
			}

		} else if (XmlGramarImdbResult.NODE_DESC.equals(localName)) {
			inDescription = true;
			String imdbDesc = atts.getValue(XmlGramarImdbResult.ATTR_IMDB_DESC);
			movieBean.setImdbDesrciption(imdbDesc != null ? Boolean.valueOf(imdbDesc) : false);
			description = new StringBuilder();
		} else if (XmlGramarImdbResult.NODE_REVIEWS.equals(localName)) {
			movieBean.setReviews(new ArrayList<ReviewBean>());
		} else if (XmlGramarImdbResult.NODE_REVIEW.equals(localName)) {
			inReview = true;
			description.delete(0, description.length());
			reviewBean = new ReviewBean();

			String rate = atts.getValue(XmlGramarImdbResult.ATTR_RATE);
			if (rate != null) {
				try {
					reviewBean.setRate(Float.valueOf(rate));
				} catch (NumberFormatException e) {
				}
			}

			String author = atts.getValue(XmlGramarImdbResult.ATTR_AUTHOR);
			if (author != null) {
				try {
					reviewBean.setAuthor(URLDecoder.decode(author, CineShowTimeEncodingUtil.getEncoding()));
				} catch (UnsupportedEncodingException e) {
				}
			}

			String urlSource = atts.getValue(XmlGramarImdbResult.ATTR_SOURCE);
			if (urlSource != null) {
				try {
					reviewBean.setSource(URLDecoder.decode(urlSource, CineShowTimeEncodingUtil.getEncoding()));
				} catch (UnsupportedEncodingException e) {
				}
			}

			String urlReview = atts.getValue(XmlGramarImdbResult.ATTR_URL_REVIEW);
			if (urlReview != null) {
				try {
					reviewBean.setUrlReview(URLDecoder.decode(urlReview, CineShowTimeEncodingUtil.getEncoding()));
				} catch (UnsupportedEncodingException e) {
				}
			}

		} else if (XmlGramarImdbResult.NODE_VIDEOS.equals(localName)) {
			movieBean.setYoutubeVideos(new ArrayList<YoutubeBean>());
		} else if (XmlGramarImdbResult.NODE_VIDEO.equals(localName)) {
			YoutubeBean youtubeBean = new YoutubeBean();

			String urlImg = atts.getValue(XmlGramarImdbResult.ATTR_URL_IMG);
			if (urlImg != null) {
				youtubeBean.setUrlImg(urlImg);
			}

			String urlVideo = atts.getValue(XmlGramarImdbResult.ATTR_URL_VIDEO);
			if (urlVideo != null) {
				youtubeBean.setUrlVideo(urlVideo);
			}

			String videoName = atts.getValue(XmlGramarImdbResult.ATTR_VIDEO_NAME);
			if (videoName != null) {
				try {
					youtubeBean.setVideoName(URLDecoder.decode(videoName, CineShowTimeEncodingUtil.getEncoding()));
				} catch (UnsupportedEncodingException e) {
				}
			}

			movieBean.getYoutubeVideos().add(youtubeBean);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {
		if (inDescription && XmlGramarImdbResult.NODE_DESC.equals(localName)) {
			inDescription = false;
			try {
				if (description != null) {
					movieBean.setDescription(URLDecoder.decode(description.toString(), CineShowTimeEncodingUtil.getEncoding()));
				}
			} catch (UnsupportedEncodingException e) {
			}
		} else if (inReview && XmlGramarImdbResult.NODE_REVIEW.equals(localName)) {
			inReview = false;
			try {
				if (description != null) {
					reviewBean.setReview(URLDecoder.decode(description.toString(), CineShowTimeEncodingUtil.getEncoding()));
					movieBean.getReviews().add(reviewBean);
				}
			} catch (UnsupportedEncodingException e) {
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if ((inDescription || inReview) && description != null) {
			description.append(new String(ch, start, length));
		}
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
