package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.drazil.nerdsuite.model.SimpleEntity;

public class ProjectTypeFactory {
	private static List<SimpleEntity> projectTypeList;

	public static SimpleEntity getProjectTypeByName(String name) {

		if (null == projectTypeList) {
			projectTypeList = new ArrayList<>();
			Bundle bundle = Platform.getBundle("de.drazil.nerdsuite");
			ObjectMapper mapper = new ObjectMapper();
			try {
				projectTypeList = Arrays.asList(
						mapper.readValue(bundle.getEntry("configuration/project_types.json"), SimpleEntity[].class));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return projectTypeList.stream().filter(gf -> gf.getId().equals(name)).findFirst().orElse(null);
	}
}
