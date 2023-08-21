
package de.drazil.nerdsuite.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.enums.GridType;
import de.drazil.nerdsuite.model.GridState;

public class GridTypeHandler {
	@Execute
	public void execute(final MToolItem item, MPart part,
			@Named("de.drazil.nerdsuite.commandparameter.GridType") String gridType,
			@Named("de.drazil.nerdsuite.commandparameter.GridVisible") String visible, IEventBroker broker) {
		broker.send("GridType", new BrokerObject((String) part.getTransientData().get(Constants.OWNER),
				new GridState(Boolean.getBoolean(visible), GridType.values()[Integer.valueOf(gridType)])));
	}
}