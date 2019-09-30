
package de.drazil.nerdsuite.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.enums.SelectionMode;

public class SelectionModeHandler {
	@Execute
	public void execute(final MToolItem item, MPart part,
			@Named("de.drazil.nerdsuite.commandparameter.SelectionMode") String selectionMode, IEventBroker broker) {
		broker.send("SelectionMode", new BrokerObject((String) part.getTransientData().get(Constants.OWNER),
				SelectionMode.values()[Integer.valueOf(selectionMode)]));
	}

}