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

package de.bolz.android.taglocate.protocol;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.view.Gravity;
import android.widget.Toast;
import de.berlin.magun.nfcmime.core.NdefMimeRecord;
import de.berlin.magun.nfcmime.core.RfidDAO;
import de.berlin.magun.nfcmime.core.ZipFileSystem;
import de.bolz.android.taglocate.R;

/**
 * This class provides functionality to resolve intents resulting from a NFC read
 * process. It shall only be instantiated on devices supporting NFC and running Android
 * 2.3.3 or higher.
 * @author Johannes Bolz
 *
 */
public class NfcIntentResolver {
	private RfidDAO dao;
	private String id;
	private String scheme;
	private String geoUriStr;
	private String iiiUriStr;
	private ZipFileSystem zfs;
	private Context context;
	
	/**
	 * @param context the {@link Context} of the calling class
	 * @param intent the {@link Intent} to be examined. Must result either from a NFC
	 * read event.
	 */
	protected NfcIntentResolver(Context context, Intent intent) {
		this.context = context;
		parseNfcIntent(intent);
	}

	/**
	 * Tries to get location information from the NFC tag and checks the tag for
	 * contained files.
	 * @param intent the {@link Intent} to be examined. Must result either from a NFC
	 * read event.
	 */
	private void parseNfcIntent(Intent intent) {

		if(intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED) ||
				intent.getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED)) {
			
			// All NFC intents:
			Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			dao = new RfidDAO();
			
			// Read tag ID:
			id = dao.getTagId(tag);
			
			// Extract payload data (files):
			if (SettingsSingleton.getInstance().isDownloadFromNfc()) {
				getFilesFromNfc(tag);
			}
			
			// Try to get links from NDEF intents:
			if (intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
				scheme = intent.getScheme();
				if(scheme != null) {
					
					// geo-URI
					if (scheme.equals("geo")) {
						geoUriStr = intent.getDataString();
					}
					
					// iii-URI
					if (scheme.equals("iii")) {
						iiiUriStr = intent.getDataString();
					}
				}
			
			}
		}
	}
	
	/**
	 * Tries to extract geometry and link files from the NFC tag and to write them to the 
	 * local file system. <br>
	 * To store files, create a NDEF record containing a ZIP-compressed dataset
	 * containing the files, and write it to the tag specifying the MIME type 'application/zip'. <br>
	 * This function will also activate the loaded files in the application.
	 * @param tag the {@link Tag} containing a zipped file dataset
	 */
	private void getFilesFromNfc(Tag tag) {
		List<NdefMimeRecord> recordList = dao.getCachedMimeRecords(tag);
		if (recordList != null) {
			for (int i = 0; i < recordList.size(); i++) {
				if ("application/zip".equalsIgnoreCase(recordList.get(i).getMimeString())) {
					try {
						// Try to create a ZipFileSystem object out of the NDEF record:
						zfs = new ZipFileSystem(recordList.get(i));
						List<String> filenames = zfs.getFileNames();
						for (int j = 0; j < filenames.size(); j++) {
							String fname = filenames.get(j);
							// Write files to data directory:
							if (fname.endsWith(".iir") || fname.endsWith(".osm")) {
								zfs.write(SettingsSingleton.getInstance().getDatapath());
							}
							
							// Apply link file and show Toast:
							if (fname.endsWith("iir")) {
								SettingsSingleton.getInstance().setDatafile(fname);
								Toast t = Toast.makeText(context, context.getString(R.string.reffile_found), Toast.LENGTH_SHORT);
								t.setGravity(Gravity.TOP, 0, 0);
								t.show();
							}
							
							// Apply geometry file and show Toast:
							if (fname.endsWith("osm")) {
								SettingsSingleton.getInstance().setGeometryfile(fname);
								Toast t = Toast.makeText(context, context.getString(R.string.geomfile_found), Toast.LENGTH_SHORT);
								t.setGravity(Gravity.TOP, 0, 0);
								t.show();
							}	
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (FormatException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}

	/**
	 * @return the tag's ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the extracted URI's scheme
	 */
	public String getScheme() {
		return scheme;
	}

	/**
	 * @return the extracted Geo-URI as String
	 */
	public String getGeoUriStr() {
		return geoUriStr;
	}

	/**
	 * @return the extracted iii-URI as String
	 */
	public String getIiiUriStr() {
		return iiiUriStr;
	}
}
