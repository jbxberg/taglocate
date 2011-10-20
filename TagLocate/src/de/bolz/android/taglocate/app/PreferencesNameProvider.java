package de.bolz.android.taglocate.app;

import android.app.Application;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * workaround for http://groups.google.com/group/roboguice/browse_thread/thread/447c22b833b4992b/9f8428348c77bfba
 * @author Johannes Bolz
 *
 */
public class PreferencesNameProvider implements Provider<String> {
	private String packageName;

	@Inject
	public PreferencesNameProvider(Application application) {
		this.packageName = application.getPackageName();
	}
	
	@Override
	public String get() {
		return packageName + "_preferences";
	}

}
