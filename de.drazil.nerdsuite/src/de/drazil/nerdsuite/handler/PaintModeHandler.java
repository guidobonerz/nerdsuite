
package de.drazil.nerdsuite.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;

import de.drazil.nerdsuite.constants.PaintMode;

public class PaintModeHandler {
	@Execute
	public void execute(final MToolItem item, IEventBroker broker,
			@Named("de.drazil.nerdsuite.commandparameter.PaintMode") String paintMode) {
		broker.post("PaintMode", PaintMode.values()[Integer.valueOf(paintMode)]);
	}
}