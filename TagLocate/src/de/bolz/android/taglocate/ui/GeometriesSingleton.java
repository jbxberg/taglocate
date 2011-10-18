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

import java.util.List;

import com.google.android.maps.GeoPoint;

/**
 * Singleton class that stores a {@link List} of {@link GeoPoint} arrays. Each array 
 * should represent a polygon geometry. It is intended to provide a static
 * set of created geometries during runtime, so the {@link GeoPoint}s need to
 * be created only once.
 * @author Johannes Bolz
 */
public final class GeometriesSingleton {
	private static GeometriesSingleton instance;
	private static List<GeoPoint[]> geometries;
	
	/**
	 * private constructor to avoid instantiation from outside the class
	 */
	private GeometriesSingleton() {}
	
	/**
	 * @return the instance of the singleton. If no instance exists, a new one will be
	 * created. 
	 */
	public static synchronized GeometriesSingleton getInstance() {
		if (instance == null) {
			instance = new GeometriesSingleton();
		}
		return instance;
	}

	/**
	 * @return a {@link List} of {@link GeoPoint} arrays. Each array shall hold the
	 * GeoPoints needed to create a polygon geometry.
	 */
	public List<GeoPoint[]> getGeometries() {
		return geometries;
	}

	/**
	 * Stores the geometries.
	 * @param geometries a {@link List} of {@link GeoPoint} arrays. Each array shall hold the
	 * GeoPoints needed to create a polygon geometry.
	 */
	public void setGeometries(List<GeoPoint[]> geometries) {
		GeometriesSingleton.geometries = geometries;
	}



}
