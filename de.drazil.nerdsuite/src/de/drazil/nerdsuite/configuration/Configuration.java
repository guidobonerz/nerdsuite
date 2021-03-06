package de.drazil.nerdsuite.configuration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Font;
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
	private Font font = null;

	public final Workspace getWorkspace() {
		return createOrReadWorkspace();
	}

	public void initialize() {
		try {

			workspace = getWorkspace();
			Bundle bundle = Platform.getBundle(Constants.APP_ID);
			URL fileURL1 = bundle.getEntry("/fonts/C64_Pro_Mono-STYLE.ttf");
			URL fileURL2 = bundle.getEntry("/fonts/DroidSansMono.ttf");
			URL fileURL3 = bundle.getEntry("/fonts/PTm55F.ttf");
			URL fileURL4 = bundle.getEntry("/fonts/SourceCodePro-Regular.ttf");
			URL fileURL5 = bundle.getEntry("/fonts/PetMe2Y.ttf");
			URL fileURL6 = bundle.getEntry("/fonts/PetMe642Y.ttf");
			URL fileURL7 = bundle.getEntry("/fonts/PetMe64.ttf");
			URL fileURL8 = bundle.getEntry("/fonts/fa-solid-900.ttf");
			URL fileURL9 = bundle.getEntry("/fonts/icomoon.ttf");
			URL fileURL10 = bundle.getEntry("/fonts/MaterialIcons-Regular.ttf");
			File file = null;
			boolean b;
			try {
				file = new File(FileLocator.resolve(fileURL1).toURI());
				b = Display.getCurrent().loadFont(file.toString());
				// b = Display.getCurrent().loadFont(file.toString());
				// b = Display.getCurrent().loadFont( new
				// URL("platform:/plugin/de.drazil.nerdsuite/fonts/C64_Pro_Mono-STYLE.ttf").toString());

				file = new File(FileLocator.resolve(fileURL2).toURI());
				b = Display.getCurrent().loadFont(file.toString());

				file = new File(FileLocator.resolve(fileURL3).toURI());
				b = Display.getCurrent().loadFont(file.toString());

				file = new File(FileLocator.resolve(fileURL4).toURI());
				b = Display.getCurrent().loadFont(file.toString());
				file = new File(FileLocator.resolve(fileURL5).toURI());
				b = Display.getCurrent().loadFont(file.toString());
				file = new File(FileLocator.resolve(fileURL6).toURI());
				b = Display.getCurrent().loadFont(file.toString());
				file = new File(FileLocator.resolve(fileURL7).toURI());
				b = Display.getCurrent().loadFont(file.toString());
				file = new File(FileLocator.resolve(fileURL8).toURI());
				b = Display.getCurrent().loadFont(file.toString());
				file = new File(FileLocator.resolve(fileURL9).toURI());
				b = Display.getCurrent().loadFont(file.toString());
				file = new File(FileLocator.resolve(fileURL10).toURI());
				b = Display.getCurrent().loadFont(file.toString());

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
