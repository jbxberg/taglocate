package de.bolz.android.taglocate.app;

import android.content.IntentFilter;
import android.nfc.NfcAdapter;

import com.google.inject.Provider;

public class NfcIntentFiltersProvider implements Provider<IntentFilter[]> {

	@Override
	public IntentFilter[] get() {
		// Create intent-filters for geo and iii scheme:
		IntentFilter geoFilter = new IntentFilter(
				NfcAdapter.ACTION_NDEF_DISCOVERED);
		geoFilter.addDataScheme("geo");
		IntentFilter iiiFilter = new IntentFilter(
				NfcAdapter.ACTION_NDEF_DISCOVERED);
		iiiFilter.addDataScheme("iii");
		
		// Create intent-filters for all NDEF compatible tags and
		// NfcA and NfcV RFID tags in order to read their UID's:
		IntentFilter ndefFilter = new IntentFilter(
				NfcAdapter.ACTION_NDEF_DISCOVERED);
		IntentFilter techFilter = new IntentFilter(
				NfcAdapter.ACTION_TECH_DISCOVERED);

	
		return new IntentFilter[] { geoFilter, iiiFilter, ndefFilter,
				techFilter };
	}

}
