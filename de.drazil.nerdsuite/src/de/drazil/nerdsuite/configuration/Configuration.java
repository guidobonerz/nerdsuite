package de.drazil.nerdsuite.configuration;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.model.Workspace;
import de.drazil.nerdsuite.util.FileUtil;

public class Configuration {

	private Logger log;
	public static final File WORKSPACE_PATH = Path
			.of(Constants.USER_HOME, Constants.FILE_SEPARATOR, Constants.DEFAULT_WORKSPACE_NAME).toFile();
	public static final File SETTINGS_FILE = Path.of(Constants.USER_HOME, Constants.FILE_SEPARATOR,
			Constants.DEFAULT_WORKSPACE_NAME, Constants.FILE_SEPARATOR, Constants.DEFAULT_SETTINGS_NAME).toFile();

	private Workspace workspace;

	public Configuration(IEclipseContext workbenchContext) {
		log = (Logger) workbenchContext.get(Logger.class.getName());
	}

	public final Workspace getWorkspace() {
		return createOrReadWorkspace();
	}

	public void initialize() {

		try {

			workspace = getWorkspace();

			Bundle bundle = Platform.getBundle(Constants.APP_ID);
			for (String pathFragment : Constants.FONT_LIST) {
				org.eclipse.core.runtime.Path path = new org.eclipse.core.runtime.Path(pathFragment);
				URL url = FileLocator.find(bundle, path, Collections.EMPTY_MAP);
				URL fileUrl = null;
				try {
					fileUrl = FileLocator.toFileURL(url);
				} catch (IOException e) {
					// Will happen if the file cannot be read for some reason
					e.printStackTrace();
				}
				File file = new File(fileUrl.getPath());
				boolean fontLoaded = Display.getCurrent().loadFont(file.toString());
				System.out.printf("font [ %s ] %s loaded\n", pathFragment, (fontLoaded ? "" : " not "));
			}

			/*
			 * try { for (String pathFragment : Constants.FONT_LIST) { String s =
			 * FileLocator.resolve(bundle.getEntry(pathFragment)).getPath();
			 * 
			 * boolean fontLoaded =Display.getCurrent().loadFont(
			 * "c:\\Users\\drazil\\git\\nerdsuite\\de.drazil.nerdsuite\\"+pathFragment);
			 * System.out.printf("font [ %s ] %s loaded\n", s,(fontLoaded?"":" not "));
			 * 
			 * } } catch (IOException e1) { e1.printStackTrace(); }
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final Workspace createOrReadWorkspace() {

		try {
			if (!WORKSPACE_PATH.exists()) {
				log.debug("create new workspace folder...");
				WORKSPACE_PATH.mkdir();
				workspace = new Workspace();
				updateWorkspace(null, false, false);
				SETTINGS_FILE.createNewFile();
			} else {
				if (!SETTINGS_FILE.exists()) {
					FileWriter fw = new FileWriter(SETTINGS_FILE);
					fw.write("#ultimate64 settings\n" + "u64.ip=\n" + "u64.port=\n" + "client.adress=");
					fw.flush();
					fw.close();
				} else {
					if (null != System.getProperty("nerdsuitePropertiesRead")) {
						Properties p = new Properties();
						p.load(new FileReader(SETTINGS_FILE));
						System.setProperties(p);
						System.setProperty("nerdsuitePropertiesRead", "true");
					}
				}
			}

			if (workspace == null) {
				log.debug("read workspace file...");
				ObjectMapper mapper = new ObjectMapper();
				workspace = mapper.readValue(new File(WORKSPACE_PATH + Constants.FILE_SEPARATOR + ".projects.json"),
						Workspace.class);
			}

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

		return workspace;
	}

	public final void updateWorkspace(Project project, boolean addProject, boolean mountOnly) {
		if (addProject) {
			workspace.add(project);
			try {
				if (!mountOnly) {
					File file = FileUtil.getFileFromProject(project);
					file.createNewFile();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			File projectPath = new File(WORKSPACE_PATH + Constants.FILE_SEPARATOR + ".projects.json");
			log.debug(projectPath.getAbsolutePath());
			mapper.writeValue(projectPath, workspace);
		} catch (JsonGenerationException e) {
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
}
