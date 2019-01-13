
package de.drazil.nerdsuite.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;

public class ShowGridHandler {
	@Execute
	public void execute(final MToolItem item, IEventBroker broker) {
		if (item.isSelected()) {
			broker.post("GridEnabled", !item.isSelected());
			broker.post("DotGridEnabled", item.isSelected());
		}
	}
}