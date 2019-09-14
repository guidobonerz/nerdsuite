
package de.drazil.nerdsuite.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;

public class InvertTileHandler {
	@Execute
	public void execute(MMenuItem item, MPart part) {
		System.out.println("InvertTile");
	}
}