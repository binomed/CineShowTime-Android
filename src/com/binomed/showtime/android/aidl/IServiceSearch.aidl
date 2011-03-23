package com.binomed.showtime.android.aidl;

import com.binomed.showtime.android.aidl.ICallbackSearch;

interface IServiceSearch{

	void finish();
	void registerCallback(ICallbackSearch cb);
	void unregisterCallback(ICallbackSearch cb);
	boolean isServiceRunning();
}
