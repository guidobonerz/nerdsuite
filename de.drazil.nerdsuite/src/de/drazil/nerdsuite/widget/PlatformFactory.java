package de.drazil.nerdsuite.widget;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.drazil.nerdsuite.model.BasicInstructions;
import de.drazil.nerdsuite.model.CharMap;
import de.drazil.nerdsuite.model.CharObject;
import de.drazil.nerdsuite.model.CpuInstructions;
import de.drazil.nerdsuite.model.PlatformColor;
import de.drazil.nerdsuite.model.PlatformData;
import de.drazil.nerdsuite.model.TargetPlatform;

public class PlatformFactory {

	private static List<TargetPlatform> targetPlatformList;
	private static Map<String, TargetPlatform> platformCache = new HashMap<>();
	private static Map<String, List<PlatformColor>> platformColorCache = new HashMap<>();

	public static List<TargetPlatform> getTargetPlatFormList() {

		if (targetPlatformList == null) {
			Bundle bundle = Platform.getBundle("de.drazil.nerdsuite");
			ObjectMapper mapper = new ObjectMapper();
			try {
				targetPlatformList = Arrays
						.asList(mapper.readValue(bundle.getEntry("configuration/platform.json"),
								TargetPlatform[].class))
						.stream().filter(c -> c.isEnabled()).collect(Collectors.toList());
			} catch (Exception e) {
				targetPlatformList = null;
			}
		}
		return targetPlatformList;
	}

	public static TargetPlatform getTargetPlatform(String id) {
		TargetPlatform platform = platformCache.get(id);
		if (platform == null) {
			platform = getTargetPlatFormList().stream().filter(p -> p.getId().equalsIgnoreCase(id)).findFirst()
					.orElse(null);
			platformCache.put(id, platform);
		}
		return platform;
	}

	public static BasicInstructions getBasicInstructions(String id) {

		BasicInstructions basicInstructions = null;
		Bundle bundle = Platform.getBundle("de.drazil.nerdsuite");
		ObjectMapper mapper = new ObjectMapper();

		try {
			PlatformData platformData = mapper.readValue(bundle.getEntry(getTargetPlatform(id).getSource()),
					PlatformData.class);
			basicInstructions = mapper.readValue(bundle.getEntry(platformData.getBasicInstructionSource()),
					BasicInstructions.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return basicInstructions;
	}

	public static CpuInstructions getCpuInstructions(String id) {

		CpuInstructions cpuInstruction = null;
		Bundle bundle = Platform.getBundle("de.drazil.nerdsuite");
		ObjectMapper mapper = new ObjectMapper();

		try {
			PlatformData platformData = mapper.readValue(bundle.getEntry(getTargetPlatform(id).getSource()),
					PlatformData.class);
			cpuInstruction = mapper.readValue(bundle.getEntry(platformData.getCpuInstructionSource()),
					CpuInstructions.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return cpuInstruction;
	}

	public static CharMap getCharMap(String id) {

		CharMap charMap = null;
		Bundle bundle = Platform.getBundle("de.drazil.nerdsuite");
		ObjectMapper mapper = new ObjectMapper();

		try {
			PlatformData platformData = mapper.readValue(bundle.getEntry(getTargetPlatform(id).getSource()),
					PlatformData.class);
//new TypeReference<List<CharObject>>() {
			charMap = charMap = mapper.readValue(bundle.getEntry(platformData.getCharMapSource()), CharMap.class);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return charMap;
	}

	public static List<PlatformColor> getPlatformColors(String id) {

		List<PlatformColor> platformColors = platformColorCache.get(id);
		if (platformColors == null) {
			Bundle bundle = Platform.getBundle("de.drazil.nerdsuite");
			ObjectMapper mapper = new ObjectMapper();
			PlatformData platformData = null;
			try {
				platformData = mapper.readValue(bundle.getEntry(getTargetPlatform(id).getSource()), PlatformData.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			platformColors = platformData.getColorPalette();
			platformColorCache.put(id, platformColors);
		}
		return platformColors;
	}
}
