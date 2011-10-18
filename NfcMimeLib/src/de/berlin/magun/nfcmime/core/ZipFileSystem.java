/*
 *    Copyright 2011 by the MAGUN project
 *    http://magun.beuth-hochschule.de
 *    johannes-bolz (at) gmx.net
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

package de.berlin.magun.nfcmime.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.nfc.FormatException;

/**
 * This class provides funtioniality to extract files from a {@link NdefMimeRecord} record containing a ZIP
 * archive.
 * @author Johannes Bolz
 *
 */
public class ZipFileSystem {
	private NdefMimeRecord record;
	private List<String> files;
	private long size = 0;
	
	/**
	 * @param record the {@link NdefMimeRecord} containing a ZIP archive
	 * @throws FormatException if the MIME format isn't 'application/zip'
	 */
	public ZipFileSystem(NdefMimeRecord record) throws FormatException {
		if (!"application/zip".equalsIgnoreCase(record.getMimeString())) {
			throw new FormatException("Bad MIME format!");
		}
		this.record = record;
	}
	
	/**
	 * Writes the contained files to the local file system.
	 * @param root The path to store the files
	 * @throws IOException
	 */
	public void write(String root) throws IOException {
		
		ZipInputStream zInStream = new ZipInputStream(
				new ByteArrayInputStream(record.getPayload()));
		
		// Extract ZIP entries:
		try {
			ZipEntry entry = null;
	        byte[] entryBuffer = new byte[8192];
	        int len = 0;
	        while ((entry = zInStream.getNextEntry()) != null) {
	        	String separator = System.getProperty("file.separator");
	        	
	        	// In case there is a folder path specified with the file, create the 
	        	// path before writing the file:
	        	if (entry.getName().contains(separator)) {
	        		String pathname = new String(entry.getName().substring(0, 
	        				entry.getName().lastIndexOf(separator)));
	        		File path = new File(root, pathname);
	        		path.mkdir();
	        	}
	        	
	        	// Write file:
	        	if (!entry.isDirectory()) {
		            File entryFile = new File(root, entry.getName());
		            FileOutputStream fos = new FileOutputStream(entryFile);
		            while ((len = zInStream.read(entryBuffer)) > 0) {
		                fos.write(entryBuffer, 0, len);
		            }
		            fos.flush();
		            fos.close();
	        	}
        }
		} finally {
			zInStream.close();
		}
	}
	
	/**
	 * @return The names of the files contained in the archive.
	 * @throws IOException
	 */
	public ArrayList<String> getFileNames() throws IOException {
		if (files == null) {
			resolveMetadata();
		}
		return (ArrayList<String>) files;
	}
	
	/**
	 * @return the overall uncompressed size of the archive. 
	 * @throws IOException
	 */
	public long getSize() throws IOException {
		if (size == 0) {
			resolveMetadata();
		}
		return size;
	}
	
	/**
	 * Extracts the file names and the data size of the archive.
	 * @throws IOException
	 */
	private void resolveMetadata() throws IOException {
		files = new ArrayList<String>();
		
		ZipInputStream zInStream = new ZipInputStream(
				new ByteArrayInputStream(record.getPayload()));
		try {
			size = 0;
			ZipEntry e = null;
			while ((e = zInStream.getNextEntry()) != null) {
				size += e.getSize();
				files.add(e.getName());
			}
		} finally {
			zInStream.close();
		}
	
	}
	
}
