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

import java.util.List;

/**
 * This class represents a polygon storing a set of {@link Coordinates} objects.
 * @author Johannes Bolz
 *
 */
public class Geometry {
	private List<Coordinates> points;
	private long id;
	
	/**
	 * @param points List of {@link Coordinates} objects representing nodes of the geometry.
	 * @param id unique id
	 */
	public Geometry(List<Coordinates> points, long id) {
		this.id = id;
		this.points = points;
	}
	
	public List<Coordinates> getPoints() {
		return points;
	}

	public long getId() {
		return id;
	}
	
}
