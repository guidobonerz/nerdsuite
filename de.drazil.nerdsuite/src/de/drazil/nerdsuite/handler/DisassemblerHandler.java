
package de.drazil.nerdsuite.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;

import de.drazil.nerdsuite.util.E4Utils;

public class DisassemblerHandler {
	@Execute
	public void execute(MPerspective activePerspective, MApplication app, IWorkbench workbench, Shell shell,
			EPartService partService, EModelService modelService) {
		MPart part = E4Utils.createPart(partService, "de.drazil.nerdsuite.partdescriptor.DisassemblerView",
				"bundleclass://de.drazil.nerdsuite/de.drazil.nerdsuite.disassembler.DisassemblerView", "Disassembler",
				"Disassembler", null);

		E4Utils.addPart2PartStack(app, modelService, partService, "de.drazil.nerdsuite.partstack.editorStack", part,
				true);
	}

}