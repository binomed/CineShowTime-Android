package com.binomed.showtime.android.movieactivity;

import com.binomed.showtime.android.movieactivity.ICallbackMovie;
import com.binomed.showtime.android.model.MovieBean;

interface IServiceMovie{

	void finish(String movieId);
	MovieBean getMovie(String movieId);
	void registerCallback(ICallbackMovie cb);
	void unregisterCallback(ICallbackMovie cb);
	boolean isServiceRunning();
	void cancelService();
}
