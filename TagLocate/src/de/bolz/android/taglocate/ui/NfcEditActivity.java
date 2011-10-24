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
 *  NFC foreground dispatch, Android API Demos: 
 *  http://developer.android.com/resources/samples/ApiDemos/src/com/example/android/apis/nfc/ForegroundDispatch.html
 *  
 */

package de.bolz.android.taglocate.ui;

import java.io.IOException;

import com.google.inject.Inject;

import de.berlin.magun.nfcmime.core.NdefMessageBuilder;
import de.berlin.magun.nfcmime.core.RfidDAO;
import de.bolz.android.taglocate.R;
import de.bolz.android.taglocate.app.annotation.NfcFilters;
import de.bolz.android.taglocate.app.annotation.TechLists;
import de.bolz.android.taglocate.protocol.GeoUri;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

/**
 * Activity that handles referencing NFC tags. May either write an explicit link to an NFC tag 
 * or reference the tag's unique ID in the reference model (iir file).
 * @author Johannes Bolz
 */
public class NfcEditActivity extends TagEditActivity{
	
	// NFC handling mode keys:
	public static final int WRITE = 0;
	public static final int READ = 1;
	private static final int NOTSET = -1;
		
	private PendingIntent pi;
	private Tag tag;
	private int mode;
	private AlertDialog infoDialog;
	@Inject @NfcFilters private IntentFilter[] filters;
	@Inject @TechLists private String[][] techLists;
	@Inject private NfcAdapter nfcAdapter;
	@Inject private NdefMessageBuilder msgBuilder;
	@Inject private RfidDAO dao;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
      
        this.mode = i.getIntExtra(getString(R.string.nfc_edit_mode), NOTSET);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.initial_nfc_dialog));
		
		// Finish activity when back button is pressed:
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				NfcEditActivity.this.finish();
			}
		});
		infoDialog = builder.create();
		infoDialog.show();
		pi = PendingIntent.getActivity(this, 0, new Intent(this, getClass())
			.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }
    
    /**
     * Standard onPause method. Deactivates NFC foreground dispatch.
     */
    @Override
    public void onPause() {
    	super.onPause();
    	nfcAdapter.disableForegroundDispatch(this);
    }
    
    /**
     * Standard onResume method. Activates NFC foreground dispatch.
     */
    @Override
    public void onResume() {
    	super.onResume();
		nfcAdapter.enableForegroundDispatch(this, pi, filters, techLists);
    }
    
    /**
	 * In this activity, this method handles NFC intents delivered by foreground dispatch.
	 */
	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		this.tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		// Write link on tag:
		if (mode == WRITE) {
			writeTag();
			
		// Reference NFC ID:
		} else if (mode == READ) {
			referenceTag();
		}
	}
    
	/**
	 * Writes a Geo-URI to the NFC tag.
	 */
    private void writeTag() {
		
		// Build Geo-URI, recalculate coordinate int values to decimal degrees: 
		GeoUri uri = new GeoUri(((double) this.latInt) / 1000000,
				((double) this.lonInt) / 1000000);
		try {
			msgBuilder.addUriRecord(uri.getString());

			// Format tag to NDEF, if not already done:
			if (NdefFormatable.get(tag) != null) {
				dao.formatNdef(tag);
				showToast(getString(R.string.format_msg), Toast.LENGTH_LONG);
			}

			// Write tag, if it is NDEF-compliant:
			if (Ndef.get(tag) != null
					&& msgBuilder.getDataSize() <= Ndef.get(tag).getMaxSize()) {
				dao.writeMessage(tag, msgBuilder.buildMessage());
				showToast(getString(R.string.done), Toast.LENGTH_SHORT);
				infoDialog.dismiss();
				this.finish();
				
			// Otherwise state that there is a problem:
			} else {
				showToast(getString(R.string.write_fail), Toast.LENGTH_SHORT);
			}
		} catch (IOException e) {
			e.printStackTrace();
			infoDialog.dismiss();
			this.finish();
		} catch (Exception e) {
			// This exception will most likely happen, if tag is read-only:
			e.printStackTrace();
			showToast(getString(R.string.write_fail), Toast.LENGTH_SHORT);
		}
    	
    }
    
    /**
     * References a NFC ID in the reference model.
     */
    private void referenceTag() {
    	String id = dao.getTagId(tag);
    	referenceId(id, NFC);	
    	showToast(getString(R.string.reference_done) + " " + id, Toast.LENGTH_SHORT);
    	infoDialog.dismiss();
    	this.finish();
    }
    
}
