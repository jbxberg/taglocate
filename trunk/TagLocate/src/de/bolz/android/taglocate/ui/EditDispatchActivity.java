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
 */

package de.bolz.android.taglocate.ui;

import roboguice.activity.RoboActivity;

import com.google.inject.Inject;

import de.bolz.android.taglocate.R;
import de.bolz.android.taglocate.app.annotation.NfcSupport;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.widget.Toast;

/**
 * This class is responsible for calling the right edit activities when referencing a new NFC Tag
 * or QR code.
 * @author Johannes Bolz
 *
 */
public class EditDispatchActivity extends RoboActivity {
	
	// Impossible coordinate value:
	public static final int COORDINATENOTSET = -2147483648;
	
	private Intent intent;
	private AlertDialog dialog;
	private int latInt;
	private int lonInt;
	@Inject @NfcSupport boolean nfcSupported;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.intent = getIntent();
        
        // Get integer coordinates:
        this.latInt = this.intent.getIntExtra(getString(R.string.center_lat), COORDINATENOTSET);
        this.lonInt = this.intent.getIntExtra(getString(R.string.center_lon), COORDINATENOTSET);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		showDialog();
		
    }
    

    /**
     * Creates a chooser dialog and starts edit activities according to
     * the item selected from the list: 'Write NFC tag', 'Reference NFC tag'
     * or 'Reference QR code'.
     */
	private void showDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(
				EditDispatchActivity.this);
		builder.setItems(R.array.edit_dialog_items,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
						// Write NFC tag:
						if (which == 0) {
							if(nfcSupported) {
								Intent i = new Intent(EditDispatchActivity.this, 
										NfcEditActivity.class);
								// Request write mode, pass coordinates:
								i.putExtra(getString(R.string.nfc_edit_mode), NfcEditActivity.WRITE);
								i.putExtra(getString(R.string.center_lon), 
										EditDispatchActivity.this.lonInt);
								i.putExtra(getString(R.string.center_lat),
										EditDispatchActivity.this.latInt);
								startActivity(i);
								EditDispatchActivity.this.kill();	
							} else {
								showNfcNotSupportedToast();
								EditDispatchActivity.this.showDialog();
							}
						}
						
						// Reference NFC ID:
						else if (which == 1) {
							if(nfcSupported) {
								Intent i = new Intent(EditDispatchActivity.this, 
										NfcEditActivity.class);
								// Request read mode, pass coordinates:
								i.putExtra(getString(R.string.nfc_edit_mode), NfcEditActivity.READ);
								i.putExtra(getString(R.string.center_lon), 
										EditDispatchActivity.this.lonInt);
								i.putExtra(getString(R.string.center_lat),
										EditDispatchActivity.this.latInt);
								startActivity(i);
								EditDispatchActivity.this.kill();	
							} else {
								showNfcNotSupportedToast();
								EditDispatchActivity.this.showDialog();
							}
						}
						
						// Reference QR code:
						else if (which == 2) {
							Intent i = new Intent(EditDispatchActivity.this, 
									QrEditActivity.class);
							// Pass coordinates:
							i.putExtra(getString(R.string.center_lon), 
									EditDispatchActivity.this.lonInt);
							i.putExtra(getString(R.string.center_lat),
									EditDispatchActivity.this.latInt);
							startActivity(i);
							EditDispatchActivity.this.kill();
						}

					}
				});
		
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				EditDispatchActivity.this.kill();
			}
		});
		
		dialog = builder.create();
		dialog.show();
	}
	
	

	/**
	 * Activity will be finished when pressing the back button.
	 */
	@Override
    public void onBackPressed() {
    	this.finish();
    	this.onDestroy();
    }
    
	/**
	 * Kills the activity.
	 */
    private void kill() {
    	this.onPause();
    	this.finish();
    	this.onDestroy();
    }
	
	/**
	 * Shows a Toast stating the device doesn't support NFC.
	 */
	private void showNfcNotSupportedToast() {
		Toast t = Toast.makeText(getApplicationContext(), getString(R.string.message_nfc_not_supported),
				Toast.LENGTH_SHORT);
		t.setGravity(Gravity.TOP, 0, 0);
		t.show();
	}
   
}
