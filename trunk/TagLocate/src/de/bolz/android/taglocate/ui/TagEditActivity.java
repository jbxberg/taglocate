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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.util.EncodingUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.CamelCaseStyle;
import org.simpleframework.xml.stream.Format;

import roboguice.activity.RoboActivity;

import de.berlin.magun.protocol.Source;
import de.bolz.android.taglocate.R;
import de.bolz.android.taglocate.protocol.GeoUri;
import de.bolz.android.taglocate.protocol.SettingsSingleton;
import de.bolz.android.taglocate.protocol.data.Reference;
import de.bolz.android.taglocate.protocol.data.ReferenceList;
import de.bolz.android.taglocate.protocol.data.Tag;
import de.bolz.android.taglocate.protocol.data.Target;
import de.bolz.android.taglocate.protocol.data.Trigger;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Abstract superclass to store common functionality for tag referencing activities.
 * @author Johannes Bolz
 *
 */
public abstract class TagEditActivity extends RoboActivity{
	
	public static final String NFC = "nfc";
	public static final String QR = "qr";
	
	protected int latInt;
	protected int lonInt;

	/**
	 * Standard onCreate method. Gets the latitude and longitude int's from the 
	 * Intent.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		this.latInt = i.getIntExtra(getString(R.string.center_lat), 
        		EditDispatchActivity.COORDINATENOTSET);
        this.lonInt = i.getIntExtra(getString(R.string.center_lon), 
        		EditDispatchActivity.COORDINATENOTSET);
	}
	
	/**
	 * Standard onPause method.
	 */
	@Override
	public void onPause() {
		super.onPause();
	}
	
	/**
	 * Standard onResume method.
	 */
	@Override
	public void onResume() {
		super.onResume();
	}
	
	/**
	 * Standard onNewIntent method.
	 */
	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}
	
	/**
	 * Adds an ID together with a location to the reference model.
	 * @param id the ID itself
	 * @param tagtype the type of tag, either 'qr' or 'nfc'.
	 */
	protected void referenceId(String id, String tagtype) {
		try {
			// Make sure file's content is decoded using UTF-8:
			InputStream is = new FileInputStream(new File(SettingsSingleton
					.getInstance().getDatapath()
					+ SettingsSingleton.getInstance().getDatafile()));
			byte[] ba = IOUtils.toByteArray(is);
			String input = EncodingUtils.getString(ba, "UTF-8");
			
			// Create a serializer that adheres to the XML's camel case element style:
			Serializer s = new Persister(new Format(new CamelCaseStyle()));
			ReferenceList linkList = s.read(ReferenceList.class, input);
			
			// Check if ID is already referenced:
			boolean isInDataSet = false;
			List<Reference> links = linkList.getReferences();
			for (int i = 0; i < links.size(); i++) {
				Reference link = links.get(i);
				if ((new Source(link.getTrigger().getTag().getTagStr())).getValue()
						.equalsIgnoreCase(id)) {
					String geoUri = new GeoUri(((double) this.latInt) / 1000000,
							((double) this.lonInt) / 1000000).getString();
					link.getTarget().setTargetStr(geoUri);
					isInDataSet = true;
					break;
				}
			}
			
			// Create new Reference element in reference model, if necessary:
			if (!isInDataSet) {
				Reference l = new Reference();
				l.setTarget(new Target());
				l.getTarget().setTargetStr(new GeoUri(((double) this.latInt) / 1000000,
						((double) this.lonInt) / 1000000).getString());
				l.setTrigger(new Trigger());
				l.getTrigger().setTag(new Tag());
				l.getTrigger().getTag().setTagStr(tagtype + ":" + id);
				links.add(l);
			}
			
			// Overwrite link file with updated reference:
			File result = new File(SettingsSingleton
					.getInstance().getDatapath()
					+ SettingsSingleton.getInstance().getDatafile());
			s.write(linkList, result);
	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Shows a Toast on top of the view.
	 * @param msg the message to display.
	 * @param length the Toast duration
	 */
	protected void showToast(String msg, int length) {
		Toast t = Toast.makeText(this, msg,
				length);
		t.setGravity(Gravity.TOP, 0, 0);
		t.show();
	}
}
