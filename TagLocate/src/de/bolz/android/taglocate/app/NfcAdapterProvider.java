package de.bolz.android.taglocate.app;

import android.app.Application;
import android.nfc.NfcAdapter;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class NfcAdapterProvider implements Provider<NfcAdapter> {

    private Application application;
    
    @Inject
    public NfcAdapterProvider(Application application) {
    	this.application = application;
    }
    
    @Override
    public NfcAdapter get() {
        return NfcAdapter.getDefaultAdapter(application);
    }


}
