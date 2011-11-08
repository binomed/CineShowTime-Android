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

import pl.polidea.coverflow.AbstractCoverFlowImageAdapter;
import pl.polidea.coverflow.CoverFlow;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.binomed.showtime.android.model.YoutubeBean;
import com.binomed.showtime.android.util.images.ImageDownloader;

public class CoverFlowTrailerAdapter extends AbstractCoverFlowImageAdapter {

	private static final String TAG = "GalleryTrailerAdapter"; //$NON-NLS-1$

	int mGalleryItemBackground;
	private List<YoutubeBean> trailersList;
	// private DrawableManager drawableManager;
	private ImageDownloader imageDownloader;

	public CoverFlowTrailerAdapter(Context c, List<YoutubeBean> trailersList, ImageDownloader imageDownloader) {
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
	public View getView(int position, View convertView, ViewGroup parent) {

		ImageView imageView;
		if (convertView == null) {
			final Context context = parent.getContext();
			Log.v(TAG, "Creating Image view at position: " + position + ":" + this);
			Log.v(TAG, "Width: " + width + ": Height:" + height);
			imageView = new ImageView(context);
			imageView.setLayoutParams(new CoverFlow.LayoutParams((int) width, (int) height));
		} else {
			Log.v(TAG, "Reusing view at position: " + position + ":" + this);
			imageView = (ImageView) convertView;
		}

		YoutubeBean trailer = (YoutubeBean) getItem(position);
		if ((trailer != null) && (trailer.getUrlImg() != null)) {
			imageDownloader.download(trailer.getUrlImg(), imageView, mContext);

			// view.setYoutubeBean(trailer);

		}

		return imageView;
	}

	private Context mContext;

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.polidea.coverflow.AbstractCoverFlowImageAdapter#createBitmap(int)
	 */
	@Override
	protected Bitmap createBitmap(final int position) {
		return null;
	}
}
