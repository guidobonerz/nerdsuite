
package de.drazil.nerdsuite.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import de.drazil.nerdsuite.Constants;

public class InvertTileHandler {
	@Execute
	public void execute(MPart part, IEventBroker broker) {
		broker.send("Invert", new BrokerObject((String) part.getTransientData().get(Constants.OWNER), 0));
	}
}