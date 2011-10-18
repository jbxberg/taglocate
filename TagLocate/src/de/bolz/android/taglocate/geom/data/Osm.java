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
 * Class representation of the 'osm' root element within an OSM file.
 * @author Johannes Bolz
 *
 */
@Root(name="osm")
public class Osm {
	
	@Attribute(required=false, name="version")
	private String version;
	
	@Attribute(required=false, name="generator")
	private String generator;
	
	@ElementList(required=false, inline=true)
	private List<Node> nodelist;
	
	@ElementList(required=false, inline=true)
	private List<Way> waylist;
	
	@ElementList(required=false, inline=true)
	private List<Relation> relationlist;
	
	public Osm() {
		super();
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getGenerator() {
		return generator;
	}

	public void setGenerator(String generator) {
		this.generator = generator;
	}

	public List<Node> getNodelist() {
		return nodelist;
	}

	public void setNodelist(List<Node> nodelist) {
		this.nodelist = nodelist;
	}

	public List<Way> getWaylist() {
		return waylist;
	}

	public void setWaylist(List<Way> waylist) {
		this.waylist = waylist;
	}

	public List<Relation> getRelationlist() {
		return relationlist;
	}

	public void setRelationlist(List<Relation> relationlist) {
		this.relationlist = relationlist;
	}

}
