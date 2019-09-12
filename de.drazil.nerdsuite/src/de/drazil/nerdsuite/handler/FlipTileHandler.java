
package de.drazil.nerdsuite.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.imaging.service.FlipService;
import de.drazil.nerdsuite.imaging.service.ServiceFactory;

public class FlipTileHandler {
	@Execute
	public void execute(MMenuItem item, MPart part,
			@Named("de.drazil.nerdsuite.commandparameter.Orientation") String orientation) {
		FlipService service = ServiceFactory.getService(part.getProperties().get(Constants.OWNER), FlipService.class);
		service.execute(Integer.valueOf(orientation));
		System.out.println("flip orientation:" + orientation);
	}

}