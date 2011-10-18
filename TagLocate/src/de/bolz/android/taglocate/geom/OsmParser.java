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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.util.EncodingUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.res.Resources.NotFoundException;
import de.bolz.android.taglocate.geom.data.Nd;
import de.bolz.android.taglocate.geom.data.Node;
import de.bolz.android.taglocate.geom.data.Osm;
import de.bolz.android.taglocate.geom.data.Way;

/**
 * This class provides functionality to parse osm files into objects and to extract
 * {@link Geometry}s, {@link Way}s and {@link Node}s.
 * @author Johannes Bolz
 *
 */
public class OsmParser {
	private Osm osm;
	
	/**
	 * @param f The osm file to be parsed.
	 */
	public OsmParser(File f) {
		parse(f);
	}
	
	/**
	 * @return an {@link ArrayList} of {@link Geometry}s created from the way elements
	 * within the osm file.
	 */
	public ArrayList<Geometry> getGeometries() {
		
		Map<String,Node> nodes = getNodeMap();
		
		List<Geometry> geometries = new ArrayList<Geometry>();
		// Extract Way objects:
		List<Way> ways = osm.getWaylist();
		
		// Iterate through ways:
		for (int i = 0; i < ways.size(); i++) {
			
			// Get Nd objects representing osm nd elements:
			List<Nd> nds = ways.get(i).getNdlist();
			List<Coordinates> coords = new ArrayList<Coordinates>();
			
			// Create Coordinates objects out of each node element corresponding to
			// the way's nd element:
			for (int j = 0; j < nds.size(); j++) {
				double lat = nodes.get(String.valueOf(nds.get(j).getRef())).getLat();
				double lon = nodes.get(String.valueOf(nds.get(j).getRef())).getLon();
				coords.add(new Coordinates(lat, lon, null));
			}
			geometries.add(new Geometry(coords, ways.get(i).getId()));
		}
		return (ArrayList<Geometry>) geometries;
	}
	
	/**
	 * @return a HashMap with {@link Node} objects as values and their OSM IDs (String) as 
	 * keys.
	 */
	public HashMap<String, Node> getNodeMap() {
		Map<String,Node> nodemap = new HashMap<String, Node>();
		List<Node> nodelist = osm.getNodelist();
		for (int i = 0; i < nodelist.size(); i++) {
			nodemap.put(String.valueOf(nodelist.get(i).getId()), 
					nodelist.get(i));
		}
		return (HashMap<String, Node>) nodemap;
	}
	
	/**
	 * @return a HashMap eith {@link Way} objects as values and their OSM IDs (String) as 
	 * keys.
	 */
	public HashMap<String, Way> getWayMap() {
		Map<String, Way> waymap = new HashMap<String, Way>();
		List<Way> waylist = osm.getWaylist();
		for (int i = 0; i < waylist.size(); i++) {
			waymap.put(String.valueOf(waylist.get(i).getId()), 
					waylist.get(i));
		}
		return (HashMap<String, Way>) waymap;
	}
	
	/**
	 * Deserializes an OSM file into an {@link Osm} object.
	 * @param f the OSM file
	 */
	private void parse(File f) {
        String input = "";
        try {
        	// Make sure to pass the file's content to the Serializer with UTF-8 encoding:
			InputStream is = new FileInputStream(f);
			byte[] ba = IOUtils.toByteArray(is);
			input = EncodingUtils.getString(ba, "UTF-8");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}        
		
		// Deserialize:
		Serializer serial = new Persister();
		try {
			osm = serial.read(Osm.class, input);
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
