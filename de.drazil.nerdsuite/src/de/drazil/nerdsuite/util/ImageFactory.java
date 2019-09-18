package de.drazil.nerdsuite.util;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

public class ImageFactory {

	private static Map<String, Image> imageCache = new HashMap<>();

	public static Image createImage(String name) {

		Image image = imageCache.get(name);
		if (image == null) {
			Bundle bundle = Platform.getBundle("de.drazil.nerdsuite");
			URL url = FileLocator.find(bundle, new Path(name), null);
			ImageDescriptor imageDesc = ImageDescriptor.createFromURL(url);
			image = imageDesc.createImage();
			imageCache.put(name, image);
		}
		return image;
	}
}
