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

import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;

/**
 * Singleton class that holds a Ndef and a NdefFormatable object
 * @author Johannes Bolz
 *
 */
public final class NdefSingleton {
	
	private static NdefSingleton instance;
	private static Ndef ndef;
	private static NdefFormatable ndefFormatable;
	
	/**
	 * private, empty constructor to avoid multiple instances
	 */
	private NdefSingleton() {}
	
	/**
	 * @return the instance of NdefSingleton.
	 */
	protected static synchronized NdefSingleton getInstance() {
		if (instance == null) {
			instance = new NdefSingleton();
		}
		return instance;
	}
	
	/**
	 * Sets the Ndef object within the singleton
	 * @param ndef Ndef
	 */
	protected synchronized void setNdef(Ndef ndef) {
		NdefSingleton.ndef = ndef;
	}
	
	/**
	 * @return the Ndef object within the singleton
	 */
	protected synchronized Ndef getNdef() {
		return NdefSingleton.ndef;
	}
	
	/**
	 * Sets the NdefFormatable object within the singleton
	 * @param ndefFormatable NdefFormatable
	 */
	protected synchronized void setNdefFormatable(NdefFormatable ndefFormatable) {
		NdefSingleton.ndefFormatable = ndefFormatable;
	}

	/**
	 * @return the NdefFormatable object within the singleton
	 */
	protected synchronized NdefFormatable getNdefFormatable() {
		return NdefSingleton.ndefFormatable;
	}
}
