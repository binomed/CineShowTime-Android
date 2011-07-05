package com.binomed.showtime.android.util.activity;

import android.support.v4.app.Fragment;

import com.binomed.showtime.R;

public abstract class AbstractSimpleCineShowTimeActivity<T extends Fragment, M extends ICineShowTimeActivityHelperModel> extends AbstractCineShowTimeActivity<M> {

	protected T fragment;

	@Override
	protected void initContentView() {
		fragment = getFragment();
		getSupportFragmentManager().beginTransaction().add(R.id.root_container, fragment);
	}

	@Override
	protected int getLayout() {
		return R.layout.empty;
	}

	protected abstract T getFragment();

}
