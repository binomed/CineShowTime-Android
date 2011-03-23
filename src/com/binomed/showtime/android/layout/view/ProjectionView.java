package com.binomed.showtime.android.layout.view;

import android.content.Context;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binomed.showtime.R;
import com.binomed.showtime.beans.ProjectionBean;

public class ProjectionView extends LinearLayout {

	private TextView projectionText;

	private ProjectionBean projectionBean;

	private Context context;

	public ProjectionBean getProjectionBean() {
		return projectionBean;
	}

	public ProjectionView(Context context, OnClickListener clickListener) {
		super(context);
		this.context = context;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_projection_group_item, this);

		// expandImg = (ImageView) this.findViewById(R.id.expand_img);
		projectionText = (TextView) this.findViewById(R.id.item_projection_name);
		ImageButton btn = (ImageButton) this.findViewById(R.id.item_projection_button);
		btn.setOnClickListener(clickListener);
	}

	public void setProjectionBean(ProjectionBean projectionBean, Spanned text) {
		this.projectionBean = projectionBean;
		projectionText.setText(text);

	}

}
