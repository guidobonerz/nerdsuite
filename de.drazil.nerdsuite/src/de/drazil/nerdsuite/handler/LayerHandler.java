
package de.drazil.nerdsuite.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.imaging.service.ServiceFactory;
import de.drazil.nerdsuite.imaging.service.TileRepositoryService;

public class LayerHandler {
	@Execute
	public void execute(final MToolItem item, MPart part,
			@Named("de.drazil.nerdsuite.commandparameter.Layer") String layerAction) {
		TileRepositoryService service = ServiceFactory.getService(part.getProperties().get(Constants.OWNER),
				TileRepositoryService.class);
		if (layerAction.equalsIgnoreCase("add")) {
			service.getSelectedTile().addLayer();
		} else if (layerAction.equalsIgnoreCase("remove")) {
			service.getSelectedTile().removeActiveLayer();
		} else {
		}
	}
}