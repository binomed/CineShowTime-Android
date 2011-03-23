package com.binomed.showtime.android.widget;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.SQLException;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.binomed.showtime.android.adapter.db.AndShowtimeDbAdapter;
import com.binomed.showtime.android.aidl.ICallbackSearchNear;
import com.binomed.showtime.android.aidl.IServiceSearchNear;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.searchnearactivity.AndShowTimeSearchNearService;
import com.binomed.showtime.android.util.AndShowTimeEncodingUtil;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.beans.NearResp;

public class ControlerAndShowTimeWidget {
	private static final String TAG = "ControlerWidgetActivity"; //$NON-NLS-1$

	private AndShowTimeWidgetConfigureActivity widgetActivity;
	private ModelAndShowTimeWidget model;
	private AndShowtimeDbAdapter mDbHelper;

	private IServiceSearchNear serviceNear;

	private static ControlerAndShowTimeWidget instance;

	private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

	public static ControlerAndShowTimeWidget getInstance() {
		if (instance == null) {
			instance = new ControlerAndShowTimeWidget();
		}
		return instance;
	}

	private ControlerAndShowTimeWidget() {
		super();
	}

	public void registerView(AndShowTimeWidgetConfigureActivity widgetActivity) {
		this.widgetActivity = widgetActivity;
		bindService();
		initDB();
		initWidgetId();
	}

	public ModelAndShowTimeWidget getModelWidgetActivity() {
		if (model == null) {
			model = new ModelAndShowTimeWidget();
		}
		return model;
	}

	public void launchNearService() throws UnsupportedEncodingException {
		// bindService();

		Location gpsLocation = model.getLocalisationSearch();
		String cityName = model.getCityName();

		Intent intentNearService = new Intent(widgetActivity, AndShowTimeSearchNearService.class);

		intentNearService.putExtra(ParamIntent.SERVICE_NEAR_LATITUDE, (gpsLocation != null) ? gpsLocation.getLatitude() : null);
		intentNearService.putExtra(ParamIntent.SERVICE_NEAR_LONGITUDE, (gpsLocation != null) ? gpsLocation.getLongitude() : null);
		intentNearService.putExtra(ParamIntent.SERVICE_NEAR_CITY, ((cityName != null) ? URLEncoder.encode(cityName, AndShowTimeEncodingUtil.getEncoding()) : cityName));

		widgetActivity.startService(intentNearService);
	}

	/*
	 * 
	 * DB
	 */

	public void openDB() {

		try {
			Log.i(TAG, "openDB"); //$NON-NLS-1$
			mDbHelper = new AndShowtimeDbAdapter(widgetActivity);
			mDbHelper.open();
		} catch (SQLException e) {
			Log.e(TAG, "error during getting fetching informations", e); //$NON-NLS-1$
		}
	}

	public void initDB() {

		try {
			openDB();

			getModelWidgetActivity();

		} catch (SQLException e) {
			Log.e(TAG, "error during getting fetching informations", e); //$NON-NLS-1$
		}
	}

	public void closeDB() {
		try {
			Log.i(TAG, "Close DB"); //$NON-NLS-1$
			mDbHelper.close();
		} catch (Exception e) {
			Log.e(TAG, "error onDestroy of movie Activity", e); //$NON-NLS-1$
		}
	}

	/*
	 * 
	 * CALL BACK SERVICE
	 */

	public void bindService() {
		widgetActivity.bindService(new Intent(widgetActivity, AndShowTimeSearchNearService.class), mConnection, Context.BIND_AUTO_CREATE);
	}

	public void unbindService() {
		try {
			serviceNear.unregisterCallback(m_callback);
			widgetActivity.unbindService(mConnection);
		} catch (Exception e) {
			Log.e(TAG, "error while unbinding service", e);
		}
	}

	/**
	 * The service connection inteface with our binded service {@link http ://code .google.com/android/reference/android/content/ServiceConnection.html}
	 */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			serviceNear = IServiceSearchNear.Stub.asInterface(service);

			try {
				serviceNear.registerCallback(m_callback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

	};

	protected boolean isServiceRunning() {
		if (serviceNear != null) {
			try {
				return serviceNear.isServiceRunning();
			} catch (RemoteException e) {
				Log.e(TAG, "Eror during checking service", e); //$NON-NLS-1$
			}
		}
		return false;
	}

	/**
	 * The callback object that will return from the service
	 */
	private ICallbackSearchNear m_callback = new ICallbackSearchNear.Stub() {

		@Override
		public void finish() throws RemoteException {

			NearResp nearResp = BeanManagerFactory.getNearResp();
			if (nearResp != null) {
				model.setTheaterResultList(nearResp.getTheaterList());
			}
			widgetActivity.m_callbackHandler.sendInputRecieved();

		}

	};

	protected void initWidgetId() {
		// Find the widget id from the intent.
		Intent intent = widgetActivity.getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		// If they gave us an intent without the widget id, just bail.
		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			widgetActivity.finish();
		}
	}

	protected void finalizeWidget() {
		final Context context = widgetActivity;

		mDbHelper.setWidgetTheater(model.getTheater());

		// Push widget update to surface with newly set prefix
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		//		
		// Intent intentTest = new Intent();
		// intentTest.putExtra("type", "Test");
		AndShowTimeWidgetHelper.updateWidget(context, null);

		// Make sure we pass back the original appWidgetId
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		widgetActivity.setResult(widgetActivity.RESULT_OK, resultValue);
		widgetActivity.finish();
	}

}
