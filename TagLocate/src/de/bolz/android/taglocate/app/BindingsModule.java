package de.bolz.android.taglocate.app;

import de.bolz.android.taglocate.app.annotation.NfcFilters;
import de.bolz.android.taglocate.app.annotation.NfcSupport;
import de.bolz.android.taglocate.app.annotation.TechLists;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import roboguice.config.AbstractAndroidModule;
import roboguice.inject.SharedPreferencesName;

public class BindingsModule extends AbstractAndroidModule {

	@Override
	protected void configure() {
		
		// workaround for http://groups.google.com/group/roboguice/browse_thread/thread/447c22b833b4992b/9f8428348c77bfba
		bind(String.class).annotatedWith(SharedPreferencesName.class)
			.toProvider(PreferencesNameProvider.class);
		
		bind(NfcAdapter.class).toProvider(NfcAdapterProvider.class);
		bind(String[][].class).annotatedWith(TechLists.class)
			.toProvider(TechListsProvider.class);
		bind(IntentFilter[].class).annotatedWith(NfcFilters.class)
			.toProvider(NfcIntentFiltersProvider.class);
		bind(boolean.class).annotatedWith(NfcSupport.class)
			.toProvider(NfcSupportedProvider.class);
	}

}
