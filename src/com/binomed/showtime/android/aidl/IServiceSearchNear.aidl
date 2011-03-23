package com.binomed.showtime.android.aidl;

import com.binomed.showtime.android.aidl.ICallbackSearchNear;

interface IServiceSearchNear{

	void finish();
	void registerCallback(ICallbackSearchNear cb);
	void unregisterCallback(ICallbackSearchNear cb);
	boolean isServiceRunning();
}
