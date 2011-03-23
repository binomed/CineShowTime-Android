package com.binomed.showtime.android.layout.dialogs.sort;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.binomed.showtime.R;

public class ListDialog extends Dialog {

	private Context mainContext;
	private ListView listSort;
	private int ressourceValue;
	private int sourceID;
	private List<String> values;

	protected ListSelectionListener listenerCallBack;
	private ListenerListDialog listener;

	public ListDialog(Context context, ListSelectionListener listener, int ressourceValue, int sourceID) {
		super(context);
		mainContext = context;
		this.listenerCallBack = listener;
		this.ressourceValue = ressourceValue;
		this.values = null;
		this.sourceID = sourceID;
		this.listener = new ListenerListDialog(this);
	}

	public ListDialog(Context context, ListSelectionListener listener, List<String> values, int sourceID) {
		super(context);
		mainContext = context;
		this.listenerCallBack = listener;
		this.values = values;
		this.ressourceValue = -1;
		this.sourceID = sourceID;
		this.listener = new ListenerListDialog(this);
	}

	/**
	 * @see android.app.Dialog#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_sort);

		listSort = (ListView) findViewById(R.id.sortListSortDialog);
		listSort.setOnItemClickListener(listener);

		ArrayAdapter<String> adapter = null;
		if (ressourceValue != -1) {
			adapter = new ArrayAdapter<String>( //
					mainContext //
					, android.R.layout.simple_list_item_1 //
					, mainContext.getResources().getStringArray(ressourceValue) //
			);
		} else {
			adapter = new ArrayAdapter<String>( //
					mainContext //
					, android.R.layout.simple_list_item_1 //
					, values //
			);

		}
		listSort.setAdapter(adapter);

	}

	public int getSourceID() {
		return sourceID;
	}

}
