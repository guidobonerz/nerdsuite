package de.drazil.nerdsuite.handler;

import java.util.List;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

public class NewSpriteEditorHandler
{

	@Execute
	public void execute(EPartService partService, MApplication application, EModelService modelService)
	{
		MPart part = MBasicFactory.INSTANCE.createPart();
		part.setLabel("Sprite Project");
		part.setContributionURI("bundleclass://de.drazil.nerdsuite/de.drazil.nerdsuite.spriteeditor.SpriteEditor");
		List<MPartStack> stacks = modelService.findElements(application, "de.drazil.nerdsuite.partstack.work", MPartStack.class, null);
		stacks.get(0).getChildren().add(part);
		partService.showPart(part, PartState.ACTIVATE);
	}

}