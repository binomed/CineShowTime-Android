package com.binomed.showtime.android.widget.one;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.view.TheaterFavMainListAdapter;
import com.binomed.showtime.android.util.AndShowTimeLayoutUtils;
import com.binomed.showtime.android.util.localisation.IListenerLocalisationUtilCallBack;
import com.binomed.showtime.beans.TheaterBean;

public class AndShowTimeWidgetConfigureActivity extends Activity {

	private static final int MENU_PREF = Menu.FIRST;

	public static final Integer ACTIVITY_OPEN_RESULTS = 0;

	private static final String TAG = "SearchWidgetActivity"; //$NON-NLS-1$

	protected AutoCompleteTextView fieldCityName;
	protected Button searchButton;
	protected ImageView gpsImgView;
	private ListView theaterFavList;
	protected TheaterFavMainListAdapter adapter;

	private ControlerAndShowTimeWidget controler;
	private ListenerAndShowTimeWidget listener;
	private ModelAndShowTimeWidget model;

	protected IListenerLocalisationUtilCallBack localisationCallBack;

	private SharedPreferences prefs;

	protected EditText getFieldName() {
		return fieldCityName;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		AndShowTimeLayoutUtils.onActivityCreateSetTheme(this, prefs);
		Log.i(TAG, "onCreate"); //$NON-NLS-1$
		setContentView(R.layout.activity_widget_one_search);

		controler = ControlerAndShowTimeWidget.getInstance();
		model = controler.getModelWidgetActivity();
		listener = new ListenerAndShowTimeWidget(this, controler, model);

		initViews();
		controler.registerView(this);

		display();
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
		controler.closeDB();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (localisationCallBack != null) {
			localisationCallBack.onPause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume"); //$NON-NLS-1$
		initListeners();
		initViewsState();

		if (localisationCallBack != null) {
			localisationCallBack.onResume();
		}
	}

	/**
	 * init the view of activity
	 */
	private void initViews() {

		gpsImgView = (ImageView) findViewById(R.id.searchWidgetImgGps);
		searchButton = (Button) findViewById(R.id.searchWidgetBtnSearch);
		fieldCityName = (AutoCompleteTextView) findViewById(R.id.searchWidgetCityName);
		theaterFavList = (ListView) findViewById(R.id.searchWidgetFavList);

		// manageCallBack
		localisationCallBack = AndShowTimeLayoutUtils.manageLocationManagement(this, gpsImgView, fieldCityName, model);
	}

	private void initViewsState() {

	}

	private void initListeners() {
		searchButton.setOnClickListener(listener);
		theaterFavList.setOnItemClickListener(listener);

	}

	protected void display() {
		List<TheaterBean> favList = controler.getFavTheater();

		if ((favList == null) || (favList.size() == 0)) {
			favList = new ArrayList<TheaterBean>();
			TheaterBean thTmp = new TheaterBean();
			thTmp.setId("0");
			thTmp.setTheaterName(getResources().getString(R.string.msgNoDFav));

			favList.add(thTmp);
		}

		model.setFavList(favList);

		adapter = new TheaterFavMainListAdapter(this, favList, listener);

		this.theaterFavList.setAdapter(adapter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_OK:
			setResult(RESULT_OK, data);
			finish();
			break;

		default:
			break;
		}
	}

}