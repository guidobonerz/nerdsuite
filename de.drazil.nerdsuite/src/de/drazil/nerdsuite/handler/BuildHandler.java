package de.drazil.nerdsuite.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import de.drazil.nerdsuite.Constants;

public class BuildHandler {
	@Execute
	public void execute(MPart part, IEventBroker broker, @Named("de.drazil.nerdsuite.commandparameter.Run") String run,
			@Named("de.drazil.nerdsuite.commandparameter.Debug") String debug) {
		broker.send("BuildAndRun",
				new BrokerObject((String) part.getTransientData().get(Constants.OWNER), new String[] { debug, run }));
	}
}