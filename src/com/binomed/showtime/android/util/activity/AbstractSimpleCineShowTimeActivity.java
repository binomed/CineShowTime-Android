package com.binomed.showtime.android.util.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.binomed.showtime.R;

public abstract class AbstractSimpleCineShowTimeActivity<T extends Fragment, M extends ICineShowTimeActivityHelperModel> extends AbstractCineShowTimeActivity<M> {

	protected T fragment;

	@Override
	protected void initContentView() {
		Fragment fragmentRecycle = getSupportFragmentManager().findFragmentById(R.id.root_container);
		fragment = getFragment(fragmentRecycle);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if (fragmentRecycle == null) {
			transaction.add(R.id.root_container, fragment);
		} else if ((fragment != null) && !fragmentRecycle.equals(fragment)) {
			transaction.replace(R.id.root_container, fragment);
		}
		transaction.commit();

	}

	@Override
	protected int getLayout() {
		return R.layout.empty;
	}

	protected abstract T getFragment(Fragment fragmentRecycle);

}
