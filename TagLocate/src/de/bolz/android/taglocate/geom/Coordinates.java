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

package de.bolz.android.taglocate.geom;

/**
 * This class represents a geographic location, holding latitide, longitude and altitude information.
 * @author Johannes Bolz
 */
public class Coordinates {
	private double lat;
	private double lon;
	private String alt;
	
	/**
	 * @param lat latitude in decimal degrees
	 * @param lon longitude in decimal degrees
	 * @param alt altitude, can be in any appropriate format.
	 */
	public Coordinates(double lat, double lon, String alt) {
		this.lat = lat;
		this.lon = lon;
		this.alt = alt;
	}
	
	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}
	
	public String getAlt() {
		return alt;
	}


}
