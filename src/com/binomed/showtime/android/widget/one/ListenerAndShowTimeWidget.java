package com.binomed.showtime.android.widget.one;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.binomed.showtime.R;
import com.binomed.showtime.beans.TheaterBean;

public class ListenerAndShowTimeWidget implements OnClickListener //
		// , LocationListener //
		, OnItemClickListener //
{

	private AndShowTimeWidgetConfigureActivity widgetActivity;
	private ControlerAndShowTimeWidget controler;
	private ModelAndShowTimeWidget model;

	private static final String TAG = "ListenerWidgetActivity"; //$NON-NLS-1$

	public ListenerAndShowTimeWidget(AndShowTimeWidgetConfigureActivity nearActivity, ControlerAndShowTimeWidget controler, ModelAndShowTimeWidget model) {
		super();
		this.widgetActivity = nearActivity;
		this.model = model;
		this.controler = controler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.searchWidgetBtnSearch: {
			String cityName = null;
			if (widgetActivity.fieldCityName.getText().toString().length() > 0) {
				cityName = widgetActivity.fieldCityName.getText().toString();
			}
			model.setCityName(cityName);

			try {

				boolean canLaunch = true;
				boolean btnCheck = widgetActivity.localisationCallBack.isGPSCheck();
				if (btnCheck && model.getLocalisation() == null) {
					Toast.makeText(widgetActivity //
							, R.string.msgNoGps //
							, Toast.LENGTH_LONG) //
							.show();
					canLaunch = false;
				} else if (!btnCheck && (cityName == null)) {
					Toast.makeText(widgetActivity //
							, R.string.msgNoCityName //
							, Toast.LENGTH_LONG) //
							.show();
					canLaunch = false;
				}

				if (canLaunch) {
					if (btnCheck) {
						model.setCityName(null);
						// model.setLocalisation(model.getLocalisation());
					} else {
						model.setLocalisation(null);
					}
					controler.openResultActivity();
				}

			} catch (Exception e) {
				Log.e(TAG, "erreur au lancement du service", e); //$NON-NLS-1$
			}
			break;
		}
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> adpater, View view, int groupPosition, long id) {
		TheaterBean theater = model.getFavList().get(groupPosition);
		model.setTheater(theater);
		AndShowTimeWidgetHelper.finalizeWidget(widgetActivity, theater, model.getCityName());
	}

}
