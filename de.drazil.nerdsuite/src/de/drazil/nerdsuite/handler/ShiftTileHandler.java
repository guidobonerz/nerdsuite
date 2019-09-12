
package de.drazil.nerdsuite.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.imaging.service.ServiceFactory;
import de.drazil.nerdsuite.imaging.service.ShiftService;

public class ShiftTileHandler {
	@Execute
	public void execute(MMenuItem item, MPart part,
			@Named("de.drazil.nerdsuite.commandparameter.Direction") String direction) {
		ShiftService service = ServiceFactory.getService(part.getProperties().get(Constants.OWNER), ShiftService.class);
		service.execute(Integer.valueOf(direction));
		System.out.println("shift direction:" + direction);
	}

}