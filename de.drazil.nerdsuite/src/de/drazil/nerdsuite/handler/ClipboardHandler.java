
package de.drazil.nerdsuite.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

public class ClipboardHandler {
	@Execute
	public void execute(MPart part, @Named("de.drazil.nerdsuite.commandparameter.ClipboardAction") String action,
			IEventBroker broker) {
		System.out.println(action);
	}

}