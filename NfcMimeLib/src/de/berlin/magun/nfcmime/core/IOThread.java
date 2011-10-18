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

/**
 * Provides a thread for I/O operations on an NDEF formatted RFID transponder.
 * @author Johannes Bolz
 *
 */
public class IOThread extends Thread{
	
	/**
	 * Will do nothing. Use run(IOTask task).
	 */
	@Deprecated
	public void run() {}
	
	/**
	 * Runs the thread.
	 * @param task IOTask that specifies the operation to be performed on the transponder.
	 */
	public void run(IOTask task) {
        try {
            int count = 0;
            
            // connect I/O
            NdefSingleton.getInstance().getNdef().connect();
            // wait 5 seconds for connection, if lost
            while (!NdefSingleton.getInstance().getNdef().isConnected()) {
                if (count > 500) {
                    throw new Exception("Unable to connect to tag");
                }
                count++;
                sleep(10);
            }
            
           	task.doTask();
            NdefSingleton.getInstance().getNdef().close();

        } catch (Throwable t) {
            // TODO Export exception handling
        }
    }
}
