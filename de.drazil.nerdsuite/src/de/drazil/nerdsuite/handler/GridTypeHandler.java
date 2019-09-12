
package de.drazil.nerdsuite.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;

import de.drazil.nerdsuite.constants.GridType;
import de.drazil.nerdsuite.imaging.GfxEditorView;
import de.drazil.nerdsuite.model.GridState;

public class GridTypeHandler {
	@Execute
	public void execute(final MToolItem item, IEventBroker broker,
			@Named("de.drazil.nerdsuite.commandparameter.GridType") String gridType,
			@Named("de.drazil.nerdsuite.commandparameter.GridVisible") String visible, MPart part) {
		
		((GfxEditorView) part.getObject())
				.controlGridState(new GridState(new Boolean(visible), GridType.values()[Integer.valueOf(gridType)]));
		// broker.post("GridState", new GridState(new Boolean(visible),
		// GridType.values()[Integer.valueOf(gridType)]));
	}
}