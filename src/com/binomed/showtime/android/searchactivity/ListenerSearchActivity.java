package com.binomed.showtime.android.searchactivity;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.binomed.showtime.R;

public class ListenerSearchActivity implements OnClickListener //
		, OnItemSelectedListener //
{

	private AndShowTimeSearchActivity searchActivity;
	private ControlerSearchActivity controler;
	private ModelSearchActivity model;

	private static final String TAG = "ListenerSearchActivity"; //$NON-NLS-1$

	public ListenerSearchActivity(AndShowTimeSearchActivity nearActivity, ControlerSearchActivity controlerNearActivity, ModelSearchActivity model) {
		super();
		this.searchActivity = nearActivity;
		this.controler = controlerNearActivity;
		this.model = model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.searchBtnSearch: {
			String cityName = null;
			String movieName = null;
			if (searchActivity.fieldCityName.getText().toString().length() > 0) {
				cityName = searchActivity.fieldCityName.getText().toString();
			}
			if (searchActivity.fieldMovieName.getText().toString().length() > 0) {
				movieName = searchActivity.fieldMovieName.getText().toString();
			}
			model.setCityName(cityName);
			model.setMovieName(movieName);
			model.setFavTheaterId(null);
			model.setStart(0);

			try {

				boolean canLaunch = true;
				boolean btnCheck = searchActivity.localisationCallBack.isGPSCheck();
				if (btnCheck && model.getLocalisation() == null) {
					Toast.makeText(searchActivity //
							, R.string.msgNoGps //
							, Toast.LENGTH_LONG) //
							.show();
					canLaunch = false;
				} else if (!btnCheck && (cityName == null)) {
					Toast.makeText(searchActivity //
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
	 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android .widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemSelected(AdapterView<?> adapter, View view, int groupPositon, long id) {
		switch (adapter.getId()) {
		case R.id.searchSpinner: {
			Log.i(TAG, "change Day : " + groupPositon);
			model.setDay(groupPositon);
			break;
		}
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android .widget.AdapterView)
	 */
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

}
