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
		return ((trailersList != null) && (trailersList.size() > position) && (position >= 0)) ? trailersList.get(position) : null;
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
		if ((trailer != null) && (trailer.getUrlImg() != null)) {

			view.setYoutubeBean(trailer);

		}

		return view;
	}

	private Context mContext;

}
