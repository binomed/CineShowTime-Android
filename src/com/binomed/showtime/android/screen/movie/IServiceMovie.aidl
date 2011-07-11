package com.binomed.showtime.android.screen.movie;

import com.binomed.showtime.android.screen.movie.ICallbackMovie;
import com.binomed.showtime.android.model.MovieBean;

interface IServiceMovie{

	void finish(String movieId);
	MovieBean getMovie(String movieId);
	void registerCallback(ICallbackMovie cb);
	void unregisterCallback(ICallbackMovie cb);
	boolean isServiceRunning();
	void cancelService();
}
