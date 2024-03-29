
package de.drazil.nerdsuite.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.enums.AnimationMode;

public class AnimationHandler {
	@Execute
	public void execute(MPart part, @Named("de.drazil.nerdsuite.commandparameter.AnimationMode") String animationMode,
			IEventBroker broker) {
		broker.send("AnimationMode", new BrokerObject((String) part.getTransientData().get(Constants.OWNER),
				AnimationMode.getAnimationModeByName(animationMode)));
	}
}