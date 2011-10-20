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

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
import com.google.inject.Inject;

import de.bolz.android.taglocate.R;

/**
 * This class is responsible for drawing a location marker on the Map View.
 * @author Johannes Bolz
 */
public class LocationMarkerOverlay extends Overlay {
	private Projection projection;

	private double lon, lat;
	private Point point;
	private GeoPoint geopoint;
	private Context context;

	/**
	 * @param context the context of the calling class (activity)
	 */
	@Inject
	public LocationMarkerOverlay(Application context) {
		this.context = context;
	}

	/**
	 * Standard Overlay method. Called everytime the objects in this class are rendered.
	 */
	public void draw(Canvas canvas, MapView view, boolean shadow) {
		super.draw(canvas, view, shadow);

		// Calculate pixel coordinates out of the location:
		projection = view.getProjection();
		geopoint = new GeoPoint((int) (Math.round(this.lat * 1000000)),
				(int) (Math.round(this.lon * 1000000)));
		point = new Point();
		projection.toPixels(geopoint, point);
		
		// Draw marker:
		Bitmap locationMarker = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.marker_white_border);
		canvas.drawBitmap(locationMarker, point.x
				- (locationMarker.getWidth() / 2),
				point.y - (locationMarker.getHeight() / 2), null);
	}

	/**
	 * Sets the marker's coordinates.
	 * @param lon geographic longitude in decimal degrees
	 * @param lat geographic latitude in decimal degrees
	 */
	public void setCoords(double lon, double lat) {
		this.lon = lon;
		this.lat = lat;
	}

}
