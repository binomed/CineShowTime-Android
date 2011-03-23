package com.binomed.showtime.android.searchnearactivity;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.view.TheaterFavListAdapter;
import com.binomed.showtime.beans.TheaterBean;

public class FavDialog extends Dialog {

	private Context mainContext;
	private ListView listFav;

	private static final int REMOVE_BOOKMARKS = Menu.FIRST; //$NON-NLS-1$

	protected List<TheaterBean> theaterList;

	protected TheaterFavSelectionListener listenerCallBack;
	private ListenerFavDialog listener;

	public FavDialog(Context context, TheaterFavSelectionListener listener, List<TheaterBean> theaterList) {
		super(context);
		mainContext = context;
		this.listenerCallBack = listener;
		this.theaterList = theaterList;
	}

	/**
	 * @see android.app.Dialog#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fav_dialog);

		listener = new ListenerFavDialog(this);

		listFav = (ListView) findViewById(R.id.favListTheaterFav);
		listFav.setOnItemClickListener(listener);
		registerForContextMenu(listFav);

		TheaterFavListAdapter adapter = new TheaterFavListAdapter(mainContext, theaterList);
		listFav.setAdapter(adapter);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		int groupId = ((AdapterContextMenuInfo) menuInfo).position;
		int itemId = REMOVE_BOOKMARKS;
		int menuStr = R.string.menuRemoveBookmark;
		menu.add(groupId, itemId, 0, menuStr);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case REMOVE_BOOKMARKS: {
			Object selectItem = theaterList.get(item.getGroupId());
			if (selectItem.getClass() == TheaterBean.class) {
				TheaterBean theater = (TheaterBean) selectItem;
				listenerCallBack.removeTheater(theater);
			}
			dismiss();
			return true;
		}
		}
		return super.onMenuItemSelected(featureId, item);
	}

}
