package com.binomed.showtime.android.util.localisation;

import android.location.Location;

public interface IModelLocalisation {

	void setLocalisation(Location location);

	Location getLocalisation();

}
