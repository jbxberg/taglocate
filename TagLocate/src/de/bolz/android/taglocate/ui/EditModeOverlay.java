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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import de.bolz.android.taglocate.R;

/**
 * This class is responsible for drawing a crosshairs marker in the view center.
 * @author Johannes Bolz
 */
public class EditModeOverlay extends Overlay {

	private Point point;
	private Context context;

	/**
	 * @param context the context of the calling class (activity)
	 */
	public EditModeOverlay(Context context) {
		this.context = context;
	}

	/**
	 * Standard Overlay method.
	 */
	public void draw(Canvas canvas, MapView view, boolean shadow) {
		super.draw(canvas, view, shadow);

		point = new Point(view.getWidth() / 2, view.getHeight() /2);
		
		// Draw marker on the map's center:
		Bitmap locationMarker = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.crosshairs);
		canvas.drawBitmap(locationMarker, point.x
				- (locationMarker.getWidth() / 2),
				point.y - (locationMarker.getHeight() / 2), null);
	}

}
