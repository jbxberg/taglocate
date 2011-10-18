/*
 *    Copyright 2011 by Johannes Bolz and the MAGUN project
 *    johannes-bolz (at) gmx.net
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

package de.bolz.android.taglocate.protocol;

/**
 * This singleton class stores various global configuration values to make them concurrently
 * accessible from different classes during runtime and to avoid inconsistencies.
 * @author Johannes Bolz
 */
public final class SettingsSingleton {
	private static SettingsSingleton instance;
	
	private static String datapath;
	private static String datafile;
	private static String geometryfile;
	private static String strategy;
	private static boolean downloadFromNfc;
	
	/**
	 * Keep constructor private.
	 */
	private SettingsSingleton() {}
	
	/**
	 * @return the instance of the singleton. If there is no instance yet, a new one will be
	 * created.
	 */
	public static synchronized SettingsSingleton getInstance() {
		if (instance == null) {
			instance = new SettingsSingleton();
		}
		return instance;
	}

	public String getDatapath() {
		return datapath;
	}

	public void setDatapath(String datapath) {
		SettingsSingleton.datapath = datapath;
	}

	public String getDatafile() {
		return datafile;
	}

	public void setDatafile(String datafile) {
		SettingsSingleton.datafile = datafile;
	}

	public String getGeometryfile() {
		return geometryfile;
	}

	public void setGeometryfile(String geometryfile) {
		SettingsSingleton.geometryfile = geometryfile;
	}

	public void setInstance(SettingsSingleton instance) {
		SettingsSingleton.instance = instance;
	}

	public String getStrategy() {
		return strategy;
	}

	public void setStrategy(String strategy) {
		SettingsSingleton.strategy = strategy;
	}

	public boolean isDownloadFromNfc() {
		return downloadFromNfc;
	}

	public void setDownloadFromNfc(boolean downloadFromNfc) {
		SettingsSingleton.downloadFromNfc = downloadFromNfc;
	}
	
	
}
