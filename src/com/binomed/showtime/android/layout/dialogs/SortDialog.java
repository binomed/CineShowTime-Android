package com.binomed.showtime.android.layout.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.binomed.showtime.R;

public class SortDialog extends Dialog {

	private Context mainContext;
	private ListView listSort;
	private int ressourceValue;

	public static final int SORT_THEATER_NAME = 0;
	public static final int SORT_THEATER_DISTANCE = 1;
	public static final int SORT_SHOWTIME = 2;

	protected SortSelectionListener listenerCallBack;
	private ListenerSortDialog listener;

	public SortDialog(Context context, SortSelectionListener listener, int ressourceValue) {
		super(context);
		mainContext = context;
		this.listenerCallBack = listener;
		this.ressourceValue = ressourceValue;
		this.listener = new ListenerSortDialog(this);
	}

	/**
	 * @see android.app.Dialog#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sort_dialog);

		listSort = (ListView) findViewById(R.id.sortListSortDialog);
		listSort.setOnItemClickListener(listener);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>( //
				mainContext //
				, android.R.layout.simple_list_item_1 //
				, mainContext.getResources().getStringArray(ressourceValue) //
		);
		listSort.setAdapter(adapter);

	}

}
