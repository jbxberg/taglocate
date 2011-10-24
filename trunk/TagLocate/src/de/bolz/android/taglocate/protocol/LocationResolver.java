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
 */

package de.bolz.android.taglocate.protocol;

import java.text.ParseException;
import com.google.inject.Inject;

import de.berlin.magun.protocol.IIIUri;
import de.bolz.android.taglocate.geom.Coordinates;
import android.app.Application;
import android.view.Gravity;
import android.widget.Toast;

/**
 * This class provides functionality to resolve intents resulting from NFC or QR read events and 
 * to extract location information out of their contents.
 * @author Johannes Bolz
 */
public class LocationResolver {
	
	/*
	 * SCENARIOS:
	 * A) URI:
	 * 	1. Geo-URI
	 * 	2. III-URI
	 * B) UID
	 * C) DATA
	 * 	1. geometry file
	 * 	2. link file
	 */
	
	
	// RESOLUTION STRATEGY KEYS:
	/**
	 * The NFC tag's unique ID will be matched against the ID reference table
	 * first. If the ID isn't associated with a location, the tag's content will
	 * be checked for location information. In case of a QR tag, the tag's content
	 * will first be looked up in the reference table (i.e. treated as an ID) and
	 * then further evaluated.
	 */
	public static final String UID_FIRST = "uid_f";
	
	/**
	 * The NFC tag's unique ID will only be matched against the ID reference table.
	 * A QR tag's content will be treated as an ID.
	 */
	public static final String UID_ONLY = "uid_o";
	
	/**
	 * The NFC tag will be checked for a location link first. If it doesn't contain
	 * an explicit location link, it's unique ID will be matched against the reference
	 * table.
	 * A QR code will be first checked for a link and then be treated as an ID.
	 */
	public static final String URI_FIRST = "uri_f";
	
	/**
	 * The NFC tag or QR code will only be checked for a location link.
	 */
	public static final String URI_ONLY = "uri_o";

	private String geoUriStr;
	private String iiiUriStr;
	private Application context;
	
	private Coordinates location;
	
	private String id;
	
	private IdResolver idResolver;
	
	@Inject
	public LocationResolver(Application context, IdResolver idResolver) {
		this.context = context;
		this.idResolver = idResolver;
	}
	
	public Coordinates resolve(String data, String id) {
		if (data != null) {
			this.id = data; // in case there is no ID specified, handle data as ID
			// geo-URI
			if (data.startsWith("geo")) {
				geoUriStr = data;
			}
			// iii-URI
			if (data.startsWith("iii")) {
				iiiUriStr = data;
			}		
		}
		if (id != null) {
			this.id = id; // if there id a specific ID, use it
		} 
		resolveData(SettingsSingleton.getInstance().getStrategy());
		return this.location;
	}
	
	public Coordinates resolve(String data) {
		return resolve(data, null);
	}
	
	/**
	 * Extracts location information from the intent according to the specified 
	 * link resolution strategy.
	 * @param strategy the link resolution strategy.
	 */
	private void resolveData(String strategy) {
		
		if (strategy.equals(URI_ONLY)) {
			// Only resolve URI, if available:
			resolveUri();
			if (this.location != null) {
				showUriToast();
			}
		} else if (strategy.equals(UID_ONLY)) {
			// Only match ID:
			resolveUid();
			if (this.location != null) {
				showUidToast();
			}
		} else if (strategy.equals(URI_FIRST)) {
			// Try to resolve URI first. If no location can be extracted,
			// try ID matching:
			resolveUri();
			if (this.location != null) {
				showUriToast();
			} else {
				resolveUid();
				if (this.location != null) {
					showUidToast();
				}
			}
		} else if (strategy.equals(UID_FIRST)) {
			// Try to match ID first. If no location can be deducted, try
			// looking for an explicitly linked location:
			resolveUid();
			if (this.location != null) {
				showUidToast();
			} else {
				resolveUri();
				if (this.location != null) {
					showUriToast();
				}
			}
		} 
	}
	
	/**
	 * Determines whether a URI is a Geo-URI or a iii-URI and dispatches it
	 * to the according URI resolution method.
	 */
	private void resolveUri() {
		if (this.geoUriStr != null) {
			resolveGeoUri(this.geoUriStr);
		} 
		if (this.iiiUriStr != null) {
			resolveIIIUri(this.iiiUriStr);
		}
	}
	
	/**
	 * Creates a {@link Coordinates} object from a Geo-URI
	 * @param uri Geo-URI
	 */
	private void resolveGeoUri(String uri) {
		GeoUri geoUri = new GeoUri(uri);
		this.location = geoUri.getCoordinate();
	}
	
	/**
	 * Creates a {@link Coordinates} object from a iii-URI, if it's target
	 * element is a Geo-URI.
	 * @param uri iii-URI
	 */
	private void resolveIIIUri(String uri) {
		IIIUri iiiUri = null;
		try {
			iiiUri = new IIIUri(uri);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (iiiUri != null) {
			if (iiiUri.getTarget().getSchema().equals("geo")) {
				resolveGeoUri(iiiUri.getTarget().getUrl());
			}
		}
	}
	
	/**
	 * Matches a NFC tag's ID or a Qr code's content against the reference model
	 * stored in the IIR reference file. 
	 */
	private void resolveUid() {
		this.location = idResolver.resolve(id);
	}
	
	
	/**
	 * Shows a Toast saying that a valid location link was found on the tag.
	 */
	private void showUriToast() {
		Toast t = Toast.makeText(context, "link", Toast.LENGTH_SHORT);
		t.setGravity(Gravity.TOP, 0, 0);
		t.show();
	}
	
	/**
	 * Shows a toast saying that a location was found by matching an ID against
	 * the reference table.
	 */
	private void showUidToast() {
		Toast t = Toast.makeText(context, "UID: " + this.id.toUpperCase(), Toast.LENGTH_SHORT);
		t.setGravity(Gravity.TOP, 0, 0);
		t.show();
	}
	
}
