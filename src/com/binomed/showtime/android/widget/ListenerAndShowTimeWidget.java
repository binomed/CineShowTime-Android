package com.binomed.showtime.android.widget;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.binomed.showtime.android.R;

public class ListenerAndShowTimeWidget implements OnClickListener, LocationListener, OnItemClickListener {

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
			if (widgetActivity.txtCityName.getText().toString().length() > 0) {
				cityName = widgetActivity.txtCityName.getText().toString();
			}
			model.setCityName(cityName);

			try {

				boolean canLaunch = true;
				boolean btnCheck = widgetActivity.chkGps.isChecked();
				if (btnCheck && model.getGpsLocalisation() == null) {
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
						model.setLocalisationSearch(model.getGpsLocalisation());
					} else {
						model.setLocalisationSearch(null);
					}
					widgetActivity.launchNearService();
				}

			} catch (Exception e) {
				Log.e(TAG, "erreur au lancement du service", e); //$NON-NLS-1$
			}
			break;
		}
		case R.id.searchWidgetLocation: {
			widgetActivity.txtCityName.setEnabled(!widgetActivity.chkGps.isChecked());
			if (!widgetActivity.chkGps.isChecked()) {
				widgetActivity.gpsImgView.setImageBitmap(widgetActivity.bitmapGpsOff);
			} else {
				widgetActivity.gpsImgView.setImageBitmap(widgetActivity.bitmapGpsOn);
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
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	@Override
	public void onLocationChanged(Location arg0) {
		Log.d(TAG, "Change location : lat : " + arg0.getLatitude() + " / lon : " + arg0.getLongitude());
		model.setGpsLocalisation(arg0);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
	 */
	@Override
	public void onProviderDisabled(String arg0) {
		widgetActivity.chkGps.setEnabled(false);
		if (widgetActivity.chkGps.isChecked()) {
			widgetActivity.chkGps.setChecked(false);
			widgetActivity.txtCityName.setEnabled(true);
			widgetActivity.gpsImgView.setImageBitmap(widgetActivity.bitmapGpsOff);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	@Override
	public void onProviderEnabled(String arg0) {
		widgetActivity.chkGps.setEnabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
	 */
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> adpater, View view, int groupPosition, long id) {
		model.setTheater(model.getTheaterResultList().get(groupPosition));
		controler.finalizeWidget();
	}

}
