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

import de.bolz.android.taglocate.geom.Coordinates;

/**
 * 
 * Class representing a Geo-URI string. (Syntax: geo:latitude,longitude[,altitude][;u=u][;crs=crs])
 * @author Johannes Bolz
 * 
 */
public class GeoUri {

	private Coordinates coordinate;
	private String string;
	
	/**
	 * @param uri URI string. Syntax: <i>geo:latitude,longitude[,altitude][;u=u][;crs=crs]</i>
	 */
	public GeoUri(String uri) {
		this.string = uri;
		String data = new String(uri.substring(4));
		
		if(data.startsWith("osm")) {
			// TODO: OSM query handling: At a later point, query for certain OSM tags / attributes
			// will be implemented.
		} else {
			// parse URI String into objects
			String[] components = data.split(";")[0].split(",");
			String latStr = new String(components[0]);
			String lonStr = new String(components[1]);
			String height = null;
			if (components.length == 3) {
				height = components[2];
			}
			// build coordinate
			coordinate = new Coordinates(Double.parseDouble(latStr), 
					Double.parseDouble(lonStr), height);
		}	
	}
	
	/**
	 * @param lat Latitude in decimal degrees
	 * @param lon Longitude in decimal degrees
	 */
	public GeoUri(double lat, double lon) {
		this.string = new StringBuilder("geo:").
		append(String.valueOf(lat)).
		append(",").
		append(String.valueOf(lon)).
		toString();
	}
	
	/**
	 * @return the Coordinates specified in the Geo-URI.
	 */
	public Coordinates getCoordinate() {
		return this.coordinate;
	}
	
	/**
	 * @return the complete Geo-URI
	 */
	public String getString() {
		return string;
	}
}
