package com.binomed.showtime.android.aidl;

import com.binomed.showtime.android.aidl.ICallbackMovie;

interface IServiceMovie{

	void finish(String movieId);
	void registerCallback(ICallbackMovie cb);
	void unregisterCallback(ICallbackMovie cb);
	boolean isServiceRunning();
}
