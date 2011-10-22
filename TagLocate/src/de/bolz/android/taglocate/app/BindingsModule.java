package de.bolz.android.taglocate.app;

import com.google.inject.Provides;

import de.bolz.android.taglocate.app.annotation.NfcFilters;
import de.bolz.android.taglocate.app.annotation.NfcSupport;
import de.bolz.android.taglocate.app.annotation.TechLists;
import android.app.Application;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcV;
import android.os.Build;
import roboguice.config.AbstractAndroidModule;
import roboguice.inject.SharedPreferencesName;

public class BindingsModule extends AbstractAndroidModule {

	@Override
	protected void configure() {
		
		// workaround for http://groups.google.com/group/roboguice/browse_thread/thread/447c22b833b4992b/9f8428348c77bfba
//		bind(String.class).annotatedWith(SharedPreferencesName.class)
//			.toProvider(PreferencesNameProvider.class);
		
//		bind(NfcAdapter.class).toProvider(NfcAdapterProvider.class);
//		bind(String[][].class).annotatedWith(TechLists.class)
//			.toProvider(TechListsProvider.class);
//		bind(IntentFilter[].class).annotatedWith(NfcFilters.class)
//			.toProvider(NfcIntentFiltersProvider.class);
//		bind(boolean.class).annotatedWith(NfcSupport.class)
//			.toProvider(NfcSupportedProvider.class);
	}
	
	// workaround for http://groups.google.com/group/roboguice/browse_thread/thread/447c22b833b4992b/9f8428348c77bfba
	@Provides @SharedPreferencesName
	String provideSharedPreferencesName(Application application) {
		return application.getPackageName() + "_preferences";
	}
	
	@Provides
	NfcAdapter provideNfcAdapter(Application application) {
		return NfcAdapter.getDefaultAdapter(application);
	}
	
	@Provides @NfcFilters
	IntentFilter[] provideNfcIntentfilters() {
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
	
	@Provides @TechLists
	String[][] provideNfcTechLists() {
		// Setup tech list filters for NfcA (ISO 14443) and NfcV (ISO 15693) tags
        String[][] techLists = new String[4][1];
		techLists[0][0] = NfcA.class.getName();
		techLists[1][0] = NfcV.class.getName();
		techLists[2][0] = Ndef.class.getName();
		techLists[3][0] = NdefFormatable.class.getName();
		return techLists;
	}
	
	@Provides @NfcSupport
	boolean provideNfcSupported(NfcAdapter adapter) {
		return (Build.VERSION.SDK_INT >= 10 && adapter != null);
	}

}
