
package de.drazil.nerdsuite.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;

import de.drazil.nerdsuite.Constants;

public class PurgeTileHandler {
	@Execute
	public void execute(MMenuItem item, MPart part, @Named("de.drazil.nerdsuite.commandparameter.Mode") String mode,
			IEventBroker broker) {
		broker.send("Purge", new BrokerObject((String) part.getTransientData().get(Constants.OWNER), 0));
	}
}