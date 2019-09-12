
package de.drazil.nerdsuite.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;

import de.drazil.nerdsuite.imaging.service.ServiceFactory;
import de.drazil.nerdsuite.imaging.service.TileRepositoryService;

public class TileHandler {
	@Execute
	public void execute(final MToolItem item, MPart part,
			@Named("de.drazil.nerdsuite.commandparameter.Tile") String tileAction) {
		TileRepositoryService service = ServiceFactory.getService(part.getProperties().get("OWNER"),
				TileRepositoryService.class);
		if (tileAction.equalsIgnoreCase("add")) {
			service.addTile("rename_me", service.getSelectedTile().getActiveLayer().size());
		} else if (tileAction.equalsIgnoreCase("remove")) {
			service.removeSelected();
		} else {
		}
	}
}