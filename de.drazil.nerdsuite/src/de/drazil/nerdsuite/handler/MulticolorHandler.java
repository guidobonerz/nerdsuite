
package de.drazil.nerdsuite.handler;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledItem;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import de.drazil.nerdsuite.Constants;

public class MulticolorHandler {

	@Execute
	public void execute(MPart part, EModelService modelService, IEventBroker broker) {
		List<String> tags = new LinkedList<>();
		tags.add("MultiColorButton");
		List<MHandledItem> element = modelService.findElements(part.getToolbar(), null, MHandledItem.class, tags);
		MHandledItem item = element.get(0);
		broker.send("Multicolor",
				new BrokerObject((String) part.getTransientData().get(Constants.OWNER), item.isSelected()));
	}

}