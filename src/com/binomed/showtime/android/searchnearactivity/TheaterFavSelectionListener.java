package com.binomed.showtime.android.searchnearactivity;

import com.binomed.showtime.beans.TheaterBean;

interface TheaterFavSelectionListener {

	void theaterSelected(TheaterBean theater);

	void removeTheater(TheaterBean theater);

}
