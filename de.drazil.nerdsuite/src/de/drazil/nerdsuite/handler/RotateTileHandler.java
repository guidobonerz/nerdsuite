
package de.drazil.nerdsuite.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.imaging.service.RotationService;
import de.drazil.nerdsuite.imaging.service.ServiceFactory;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;

public class RotateTileHandler {
	@Execute
	public void execute(MMenuItem item, MPart part,
			@Named("de.drazil.nerdsuite.commandparameter.Turn") String turn) {
		RotationService service = ServiceFactory.getService((String) part.getTransientData().get(Constants.OWNER),
				RotationService.class);
		service.setImagingWidgetConfiguration((ImagingWidgetConfiguration) part.getTransientData().get("CONFIG"));
		service.execute(Integer.valueOf(turn));
		System.out.println("rotate direction:" + turn);
	}

}