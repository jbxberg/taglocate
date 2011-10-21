package de.bolz.android.taglocate.app;

import android.nfc.NfcAdapter;
import android.os.Build;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class NfcSupportedProvider implements Provider<Boolean>{
	private NfcAdapter nfcAdapter;
	
	@Inject
	public NfcSupportedProvider(NfcAdapter nfcAdapter) {
		this.nfcAdapter = nfcAdapter;
	}

	@Override
	public Boolean get() {
		return (Build.VERSION.SDK_INT >= 10 && nfcAdapter != null);
	}

}
