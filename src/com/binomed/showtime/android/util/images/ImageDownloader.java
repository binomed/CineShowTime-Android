package com.binomed.showtime.android.util.images;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.cst.CineShowtimeCst;

public class ImageDownloader {

	private static final String LOG_TAG = "ImageDownloader";

	public enum Mode {
		NO_ASYNC_TASK, NO_DOWNLOADED_DRAWABLE, CORRECT
	}

	private Mode mode = Mode.CORRECT;

	public void download(String url, ImageView imageView, Context context) {
		resetPurgeTimer();
		Bitmap bitmap = getBitmapFromCache(url);

		if (bitmap == null) {
			forceDownload(url, imageView, context);
		} else {
			cancelPotentialDownload(url, imageView);
			imageView.setImageBitmap(bitmap);
		}
	}

	/**
	 * Same as download but the image is always downloaded and the cache is not used. Kept private at the moment as its interest is not clear.
	 */
	private void forceDownload(String url, ImageView imageView, Context context) {
		// State sanity: url is guaranteed to never be null in DownloadedDrawable and cache keys.
		if (url == null) {
			imageView.setImageDrawable(null);
			return;
		}

		Bitmap image = getFileDrawable(url);
		if (image != null) {
			imageView.setImageBitmap(image);
		} else if (cancelPotentialDownload(url, imageView)) {
			switch (mode) {
			case NO_ASYNC_TASK:
				Bitmap bitmap = downloadBitmap(url);
				addBitmapToCache(url, bitmap);
				imageView.setImageBitmap(bitmap);
				break;

			case NO_DOWNLOADED_DRAWABLE:
				imageView.setMinimumHeight(156);
				BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
				task.execute(url);
				break;

			case CORRECT:
				task = new BitmapDownloaderTask(imageView);
				DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task, context);
				imageView.setImageDrawable(downloadedDrawable);
				imageView.setMinimumHeight(156);
				task.execute(url);
				break;
			}
		}
	}

	private Bitmap getFileDrawable(String url) {
		Bitmap image = null;
		String finalFileName = url.substring(url.lastIndexOf("/"), url.length());
		try {
			File root = Environment.getExternalStorageDirectory();
			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
				File imageFile = new File(root, new StringBuilder(CineShowtimeCst.FOLDER_POSTER).append(getFileName(url)).toString());
				Log.d(LOG_TAG, "Try getting file : " + imageFile.getAbsolutePath());
				if (imageFile.exists()) {
					image = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
				}
			} else {
				Log.d(LOG_TAG, "SD card unmounted : " + url);

			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error creating file", e);
		}

		if (image != null) {
			addBitmapToCache(url, image);
		}

		return image;
	}

	private InputStream writeFile(String url, InputStream inputStream) {
		InputStream returnInputStream = inputStream;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			try {
				File root = Environment.getExternalStorageDirectory();
				File imageFile = new File(root, new StringBuilder(CineShowtimeCst.FOLDER_POSTER).append(getFileName(url)).toString());
				if (!imageFile.getParentFile().exists()) {
					imageFile.mkdirs();
				}
				if (!imageFile.exists()) {
					imageFile.createNewFile();
					FileOutputStream fileOutPut = new FileOutputStream(imageFile);
					byte[] tempon = new byte[10240];

					while (true) {
						int nRead = inputStream.read(tempon, 0, tempon.length);
						if (nRead <= 0) {
							break;
						}
						fileOutPut.write(tempon, 0, nRead);
					}
					fileOutPut.close();
					returnInputStream = new FileInputStream(imageFile);
				}
			} catch (Exception e) {
				Log.e(LOG_TAG, "Error creating file", e);
			}
		}
		return returnInputStream;
	}

	private String getFileName(String url) {
		String fileName = null;
		fileName = url.replaceAll("/", "");
		fileName = fileName.substring(6, fileName.length());
		return fileName;
	}

	/**
	 * Returns true if the current download has been canceled or if there was no download in progress on this image view. Returns false if the download in progress deals with the same url. The download is not stopped in that case.
	 */
	private static boolean cancelPotentialDownload(String url, ImageView imageView) {
		BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

		if (bitmapDownloaderTask != null) {
			String bitmapUrl = bitmapDownloaderTask.url;
			if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
				bitmapDownloaderTask.cancel(true);
			} else {
				// The same URL is already being downloaded.
				return false;
			}
		}
		return true;
	}

	/**
	 * @param imageView
	 *            Any imageView
	 * @return Retrieve the currently active download task (if any) associated with this imageView. null if there is no such task.
	 */
	private static BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
		if (imageView != null) {
			Drawable drawable = imageView.getDrawable();
			if (drawable instanceof DownloadedDrawable) {
				DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
				return downloadedDrawable.getBitmapDownloaderTask();
			}
		}
		return null;
	}

	Bitmap downloadBitmap(String url) {
		final int IO_BUFFER_SIZE = 4 * 1024;

		// AndroidHttpClient is not allowed to be used from the main thread
		final HttpClient client = new DefaultHttpClient();
		final HttpGet getRequest = new HttpGet(url);

		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w("ImageDownloader", "Error " + statusCode + " while retrieving bitmap from " + url);
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					inputStream = writeFile(url, inputStream);
					// return BitmapFactory.decodeStream(inputStream);
					// Bug on slow connections, fixed in future release.
					return BitmapFactory.decodeStream(new FlushedInputStream(inputStream));
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (IOException e) {
			getRequest.abort();
			Log.w(LOG_TAG, "I/O error while retrieving bitmap from " + url, e);
		} catch (IllegalStateException e) {
			getRequest.abort();
			Log.w(LOG_TAG, "Incorrect URL: " + url);
		} catch (Exception e) {
			getRequest.abort();
			Log.w(LOG_TAG, "Error while retrieving bitmap from " + url, e);
		} finally {
			// if ((client instanceof AndroidHttpClient)) {
			// ((AndroidHttpClient) client).close();
			// }
		}
		return null;
	}

	/*
	 * An InputStream that skips the exact number of bytes provided, unless it reaches EOF.
	 */
	static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int b = read();
					if (b < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}

	/**
	 * The actual AsyncTask that will asynchronously download the image.
	 */
	class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
		private String url;
		private final WeakReference<ImageView> imageViewReference;

		public BitmapDownloaderTask(ImageView imageView) {
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		/**
		 * Actual download method.
		 */
		@Override
		protected Bitmap doInBackground(String... params) {
			url = params[0];
			return downloadBitmap(url);
		}

		/**
		 * Once the image is downloaded, associates it to the imageView
		 */
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}

			addBitmapToCache(url, bitmap);

			if (imageViewReference != null) {
				ImageView imageView = imageViewReference.get();
				BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
				// Change bitmap only if this process is still associated with it
				// Or if we don't use any bitmap to task association (NO_DOWNLOADED_DRAWABLE mode)
				if ((this == bitmapDownloaderTask) || (mode != Mode.CORRECT)) {
					imageView.setImageBitmap(bitmap);
				}
			}
		}
	}

	/**
	 * A fake Drawable that will be attached to the imageView while the download is in progress.
	 * 
	 * <p>
	 * Contains a reference to the actual download task, so that a download task can be stopped if a new binding is required, and makes sure that only the last started download process can bind its result, independently of the download finish order.
	 * </p>
	 */
	// static class DownloadedDrawable extends ColorDrawable {
	// private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;
	//
	// public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) {
	// super(Color.DKGRAY);
	// bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(bitmapDownloaderTask);
	// }
	//
	// public BitmapDownloaderTask getBitmapDownloaderTask() {
	// return bitmapDownloaderTaskReference.get();
	// }
	// }

	/**
	 * A fake Drawable that will be attached to the imageView while the download is in progress.
	 * 
	 * <p>
	 * Contains a reference to the actual download task, so that a download task can be stopped if a new binding is required, and makes sure that only the last started download process can bind its result, independently of the download finish order.
	 * </p>
	 */
	static class DownloadedDrawable extends BitmapDrawable {
		private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

		public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask, Context context) {
			super(BitmapFactory.decodeResource(context.getResources(), R.drawable.loading_preview));
			bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(bitmapDownloaderTask);
		}

		public BitmapDownloaderTask getBitmapDownloaderTask() {
			return bitmapDownloaderTaskReference.get();
		}
	}

	public void setMode(Mode mode) {
		this.mode = mode;
		clearCache();
	}

	/*
	 * Cache-related fields and methods.
	 * 
	 * We use a hard and a soft cache. A soft reference cache is too aggressively cleared by the Garbage Collector.
	 */

	private static final int HARD_CACHE_CAPACITY = 10;
	private static final int DELAY_BEFORE_PURGE = 10 * 1000; // in milliseconds

	// Hard cache, with a fixed maximum capacity and a life duration
	private final HashMap<String, Bitmap> sHardBitmapCache = new LinkedHashMap<String, Bitmap>(HARD_CACHE_CAPACITY / 2, 0.75f, true) {
		@Override
		protected boolean removeEldestEntry(LinkedHashMap.Entry<String, Bitmap> eldest) {
			if (size() > HARD_CACHE_CAPACITY) {
				// Entries push-out of hard reference cache are transferred to soft reference cache
				sSoftBitmapCache.put(eldest.getKey(), new SoftReference<Bitmap>(eldest.getValue()));
				return true;
			} else {
				return false;
			}
		}
	};

	// Soft cache for bitmaps kicked out of hard cache
	private final static ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(HARD_CACHE_CAPACITY / 2);

	private final Handler purgeHandler = new Handler();

	private final Runnable purger = new Runnable() {
		@Override
		public void run() {
			clearCache();
		}
	};

	/**
	 * Adds this bitmap to the cache.
	 * 
	 * @param bitmap
	 *            The newly downloaded bitmap.
	 */
	private void addBitmapToCache(String url, Bitmap bitmap) {
		if (bitmap != null) {
			synchronized (sHardBitmapCache) {
				sHardBitmapCache.put(url, bitmap);
			}
		}
	}

	/**
	 * @param url
	 *            The URL of the image that will be retrieved from the cache.
	 * @return The cached bitmap or null if it was not found.
	 */
	private Bitmap getBitmapFromCache(String url) {
		// First try the hard reference cache
		synchronized (sHardBitmapCache) {
			final Bitmap bitmap = sHardBitmapCache.get(url);
			if (bitmap != null) {
				// Bitmap found in hard cache
				// Move element to first position, so that it is removed last
				sHardBitmapCache.remove(url);
				sHardBitmapCache.put(url, bitmap);
				return bitmap;
			}
		}

		// Then try the soft reference cache
		SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(url);
		if (bitmapReference != null) {
			final Bitmap bitmap = bitmapReference.get();
			if (bitmap != null) {
				// Bitmap found in soft cache
				return bitmap;
			} else {
				// Soft reference has been Garbage Collected
				sSoftBitmapCache.remove(url);
			}
		}

		return null;
	}

	/**
	 * Clears the image cache used internally to improve performance. Note that for memory efficiency reasons, the cache will automatically be cleared after a certain inactivity delay.
	 */
	public void clearCache() {
		sHardBitmapCache.clear();
		sSoftBitmapCache.clear();
	}

	/**
	 * Allow a new delay before the automatic cache clear is done.
	 */
	private void resetPurgeTimer() {
		purgeHandler.removeCallbacks(purger);
		purgeHandler.postDelayed(purger, DELAY_BEFORE_PURGE);
	}

}
