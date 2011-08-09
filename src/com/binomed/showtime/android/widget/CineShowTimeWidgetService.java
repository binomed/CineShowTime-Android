package com.binomed.showtime.android.widget;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;

import com.binomed.showtime.android.cst.ParamIntent;

public class CineShowTimeWidgetService extends Service {

	private static final String TAG = "ServiceWidget"; //$NON-NLS-1$

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public ComponentName startService(Intent service) {
		return super.startService(service);
	}

	@Override
	public boolean stopService(Intent name) {
		return super.stopService(name);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		CineShowTimeWidgetHelper.updateWidget(this, intent, null, intent.getIntExtra(ParamIntent.WIDGET_ID, 0));
	}

}
