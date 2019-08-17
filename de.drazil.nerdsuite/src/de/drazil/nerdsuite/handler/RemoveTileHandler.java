
package de.drazil.nerdsuite.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;

public class RemoveTileHandler {
	@Execute
	public void execute(final MToolItem item, IEventBroker broker) {
		broker.post("addOrRemoveLayer", -1);
	}

}