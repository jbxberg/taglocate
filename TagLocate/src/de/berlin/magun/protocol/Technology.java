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
 * Representation of the technology part of a iii URI
 * @author Johannes Bolz
 *
 */
public class Technology {
	
	public static final String WIFI = "WIFI";
	public static final String WPA = "WPA";
	public static final String WEP = "WEP";
	public static final String OPEN = "OPEN";

	private String technology;
	private String networkid;
	private String encryptiontype;
	private String password;
	
	/**
	 * @param content the technology part of a iii URI
	 */
	public Technology(String content) {
		// Syntax: WIFI:T:WPA;S:mynetwork;P:mypass;;

		if (content.toUpperCase().startsWith("WIFI")) {
			this.technology = WIFI;
			this.networkid = content.substring(content.indexOf("S:") + 2, 
					content.indexOf(";", content.indexOf("S:") + 2));
			
			String encryption = content.substring(content.indexOf("T:") + 2, 
					content.indexOf(";", content.indexOf("T:") + 2));
			
			if (encryption.equalsIgnoreCase("nopass")) {
				this.encryptiontype = OPEN;
			} else if (encryption.equalsIgnoreCase("WEP")) {
				this.encryptiontype = WEP;
				this.password = content.substring(content.indexOf("P:") + 2, 
						content.indexOf(";", content.indexOf("P:") + 2));
			} else if (encryption.equalsIgnoreCase("WPA")) {
				this.encryptiontype = WPA;
				this.password = content.substring(content.indexOf("P:") + 2, 
						content.indexOf(";", content.indexOf("P:") + 2));
			}
			
		}	
	}
	
	public String getTechnology() {
		return this.technology;
	}
	
	public String getNetworkId() {
		return this.networkid;
	}
	
	public String getEncryptionType() {
		return this.encryptiontype;
	}
	
	public String getPassword() {
		return this.password;
	}

}
