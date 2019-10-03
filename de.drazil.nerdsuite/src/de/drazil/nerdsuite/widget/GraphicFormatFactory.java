package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.drazil.nerdsuite.model.GraphicFormat;
import de.drazil.nerdsuite.model.GraphicFormatVariant;

public class GraphicFormatFactory {
	private static List<GraphicFormat> graphicFormatList;

	public static GraphicFormat getFormatByName(String name) {

		if (null == graphicFormatList) {
			graphicFormatList = new ArrayList<>();
			Bundle bundle = Platform.getBundle("de.drazil.nerdsuite");
			ObjectMapper mapper = new ObjectMapper();
			try {
				graphicFormatList = Arrays.asList(
						mapper.readValue(bundle.getEntry("configuration/graphic_formats.json"), GraphicFormat[].class));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return graphicFormatList.stream().filter(gf -> gf.getId().equals(name)).findFirst().orElse(null);
	}

	public static List<GraphicFormat> getFormatByPrefix(String name) {

		if (null == graphicFormatList) {
			graphicFormatList = new ArrayList<>();
			Bundle bundle = Platform.getBundle("de.drazil.nerdsuite");
			ObjectMapper mapper = new ObjectMapper();
			try {
				graphicFormatList = Arrays.asList(
						mapper.readValue(bundle.getEntry("configuration/graphic_formats.json"), GraphicFormat[].class));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return graphicFormatList.stream().filter(gf -> gf.getId().startsWith(name)).collect(Collectors.toList());
	}

	public static List<GraphicFormatVariant> getFormatVariantListByPrefix(String name) {
		return getFormatByName(name).getVariants();
	}

	public static GraphicFormatVariant getGraphicFormatVariantByName(String name, String variant) {
		return getFormatByName(name).getVariants().stream().filter(v -> v.getId().equalsIgnoreCase(variant)).findFirst()
				.orElse(null);
	}

}
