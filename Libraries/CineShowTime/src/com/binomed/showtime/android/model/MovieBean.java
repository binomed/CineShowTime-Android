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
package com.binomed.showtime.android.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieBean extends AbstractModel implements Parcelable {

	public static final Parcelable.Creator<MovieBean> CREATOR = new Creator<MovieBean>() {

		@Override
		public MovieBean[] newArray(int size) {
			return new MovieBean[size];
		}

		@Override
		public MovieBean createFromParcel(Parcel source) {
			return new MovieBean(source);
		}
	};

	private static final int FIELD_ID = 0;
	private static final int FIELD_CID = 1;
	private static final int FIELD_IMDB_ID = 2;
	private static final int FIELD_YEAR = 3;
	private static final int FIELD_MOVIE_NAME = 4;
	private static final int FIELD_ENGLISH_MOVIE_NAME = 5;
	private static final int FIELD_MOVIE_TIME = 6;
	private static final int FIELD_LANG = 7;
	private static final int FIELD_STYLE = 8;
	private static final int FIELD_DESCRIPTION = 9;
	private static final int FIELD_TR_DESCRIPTION = 10;
	private static final int FIELD_URL_IMG = 11;
	private static final int FIELD_URL_IMDB = 12;
	private static final int FIELD_URL_WIKIPEDIA = 13;
	private static final int FIELD_RATE = 14;
	private static final int FIELD_IMDB_DESCRIPTION = 15;
	private static final int FIELD_ACTOR_LIST = 16;
	private static final int FIELD_DIRECTOR_LIST = 17;
	private static final int FIELD_REVIEWS = 18;
	private static final int FIELD_YOUTUBE_VIDEOS = 19;
	private static final int FIELD_THEATER_LIST = 20;
	private static final int FIELD_MOVIE_TIME_FORMAT = 21;
	private static final int FIELD_END = -1;

	private String id;

	private String cid;

	private String imdbId;

	private Integer year;

	private String movieName;

	private String englishMovieName;

	private Long movieTime;

	private String movieTimeFormat;

	private String lang;

	private String style;

	private String description;

	private String trDescription;

	private String urlImg;

	private String urlImdb;

	private String urlWikipedia;

	private InputStream imgStream;

	private Double rate;

	private boolean imdbDesrciption;

	private String actorList;

	private String directorList;

	private List<ReviewBean> reviews;

	private List<YoutubeBean> youtubeVideos;

	private List<String> theaterList;

	private transient String[] decomposedName;

	public MovieBean() {
		super();
	}

	public MovieBean(Parcel parcel) {
		this();
		readFromParcel(parcel);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getImdbId() {
		return imdbId;
	}

	public void setImdbId(String imdbId) {
		this.imdbId = imdbId;
	}

	public String getMovieName() {
		return movieName;
	}

	public void setMovieName(String movieName) {
		this.decomposedName = movieName.split(" ");
		this.movieName = movieName;
	}

	public String getEnglishMovieName() {
		return englishMovieName;
	}

	public void setEnglishMovieName(String englishMovieName) {
		this.englishMovieName = englishMovieName;
	}

	public Long getMovieTime() {
		return movieTime;
	}

	public void setMovieTime(Long movieTime) {
		this.movieTime = movieTime;
	}

	public String getMovieTimeFormat() {
		return movieTimeFormat;
	}

	public void setMovieTimeFormat(String movieTimeFormat) {
		this.movieTimeFormat = movieTimeFormat;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTrDescription() {
		return trDescription;
	}

	public void setTrDescription(String trDescription) {
		this.trDescription = trDescription;
	}

	public String getUrlImg() {
		return urlImg;
	}

	public void setUrlImg(String urlImg) {
		this.urlImg = urlImg;
	}

	public String getUrlImdb() {
		return urlImdb;
	}

	public void setUrlImdb(String urlImdb) {
		this.urlImdb = urlImdb;
	}

	public InputStream getImgStream() {
		return imgStream;
	}

	public void setImgStream(InputStream imgStream) {
		this.imgStream = imgStream;
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public boolean isImdbDesrciption() {
		return imdbDesrciption;
	}

	public void setImdbDesrciption(boolean imdbDesrciption) {
		this.imdbDesrciption = imdbDesrciption;
	}

	public String getActorList() {
		return actorList;
	}

	public void setActorList(String actorList) {
		this.actorList = actorList;
	}

	public String getDirectorList() {
		return directorList;
	}

	public void setDirectorList(String directorList) {
		this.directorList = directorList;
	}

	public String getUrlWikipedia() {
		return urlWikipedia;
	}

	public void setUrlWikipedia(String urlWikipedia) {
		this.urlWikipedia = urlWikipedia;
	}

	public List<ReviewBean> getReviews() {
		return reviews;
	}

	public void setReviews(List<ReviewBean> reviews) {
		this.reviews = reviews;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public List<YoutubeBean> getYoutubeVideos() {
		return youtubeVideos;
	}

	public void setYoutubeVideos(List<YoutubeBean> youtubeVideos) {
		this.youtubeVideos = youtubeVideos;
	}

	public List<String> getTheaterList() {
		return theaterList;
	}

	public void setTheaterList(List<String> theaterList) {
		this.theaterList = theaterList;
	}

	public String[] getDecomposedName() {
		return decomposedName;
	}

	private void readFromParcel(Parcel parcel) {
		boolean end = false;
		int code = 0;
		while (!end) {
			code = parcel.readInt();
			switch (code) {
			case FIELD_ACTOR_LIST: {
				setActorList(readString(parcel));
				break;
			}
			case FIELD_CID: {
				setCid(readString(parcel));
				break;
			}
			case FIELD_DESCRIPTION: {
				setDescription(readString(parcel));
				break;
			}
			case FIELD_DIRECTOR_LIST: {
				setDirectorList(readString(parcel));
				break;
			}
			case FIELD_ENGLISH_MOVIE_NAME: {
				setEnglishMovieName(readString(parcel));
				break;
			}
			case FIELD_ID: {
				setId(readString(parcel));
				break;
			}
			case FIELD_IMDB_DESCRIPTION: {
				setImdbDesrciption(readBoolean(parcel));
				break;
			}
			case FIELD_IMDB_ID: {
				setImdbId(readString(parcel));
				break;
			}
			case FIELD_LANG: {
				setLang(readString(parcel));
				break;
			}
			case FIELD_MOVIE_NAME: {
				setMovieName(readString(parcel));
				break;
			}
			case FIELD_MOVIE_TIME: {
				setMovieTime(readLong(parcel));
				break;
			}
			case FIELD_MOVIE_TIME_FORMAT: {
				setMovieTimeFormat(readString(parcel));
				break;
			}
			case FIELD_RATE: {
				setRate(readDouble(parcel));
				break;
			}
			case FIELD_REVIEWS: {
				setReviews(readList(parcel, ReviewBean.class));
				break;
			}
			case FIELD_STYLE: {
				setStyle(readString(parcel));
				break;
			}
			case FIELD_THEATER_LIST: {
				setTheaterList(new ArrayList<String>());
				parcel.readStringList(getTheaterList());
				break;
			}
			case FIELD_TR_DESCRIPTION: {
				setTrDescription(readString(parcel));
				break;
			}
			case FIELD_URL_IMDB: {
				setUrlImdb(readString(parcel));
				break;
			}
			case FIELD_URL_IMG: {
				setUrlImg(readString(parcel));
				break;
			}
			case FIELD_URL_WIKIPEDIA: {
				setUrlWikipedia(readString(parcel));
				break;
			}
			case FIELD_YEAR: {
				setYear(readInt(parcel));
				break;
			}
			case FIELD_YOUTUBE_VIDEOS: {
				setYoutubeVideos(readList(parcel, YoutubeBean.class));
				break;
			}
			case FIELD_END: {
				end = true;
				break;
			}
			default:
				break;
			}
		}

	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if (getActorList() != null) {
			dest.writeInt(FIELD_ACTOR_LIST);
			writeString(dest, getActorList());
		}
		if (getCid() != null) {
			dest.writeInt(FIELD_CID);
			writeString(dest, getCid());
		}
		if (getDescription() != null) {
			dest.writeInt(FIELD_DESCRIPTION);
			writeString(dest, getDescription());
		}
		if (getDirectorList() != null) {
			dest.writeInt(FIELD_DIRECTOR_LIST);
			writeString(dest, getDirectorList());
		}
		if (getEnglishMovieName() != null) {
			dest.writeInt(FIELD_ENGLISH_MOVIE_NAME);
			writeString(dest, getEnglishMovieName());
		}
		if (getId() != null) {
			dest.writeInt(FIELD_ID);
			writeString(dest, getId());
		}
		dest.writeInt(FIELD_IMDB_DESCRIPTION);
		writeBoolean(dest, isImdbDesrciption());
		if (getImdbId() != null) {
			dest.writeInt(FIELD_IMDB_ID);
			writeString(dest, getImdbId());
		}
		if (getLang() != null) {
			dest.writeInt(FIELD_LANG);
			writeString(dest, getLang());
		}
		if (getMovieName() != null) {
			dest.writeInt(FIELD_MOVIE_NAME);
			writeString(dest, getMovieName());
		}
		if (getMovieTime() != null) {
			dest.writeInt(FIELD_MOVIE_TIME);
			writeLong(dest, getMovieTime());
		}
		if (getMovieTimeFormat() != null) {
			dest.writeInt(FIELD_MOVIE_TIME_FORMAT);
			writeString(dest, getMovieTimeFormat());
		}
		if (getRate() != null) {
			dest.writeInt(FIELD_RATE);
			writeDouble(dest, getRate());
		}
		if (getReviews() != null) {
			dest.writeInt(FIELD_REVIEWS);
			writeList(dest, getReviews(), new ReviewBean[getReviews().size()], ReviewBean.class, flags);
		}
		if (getStyle() != null) {
			dest.writeInt(FIELD_STYLE);
			writeString(dest, getStyle());
		}
		if (getTheaterList() != null) {
			dest.writeInt(FIELD_THEATER_LIST);
			dest.writeStringList(getTheaterList());
		}
		if (getTrDescription() != null) {
			dest.writeInt(FIELD_TR_DESCRIPTION);
			writeString(dest, getTrDescription());
		}
		if (getUrlImdb() != null) {
			dest.writeInt(FIELD_URL_IMDB);
			writeString(dest, getUrlImdb());
		}
		if (getUrlImg() != null) {
			dest.writeInt(FIELD_URL_IMG);
			writeString(dest, getUrlImg());
		}
		if (getUrlWikipedia() != null) {
			dest.writeInt(FIELD_URL_WIKIPEDIA);
			writeString(dest, getUrlWikipedia());
		}
		if (getYear() != null) {
			dest.writeInt(FIELD_YEAR);
			writeInt(dest, getYear());
		}
		if (getYoutubeVideos() != null) {
			dest.writeInt(FIELD_YOUTUBE_VIDEOS);
			writeList(dest, getYoutubeVideos(), new YoutubeBean[getYoutubeVideos().size()], YoutubeBean.class, flags);
		}
		dest.writeInt(FIELD_END);

	}
}
