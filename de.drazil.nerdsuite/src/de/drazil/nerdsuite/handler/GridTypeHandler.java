
package de.drazil.nerdsuite.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;

import de.drazil.nerdsuite.constants.GridStyle;
import de.drazil.nerdsuite.model.GridState;

public class GridTypeHandler {
	@Execute
	public void execute(final MToolItem item, IEventBroker broker) {
		broker.post("GridState", new GridState(item.isSelected(), GridStyle.Dot));
	}
}