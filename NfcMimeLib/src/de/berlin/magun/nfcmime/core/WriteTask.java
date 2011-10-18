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

import android.nfc.FormatException;
import android.nfc.NdefMessage;

/**
 * IOTask implementation to write NdefMessages
 * @author Johannes Bolz
 *
 */
public class WriteTask implements IOTask{
	
	private NdefMessage message;

	public WriteTask(NdefMessage message) {
		this.message = message;
	}
	
	/**
	 * writes the NdefMessage to the Ndef stored in NdefSingleton
	 */
	@Override
	public void doTask() throws IOException, FormatException {
		NdefSingleton.getInstance().getNdef().writeNdefMessage(message);	
	}
}
