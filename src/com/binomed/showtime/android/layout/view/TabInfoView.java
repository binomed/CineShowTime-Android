package com.binomed.showtime.android.layout.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binomed.showtime.R;

public class TabInfoView extends LinearLayout {

	private Context context;
	private ImageView tabImg;
	private TextView tabInfo;

	public TabInfoView(Context context, String textTab, Drawable drawable) {
		super(context);
		this.context = context;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_tab_item, this);

		// expandImg = (ImageView) this.findViewById(R.id.expand_img);
		tabImg = (ImageView) this.findViewById(R.id.tabImg);
		tabInfo = (TextView) this.findViewById(R.id.tabInfo);

		setInfos(textTab, drawable);
	}

	public void setInfos(String textTab, Drawable drawable) {
		tabImg.setImageDrawable(drawable);
		tabInfo.setText(textTab);

	}

}
