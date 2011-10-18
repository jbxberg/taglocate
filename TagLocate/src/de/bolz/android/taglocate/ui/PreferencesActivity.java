/*
 *    Copyright 2011 by Johannes Bolz and the MAGUN project
 *    johannes-bolz (at) gmx.net
 *    http://magun.beuth-hochschule.de
 *   
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 *  ## CREDITS: Some parts of this class were developed upon code examples  ##
 *  ## and / or snippets from the following sources:                        ##
 *  
 *  PreferenceActivity, Android Developers class description:
 *  http://developer.android.com/reference/android/preference/PreferenceActivity.html
 */

package de.bolz.android.taglocate.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.bolz.android.taglocate.R;
import de.bolz.android.taglocate.protocol.SettingsSingleton;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

/**
 * This activity provides a user interface to change application settings.
 * 
 * @author Johannes Bolz
 */
public class PreferencesActivity extends PreferenceActivity {
	private CharSequence[] osmCs;
	private CharSequence[] iirCs;
	private ListPreference geometryFilePreference, linkFilePreference,
			strategyPreference;
	private CheckBoxPreference loadFromNfc;

	/**
	 * Standard onCreate method.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		PreferenceManager.setDefaultValues(getApplicationContext(),
				R.xml.preferences, false);

		// Create checkbox for the 'Load Files from NFC' setting:
		loadFromNfc = (CheckBoxPreference) findPreference(getText(R.string.loadfiles_preference));
		if (!hasNfcSupport()) {
			loadFromNfc
					.setSummary(getString(R.string.message_nfc_not_supported));
			loadFromNfc.setChecked(false);
			loadFromNfc.setEnabled(false);
		}
		loadFromNfc
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						loadFromNfc.setChecked((Boolean) newValue);
						SettingsSingleton.getInstance().setDownloadFromNfc(
								(Boolean) newValue);
						return false;
					}
				});

		// Load file names into arrays:
		getFileArrays();

		// Create radio button chooser menu for geometry files:
		geometryFilePreference = (ListPreference) findPreference(getText(R.string.geometry_file_preference));
		geometryFilePreference.setEntries(osmCs);
		geometryFilePreference.setEntryValues(osmCs);
		geometryFilePreference.setSummary(geometryFilePreference.getEntry());
		geometryFilePreference
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						geometryFilePreference.setValue((String) newValue);
						SettingsSingleton.getInstance().setGeometryfile(
								(String) newValue);
						geometryFilePreference
								.setSummary(geometryFilePreference.getEntry());
						return false;
					}
				});

		// Create radio button chooser menu for link files:
		linkFilePreference = (ListPreference) findPreference(getText(R.string.link_file_preference));
		linkFilePreference.setEntries(iirCs);
		linkFilePreference.setEntryValues(iirCs);
		linkFilePreference.setSummary(linkFilePreference.getEntry());
		linkFilePreference
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						linkFilePreference.setValue((String) newValue);
						SettingsSingleton.getInstance().setDatafile(
								(String) newValue);
						linkFilePreference.setSummary(linkFilePreference
								.getEntry());
						return false;
					}
				});

		// Create radio button chooser menu for link resolution strategies:
		strategyPreference = (ListPreference) findPreference(getText(R.string.strategy_preference));
		strategyPreference.setSummary(strategyPreference.getEntry());
		strategyPreference
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						strategyPreference.setValue((String) newValue);
						SettingsSingleton.getInstance().setStrategy(
								(String) newValue);
						strategyPreference.setSummary(strategyPreference
								.getEntry());
						return false;
					}
				});

		// remove preferences that shall not appear in the view:
		PreferenceCategory category = (PreferenceCategory) findPreference(getString(R.string.hidden_prefs));
		PreferenceScreen screen = getPreferenceScreen();
		screen.removePreference(category);

	}

	/**
	 * Builds an array with all geometry file names and one for all link file names within
	 * the taglocate sdcard folder.
	 */
	private void getFileArrays() {
		File dir = new File(Environment.getExternalStorageDirectory().getName()
				+ GMapActivity.DATAPATH);
		String[] files = dir.list();
		List<CharSequence> osm = new ArrayList<CharSequence>();
		List<CharSequence> iir = new ArrayList<CharSequence>();
		for (int i = 0; i < files.length; i++) {
			if (files[i].endsWith(".osm")) {
				osm.add(files[i]);
			}
			if (files[i].endsWith("iir")) {
				iir.add(files[i]);
			}
		}

		osmCs = new CharSequence[osm.size()];
		for (int i = 0; i < osmCs.length; i++) {
			osmCs[i] = osm.get(i);
		}
		iirCs = new CharSequence[iir.size()];
		for (int i = 0; i < iirCs.length; i++) {
			iirCs[i] = iir.get(i);
		}
	}

	/**
	 * Checks if there is NFC support on the device and the Android versio is at least 2.3.3.
	 * @return true if both conditions apply.
	 */
	private boolean hasNfcSupport() {
		return (Build.VERSION.SDK_INT >= 10 && NfcAdapter
				.getDefaultAdapter(getApplicationContext()) != null);
	}
}
