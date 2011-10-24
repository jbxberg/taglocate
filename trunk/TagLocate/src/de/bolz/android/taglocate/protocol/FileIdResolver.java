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
 */

package de.bolz.android.taglocate.protocol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.util.EncodingUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.CamelCaseStyle;
import org.simpleframework.xml.stream.Format;

import de.berlin.magun.protocol.Source;
import de.bolz.android.taglocate.geom.Coordinates;
import de.bolz.android.taglocate.protocol.data.Reference;
import de.bolz.android.taglocate.protocol.data.ReferenceList;


/**
 * {@link IdResolver} implementation that matches IDs against IIR reference files.
 * @author Johannes Bolz
 *
 */
public class FileIdResolver implements IdResolver {

	@Override
	public Coordinates resolve(String id) {
		
		try {
			// Deserialize link file:
			File referencefile = new File(SettingsSingleton.getInstance().getDatapath() + 
					SettingsSingleton.getInstance().getDatafile());
			InputStream is;
			ReferenceList list = null;
			String input = "";
			
			// Make sure file's content is decoded using UTF-8:
			is = new FileInputStream(referencefile);
			byte[] ba = IOUtils.toByteArray(is);
			input = EncodingUtils.getString(ba, "UTF-8");
			
			// Create a serializer that adheres to the XML's camel case element style:
			Serializer s = new Persister(new Format(new CamelCaseStyle()));
			list = s.read(ReferenceList.class, input);
			if (list != null) {
				List<Reference> links = list.getReferences();
				String trigger;
				
				// Match ID against each item in the link list (=reference table):
				for (int i = 0; i < links.size(); i++) {
					trigger = (new Source(links.get(i).getTrigger().getTag().getTagStr()))
						.getValue();
					if (id.equalsIgnoreCase(trigger)) {
						return (new GeoUri(links.get(i).getTarget().getTargetStr())).getCoordinate();
					}
					
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

}
