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
package com.binomed.showtime.android.layout.dialogs.sort;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

class ListenerListDialog implements OnItemClickListener, OnItemSelectedListener, OnClickListener {

	private ListDialog dialog;

	private static final String TAG = "ListenerSortDialog"; //$NON-NLS-1$

	public ListenerListDialog(ListDialog dialog) {
		super();
		this.dialog = dialog;
	}

	@Override
	public void onItemClick(AdapterView<?> adpater, View view, int groupPosition, long id) {
		Log.i(TAG, "onItemClick");
		dialog.listenerCallBack.sortSelected(dialog.getSourceID(), groupPosition);
		dialog.dismiss();
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Log.i(TAG, "onItemSelectedClick");
		// TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		Log.i(TAG, "onNothingSelectedClick");
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		Log.i(TAG, "onClick");
		// TODO Auto-generated method stub

	}

}
