
package de.drazil.nerdsuite.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;

public class NewImageHandler {
	@Execute
	public void execute(@Named("de.drazil.nerdsuite.commandparameter.width") String width,
			@Named("de.drazil.nerdsuite.commandparameter.width") String height) {
		System.out.println(width + ":" + height);
	}

}