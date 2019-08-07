package de.drazil.nerdsuite.widget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.drazil.nerdsuite.model.GraphicFormat;

public class GraphicFormatFactory {
	private static List<GraphicFormat> graphicFormatList;

	public static GraphicFormat getFormatByName(String name) {
		// GraphicFormat returnValue = null;
		if (null == graphicFormatList) {
			graphicFormatList = new ArrayList<>();
			Bundle bundle = Platform.getBundle("de.drazil.nerdsuite");
			ObjectMapper mapper = new ObjectMapper();
			try {
				graphicFormatList = Arrays.asList(
						mapper.readValue(bundle.getEntry("configuration/graphic_formats.json"), GraphicFormat[].class));
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return graphicFormatList.stream().filter(gf -> gf.getId().equals(name)).findFirst().orElse(null);
	}
}
