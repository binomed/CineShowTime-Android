package com.binomed.showtime.android.layout.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.model.YoutubeBean;
import com.binomed.showtime.android.util.images.ImageDownloader;

public class GalleryTrailerView extends LinearLayout {

	private static final String TAG = "GalleryTrailerView"; //$NON-NLS-1$

	private Context context;
	private ImageView imgTrailer;
	private TextView trailerTxt;
	private YoutubeBean youtubeBean;
	// private DrawableManager drawableManager;
	private ImageDownloader imageDownloader;

	public GalleryTrailerView(Context context, ImageDownloader imageDownloader) {
		super(context);
		this.context = context;
		this.imageDownloader = imageDownloader;
		// this.drawableManager = drawableManager;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_gallery_trailer_group_item, this);

		imgTrailer = (ImageView) this.findViewById(R.id.galleryTrailerImg);

		trailerTxt = (TextView) this.findViewById(R.id.galleryTrailerText);
	}

	public YoutubeBean getYoutubeBean() {
		return youtubeBean;
	}

	public void setYoutubeBean(YoutubeBean youtubeBean) {
		this.youtubeBean = youtubeBean;
		imageDownloader.download(youtubeBean.getUrlImg(), imgTrailer, context);
		// drawableManager.fetchDrawableOnThread(youtubeBean.getUrlImg(), null, imgTrailer);
		trailerTxt.setText(youtubeBean.getVideoName());

	}

}
