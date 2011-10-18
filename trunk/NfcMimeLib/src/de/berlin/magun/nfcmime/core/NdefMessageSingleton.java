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

import java.util.HashMap;
import java.util.Map;

import android.nfc.NdefMessage;

/**
 * This singleton class stores a map of NdefMessages, identified by a unique Long
 * (e.g. timestamp).
 * @author Johannes Bolz
 *
 */
public final class NdefMessageSingleton {

	private static NdefMessageSingleton instance;
	private static Map<Long, NdefMessage> messageMap;
	
	/**
	 * empty, private constructor to avoid multiple instantiation
	 */
	private NdefMessageSingleton() {}
	
	/** 
	 * @return the instance of the singleton object. Creates a new one if none exists.
	 */
	protected static synchronized NdefMessageSingleton getInstance() {
		if (instance == null) {
			instance = new NdefMessageSingleton();
		}
		if (messageMap == null) {
			messageMap = new HashMap<Long, NdefMessage>();
		}
		return instance;
	}
	
	/**
	 * Add a NDEF message to the map
	 * @param key long to identify the stored NdefMessage
	 * @param message NdefMessage to store
	 */
	protected synchronized void addMessage(Long key, NdefMessage message) {
		messageMap.put(key, message);
	}
	
	/**
	 * @param key the unique identifier that was assigned to specific {@link NdefMessage}
	 * @return NdefMessage
	 */
	protected synchronized NdefMessage getMessage(Long key) {
		return messageMap.get(key);
	}
	
	/**
	 * Clears the map of all NdefMessage entries.
	 */
	protected synchronized void clearMap() {
		messageMap.clear();
	}
}
