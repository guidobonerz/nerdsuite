
package de.drazil.nerdsuite.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import de.drazil.nerdsuite.Constants;

public class TileHandler {
	@Execute
	public void execute(MPart part, @Named("de.drazil.nerdsuite.commandparameter.Tile") String tileAction,
			IEventBroker broker) {
		broker.send("Tile", new BrokerObject((String) part.getTransientData().get(Constants.OWNER), tileAction));
	}
}