
package de.drazil.nerdsuite.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import de.drazil.nerdsuite.Constants;

public class ShiftTileHandler {

	@Execute
	public void execute(MPart part, @Named("de.drazil.nerdsuite.commandparameter.Direction") String direction,
			IEventBroker broker) {
		broker.send("Shift",
				new BrokerObject((String) part.getTransientData().get(Constants.OWNER), Integer.valueOf(direction)));
	}
}