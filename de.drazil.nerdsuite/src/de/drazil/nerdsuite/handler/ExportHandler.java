
package de.drazil.nerdsuite.handler;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;

public class ExportHandler {

	@Inject
	private IEventBroker eventBroker;

	@Execute
	public void execute() {
		eventBroker.post("doExportFile", true);
	}
}