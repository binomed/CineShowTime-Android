package com.binomed.showtime.android.widget;

import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;

import com.binomed.showtime.android.R;
import com.binomed.showtime.android.adapter.view.TheaterFavListAdapter;
import com.binomed.showtime.android.handler.ServiceCallBackNear;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.android.util.LocationUtils;
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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set the result to CANCELED. This will cause the widget host to cancel
		// out of the widget placement if they press the back button.
		setResult(RESULT_CANCELED);

		setContentView(R.layout.and_showtime_widget_activity);

		controler = ControlerAndShowTimeWidget.getInstance();
		model = controler.getModelWidgetActivity();
		listener = new ListenerAndShowTimeWidget(this, controler, model);

		initViews();
		initListeners();

		model.setGpsLocalisation(LocationUtils.getLastLocation(AndShowTimeWidgetConfigureActivity.this));

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
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		removeListeners();
		controler.unbindService();
		controler.closeDB();
	}

	private void initViews() {
		bitmapGpsOn = BitmapFactory.decodeResource(getResources(), R.drawable.gps_activ);
		bitmapGpsOff = BitmapFactory.decodeResource(getResources(), R.drawable.gps_not_activ);

		txtCityName = (AutoCompleteTextView) findViewById(R.id.searchWidgetCityName);
		btnSearch = (Button) findViewById(R.id.searchWidgetBtnSearch);
		chkGps = (CheckBox) findViewById(R.id.searchWidgetLocation);
		listResult = (ListView) findViewById(R.id.searchWidgetListResult);
		gpsImgView = (ImageView) findViewById(R.id.searchWidgetImgGps);

		gpsImgView.setImageBitmap(bitmapGpsOff);
		chkGps.setChecked(false);
		chkGps.setEnabled(LocationUtils.isGPSEnabled(AndShowTimeWidgetConfigureActivity.this));
	}

	private void initListeners() {
		btnSearch.setOnClickListener(listener);
		listResult.setOnItemClickListener(listener);

		chkGps.setOnClickListener(listener);
		LocationUtils.registerListener(AndShowTimeWidgetConfigureActivity.this, listener);
	}

	private void removeListeners() {
		LocationUtils.unRegisterListener(AndShowTimeWidgetConfigureActivity.this, listener);
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