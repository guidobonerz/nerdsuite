package de.drazil.nerdsuite.util;

import java.io.File;
import java.nio.file.Path;

import de.drazil.nerdsuite.configuration.Configuration;
import de.drazil.nerdsuite.model.Project;

public class FileUtil {
	public static File getFileFromProject(Project project) {
		return Path.of(Configuration.WORKSPACE_PATH.toString(), project.getName() + "." + project.getSuffix()).toFile();
	}
}
