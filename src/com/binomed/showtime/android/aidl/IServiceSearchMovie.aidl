package com.binomed.showtime.android.aidl;

import com.binomed.showtime.android.aidl.ICallbackSearchMovie;

interface IServiceSearchMovie{

	void finish();
	void registerCallback(ICallbackSearchMovie cb);
	void unregisterCallback(ICallbackSearchMovie cb);
	boolean isServiceRunning();
}
