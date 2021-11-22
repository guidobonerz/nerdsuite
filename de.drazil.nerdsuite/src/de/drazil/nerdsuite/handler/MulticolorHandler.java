
package de.drazil.nerdsuite.handler;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledItem;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.util.E4Utils;

public class MulticolorHandler {

	@Execute
	public void execute(MPart part, EModelService modelService, IEventBroker broker) {
		List<String> tags = new LinkedList<>();
		tags.add("MultiColorButton");
		MHandledItem item = E4Utils.getMenuITemByTag(part, modelService, tags);
		broker.send("Multicolor", new BrokerObject((String) part.getTransientData().get(Constants.OWNER), item.isSelected()));
	}

}