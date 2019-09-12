
package de.drazil.nerdsuite.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.imaging.service.PurgeService;
import de.drazil.nerdsuite.imaging.service.ServiceFactory;

public class PurgeTileHandler {
	@Execute
	public void execute(MMenuItem item, MPart part, @Named("de.drazil.nerdsuite.commandparameter.Mode") String mode) {
		PurgeService service = ServiceFactory.getService(part.getProperties().get(Constants.OWNER), PurgeService.class);
		service.execute();
		System.out.println("purge mode:" + mode);
	}

}