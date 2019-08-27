package de.drazil.nerdsuite.util;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FontFactory {
	private static Map<String, String> fontSource = new HashMap<String, String>();
	private static Map<String, Font> fontCache = new HashMap<>();

	public static Font getFont(String fontName) {
		Font font = fontCache.get(fontName);
		if (font == null) {
			Bundle bundle = Platform.getBundle("de.drazil.nerdsuite");
			ObjectMapper mapper = new ObjectMapper();
			try {
				String[] s = fontName.split("\\|");
				fontSource = mapper.readValue(bundle.getEntry("configuration/fonts.json"), HashMap.class);
				URL url = bundle.getEntry("/fonts/" + fontSource.get(s[0]));
				File file = new File(FileLocator.resolve(url).toURI());
				Display.getCurrent().loadFont(file.toString());
				font = new Font(Display.getCurrent(), s[0], Integer.valueOf(s[1]), SWT.NORMAL);
				fontCache.put(fontName, font);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return font;
	}
}
