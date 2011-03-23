package com.binomed.showtime.android.widget;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.binomed.showtime.android.aidl.ICallbackSearchNear;
import com.binomed.showtime.android.aidl.IServiceSearchNear;
import com.binomed.showtime.android.cst.AndShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.searchnearactivity.AndShowTimeSearchNearService;
import com.binomed.showtime.android.service.AndShowDBGlobalService;
import com.binomed.showtime.android.util.AndShowTimeEncodingUtil;
import com.binomed.showtime.android.util.AndShowtimeFactory;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.android.util.localisation.LocationUtils;
import com.binomed.showtime.beans.LocalisationBean;
import com.binomed.showtime.beans.NearResp;
import com.binomed.showtime.beans.TheaterBean;

public class ControlerAndShowTimeWidget {
	private static final String TAG = "ControlerWidgetActivity"; //$NON-NLS-1$

	private AndShowTimeWidgetConfigureActivity widgetActivity;
	private ModelAndShowTimeWidget model;

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
		initWidgetId();
	}

	public ModelAndShowTimeWidget getModelWidgetActivity() {
		if (model == null) {
			model = new ModelAndShowTimeWidget();
		}
		return model;
	}

	public void launchNearService() throws UnsupportedEncodingException {

		Location gpsLocation = model.getLocalisation();
		String cityName = model.getCityName();
		int start = model.getStart();

		AndShowtimeFactory.initGeocoder(widgetActivity);
		Intent intentNearService = new Intent(widgetActivity, AndShowTimeSearchNearService.class);

		intentNearService.putExtra(ParamIntent.SERVICE_NEAR_LATITUDE, (gpsLocation != null) ? gpsLocation.getLatitude() : null);
		intentNearService.putExtra(ParamIntent.SERVICE_NEAR_LONGITUDE, (gpsLocation != null) ? gpsLocation.getLongitude() : null);
		intentNearService.putExtra(ParamIntent.SERVICE_NEAR_CITY, ((cityName != null) ? URLEncoder.encode(cityName, AndShowTimeEncodingUtil.getEncoding()) : cityName));
		intentNearService.putExtra(ParamIntent.SERVICE_NEAR_ORIGIN, AndShowTimeWidgetConfigureActivity.class.getName());
		intentNearService.putExtra(ParamIntent.SERVICE_NEAR_START, start);

		widgetActivity.startService(intentNearService);
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

			NearResp nearResp = BeanManagerFactory.getNearRespFromWidget();
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

		// We fill db
		Intent intentWidgetDb = new Intent(widgetActivity, AndShowDBGlobalService.class);
		intentWidgetDb.putExtra(ParamIntent.SERVICE_DB_TYPE, AndShowtimeCst.DB_TYPE_WIDGET_WRITE);
		TheaterBean theater = model.getTheater();
		if (LocationUtils.isEmptyLocation(theater.getPlace())) {
			LocalisationBean place = theater.getPlace();
			if (theater.getPlace() == null) {
				place = new LocalisationBean();
				theater.setPlace(place);
			}
			place.setCityName(model.getCityName());
		}
		BeanManagerFactory.setTheaterTemp(theater);
		widgetActivity.startService(intentWidgetDb);
		// We force widget to refresh
		Intent intentRefreshWidget = new Intent(widgetActivity, AndShowTimeWidgetHelper.class);
		intentRefreshWidget.putExtra(ParamIntent.WIDGET_REFRESH, true);
		AndShowTimeWidgetHelper.updateWidget(context, intentRefreshWidget, theater);

		// Make sure we pass back the original appWidgetId
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		widgetActivity.setResult(widgetActivity.RESULT_OK, resultValue);
		widgetActivity.finish();
	}
}
