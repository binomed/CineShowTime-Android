package com.binomed.showtime.android.layout.dialogs.fav;

import com.binomed.showtime.beans.TheaterBean;

public interface TheaterFavSelectionListener {

	void theaterSelected(TheaterBean theater);

	void removeTheater(TheaterBean theater);

}
