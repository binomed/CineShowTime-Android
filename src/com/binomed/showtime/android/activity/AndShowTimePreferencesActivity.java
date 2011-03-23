package com.binomed.showtime.android.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

import com.binomed.showtime.R;
import com.binomed.showtime.android.util.localisation.LocationUtils;
import com.binomed.showtime.android.util.localisation.LocationUtils.ProviderEnum;

public class AndShowTimePreferencesActivity extends PreferenceActivity {

	private static final String TAG = "AndShowTimePreferencesActivity"; //$NON-NLS-1$

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.and_showtime_preferences);

		// We get the actual preferences in order to manage default value
		ListPreference listProvider = (ListPreference) findPreference(getResources().getString(R.string.preference_loc_key_localisation_provider));
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
			String[] entriesInitial = getResources().getStringArray(R.array.mode_localisation_code);
			String[] entryValuesInitial = getResources().getStringArray(R.array.mode_localisation);
			for (ProviderEnum providerTmp : entryList) {
				index = 0;
				for (String entrieTemp : entriesInitial) {
					if (entrieTemp.equals(providerTmp.getPreferencesCode())) {
						break;
					}
					index++;
				}
				if (index < entriesInitial.length) {
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
			for (String entrieTemp : entriesInitial) {
				if (entrieTemp.equals(provider.getPreferencesCode())) {
					break;
				}
				index++;
			}
			if (index < entriesInitial.length) {
				listProvider.setValue(entryValuesInitial[index]);
			}

			listProvider.setEntries(entries);
			listProvider.setEntryValues(entryValues);
		}
	}
}