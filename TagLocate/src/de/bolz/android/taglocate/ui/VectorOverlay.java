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
 *  Drawing polygon overlays, discussion on the Stack Overflow forum:
 *  http://stackoverflow.com/questions/2176397/drawing-a-line-path-on-google-maps
 */

package de.bolz.android.taglocate.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

import de.bolz.android.taglocate.geom.Coordinates;
import de.bolz.android.taglocate.geom.Geometry;
import de.bolz.android.taglocate.geom.OsmParser;
import de.bolz.android.taglocate.protocol.SettingsSingleton;

/**
 * This overlay class draws polygon geometries on the map. For better performance, it will
 * cache calculated GeoPoints using a Singleton ({@link GeometriesSingleton}).
 * @author Johannes Bolz
 *
 */
class VectorOverlay extends Overlay {
	private Paint paint, fillpaint;
	private Path path;
	private Projection projection;
	private List<Geometry> geometries;
	private Canvas canvas;
	private String geometryfile;
	
	public VectorOverlay() {
		setGeometries();

		// Polygon line style:
		paint = new Paint();
		paint.setDither(true);
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(3);
		paint.setAntiAlias(true);

		// Polygon fill style:
		fillpaint = new Paint();
		fillpaint.setColor(Color.DKGRAY);
		fillpaint.setStyle(Paint.Style.FILL);
	}
	
	/**
	 * Standard Overlay method. Will be called each time the overlay is rendered.
	 */
	public void draw(Canvas canvas, MapView view, boolean shadow){
        super.draw(canvas, view, shadow);       
        
        // Check if GeoPoints have already been calculated out of the current geometry file.
        // If not, get the geometried from the file and calculate the GeoPoints:
        if (!(SettingsSingleton.getInstance().getDatapath() + 
        		SettingsSingleton.getInstance().getGeometryfile()).equals(geometryfile)) {
        	setGeometries();
        	calculateGeoPoints();
        }
   
        // Make sure the GeometriesSingleton is populated with GeoPoints:
        if (GeometriesSingleton.getInstance().getGeometries() == null) {
        	calculateGeoPoints();
        }
        
        // Draw:
        projection = view.getProjection();
        this.canvas = canvas;
        drawPolygons(fillpaint);
        drawPolygons(paint);
  	}
	
	/**
	 * Draws polygons from the GeoPoints stored in GeometrySingleton.
	 * @param paint the {@link Paint} style information that shall be applied when
	 * rendering
	 */
	private void drawPolygons(Paint paint) {
		
		// Get GeoPoints from Singleton:
		List<GeoPoint[]> geomList = GeometriesSingleton.getInstance().getGeometries();
		
		// Declare only one Point object that will be overriden during each iteration:
		Point point = new Point();
		
		// Each array within the list represents one polygon. Each polygon will be drawn
		// separately using a Path object:
		for (int i = 0; i < geomList.size(); i++) {
			path = new Path();
			GeoPoint[] points = geomList.get(i);
			
			// Reproject coordinates to pixel coordinates:
			projection.toPixels(points[0], point);
			
			// Set start point of the path:
			path.moveTo(point.x, point.y);
			
			// Build the path:
			for (int j = 1; j < points.length; j++) {
				projection.toPixels(points[j], point);
				path.lineTo(point.x, point.y);
			}
			
			// Close the path, making it a polygon:
			path.close();
			
			// Draw the path:
			canvas.drawPath(path, paint);
		}
	}
	
	/**
	 * Calculates a {@link List} of {@link GeoPoint}s from the {@link Geometry}s 
	 * that was extracted from the geometry (osm) file. The list will be stored in
	 * {@link GeometriesSingleton} so that they are accessible without recalculating
	 * during each rendering process.
	 */
	private void calculateGeoPoints() {
		List<GeoPoint[]> geometryList = new ArrayList<GeoPoint[]>();
		
		// Each Geometry object represents a polygon:
		for (int i = 0; i < geometries.size(); i++) {
			GeoPoint[] geopoints = new GeoPoint[geometries.get(i).getPoints().size()];
			List<Coordinates> coords = geometries.get(i).getPoints();
			GeoPoint geopoint;
			
			// Create GeoPoints for each point within the polygon:
			for (int j = 0; j < coords.size(); j++) {
				geopoint = new GeoPoint((int) (Math.round(coords.get(j).getLat() * 1000000)), 
						(int) (Math.round(coords.get(j).getLon() * 1000000)));				
				geopoints[j] = geopoint;
			}
			geometryList.add(geopoints);
		}
		
		// Store into singleton:
		GeometriesSingleton.getInstance().setGeometries(geometryList);
	}
	
	/**
	 * Creates {@link Geometry} objects from the geometry (osm) file.
	 */
	private void setGeometries() {
		this.geometryfile = SettingsSingleton.getInstance().getDatapath() + 
		SettingsSingleton.getInstance().getGeometryfile();
		OsmParser p = new OsmParser(new File(geometryfile));
        geometries = p.getGeometries();
	}

}
