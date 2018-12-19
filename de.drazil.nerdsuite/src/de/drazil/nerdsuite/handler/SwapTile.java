
package de.drazil.nerdsuite.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class SwapTile {
	@Execute
	public void execute(MPerspective activePerspective, MApplication app, EPartService partService,
			EModelService modelService, MPart part) {
		// ((IconEditor)part.getObject())
		System.out.println("swap tile");
	}

}