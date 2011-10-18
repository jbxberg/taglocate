/* 
 * This class was part of an attempt to use OpenLayers mobile together with PhoneGap as map
 * renderer. It is being preserved for documentation purposes. 
 */

//package de.bolz.android.taglocate.olviewer;

//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.Charset;
//import java.util.List;
//
//import org.apache.commons.io.IOUtils;
//import org.simpleframework.xml.Serializer;
//import org.simpleframework.xml.core.Persister;
//
//import com.phonegap.DroidGap;
//
//import de.berlin.magun.nfcmime.core.NdefMimeRecord;
//import de.berlin.magun.nfcmime.core.RfidDAO;
//import de.bolz.android.taglocate.geom.data.Osm;
//import de.bolz.android.taglocate.geom.OsmParser;
//import android.app.PendingIntent;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.SharedPreferences;
//import android.content.res.Resources.NotFoundException;
//import android.nfc.NfcAdapter;
//import android.nfc.Tag;
//import android.nfc.tech.Ndef;
//import android.os.Bundle;
//import android.util.Log;

//@Deprecated
//public class MapActivity extends DroidGap {
//	private NfcAdapter adapter;
//	private PendingIntent pi;
//	private IntentFilter[] filters;
//	private String[][] techLists;
//	private String x, y;
//	private final String X_KEY = "xValue";
//	private final String Y_KEY = "yValue";
//
//	
//    /** Called when the activity is first created. */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
//        this.x = prefs.getString(X_KEY, "0");
//        this.y = prefs.getString(Y_KEY, "0");
//        
//
//    	OsmParser p = new OsmParser(new File("/mnt/sdcard/test.osm"));
////    	comm.drawGeometries(p.getGeometries());
//        
//        super.loadUrl("file:///android_asset/www/map.html");
//        JSComm comm = new JSComm(this.callbackServer);
//        this.callbackServer.sendJavascript("reloadPage();");
//    	comm.clearMarkers();
//    	comm.drawMarker(this.x, this.y, "EPSG:4326");
//    	comm.zoomTo(this.x, this.y, "EPSG:4326", "16");
//    	
//    	comm.drawGeometries();
//    	
//        adapter = NfcAdapter.getDefaultAdapter(this);
//        pi = PendingIntent.getActivity(this, 0,
//                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
//        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
//        filters = new IntentFilter[] {filter,};
//        techLists = new String[1][1];
//        techLists[0][0] = Ndef.class.getName();
//
//        }
//    
//    @Override
//    public void onResume() {
//        super.onResume();
//        adapter.enableForegroundDispatch(this, pi, filters, techLists);
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//    	super.onNewIntent(intent);
//    	
//    	Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//    	RfidDAO dao = new RfidDAO();
//    	List<NdefMimeRecord> recordlist = dao.getCachedMimeRecords(tag);
//    	
//    	if (recordlist != null) {
//    		NdefMimeRecord coordsrec = recordlist.get(0);
//        	if (coordsrec.getMimeString().equals("test/coordinates")) {
//        		String coords = new String(coordsrec.getPayload(), Charset.forName("UTF-8"));
//        		this.x = coords.substring(0, coords.indexOf(","));
//        		this.y = coords.substring(coords.indexOf(",") + 1, coords.length());
//        		JSComm comm = new JSComm(this.callbackServer);
//            	comm.clearMarkers();
//            	comm.drawMarker(this.x, this.y, "EPSG:4326");
//            	comm.zoomTo(this.x, this.y, "EPSG:4326", "16");
//        	}
//    	}
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        
//        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
//        SharedPreferences.Editor ed = prefs.edit();
//        ed.putString(X_KEY, this.x);
//        ed.putString(Y_KEY, this.y);
//        ed.commit();
//        
//        adapter.disableForegroundDispatch(this);
//    }
//    
////    @Override
////    public boolean onCreateOptionsMenu(Menu menu) {
////        MenuInflater inflater = getMenuInflater();
////        inflater.inflate(R.menu.map_menu, menu);
////        return true;
////    }
////    
////    @Override
////    public boolean onOptionsItemSelected(MenuItem item) {
////        // Handle item selection
////        switch (item.getItemId()) {
////        case R.id.item1: {
////        	Intent intent = new Intent("com.google.zxing.client.android.SCAN");
////            intent.setPackage("com.google.zxing.client.android");
////            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
////            startActivityForResult(null, intent, 0);
////            return true;
////        }
////        default: {
////            return super.onOptionsItemSelected(item);
////        }
////        }
////    }
////    
////    @Override
////    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
////        if (requestCode == 0) {
////            if (resultCode == RESULT_OK) {
////                String contents = intent.getStringExtra("SCAN_RESULT");
////                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
////                // geo:52.520839,13.409413
////                if (contents != null && format != null) {
////                	if (contents.startsWith("geo:")) {
////                		this.y = contents.substring(4, contents.indexOf(","));
////                		this.x = contents.substring(contents.indexOf(",") + 1);
////                		JSComm comm = new JSComm(this.callbackServer);
////                    	comm.clearMarkers();
////                    	comm.drawMarker(this.x, this.y, "EPSG:4326");
////                    	comm.zoomTo(this.x, this.y, "EPSG:4326", "16");
////                	}
////                }
////            } else if (resultCode == RESULT_CANCELED) {
////                // Handle cancel
////            }
////        }
////    }
//}