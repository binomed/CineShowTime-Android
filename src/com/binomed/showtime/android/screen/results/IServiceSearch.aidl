package com.binomed.showtime.android.screen.results;

import com.binomed.showtime.android.screen.results.ICallbackSearch;
import com.binomed.showtime.android.model.NearResp;
import com.binomed.showtime.android.model.LocalisationBean;

interface IServiceSearch{

	void finish();
	void finishLocation(String theaterId);
	void registerCallback(ICallbackSearch cb);
	void unregisterCallback(ICallbackSearch cb);
	NearResp getNearResp();
	LocalisationBean getLocalisation(String theaterId);
	boolean isServiceRunning();
	void cancelService();
}
