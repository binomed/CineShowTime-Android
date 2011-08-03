package com.binomed.showtime.android.screen.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.binomed.showtime.R;

public class CineShowTimeHeaderFragment extends Fragment {

	/** Called when the activity is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i("LIFECYCLE-FRAGMENT", "onCreateView");

		View mainView = inflater.inflate(R.layout.fragment_fav, container, false);
		return mainView;
	}

}