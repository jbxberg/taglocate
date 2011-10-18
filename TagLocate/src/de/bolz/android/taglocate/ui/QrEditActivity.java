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

import de.bolz.android.taglocate.R;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Activity that handles referencing QR codes.
 * @author Johannes Bolz
 *
 */
public class QrEditActivity extends TagEditActivity {
	
	/**
	 * Standard onCreate method. Calls the 'Barcode Scanner' app
	 * to get QR contents.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Check availability of 'Barcode Scanner' (if it is installed, no
		// exception will be thrown:
		PackageManager pm = getPackageManager();
		try {
			pm.getPackageInfo(getString(R.string.qr_reader_package),
					PackageManager.GET_ACTIVITIES);
			
			// Start 'Barcode Scanner':
			Intent intent = new Intent(getString(R.string.qr_scan_action));
			intent.setPackage(getString(R.string.qr_reader_package));
			intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			startActivityForResult(intent, 0);
		
		// Handle unavailability of 'Barcode Scanner':
		} catch (PackageManager.NameNotFoundException e) {
			Toast.makeText(getApplicationContext(),
					getString(R.string.message_bc_unavailable),
					Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * Handles results from Barcode Scanner in this activity, i.e. reference
	 * a scanned QR code.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String id = intent.getStringExtra("SCAN_RESULT");
				referenceId(id, QR);
				showToast(getString(R.string.reference_done) + " " + id, Toast.LENGTH_SHORT);
				this.finish();
			} else if (resultCode == RESULT_CANCELED) {
				this.finish();
			}
		}
	}
	
}
