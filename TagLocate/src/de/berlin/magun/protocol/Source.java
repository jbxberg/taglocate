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

/**
 * Representation of the source (trigger) part of a iii URI.
 * @author Johannes Bolz
 *
 */
public class Source {
	private String content;
	private String scheme;
	private String value;
	
	/**
	 * @param content the part of the iii URI describing the source (trigger)
	 */
	public Source(String content) {
		this.content = content;
		this.scheme = content.substring(0, content.indexOf(":"));
		this.value = content.substring(content.indexOf(":") + 1);
		if (this.value.startsWith("//")) {
			this.value = this.value.substring(2);
		}
	}
	
	public String getUri() {
		return this.content;
	}
	
	public String getSchema() {
		return this.scheme;
	}
	
	public String getValue() {
		return this.value;
	}
	

}
