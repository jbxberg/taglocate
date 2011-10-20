package de.bolz.android.taglocate.app;

import java.util.List;
import com.google.inject.Module;
import roboguice.application.RoboApplication;

public class TagLocate extends RoboApplication {

	@Override
	public void addApplicationModules(List<Module> modules) {
		modules.add(new BindingsModule());
	}
}
