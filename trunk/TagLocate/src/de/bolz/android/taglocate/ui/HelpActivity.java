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
package de.bolz.android.taglocate.ui;


import de.bolz.android.taglocate.R;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * This activity displays the application's HTML help / credits pages.
 * @author Johannes Bolz
 */
public class HelpActivity extends Activity{
	private WebView webView;
	
	/**
	 * Standard onCreate method.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.help);

	    // Populate WebVierw with HTML help page:
	    webView = (WebView) findViewById(R.id.webview);
	    webView.getSettings().setJavaScriptEnabled(true);
	    webView.loadUrl(getString(R.string.help_url));
	}
}
