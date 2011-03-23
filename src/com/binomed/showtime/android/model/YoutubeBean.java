package com.binomed.showtime.android.model;

import java.io.InputStream;

import android.os.Parcel;
import android.os.Parcelable;

public class YoutubeBean extends AbstractModel implements Parcelable {

	public static final Parcelable.Creator<YoutubeBean> CREATOR = new Creator<YoutubeBean>() {

		@Override
		public YoutubeBean[] newArray(int size) {
			return new YoutubeBean[size];
		}

		@Override
		public YoutubeBean createFromParcel(Parcel source) {
			return new YoutubeBean(source);
		}
	};

	private static final int FIELD_URL_VIDEO = 0;
	private static final int FIELD_URL_IMG = 1;
	private static final int FIELD_VIDEO_NAME = 2;
	private static final int FIELD_END = -1;

	private String urlVideo;

	private String urlImg;

	private String videoName;

	private InputStream imgStream;

	public YoutubeBean() {
		super();
	}

	public YoutubeBean(Parcel parcel) {
		this();
		readFromParcel(parcel);
	}

	public String getUrlVideo() {
		return urlVideo;
	}

	public void setUrlVideo(String urlVideo) {
		this.urlVideo = urlVideo;
	}

	public String getUrlImg() {
		return urlImg;
	}

	public void setUrlImg(String urlImg) {
		this.urlImg = urlImg;
	}

	public String getVideoName() {
		return videoName;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	public InputStream getImgStream() {
		return imgStream;
	}

	public void setImgStream(InputStream imgStream) {
		this.imgStream = imgStream;
	}

	private void readFromParcel(Parcel parcel) {
		boolean end = false;
		int code = 0;
		while (!end) {
			code = parcel.readInt();
			switch (code) {
			case FIELD_URL_IMG: {
				setUrlImg(readString(parcel));
				break;
			}
			case FIELD_URL_VIDEO: {
				setUrlVideo(readString(parcel));
				break;
			}
			case FIELD_VIDEO_NAME: {
				setVideoName(readString(parcel));
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
		if (getUrlImg() != null) {
			writeInt(dest, FIELD_URL_IMG);
			writeString(dest, getUrlImg());
		}
		if (getUrlVideo() != null) {
			writeInt(dest, FIELD_URL_VIDEO);
			writeString(dest, getUrlVideo());
		}
		if (getVideoName() != null) {
			writeInt(dest, FIELD_VIDEO_NAME);
			writeString(dest, getVideoName());
		}
		writeInt(dest, FIELD_END);

	}

}
