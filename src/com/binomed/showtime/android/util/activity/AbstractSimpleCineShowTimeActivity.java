package com.binomed.showtime.android.util.activity;

import android.support.v4.app.Fragment;

import com.binomed.showtime.R;

public abstract class AbstractSimpleCineShowTimeActivity<T extends Fragment> extends AbstractCineShowTimeActivity {

	private T fragment;

	@Override
	protected void setContentView() {
		setContentView(R.layout.empty);

		fragment = getFragment();
		getSupportFragmentManager().beginTransaction().add(R.id.root_container, fragment);
	}

	protected abstract T getFragment();

}
