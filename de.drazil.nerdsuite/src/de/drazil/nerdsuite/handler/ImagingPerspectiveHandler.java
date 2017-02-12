package de.drazil.nerdsuite.handler;

import java.util.List;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class ImagingPerspectiveHandler
{
	@Execute
	public void execute(MPerspective activePerspective, MApplication app, EPartService partService, EModelService modelService)
	{
		List<MPerspective> perspectives = modelService.findElements(app, "de.drazil.nerdsuite.perspective.imaging", MPerspective.class, null);
		for (MPerspective perspective : perspectives)
		{
			if (!perspective.equals(activePerspective))
			{
				partService.switchPerspective(perspective);
			}
		}
	}

}