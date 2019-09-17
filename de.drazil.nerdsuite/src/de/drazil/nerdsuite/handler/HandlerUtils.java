package de.drazil.nerdsuite.handler;

import java.util.List;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledItem;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

public class HandlerUtils {

	public static MHandledItem getMenuITem(MPart part, EModelService modelService, List<String> tags) {
		List<MHandledItem> element = modelService.findElements(part.getToolbar(), null, MHandledItem.class, tags);
		return element.get(0);
	}

}
