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

import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.apache.commons.lang.ArrayUtils;
import android.nfc.NdefRecord;

/**
 * This class provides methods to generate and verify checksums for NdefRecord arrays.
 * @author Johannes Bolz
 *
 */
public class CrcGenerator {
	
	/**
	 * Generates a CRC32 checksum for an array of NdefRecords.
	 * @param records
	 * @return CRC32 checksum represented as long.
	 */
	public long getChecksum(NdefRecord[] records) {
		byte[] overallBytes = getByteArray(records);
		Checksum cs = new CRC32();
		cs.update(overallBytes, 0, overallBytes.length);
		return cs.getValue();
	}
	
	/**
	 * Checks if the CRC32 checksum of a NdefRecord[] array matches a reference 
	 * checksum
	 * @param records NdefRecord[] to check against checksum
	 * @param checksum long representation of a CRC32 reference checksum
	 * @return true if the CRC32 checksum of records matches checksum.
	 */
	public boolean checkHash(NdefRecord[] records, long checksum) {
		String chksumStr = String.valueOf(checksum);
		String toValidate = String.valueOf(getChecksum(records));
		return (toValidate.equals(chksumStr));
	}
	
	/**
	 * Creates a single byte array out of a NdefRecord[] array.
	 * @param records
	 * @return byte[]
	 */
	private byte[] getByteArray(NdefRecord[] records) {
		byte[] overallBytes = new byte[0];
		for (int i = 0; i < records.length; i++) {
			overallBytes = ArrayUtils.addAll(overallBytes, records[i].toByteArray());
		}
		return overallBytes;	
	}
	
}
