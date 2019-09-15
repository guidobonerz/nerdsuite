
package de.drazil.nerdsuite.handler;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledItem;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.enums.PencilMode;

public class TogglePencilModeHandler {
	@Execute
	public void execute(MPart part, EModelService modelService, IEventBroker broker) {
		List<String> tags = new LinkedList<>();
		tags.add("DrawButton");
		List<MHandledItem> element = modelService.findElements(part.getToolbar(), null, MHandledItem.class, tags);
		MHandledItem item1 = element.get(0);
		MHandledItem item2 = element.get(1);
		if (item1.isSelected()) {
			item1.setSelected(false);
			item2.setSelected(true);
		} else {
			item1.setSelected(true);
			item2.setSelected(false);
		}
		broker.send("PencilMode", new BrokerObject((String) part.getTransientData().get(Constants.OWNER),
				PencilMode.values()[Integer.valueOf(item1.isSelected()?0:1)]));
	}
}