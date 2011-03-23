package com.binomed.showtime.android.widget;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;

import com.binomed.showtime.android.adapter.db.AndShowtimeDbAdapter;

public class AndShowTimeWidgetService extends Service {

	private static final String TAG = "ServiceWidget"; //$NON-NLS-1$

	private boolean inThread;

	private AndShowtimeDbAdapter mDbHelper;

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

		AndShowTimeWidgetHelper.updateWidget(this, intent);
	}

}
