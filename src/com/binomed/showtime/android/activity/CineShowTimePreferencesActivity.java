package com.binomed.showtime.android.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.binomed.showtime.R;
import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.util.localisation.LocationUtils;
import com.binomed.showtime.android.util.localisation.LocationUtils.ProviderEnum;

public class CineShowTimePreferencesActivity extends PreferenceActivity implements OnPreferenceChangeListener {

	private static final String TAG = "AndShowTimePreferencesActivity"; //$NON-NLS-1$

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			addPreferencesFromResource(R.xml.and_showtime_preferences);
		} catch (ClassCastException e) {
			// We manage change in code version 23
			Log.e(TAG, "Controled error : ", e);
			Editor editor = getPreferenceManager().getSharedPreferences().edit();
			editor.putString(getResources().getString(R.string.preference_gen_key_time_format), getResources().getString(R.string.preference_gen_default_time_format));
			editor.commit();
			addPreferencesFromResource(R.xml.and_showtime_preferences);
		}

		// We get the actual preferences in order to manage default value
		ListPreference listProvider = (ListPreference) findPreference(getResources().getString(R.string.preference_loc_key_localisation_provider));
		ListPreference listThemes = (ListPreference) findPreference(getResources().getString(R.string.preference_gen_key_theme));
		CheckBoxPreference checkProvider = (CheckBoxPreference) findPreference(getResources().getString(R.string.preference_loc_key_enable_localisation));

		ProviderEnum provider = LocationUtils.getProvider(getPreferenceManager().getSharedPreferences(), this);

		// We search all providers available
		List<ProviderEnum> entryList = new ArrayList<ProviderEnum>();
		CharSequence[] entries = null;
		CharSequence[] entryValues = null;

		for (ProviderEnum providerTmp : ProviderEnum.values()) {
			if (LocationUtils.isLocalisationEnabled(this, providerTmp)) {
				entryList.add(providerTmp);
			}
		}

		entries = new CharSequence[entryList.size()];
		entryValues = new CharSequence[entryList.size()];

		// We add to preference list only values for provider enable
		if (!entryList.isEmpty()) {
			int i = 0;
			int index = 0;
			String[] entriesInitial = getResources().getStringArray(R.array.mode_localisation);
			String[] entryValuesInitial = getResources().getStringArray(R.array.mode_localisation_code);
			for (ProviderEnum providerTmp : entryList) {
				index = 0;
				for (String entryValueTemp : entryValuesInitial) {
					if (entryValueTemp.equals(providerTmp.getPreferencesCode())) {
						break;
					}
					index++;
				}
				if (index < entryValuesInitial.length) {
					entries[i] = entriesInitial[index];
					entryValues[i] = entryValuesInitial[index];
				}
				i++;
			}

			// We manage default value
			if (!LocationUtils.isLocalisationEnabled(this, provider)) {
				provider = entryList.get(0);
			}
			index = 0;
			for (String entryValueTemp : entryValuesInitial) {
				if (entryValueTemp.equals(provider.getPreferencesCode())) {
					break;
				}
				index++;
			}
			if (index < entryValuesInitial.length) {
				listProvider.setValue(entriesInitial[index]);
			}

			// listProvider.setEntries(entries);
			// listProvider.setEntryValues(entryValues);
			listProvider.setEntries(entries);
			listProvider.setEntryValues(entries);

		}

		Editor editor = getPreferenceManager().getSharedPreferences().edit();

		editor.commit();

		listThemes.setOnPreferenceChangeListener(this);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {

		String value = (String) newValue;
		String darkValue = getResources().getString(R.string.preference_gen_default_theme);
		int theme = R.style.Theme_Dark_Night;
		if (!darkValue.equals(value)) {
			theme = R.style.Theme_Shine_the_lite;
			// getApplication().setTheme(R.style.Theme_Dark_Night);

		} else {
			// getApplication().setTheme(R.style.Theme_Shine_the_lite);
		}
		Intent data = new Intent();
		data.putExtra(ParamIntent.PREFERENCE_RESULT_THEME, true);
		setResult(CineShowtimeCst.RESULT_PREF_WITH_NEW_THEME, data);

		// AndShowTimePreferencesActivity.super.onCreate(savedInstanceState);

		return true;
	}
}