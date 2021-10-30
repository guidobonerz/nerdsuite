package de.drazil.nerdsuite.util;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGBA;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class SwtUtil {
	public final static RGBA toRGB(String hexValue) {
		return new RGBA(Integer.valueOf(hexValue.substring(1, 3), 16), Integer.valueOf(hexValue.substring(3, 5), 16),
				Integer.valueOf(hexValue.substring(5, 7), 16), 255);
	}

	public static Font loadMonospacedFont(Display display) {
		String jreHome = System.getProperty("java.home");
		File file = new File(jreHome, "/lib/fonts/LucidaTypewriterRegular.ttf");
		if (!file.exists()) {
			throw new IllegalStateException(file.toString());
		}
		if (!display.loadFont(file.toString())) {
			throw new IllegalStateException(file.toString());
		}
		final Font font = new Font(display, "Lucida Sans Typewriter", 10, SWT.NORMAL);
		display.addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event event) {
				font.dispose();
			}
		});
		return font;
	}
}
