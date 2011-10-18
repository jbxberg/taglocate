/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.berlin.magun.nfcmime.core;

import org.apache.http.util.EncodingUtils;

/**
 * Wrapper class that holds the payload and MIME type byte arrays that may form an NdefRecord.
 * @author Johannes Bolz
 */
public class NdefMimeRecord {
	private byte[] payload;
	private byte[] mime;
	
	/**
	 * Constructor.
	 * @param payload byte[] 
	 * @param mime byte[]
	 */
	public NdefMimeRecord(byte[] payload, byte[] mime) {
		this.payload = payload;
		this.mime = mime;
	}
	
	/**
	 * @return payload byte array
	 */
	public byte[] getPayload() {
		return payload;
	}
	
	/**
	 * @return MIME definition as byte array
	 */
	public byte[] getMime() {
		return mime;
	}
	
	/**
	 * @return MIME definition as String.
	 */
	public String getMimeString() {
//		return new String(this.mime, Charset.forName("US-ASCII"));
		return EncodingUtils.getString(this.mime, "UTF-8");
	}
	
}
