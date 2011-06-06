package com.binomed.showtime.android.util;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.binomed.showtime.R;

public abstract class SingleActivity extends FragmentActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.empty);

		getSupportFragmentManager().beginTransaction().add(R.id.root_container, getFragmentLayout()).commit();

	}

	protected abstract Fragment getFragmentLayout();

}