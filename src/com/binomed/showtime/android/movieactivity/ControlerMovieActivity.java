package com.binomed.showtime.android.movieactivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

import com.binomed.showtime.android.adapter.db.AndShowtimeDbAdapter;
import com.binomed.showtime.android.aidl.ICallbackMovie;
import com.binomed.showtime.android.aidl.IServiceMovie;
import com.binomed.showtime.android.cst.AndShowtimeCst;
import com.binomed.showtime.android.cst.IntentShowtime;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.service.AndShowDBGlobalService;
import com.binomed.showtime.android.util.AndShowTimeEncodingUtil;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.beans.MovieBean;
import com.google.api.translate.Language;
import com.google.api.translate.Translate;

public class ControlerMovieActivity {

	private static final String TAG = "ControlerMovieActivity"; //$NON-NLS-1$

	private AndShowTimeMovieActivity movieActivity;
	private ModelMovieActivity model;

	private IServiceMovie serviceMovie;
	private AndShowtimeDbAdapter mDbHelper;

	private static ControlerMovieActivity instance;

	public static ControlerMovieActivity getInstance() {
		if (instance == null) {
			instance = new ControlerMovieActivity();
		}
		return instance;
	}

	private ControlerMovieActivity() {
		super();
	}

	public void registerView(AndShowTimeMovieActivity movieActivity) {
		this.movieActivity = movieActivity;
		bindService();
		// initDB();
	}

	public ModelMovieActivity getModel() {
		if (model == null) {
			model = new ModelMovieActivity();
		}
		return model;
	}

	/*
	 * 
	 * DB
	 */

	public void initDB() {

		try {
			mDbHelper = new AndShowtimeDbAdapter(movieActivity);
			mDbHelper.open();

		} catch (SQLException e) {
			Log.e(TAG, "error during opening data base", e); //$NON-NLS-1$
		}
	}

	public void closeDB() {
		try {
			if (mDbHelper != null) {
				mDbHelper.close();
			}
		} catch (Exception e) {
			Log.e(TAG, "error onDestroy of movie Activity", e);
		}
	}

	public void fillDB() {
		Intent intentUpdateMovie = new Intent(movieActivity, AndShowDBGlobalService.class);
		intentUpdateMovie.putExtra(ParamIntent.SERVICE_DB_TYPE, AndShowtimeCst.DB_TYPE_MOVIE_WRITE);
		BeanManagerFactory.setMovieDesc(model.getMovie());
		movieActivity.startService(intentUpdateMovie);
		// initDB();
		// Thread threadFillDB = new Thread(fillDBRunnable);
		// threadFillDB.start();

	}

	private Runnable fillDBRunnable = new Runnable() {
		public void run() {
			mDbHelper.createOrUpdateMovie(model.getMovie());
			closeDB();

		}
	};

	/*
	 * SERVICE
	 */

	public void bindService() {
		movieActivity.bindService(new Intent(movieActivity, AndShowTimeMovieService.class), mConnection, Context.BIND_AUTO_CREATE);
	}

	public void unbindService() {
		try {
			serviceMovie.unregisterCallback(m_callback);
			movieActivity.unbindService(mConnection);
		} catch (Exception e) {
			Log.e(TAG, "error while unbinding service", e);
		}
	}

	protected boolean isServiceRunning() {
		if (serviceMovie != null) {
			try {
				return serviceMovie.isServiceRunning();
			} catch (RemoteException e) {
				Log.e(TAG, "Error during checking service", e); //$NON-NLS-1$
			}
		}
		return false;
	}

	/**
	 * The service connection inteface with our binded service {@link http ://code .google.com/android/reference/android/content/ServiceConnection.html}
	 */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			serviceMovie = IServiceMovie.Stub.asInterface(service);

			try {
				serviceMovie.registerCallback(m_callback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

	};

	/**
	 * The callback object that will return from the service
	 */
	private ICallbackMovie m_callback = new ICallbackMovie.Stub() {

		@Override
		public void finish(String idMovie) throws RemoteException {
			movieActivity.m_callbackHandler.sendInputRecieved(idMovie);

		}

	};

	public void searchMovieDetail(MovieBean movie, String near) throws Exception {

		// bindService();

		movieActivity.openDialog();

		boolean checkboxPreference;
		// Get the xml/preferences.xml preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(movieActivity.getBaseContext());
		checkboxPreference = prefs.getBoolean("checkbox_preference", false);

		Intent intentMovieService = new Intent(movieActivity, AndShowTimeMovieService.class);

		intentMovieService.putExtra(ParamIntent.SERVICE_MOVIE_ID, movie.getId());
		intentMovieService.putExtra(ParamIntent.SERVICE_MOVIE_NEAR, near);
		intentMovieService.putExtra(ParamIntent.SERVICE_MOVIE_TRANSLATE, checkboxPreference);

		movieActivity.startService(intentMovieService);

	}

	public String translateDesc() throws Exception {
		MovieBean movie = model.getMovie();
		String descTlt = movie.getDescription();
		if (!model.isTranslate()) {
			descTlt = movie.getTrDescription();
			if ((descTlt == null) // 
					|| (descTlt.length() == 0)) {
				Language convertLanguage = AndShowTimeEncodingUtil.convertLocaleToLanguage();
				if (!convertLanguage.equals(Language.ENGLISH)) {
					descTlt = Translate.translate(movie.getDescription(), Language.ENGLISH, convertLanguage);
					movie.setTrDescription(descTlt);
					fillDB();
				}
			}
			model.setTranslate(true);
		} else {
			model.setTranslate(false);
		}
		return descTlt;
	}

	/*
	 * Intents
	 */

	public void openImdbBrowser() {
		movieActivity.startActivity(IntentShowtime.createImdbBrowserIntent(model.getMovie()));
	}

}
