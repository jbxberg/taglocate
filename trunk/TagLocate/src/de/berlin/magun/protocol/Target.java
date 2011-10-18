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
 * Representation of the target part of a iii URI
 * @author Johannes Bolz
 *
 */
public class Target {
	public static final String FTP = "FTP";
	public static final String HTTP = "HTTP";
	
	private String schema;
	private String domain;
	private String path;
	private String url;

	/**
	 * @param content the target part of a iii URI
	 */
	public Target(String content) {
		this.url = content;
		this.schema = content.substring(0, content.indexOf(":"));
		
		if (content.contains("//")) {
			this.domain = content.substring(content.indexOf("//") + 2, 
					content.indexOf("/", content.indexOf("//") + 2));
			this.path = content.substring(content.indexOf(this.domain) + this.domain.length());
		} 
	}
	
	public String getUrl() {
		return this.url;
	}

	public String getSchema() {
		return this.schema;
	}
	
	public String getDomain() {
		return this.domain;
	}
	
	public String getPath() {
		return this.path;
	}
}
