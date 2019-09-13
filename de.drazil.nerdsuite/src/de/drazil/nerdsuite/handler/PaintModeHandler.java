
package de.drazil.nerdsuite.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;

public class PaintModeHandler {
	@Execute
	public void execute(MToolItem item, MPart part,
			@Named("de.drazil.nerdsuite.commandparameter.PaintMode") String paintMode) {
		// ShiftService service = ServiceFactory.getService((String)
		// part.getTransientData().get(Constants.OWNER),ShiftService.class);
		// service.setImagingWidgetConfiguration((ImagingWidgetConfiguration)
		// part.getTransientData().get("CONFIG"));
		// service.execute(Integer.valueOf(direction));
		System.out.println("paint mode:" + paintMode);
	}

}