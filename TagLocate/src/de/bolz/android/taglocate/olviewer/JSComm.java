/* 
 * This class was part of an attempt to use OpenLayers mobile together with PhoneGap as map
 * renderer. It is being preserved for documentation purposes. 
 */

//package de.bolz.android.taglocate.olviewer;

//import java.util.ArrayList;
//import java.util.List;
//
//import com.phonegap.CallbackServer;
//
//import de.bolz.android.taglocate.geom.Coordinates;
//import de.bolz.android.taglocate.geom.Geometry;

//@Deprecated
//public class JSComm {
//	private CallbackServer callbackServer;
//	
//	protected JSComm(CallbackServer cs) {
//		this.callbackServer = cs;
//	}
//	
//	protected void drawMarker(String lon, String lat, String srs) {
//		callbackServer.sendJavascript((new StringBuilder("drawMarker(").append(
//				lon).
//				append(", ").
//				append(lat).
//				append(", \"").
//				append(srs).
//				append("\");")).toString());
//	}
//	
//	protected void zoomTo(String lon, String lat, String srs, String zoomlevel) {
//		callbackServer.sendJavascript((new StringBuilder("zoomTo(").append(
//				lon).
//				append(", ").
//				append(lat).
//				append(", \"").
//				append(srs).
//				append("\", ").
//				append(zoomlevel).
//				append(");")).toString());
//	}
//	
//	protected void clearMarkers() {
//		callbackServer.sendJavascript("clearMarkers();");
//	}
//	
//	protected void drawGeometries() {	
////	protected void drawGeometries(ArrayList<Geometry> geometries) {	
////		for (int i = 0; i < geometries.size(); i++) {
////			StringBuilder sb = new StringBuilder();
////			sb.append("drawGeometry(\"");
////			List<Coordinates> coords = geometries.get(i).getPoints();
////			
////			for (int j = 0; j < coords.size(); j++) {
////				Coordinates c = coords.get(j);
////				sb.append(c.getLon()).append(",")
////					.append(c.getLat()).append(";");
////			}
////			sb.append("\");");
////			String s = sb.toString();
////			callbackServer.sendJavascript(sb.toString());
////		}
//		callbackServer.sendJavascript("drawGeometry(\"13.41925811021193,52.48896316877748;13.4192556151661,52.488937379946265;13.419165924755664,52.48894253890429;13.419168978011534,52.48896697372756;13.41925811021193,52.48896316877748\");");
//		
//		
//	}
	
	
//}
