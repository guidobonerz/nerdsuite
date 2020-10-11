package de.drazil.nerdsuite.util;

import java.util.List;
import java.util.Map;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledItem;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

public class E4Utils {

	public static MHandledItem getMenuITemByTag(MPart part, EModelService modelService, List<String> tags) {
		return modelService.findElements(part.getToolbar(), null, MHandledItem.class, tags).get(0);
	}

	public static void setToolItemEnabled(MPart part, EModelService modelService, List<String> tags, boolean enabled) {
		List<MHandledItem> items = modelService.findElements(part.getToolbar(), null, MHandledItem.class, tags);
		for (MHandledItem item : items) {
			item.setEnabled(enabled);
		}
	}

	public static void setToolItemSelected(MPart part, EModelService modelService, List<String> tags, boolean enabled) {
		List<MHandledItem> items = modelService.findElements(part.getToolbar(), null, MHandledItem.class, tags);
		for (MHandledItem item : items) {
			item.setSelected(enabled);
		}
	}

	public static void setToolItemVisible(MPart part, EModelService modelService, List<String> tags, boolean visible) {
		List<MHandledItem> items = modelService.findElements(part.getToolbar(), null, MHandledItem.class, tags);
		for (MHandledItem item : items) {
			item.setVisible(visible);
		}
	}

	public static void addPart2PartStack(MApplication app, EModelService modelService, EPartService partService,
			String id, MPart part, boolean setPartActive) {
		modelService.findElements(app, id, MPartStack.class, null).get(0).getChildren().add(part);
		if (setPartActive) {
			partService.showPart(part, PartState.ACTIVATE);
		}
	}

	public static MPart createPart(EPartService partService, String id, String contributionUrl, String owner,
			String label, Map<String, Object> parameterMap) {
		MPart part = partService.createPart(id);
		part.setLabel(label);
		if (parameterMap != null) {
			part.setObject(parameterMap);
		}
		part.setElementId(owner);
		part.setContributionURI(contributionUrl);
		return part;
	}

	public static <O> O findPartObject(EPartService partService, String id, Class<? super O> clazz) {
		MPart part = partService.findPart(id);
		@SuppressWarnings("unchecked")
		O o = (O) part.getObject();
		return o;
	}
}
