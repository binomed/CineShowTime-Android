package com.binomed.showtime.android.adapter.view;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.binomed.showtime.android.layout.view.OptionProjectionView;
import com.binomed.showtime.android.model.OptionEnum;

public class ProjectionOptionListAdapter extends BaseAdapter {

	private Context mainContext;

	private List<OptionEnum> listOptions;

	public ProjectionOptionListAdapter(Context context) {
		super();
		mainContext = context;

	}

	public void setListOptions(List<OptionEnum> listOptions) {
		this.listOptions = listOptions;
	}

	@Override
	public int getCount() {
		return listOptions != null ? listOptions.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		OptionEnum option = null;
		if ((listOptions != null) && (listOptions.size() >= position)) {
			option = listOptions.get(position);
		}
		return option;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int groupPosition, View convertView, ViewGroup parent) {

		OptionProjectionView optionView = null;
		if (convertView == null) {
			optionView = new OptionProjectionView(mainContext);

		} else {
			optionView = (OptionProjectionView) convertView;
		}

		OptionEnum option = (OptionEnum) getItem(groupPosition);

		if (option != null) {
			optionView.setOption(option);
		}

		return optionView;
	}

}
