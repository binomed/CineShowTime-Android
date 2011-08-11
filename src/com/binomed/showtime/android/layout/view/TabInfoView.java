package com.binomed.showtime.android.layout.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binomed.showtime.R;

public class TabInfoView extends LinearLayout {

	private TextView tabInfo;

	public TabInfoView(Context context, String textTab, Drawable drawable) {
		super(context);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_tab_item, this);
		tabInfo = (TextView) this.findViewById(R.id.tabInfo);

		setInfos(textTab, drawable);
	}

	public void setInfos(String textTab, Drawable drawable) {
		tabInfo.setText(textTab);
		tabInfo.setCompoundDrawables(null // left
				, drawable // top
				, null // right
				, null // bottom
		);

	}
}
