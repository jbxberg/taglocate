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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

/**
 * Provides methods to create NdefMessages. 
 * @author Johannes Bolz
 *
 */
public class NdefMessageBuilder {
	private ArrayList<NdefRecord> recordlist;
	private boolean hasChecksum = false;
	private NdefMessage message;
	
	public NdefMessageBuilder() {
		this.recordlist = new ArrayList<NdefRecord>();
	}
	
	/**
	 * Stores an NDEF record from an InputStream. Will throw an IOException if the last NDEF record is
	 * a checksum added with addChecksum().
	 * @param is InputStream that holds the payload data of the NDEF record.
	 * @param mimetype MIME type definition, e.g. "text/plain"
	 * @throws IOException
	 */
	public void addMimeRecord(InputStream is, String mimetype) throws IOException {
		if (!this.hasChecksum) {
			recordlist.add(new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimetype.getBytes(Charset.forName("US-ASCII")), 
					new byte[0], IOUtils.toByteArray(is)));
		} else {
			throw new IOException("Cannot add record - last record is a checksum.");
		}
	}
	
	/**
	 * Stores an NDEF record containing a URI, using the URI format definition.
	 * @param uri the URI to be written
	 * @throws IOException
	 */
	public void addUriRecord(String uri) throws IOException {
		if (!this.hasChecksum) {
			recordlist.add(new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, 
					new byte[0], ArrayUtils.addAll(new byte[] {(byte) 0x00}, uri.getBytes(Charset.forName("UTF-8")))));
		} else {
			throw new IOException("Cannot add record - last record is a checksum.");
		}
	}
	
	/**
	 * Builds an NdefMessage from all stored NDEF records.
	 * @return NdefMessage
	 */
	public NdefMessage buildMessage() {
		NdefRecord[] records = new NdefRecord[recordlist.size()];
		for (int i = 0; i < recordlist.size(); i++) {
			records[i] = recordlist.get(i);
		}
		this.message = new NdefMessage(records);
		return this.message;
	}
	
	/**
	 * Creates a CRC32 checksum out of all previously added NDEF records and adds it as last record entry. 
	 * After that, it will not be possible to add additional records. The record payload will be a String 
	 * consisting of the prefix 'crc32:' and a String representation of the checksum long, e.g. 
	 * 'crc32:2868450884'. The record will be MIME formatted as 'text/plain'.
	 */
	public void addChecksum() {
		CrcGenerator generator = new CrcGenerator();
		NdefRecord[] records = new NdefRecord[recordlist.size()];
		for (int i = 0; i < recordlist.size(); i++) {
			records[i] = recordlist.get(i);
		}
		recordlist.add(new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "text/plain".getBytes(Charset.forName("US-ASCII")), 
				new byte[0], ("crc32:" + String.valueOf(generator.getChecksum(records))).getBytes(Charset.forName("US-ASCII")))); 
	}
		
	/**
	 * @return the overall NdefMessage size.
	 */
	public int getDataSize() {
		return buildMessage().toByteArray().length;
	}
}
