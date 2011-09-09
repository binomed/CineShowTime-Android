/*
 * Copyright (C) 2011 Binomed (http://blog.binomed.fr)
 *
 * Licensed under the Eclipse Public License - v 1.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 * THE ACCOMPANYING PROGRAM IS PROVIDED UNDER THE TERMS OF THIS ECLIPSE PUBLIC 
 * LICENSE ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM 
 * CONSTITUTES RECIPIENT'S ACCEPTANCE OF THIS AGREEMENT.
 */
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
