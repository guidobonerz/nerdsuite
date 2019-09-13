
package de.drazil.nerdsuite.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.constants.PencilMode;

public class PencilModeHandler {
	@Execute
	public void execute(final MToolItem item, MPart part,
			@Named("de.drazil.nerdsuite.commandparameter.PencilMode") String pencilMode, IEventBroker broker) {
		broker.send("PencilMode", new BrokerObject((String) part.getTransientData().get(Constants.OWNER),
				PencilMode.values()[Integer.valueOf(pencilMode)]));
	}
}