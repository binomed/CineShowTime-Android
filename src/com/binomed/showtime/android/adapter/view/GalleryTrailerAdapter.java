package com.binomed.showtime.android.adapter.view;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.binomed.showtime.android.layout.view.GalleryTrailerView;
import com.binomed.showtime.android.model.YoutubeBean;
import com.binomed.showtime.android.util.images.ImageDownloader;

public class GalleryTrailerAdapter extends BaseAdapter {

	private static final String TAG = "GalleryTrailerAdapter"; //$NON-NLS-1$

	int mGalleryItemBackground;
	private List<YoutubeBean> trailersList;
	// private DrawableManager drawableManager;
	private ImageDownloader imageDownloader;

	public GalleryTrailerAdapter(Context c, List<YoutubeBean> trailersList, ImageDownloader imageDownloader) {
		mContext = c;
		this.trailersList = trailersList;
		this.imageDownloader = imageDownloader;
		// this.drawableManager = drawableManager;
	}

	@Override
	public int getCount() {
		return (trailersList != null) ? trailersList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return (trailersList != null && trailersList.size() > position && position >= 0) ? trailersList.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		GalleryTrailerView view = null;
		if (convertView == null) {
			view = new GalleryTrailerView(mContext, imageDownloader);
		} else {
			view = (GalleryTrailerView) convertView;
		}
		// ImageView i = new ImageView(mContext);

		YoutubeBean trailer = (YoutubeBean) getItem(position);
		if (trailer != null && trailer.getUrlImg() != null) {

			view.setYoutubeBean(trailer);

		}

		return view;
	}

	private Context mContext;

}
