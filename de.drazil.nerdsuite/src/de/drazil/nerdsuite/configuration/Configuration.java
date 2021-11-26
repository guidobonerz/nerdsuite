package de.drazil.nerdsuite.configuration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
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
	public static final File WORKSPACE_PATH = new File(
			Constants.USER_HOME + Constants.FILE_SEPARATOR + Constants.DEFAULT_WORKSPACE_NAME);

	private Workspace workspace;

	public final Workspace getWorkspace() {
		return createOrReadWorkspace();
	}

	public void initialize() {
		try {

			workspace = getWorkspace();
			Bundle bundle = Platform.getBundle(Constants.APP_ID);
			URL fileURL1 = bundle.getEntry("/fonts/ttf/C64_Pro_Mono-STYLE.ttf");
			URL fileURL2 = bundle.getEntry("/fonts/ttf/MaterialIcons-Regular.ttf");
			URL fileURL3 = bundle.getEntry("/fonts/ttf/RobotoMono-Bold.ttf");

			File file = null;

			try {
				file = new File(FileLocator.resolve(fileURL1).toURI());
				Display.getCurrent().loadFont(file.toString());

				file = new File(FileLocator.resolve(fileURL2).toURI());
				Display.getCurrent().loadFont(file.toString());

				file = new File(FileLocator.resolve(fileURL3).toURI());
				Display.getCurrent().loadFont(file.toString());

			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final Workspace createOrReadWorkspace() {

		try {
			if (!WORKSPACE_PATH.exists()) {
				System.out.println("create new workspace folder...");
				WORKSPACE_PATH.mkdir();
				workspace = new Workspace();
				updateWorkspace(null, false, false);
			}

			if (workspace == null) {
				System.out.println("read workspace file...");
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
			mapper.writeValue(new File(WORKSPACE_PATH + Constants.FILE_SEPARATOR + ".projects.json"), workspace);
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
