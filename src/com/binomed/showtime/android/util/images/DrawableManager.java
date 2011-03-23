package com.binomed.showtime.android.util.images;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.binomed.showtime.android.cst.CineShowtimeCst;

public class DrawableManager {
	private final Map<String, Drawable> drawableMap;

	private static final String TAG = "DrawableManager";

	public DrawableManager() {
		drawableMap = new HashMap<String, Drawable>();
	}

	public Drawable fetchDrawable(String urlString, String fileName) {
		String finalFileName = fileName;
		if (finalFileName == null) {
			finalFileName = urlString.substring(urlString.lastIndexOf("/"), urlString.length());
		}

		if (drawableMap.containsKey(finalFileName)) {
			return drawableMap.get(finalFileName);
		}

		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(this.getClass().getSimpleName(), "image url:" + urlString);
		}
		try {
			File root = Environment.getExternalStorageDirectory();
			InputStream is = null;
			Drawable drawable = null;
			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
				File posterFile = new File(root, new StringBuilder(CineShowtimeCst.FOLDER_POSTER).append(finalFileName).toString());
				posterFile.getParentFile().mkdirs();
				if (posterFile.exists()) {
					Log.i(TAG, "img existe");
					is = new FileInputStream(posterFile);
					drawable = Drawable.createFromStream(is, "src");
					drawableMap.put(urlString, drawable);
					FileOutputStream fileOutPut = new FileOutputStream(posterFile);
					byte[] tempon = new byte[10240];

					while (true) {
						int nRead = is.read(tempon, 0, tempon.length);
						if (nRead <= 0) {
							break;
						}
						fileOutPut.write(tempon, 0, nRead);
					}
					fileOutPut.close();
				}
			}

			if (drawable == null) {
				is = fetch(urlString);
				drawable = Drawable.createFromStream(is, "src");
				drawableMap.put(urlString, drawable);

			}
			if (Log.isLoggable(TAG, Log.DEBUG)) {
				Log.d(TAG, "got a thumbnail drawable: " + drawable.getBounds() + ", " + drawable.getIntrinsicHeight() + "," + drawable.getIntrinsicWidth() + ", " + drawable.getMinimumHeight() + "," + drawable.getMinimumWidth());
			}
			return drawable;
		} catch (MalformedURLException e) {
			Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
			return null;
		} catch (IOException e) {
			Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
			return null;
		}
	}

	public void fetchDrawableOnThread(final String urlString, final String fileName, final ImageView imageView) {
		if (drawableMap.containsKey(urlString)) {
			imageView.setImageDrawable(drawableMap.get(urlString));
		}

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				imageView.setImageDrawable((Drawable) message.obj);
			}
		};

		Thread thread = new Thread() {
			@Override
			public void run() {
				// TODO : set imageView to a "pending" image
				Drawable drawable = fetchDrawable(urlString, fileName);
				Message message = handler.obtainMessage(1, drawable);
				handler.sendMessage(message);
			}
		};
		thread.start();
	}

	private InputStream fetch(String urlString) throws MalformedURLException, IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(urlString);
		HttpResponse response = httpClient.execute(request);
		return response.getEntity().getContent();
	}

}