
package de.drazil.nerdsuite.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;

public class MuteHandler {
	@Execute
	public void execute(IEventBroker broker) {
		broker.send("U64Mute", null);
	}

}