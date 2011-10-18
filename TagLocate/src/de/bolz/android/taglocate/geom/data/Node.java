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

package de.bolz.android.taglocate.geom.data;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * Class representation of a 'node' element within an OSM file.
 * @author Johannes Bolz
 *
 */
@Root(name="node")
public class Node {

	@Attribute
	private long id;
	
	@Attribute
	private double lat;
	
	@Attribute
	private double lon;
	
	@Attribute(required=false)
	private boolean visible;
	
	@Attribute(required=false)
	private String action;
	
	@ElementList(required=false, inline=true)
	private List<Tag> taglist;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setTaglist(List<Tag> taglist) {
		this.taglist = taglist;
	}

	public List<Tag> getTaglist() {
		return taglist;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	
}
