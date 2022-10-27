package de.drazil.nerdsuite.configuration;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.lifecycle.PreSave;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessAdditions;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessRemovals;

public class Initializer {
	private static Configuration configuration;

	/*
	 * @PostContextCreate void postContextCreate(final IEventBroker eventBroker,
	 * IApplicationContext context) {
	 * 
	 * }
	 */
	@PostContextCreate
	void postContextCreate(IEclipseContext workbenchContext) {
		configuration = new Configuration();
		configuration.initialize();
	}

	@PreSave
	void preSave(IEclipseContext workbenchContext) {
	}

	@ProcessAdditions
	void processAdditions(IEclipseContext workbenchContext) {
	}

	@ProcessRemovals
	void processRemovals(IEclipseContext workbenchContext) {
	}

	public static Configuration getConfiguration() {
		return configuration;
	}
}