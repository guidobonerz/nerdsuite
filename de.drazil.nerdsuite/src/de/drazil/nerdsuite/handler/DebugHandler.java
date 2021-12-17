
package de.drazil.nerdsuite.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;

public class DebugHandler {
	@Execute
	public void execute(IEventBroker broker) {
		broker.send("U64_Debug", null);
	}

}