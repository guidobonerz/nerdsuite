 
package de.drazil.nerdsuite.widget;

import java.util.List;

import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.di.AboutToHide;

import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;

public class Test {
	@AboutToShow
	public void aboutToShow(List<MMenuElement> items) {
		
	}
	
	
	@AboutToHide
	public void aboutToHide(List<MMenuElement> items) {
		
	}
		
}