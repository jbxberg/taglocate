package de.bolz.android.taglocate.app;

import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcV;

import com.google.inject.Provider;

public class TechListsProvider implements Provider<String[][]>{

    @Override
    public String[][] get() {
    	
    	// Setup tech list filters for NfcA (ISO 14443) and NfcV (ISO 15693) tags
        String[][] techLists = new String[4][1];
		techLists[0][0] = NfcA.class.getName();
		techLists[1][0] = NfcV.class.getName();
		techLists[2][0] = Ndef.class.getName();
		techLists[3][0] = NdefFormatable.class.getName();
		return techLists;
    }
}
