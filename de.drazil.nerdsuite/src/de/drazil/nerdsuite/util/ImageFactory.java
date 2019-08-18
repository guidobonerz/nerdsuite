package de.drazil.nerdsuite.util;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

public class ImageFactory {
	public static Image createImage(String name) {

		Bundle bundle = Platform.getBundle("de.drazil.nerdsuite");
		URL url = FileLocator.find(bundle, new Path(name), null);
		ImageDescriptor imageDesc = ImageDescriptor.createFromURL(url);
		Image image = imageDesc.createImage();
		return image;
	}
}
