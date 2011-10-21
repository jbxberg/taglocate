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
 *  Creating a custom location provider, discussion on the Stack Overflow forum:
 *  http://stackoverflow.com/questions/2531317/android-mock-location-on-device
 *
 *
 */

package de.bolz.android.taglocate.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import roboguice.activity.RoboMapActivity;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.inject.Inject;

import de.bolz.android.taglocate.R;
import de.bolz.android.taglocate.app.annotation.NfcFilters;
import de.bolz.android.taglocate.app.annotation.NfcSupport;
import de.bolz.android.taglocate.app.annotation.TechLists;
import de.bolz.android.taglocate.protocol.IntentResolver;
import de.bolz.android.taglocate.protocol.SettingsSingleton;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * This is the core activity of the application. It renders simple indoor room
 * geometries on top of an aerial (Google) map. It creates a custom {@link LocationProvider} that contains
 * the location of the last read marker. That {@link LocationProvider} may also be used by other apps by 
 * calling the {@link LocationProvider} "taglocation". In addition, the current location
 * is displayed on the map. <br><br>
 * For reading NFC tags containing location information, the NFC Foreground dispatch (=all intents matching the
 * specified intent-filters will be delivered to this activity) is enabled. It will
 * read NfcA (ISO 14443-3A), NfcV (ISO 15693) and all NDEF tags containing adequate data. All NFC functionality will be disabled on
 * devices without NFC support or running an Android version prior to 2.3.3. <br><br>
 * The QR reader capability uses the ZXing Barcode Scanner app. It will not work without said application
 * being installed. <br><br>
 * The application performs a check whether the 'Allow Mock Locations' setting is enabled, and will not work
 * if it isn't. 
 * @author Johannes Bolz
 *
 */
public class GMapActivity extends RoboMapActivity {

	// custom LocationProvider ID:
	@InjectResource(R.string.tag_location_provider) protected String tagLocation; 
	
	// default configuration values:
	@InjectResource(R.string.default_link_file) protected String defaultLinkFile;
	@InjectResource(R.string.datapath) protected String dataPath;
	@InjectResource(R.string.default_geometry_file) protected String defaultGeometryFile;
	@InjectResource(R.string.default_zoom) protected String defaultZoom;
	@InjectResource(R.string.default_lat) protected String defaultLat;
	@InjectResource(R.string.default_lon) protected String defaultLon;

	// private objects:
	@InjectView(R.id.mapview) private MapView mapView;
	@InjectView(R.id.btnLayout) private LinearLayout btnLayout;
	@Inject private LocationMarkerOverlay locOverlay;
	@Inject private EditModeOverlay editOverlay;
	@Inject private LocationManager locationManager;
	@Inject SharedPreferences prefs;
	@Inject NfcAdapter nfcAdapter;
	@Inject @NfcFilters private IntentFilter[] filters;
	@Inject @TechLists private String[][] techLists;
	@Inject @NfcSupport boolean nfcSupported;
	
	private List<Overlay> mapOverlays;
	private MapController mapController;
	private PendingIntent pi;
	private LocationListener listener;
	private boolean editMode = false;
	private double lon = 0;
	private double lat = 0;
	private int zoom = 0;

	/**
	 * Standard onCreate method.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Check if the 'Allow Mock Location' system setting is enabled.
		// If not, -display an info dialog and close the application.
		if (!mockLocationEnabled()) {
			showMLDialog();
		} else {

			// Get preferences from saved application state or apply default values:
			this.lon = Double.parseDouble(prefs.getString(getString(R.string.lon_preference),
					defaultLon));
			this.lat = Double.parseDouble(prefs.getString(getString(R.string.lat_preference),
					defaultLat));
			this.zoom = Integer.parseInt(prefs.getString(getString(R.string.zoom_preference),
					defaultZoom));
			
			this.editMode = prefs.getBoolean(getString(R.string.edit_mode_preference), this.editMode);

			SettingsSingleton.getInstance().setDatafile(
					prefs.getString(getString(R.string.link_file_preference),
							defaultLinkFile));
			SettingsSingleton.getInstance().setDatapath(
					Environment.getExternalStorageDirectory().getName()
							+ dataPath);
			SettingsSingleton.getInstance().setGeometryfile(
					prefs.getString(
							getString(R.string.geometry_file_preference),
							defaultGeometryFile));
			SettingsSingleton.getInstance().setDownloadFromNfc(
					prefs.getBoolean(getString(R.string.loadfiles_preference),
							true));
			SettingsSingleton.getInstance().setStrategy(
					prefs.getString(getString(R.string.strategy_preference),
							IntentResolver.UID_FIRST));

			// Check if link and geometry files are available within the
			// application folder on the sdcard file system. If not, create default
			// ones:
			checkForFiles();

			// setup map view:
			setContentView(R.layout.main);
			mapView.setBuiltInZoomControls(true);
			mapView.setSatellite(true);
			mapOverlays = mapView.getOverlays();
			mapOverlays.add(new VectorOverlay());
			locOverlay.setCoords(this.lon, this.lat);
			mapOverlays.add(locOverlay);
			mapController = mapView.getController();
			mapController.setZoom(Integer.valueOf(zoom));
			
			if (editMode) {
				startEditMode();
			} else {
				stopEditMode();
			}
			

			// Create a new LocationProvider for tag locations and an according listener
			// requesting location updates from it:
			listener = new TagLocationListener();
			createLocationProvider();
			locationManager.requestLocationUpdates(tagLocation, 0, 0, listener);
			
			// Update LocationProvider to last tag location:
			updateLocation(this.lon, this.lat);

			// Check for NFC support on the device. If NFC is supported,
			// create appropriate intent-filters:
			if (nfcSupported) {
				pi = PendingIntent.getActivity(this, 0, new Intent(this, getClass())
				.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
			}
		}
	}

	/**
	 * Standard onPause method.
	 */
	@Override
	public void onPause() {
		super.onPause();
		if (mockLocationEnabled()) {
			// Store persistent values into preferences:
			SharedPreferences.Editor ed = prefs.edit();
			ed.putString(getString(R.string.lon_preference), String.valueOf(this.lon));
			ed.putString(getString(R.string.lat_preference), String.valueOf(this.lat));
			ed.putString(getString(R.string.link_file_preference),
					SettingsSingleton.getInstance().getDatafile());
			ed.putString(getString(R.string.geometry_file_preference),
					SettingsSingleton.getInstance().getGeometryfile());
			ed.putString(getString(R.string.strategy_preference), String
					.valueOf(SettingsSingleton.getInstance().getStrategy()));
			ed.putBoolean(getString(R.string.loadfiles_preference),
					SettingsSingleton.getInstance().isDownloadFromNfc());
			ed.putString(getString(R.string.zoom_preference), String.valueOf(mapView.getZoomLevel()));
			ed.putBoolean(getString(R.string.edit_mode_preference), editMode);
			ed.commit();
			
			// Check NFC support, disable foreground intent dispatch:
			if (nfcSupported) {
				nfcAdapter.disableForegroundDispatch(
						this);
			}
		}
	}

	/**
	 * Standard onResume method.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		// Check for 'Mock Location' system setting:
		if (!mockLocationEnabled()) {
			showMLDialog();
		} else {
			// Check for NFC support, enable foreground intent dispatch:
			if (nfcSupported) {
				nfcAdapter.enableForegroundDispatch(this, pi, filters, techLists);
			}
		}
	}

	/**
	 * In this activity, this method handles NFC intents delivered by foreground dispatch.
	 */
	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// Make sure the activity is receiving location updates by the LocationProvider:
		locationManager.requestLocationUpdates(tagLocation, 0, 0, listener);
		
		// Resolve intent and update location:
		IntentResolver i = new IntentResolver(getApplicationContext(), intent);
		if (i.getLocation() != null) {
			updateLocation(i.getLocation().getLon(), i.getLocation().getLat());
		}
	}

	/** 
	 * Required MapActivity method.
	 */
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	/**
	 * Creates the options menu view.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map_menu, menu);
		return true;
	}

	/**
	 * Handles touch events on options menu items.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		int id = item.getItemId();
		
		// QR Locate:
		if (id == R.id.qr_menu_item) {
			
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
				return true;
			
			// Handle unavailability of 'Barcode Scanner':
			} catch (PackageManager.NameNotFoundException e) {
				Toast.makeText(getApplicationContext(),
						getString(R.string.message_bc_unavailable),
						Toast.LENGTH_LONG).show();
			}
	
		// Settings:
		} else if (id == R.id.settings_menu_item) {
			startActivity(new Intent(this, PreferencesActivity.class));
			
		// About / How to Use:
		} else if (id == R.id.help_menu_item) {
			startActivity(new Intent(this, HelpActivity.class));
		
		// Create Tag:
		} else if (id == R.id.reference_tag_item) {
			startEditMode();
		}
		
		
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Handles results from Barcode Scanner in this activity.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				
				// Make sure the activity is getting location updates:
				locationManager.requestLocationUpdates(tagLocation, 0, 0,
						listener);
				
				// Resolve resulting intent:
				IntentResolver i = new IntentResolver(getApplicationContext(),
						intent);
				if (i.getLocation() != null) {
					updateLocation(i.getLocation().getLon(), i.getLocation()
							.getLat());
				}
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel = do nothing.
			}
		}
	}

	/**
	 * Creates custom LocationProvider. 
	 */
	private void createLocationProvider() {

		// If LocationProvider already exists, remove it and renew it:
		if (locationManager.getProvider(tagLocation) != null) {
			locationManager.removeTestProvider(tagLocation);
		}
		locationManager.addTestProvider(tagLocation, "requiresNetwork" == "",
				"requiresSatellite" == "", "requiresCell" == "",
				"hasMonetaryCost" == "", "supportsAltitude" == "",
				"supportsSpeed" == "", "supportsBearing" == "",

				android.location.Criteria.POWER_LOW,
				android.location.Criteria.ACCURACY_FINE);
	}

	/**
	 * Updates the LocationProvider with a new location:
	 * @param lon geographic longitude
	 * @param lat geographic latitude
	 */
	private void updateLocation(double lon, double lat) {
		Location newLocation = new Location(tagLocation);

		newLocation.setLatitude(lat);
		newLocation.setLongitude(lon);

		locationManager.setTestProviderEnabled(tagLocation, true);

		locationManager.setTestProviderStatus(tagLocation,
				LocationProvider.AVAILABLE, null, System.currentTimeMillis());

		locationManager.setTestProviderLocation(tagLocation, newLocation);
	}

	/**
	 * Creates a dialog saying the mock location system setting is disabled.
	 */
	private void showMLDialog() {
		AlertDialog alert;
		AlertDialog.Builder builder = new AlertDialog.Builder(GMapActivity.this);
		builder.setTitle(getString(R.string.title_ml_disabled));
		builder.setMessage(getString(R.string.message_ml_disabled))
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						finish();
					}
				});
		alert = builder.create();
		alert.show();
	}

	/**
	 * Checks if the 'Mock Location' system setting is enabled.
	 * @return true if it is enabled.
	 */
	private boolean mockLocationEnabled() {
		return "1".equals(Settings.Secure.getString(getContentResolver(),
				Settings.Secure.ALLOW_MOCK_LOCATION));
	}
	

	/**
	 * Checks if there is a taglocate folder on the sdcard directory and whether
	 * it contains the currently used geometry and link files. If not, the default
	 * ones will be copied and applied.
	 */
	private void checkForFiles() {
		if (!fileExists(SettingsSingleton.getInstance().getDatapath()
				+ SettingsSingleton.getInstance().getDatafile())) {
			copyDefault(defaultLinkFile);
			SettingsSingleton.getInstance().setDatafile(defaultLinkFile);
		}
		if (!fileExists(SettingsSingleton.getInstance().getDatapath()
				+ SettingsSingleton.getInstance().getGeometryfile())) {
			copyDefault(defaultGeometryFile);
			SettingsSingleton.getInstance()
					.setGeometryfile(defaultGeometryFile);
		}
	}

	/**
	 * Checks if a specifies file exists.
	 * @param uri the file's full path name
	 * @return true if the file exists
	 */
	private boolean fileExists(String uri) {
		File f = new File(uri);
		return f.exists();
	}
	

	/**
	 * Copies default files to the taglocate sdcard folder.
	 * @param file
	 */
	private void copyDefault(String file) {
		File dir = new File(Environment.getExternalStorageDirectory().getName()
				+ dataPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		try {
			InputStream in = getAssets().open(file);
			OutputStream out = new FileOutputStream(new File(Environment
					.getExternalStorageDirectory().getName() + dataPath + file));

			byte[] buf = new byte[8192];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Starts the edit mode. Adds the {@link EditModeOverlay} to the map and displays 
	 * edit buttons.
	 */
	private void startEditMode() {
		this.editMode = true;
		mapOverlays.add(editOverlay);
		mapView.invalidate();
		btnLayout.setVisibility(LinearLayout.VISIBLE);
		Button cancelBtn = (Button) findViewById(R.id.cancelAddTagBtn);
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopEditMode();		
			}
		});
		Button addBtn = (Button) findViewById(R.id.addTagBtn);
		addBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Create GeoPoint out of view center pixel coordinates:
				GeoPoint gp = mapView.getProjection().fromPixels((int) Math.round(((double)mapView.getWidth()) / 2),
						(int) Math.round(((double)mapView.getHeight()) / 2));
				
				Intent i = new Intent(GMapActivity.this, EditDispatchActivity.class);
				i.putExtra(getString(R.string.center_lon), gp.getLongitudeE6());
				i.putExtra(getString(R.string.center_lat), gp.getLatitudeE6());
				startActivity(i);
				stopEditMode();
			}
		});
	}
	
	/**
	 * Stops the edit mode. Removes edit buttons and the {@link EditModeOverlay}.
	 */
	private void stopEditMode() {
		this.editMode = false;
		if (mapOverlays.contains(editOverlay)) {
			mapOverlays.remove(mapOverlays.indexOf(editOverlay));
		}
		mapView.invalidate();
		btnLayout.setVisibility(LinearLayout.INVISIBLE);
	}


	/**
	 * Implementation of a LocationListener that updated the map to changes in the current
	 * location.
	 * @author Johannes Bolz
	 */
	private final class TagLocationListener implements LocationListener {
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onLocationChanged(Location location) {
			// Get new location, set marker, center map on new location:
			GMapActivity.this.lon = location.getLongitude();
			GMapActivity.this.lat = location.getLatitude();
			GMapActivity.this.locOverlay.setCoords(location.getLongitude(),
					location.getLatitude());
			GMapActivity.this.mapView.invalidate();
			MapController mapController = GMapActivity.this.mapView
					.getController();
			mapController.animateTo(new GeoPoint((int) Math
					.round(GMapActivity.this.lat * 1000000), (int) Math
					.round(GMapActivity.this.lon * 1000000)));
		}
	}
}
