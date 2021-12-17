
package de.drazil.nerdsuite.handler;

import org.eclipse.e4.core.di.annotations.Execute;

import de.drazil.nerdsuite.log.Console;

public class ClearConsoleHandler {
	@Execute
	public void execute() {
		Console.clear();
	}

}