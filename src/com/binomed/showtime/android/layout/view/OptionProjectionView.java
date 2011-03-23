package com.binomed.showtime.android.layout.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.objects.OptionEnum;

public class OptionProjectionView extends LinearLayout {

	private TextView optionText;
	private ImageView optionImage;

	private Context context;

	private OptionEnum option;

	public OptionProjectionView(Context context) {
		super(context);
		this.context = context;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_option_projection_group_item, this);

		// expandImg = (ImageView) this.findViewById(R.id.expand_img);
		optionText = (TextView) this.findViewById(R.id.item_option_projection_name);
		optionImage = (ImageView) this.findViewById(R.id.item_option_projection_image);
	}

	public OptionEnum getOption() {
		return option;
	}

	public void setOption(OptionEnum option) {
		this.option = option;
		optionText.setText(option.getRessourceText());
		optionImage.setBackgroundResource(option.getRessourceDrawable());

	}

}
