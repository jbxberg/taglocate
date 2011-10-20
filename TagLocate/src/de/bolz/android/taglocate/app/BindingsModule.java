package de.bolz.android.taglocate.app;

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
	}

}
