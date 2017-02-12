package de.drazil.nerdsuite.configuration;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.equinox.app.IApplicationContext;

public class Initializer {
	private static Configuration configuration;

	@PostContextCreate
	void postContextCreate(final IEventBroker eventBroker, IApplicationContext context) {
		configuration = new Configuration();
		configuration.initialize();
	}

	public static Configuration getConfiguration() {
		return configuration;
	}
}