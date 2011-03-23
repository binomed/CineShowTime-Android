package com.binomed.showtime.android.layout.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.view.ProjectionOptionListAdapter;
import com.binomed.showtime.android.objects.OptionEnum;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.beans.ProjectionBean;

public class ListOptionProjectionView extends LinearLayout {

	private ListView listOption;
	private List<OptionEnum> listOptions;

	private ProjectionBean projectionBean;

	private ProjectionOptionListAdapter adapter;

	private Context context;

	public ProjectionBean getProjectionBean() {
		return projectionBean;
	}

	public ListOptionProjectionView(Context context, OnItemClickListener clickListener) {
		super(context);
		this.context = context;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.popup_list_option_projection, this);

		adapter = new ProjectionOptionListAdapter(context);

		// expandImg = (ImageView) this.findViewById(R.id.expand_img);
		listOption = (ListView) this.findViewById(R.id.list_option_projection);
		listOption.setAdapter(adapter);
		listOption.setOnItemClickListener(clickListener);
	}

	public void setProjectionBean(ProjectionBean projectionBean) {
		this.projectionBean = projectionBean;
		listOptions = new ArrayList<OptionEnum>();

		listOptions.add(OptionEnum.SMS);
		listOptions.add(OptionEnum.MAIL);
		if (Integer.valueOf(Build.VERSION.SDK) <= 8 && BeanManagerFactory.isCalendarInstalled(context.getPackageManager())) {
			listOptions.add(OptionEnum.AGENDA);
		}
		if (this.projectionBean.getReservationLink() != null && this.projectionBean.getReservationLink().length() > 0) {
			listOptions.add(OptionEnum.RESERVATION);
		}

		adapter.setListOptions(listOptions);

	}

	public List<OptionEnum> getOptions() {
		return listOptions;
	}

}
