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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcV;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.ArrayUtils;
import org.apache.http.util.EncodingUtils;

/**
 * Data access object class that contains various read/write functions for RFID
 * transponders
 * @author Johannes Bolz
 */
public class RfidDAO {
	
	private NdefRecord[] records;
	
	/** 
	 * @param tag Tag
	 * @return a hexadecimal String representation of a Tag's ID or UID
	 */
	public String getTagId(Tag tag) {
		
		// get the ID byte array
		byte[] id = tag.getId();
		
		if (NfcV.get(tag) != null) {
			ArrayUtils.reverse(id);
		}

		return new String(Hex.encodeHex(id));
	}
	
	/**
	 * @param tag Tag
	 * @return true, if the Tag is NDEF formatted 
	 */
	public boolean isNdef(Tag tag) {
		return (Ndef.get(tag) != null);
	}
	
	/**
	 * @param tag Tag
	 * @return true, if the Tag is NDEF formatable.
	 */
	public boolean isNdefFormatable(Tag tag) {
		return (NdefFormatable.get(tag) != null);
	}
	
	/**
	 * @param tag Tag
	 * @return an ArrayList of NdefMimeRecord objects out of the Tag. Performs a read operation 
	 * on the NFC adapter.
	 * @throws IOException
	 * @throws FormatException
	 */
	public ArrayList<NdefMimeRecord> getMimeRecords(Tag tag) {
		readRecords(tag);
		return extractMimeRecords(this.records);
	}
	
	/**
	 * Reads URI records from NDEF records
	 * @param tag
	 * @return an ArrayList of URIs, represented as Strings.
	 */
	public ArrayList<String> getUriRecords(Tag tag) {
		readRecords(tag);
		if (this.records != null) {
			ArrayList<String> uriStrings = new ArrayList<String>();
			for (int i = 0; i < this.records.length; i++) {
				if(this.records[i].getTnf() == NdefRecord.TNF_WELL_KNOWN && 
						Arrays.equals(this.records[i].getType(), NdefRecord.RTD_URI)) {
					uriStrings.add(EncodingUtils.getString(ArrayUtils.remove(records[i].getPayload(), 0), "UTF-8"));
				}
			}
			return uriStrings;
		} else {
			return null;
		}
	}
	
	
	/**
	 * Checks all NDEF records against the CRC value contained in the last record.
	 * @return true, if the checksum is correct.
	 */
	public boolean verifyMimeRecords() {
		CrcGenerator generator = new CrcGenerator();
		NdefRecord checksumRecord = this.records[this.records.length -1];
		NdefRecord[] payloadRecords = (NdefRecord[]) ArrayUtils.remove(this.records, this.records.length -1);
		if (checksumRecord.getTnf() == NdefRecord.TNF_MIME_MEDIA &&
			EncodingUtils.getString(checksumRecord.getPayload(), "UTF-8").startsWith("crc32:")) {
			String checksumStr = EncodingUtils.getString(checksumRecord.getPayload(), "UTF--8");
			long checksum = Long.parseLong(checksumStr.substring(checksumStr.indexOf("crc32:") + 6));
			return generator.checkHash(payloadRecords, checksum);
		} else {
			return false;
		}
	}
	
	/**
	 * @param tag Tag
	 * @return an ArrayList of cached NdefMimeRecords, i.e. doesn't perform a read operation
	 * on the NFC adapter. Will return null if the cache doesn't hold a Ndef object.
	 */
	public ArrayList<NdefMimeRecord> getCachedMimeRecords(Tag tag) {
		NdefSingleton.getInstance().setNdef(Ndef.get(tag));
		if (NdefSingleton.getInstance().getNdef() != null) {
			if (NdefSingleton.getInstance().getNdef().getCachedNdefMessage() != null) {
				this.records = NdefSingleton.getInstance().getNdef().getCachedNdefMessage().getRecords();
				return extractMimeRecords(this.records);
			}
		}
		return null;
	}
	
	
	/**
	 * @param records Ndefrecord[] array
	 * @return an ArrayList of the NdefRecords within records. Will only put MIME formatted
	 * NdefRecords in the ArrayList. Returns null if the array is null.
	 */
	private ArrayList<NdefMimeRecord> extractMimeRecords(NdefRecord[] records) {
		if (records == null) {
			return null; // TODO Give more specific feedback?
		}
		ArrayList<NdefMimeRecord> recordlist = new ArrayList<NdefMimeRecord>();
		for (int i = 0; i < records.length; i++) {
			// check if NdefRecord is MIME formatted
			if (records[i].getTnf() == NdefRecord.TNF_MIME_MEDIA) {
				recordlist.add(new NdefMimeRecord(records[i].getPayload(), records[i].getType()));
			} 
		}
		return recordlist;
	}
	
	/**
	 * Stores a NDEF into the {@link NdefMessageSingleton}.
	 * @param tag
	 */
	private void readRecords(Tag tag) {
		NdefSingleton.getInstance().setNdef(Ndef.get(tag));
		// Create timestamp for NDEF message.
		long timestamp = (new Date()).getTime();
		// Create ReadTask, which will the message into a Map in NdefMessageSingleton, identified by timestamp.
		ReadTask rt = new ReadTask(timestamp);
		IOThread readThread = new IOThread();
		readThread.run(rt);
		
		// Retrieve NDEF message out of NdefMessageSingleton by it's timestamp ID, extract records.
		NdefMessage m = NdefMessageSingleton.getInstance().getMessage(timestamp);
		if (m != null) {
			this.records = NdefMessageSingleton.getInstance().getMessage(timestamp).getRecords();
		} else {
			this.records = null;
		}
	}
	
	/**
	 * Writes an NDEF message on a NFC-compliant RFID transponder.
	 * @param tag Tag
	 * @param message the NdefMessage to write
	 * @throws Exception
	 */
	public void writeMessage(Tag tag, NdefMessage message) throws Exception {

		// use NdefSingleton for I/O operations (which is also accessed by the separate Thread)
		NdefSingleton.getInstance().setNdef(Ndef.get(tag));
		
		// make sure the tag can be written to
    	if (!NdefSingleton.getInstance().getNdef().isWritable()) {
    		throw new Exception("Tag is read-only!"); // TODO Create custom Exception for that purpose
    	}
    

    	// make sure the NDEF message is neither null, nor larger than the available tag memory
	    if (message != null) {
	    	if (message.toByteArray().length > NdefSingleton.getInstance().getNdef().getMaxSize()) {
	       		throw new Exception("NDEF message too long!"); // TODO Create custom Exception for that purpose
	       		} 
	    	} else {
	       		throw new NullPointerException();
	       	}
		
	    // create and run a IOThread that writes the message
	    WriteTask wt = new WriteTask(message);
	    IOThread writeThread = new IOThread();
	    writeThread.run(wt);
	    
	}
	
	/**
	 * Formats an NDEF formatable RFID transponder, so NDEF messages can be written to it.
	 * @param tag Tag
	 */
	public void formatNdef(Tag tag) {
		// check if Tag is NDEF formattable. If it is, NDEF-format it
		if (NdefFormatable.get(tag) != null) {
			// use NdefSingleton for I/O operations
			NdefSingleton.getInstance().setNdefFormatable(NdefFormatable.get(tag));
			FormatThread ft = new FormatThread();
			ft.run();
		} 
	}
		
	
	
}
