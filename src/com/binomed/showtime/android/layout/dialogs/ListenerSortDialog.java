package com.binomed.showtime.android.layout.dialogs;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

class ListenerSortDialog implements OnItemClickListener, OnItemSelectedListener, OnClickListener {

	private SortDialog dialog;

	private static final String TAG = "ListenerSortDialog"; //$NON-NLS-1$

	public ListenerSortDialog(SortDialog dialog) {
		super();
		this.dialog = dialog;
	}

	@Override
	public void onItemClick(AdapterView<?> adpater, View view, int groupPosition, long id) {
		Log.i(TAG, "onItemClick");
		dialog.listenerCallBack.sortSelected(groupPosition);
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
