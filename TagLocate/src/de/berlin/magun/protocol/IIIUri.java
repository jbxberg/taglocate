/*
 *    Copyright 2011 by the MAGUN project
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

package de.berlin.magun.protocol;

import java.text.ParseException;

/**
 * Class representing a iii URI.
 * @author Johannes Bolz
 *
 */
public class IIIUri {
	private Source source;
	private Target target;
	private Technology technology;
	
	/**
	 * @param uri the URI string.
	 * @throws ParseException if the URI doesn't conform to the iii URI syntax specification.
	 */
	public IIIUri(String uri) throws ParseException {
		// check if the URI is conform to the specification
		checkConformity(uri);
		
		// parse URI into objects, respect escaped characters
		String[] substrings = uri.replace("\\,", " ").split("," , -1);
		String targetname = substrings[0].replace("iii://", "");
		String sourcename = substrings[1];
		String technologyname = substrings[2];
		
		
		if (targetname.length() > 0) {
			this.target = new Target(targetname.replace(" ", ","));
		}
		if (sourcename.length() > 0) {
			this.source = new Source(sourcename.replace(" ", ","));
		}
		
		if (technologyname.length() > 0) {
			this.technology = new Technology(technologyname.replace(" ", ","));
		}

		
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public Source getSource() {
		return source;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	public Target getTarget() {
		return target;
	}

	public void setTechnology(Technology technology) {
		this.technology = technology;
	}

	public Technology getTechnology() {
		return technology;
	}
	
	/**
	 * Checks URI conformity to the definition
	 * @param uri
	 * @throws ParseException
	 */
	private void checkConformity(String uri) throws ParseException {
		
		if (!uri.startsWith("iii://")) {
			throw new ParseException("Invalid syntax.", 0);
		}
		
		if (uri.contains(" ")) {
			throw new ParseException("URI must not contain white spaces.", 0);
		}
				
		uri	= uri.replace("iii://", "").replace("\\,", " ");
	
		//		"bla,bla","bla","bkl"bki"
		
		int commacount = 0;
		for (int i = 0; i < uri.length(); i++) {
			if (uri.substring(i, i + 1).equals(",")) {
				commacount++;
			}
		}
		if (commacount != 2) {
			throw new ParseException("Wrong number of separators or incorrectly escaped characters.", 0);
		}
	}
	
}
