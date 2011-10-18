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

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

/**
 * Thread that formats a NDEF compatible NFC tag.
 * @author Johannes Bolz
 */
public class FormatThread extends Thread{
	
	public void run() {
        try {
            int count = 0;
            
            // connect I/O
            NdefSingleton.getInstance().getNdefFormatable().connect();
            // wait 5 seconds for connection, if lost
            while (!NdefSingleton.getInstance().getNdefFormatable().isConnected()) {
                if (count > 500) {
                    throw new Exception("Unable to connect to tag");
                }
                count++;
                sleep(10);
            }

            // create an empty NDEF message as initial dataset 
            NdefRecord[] mockRecords = new NdefRecord[1];
            mockRecords[0] = new NdefRecord(NdefRecord.TNF_EMPTY, new byte[0], new byte[0], new byte[0]);
           	NdefSingleton.getInstance().getNdefFormatable().format(new NdefMessage(mockRecords));
            NdefSingleton.getInstance().getNdef().close();

        } catch (Throwable t) {
            // TODO Export exception handling
        }
    }
}
