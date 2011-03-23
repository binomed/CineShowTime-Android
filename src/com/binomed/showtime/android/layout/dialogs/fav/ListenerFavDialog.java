package com.binomed.showtime.android.layout.dialogs.fav;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

class ListenerFavDialog implements OnItemClickListener {

	private FavDialog dialog;

	private static final String TAG = "ListenerFavDialog"; //$NON-NLS-1$

	public ListenerFavDialog(FavDialog dialog) {
		super();
		this.dialog = dialog;
	}

	@Override
	public void onItemClick(AdapterView<?> adpater, View view, int groupPosition, long id) {
		Log.i(TAG, "onItemClick");
		dialog.listenerCallBack.theaterSelected(dialog.theaterList.get(groupPosition));
		dialog.dismiss();
	}

}
