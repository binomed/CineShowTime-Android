package com.binomed.showtime.android.widget;

import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.view.TheaterFavListAdapter;
import com.binomed.showtime.android.handler.ServiceCallBackNear;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.android.util.LocationUtils;
import com.binomed.showtime.android.util.LocationUtils.ProviderEnum;
import com.binomed.showtime.beans.NearResp;

public class AndShowTimeWidgetConfigureActivity extends Activity {

	private static final String TAG = "WidgetActivity"; //$NON-NLS-1$

	private ControlerAndShowTimeWidget controler;
	private ListenerAndShowTimeWidget listener;
	private ModelAndShowTimeWidget model;

	protected AutoCompleteTextView txtCityName = null;
	protected Button btnSearch = null;
	protected CheckBox chkGps = null;
	protected ListView listResult = null;
	protected ImageView gpsImgView = null;
	protected ProgressDialog progressDialog;

	protected Bitmap bitmapGpsOn;
	protected Bitmap bitmapGpsOff;

	private ProviderEnum provider;

	private boolean checkboxPreference, locationListener;
	private SharedPreferences prefs;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set the result to CANCELED. This will cause the widget host to cancel
		// out of the widget placement if they press the back button.
		setResult(RESULT_CANCELED);

		setContentView(R.layout.and_showtime_widget_activity);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		controler = ControlerAndShowTimeWidget.getInstance();
		model = controler.getModelWidgetActivity();
		listener = new ListenerAndShowTimeWidget(this, controler, model);

		initViews();
		initListeners();

		controler.registerView(this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy"); //$NON-NLS-1$
		controler.unbindService();
		controler.closeDB();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "onPause"); //$NON-NLS-1$
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		removeListenersLocation();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume"); //$NON-NLS-1$
		initProvider();
		initListeners();
		initViewsState();

		model.setGpsLocalisation(checkboxPreference ? LocationUtils.getLastLocation(AndShowTimeWidgetConfigureActivity.this, provider) : null);

		display();

	}

	@Override
	protected void onRestart() {
		Log.i(TAG, "onRestart"); //$NON-NLS-1$
		super.onRestart();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.i(TAG, "onRestoreInstance"); //$NON-NLS-1$
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.i(TAG, "onSaveInstance"); //$NON-NLS-1$
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onStart() {
		Log.i(TAG, "onStart"); //$NON-NLS-1$
		super.onStart();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "onStop"); //$NON-NLS-1$
		super.onStop();
	}

	private void initViews() {
		bitmapGpsOn = BitmapFactory.decodeResource(getResources(), R.drawable.gps_activ);
		bitmapGpsOff = BitmapFactory.decodeResource(getResources(), R.drawable.gps_not_activ);

		txtCityName = (AutoCompleteTextView) findViewById(R.id.searchWidgetCityName);
		btnSearch = (Button) findViewById(R.id.searchWidgetBtnSearch);
		chkGps = (CheckBox) findViewById(R.id.searchWidgetLocation);
		listResult = (ListView) findViewById(R.id.searchWidgetListResult);
		gpsImgView = (ImageView) findViewById(R.id.searchWidgetImgGps);
	}

	private void initViewsState() {

		gpsImgView.setImageBitmap(bitmapGpsOff);
		chkGps.setChecked(false);
		chkGps.setEnabled(LocationUtils.isLocalisationEnabled(AndShowTimeWidgetConfigureActivity.this, provider));
	}

	private void initListeners() {
		btnSearch.setOnClickListener(listener);
		listResult.setOnItemClickListener(listener);
	}

	protected void initListenersLocation() {
		if (checkboxPreference) {
			locationListener = true;
			LocationUtils.registerLocalisationListener(AndShowTimeWidgetConfigureActivity.this, provider, listener);
		}

	}

	protected void removeListenersLocation() {
		locationListener = false;
		LocationUtils.unRegisterListener(AndShowTimeWidgetConfigureActivity.this, listener);
	}

	private void initProvider() {
		provider = LocationUtils.getProvider(prefs, this);
		checkboxPreference = prefs.getBoolean(getResources().getString(R.string.preference_loc_key_enable_localisation), true);

	}

	protected void launchNearService() throws UnsupportedEncodingException {
		openDialog();

		controler.launchNearService();
	}

	protected void display() {

		if (controler.isServiceRunning()) {
			openDialog();
		} else {
			NearResp nearResp = BeanManagerFactory.getNearResp();
			if (nearResp != null) {
				listResult.setAdapter(new TheaterFavListAdapter(AndShowTimeWidgetConfigureActivity.this, nearResp.getTheaterList()));
				if ((nearResp != null) && (nearResp.getCityName() != null) && (nearResp.getCityName().length() > 0)) {
					model.setCityName(nearResp.getCityName());
				}
				txtCityName.setText(model.getCityName());
			}
		}
	}

	/**
	 * 
	 */
	protected void openDialog() {
		progressDialog = ProgressDialog.show(AndShowTimeWidgetConfigureActivity.this, //
				AndShowTimeWidgetConfigureActivity.this.getResources().getString(R.string.searchNearProgressTitle)//
				, AndShowTimeWidgetConfigureActivity.this.getResources().getString(R.string.searchNearProgressMsg) //
				, true, false);
	}

	/**
	 * The call back message handler
	 */
	public ServiceCallBackNear m_callbackHandler = new ServiceCallBackNear() {

		@Override
		public void handleInputRecived() {

			display();
			if (progressDialog != null) {
				progressDialog.dismiss();
			}

		}

	};

}