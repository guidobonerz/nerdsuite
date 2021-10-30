package de.drazil.nerdsuite.basic;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;

import org.eclipse.swt.graphics.Rectangle;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.configuration.Initializer;
import de.drazil.nerdsuite.imaging.service.IService;
import de.drazil.nerdsuite.model.BasicSourceMetadata;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.model.SourceContainer;
import de.drazil.nerdsuite.util.FileUtil;
import lombok.Getter;
import lombok.Setter;

public class SourceRepositoryService implements IService {
	@Getter
	@Setter
	private String owner = null;
	@Getter
	@Setter
	private Rectangle selection;
	private SourceContainer<BasicSourceMetadata> container;

	public SourceRepositoryService() {
		container = new SourceContainer<BasicSourceMetadata>();
	}

	public void setMetadata(BasicSourceMetadata metadata) {
		container.setMetadata(metadata);
	}

	public BasicSourceMetadata getMetadata() {
		return container.getMetadata();
	}

	public SourceContainer<BasicSourceMetadata> load(String id) {
		return load(id, false);
	}

	public SourceContainer<BasicSourceMetadata> load(String id, boolean isReference) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
		try {
			Project project = Initializer.getConfiguration().getWorkspace().getProjectById(id);
			File file = FileUtil.getFileFromProject(project);
			container = mapper.readValue(file, SourceContainer.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return container;
	}

	public void save(Project project) {
		try {
			File file = FileUtil.getFileFromProject(project);
			FileWriter fw = new FileWriter(file);
			fw.write(getHeaderText(project, container.getMetadata()));
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			mapper.writeValue(fw, container);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String getHeaderText(Project project, BasicSourceMetadata metadata) {
		String s = String.format(Constants.PROJECT_FILE_INFO_HEADER, project.getName(),
				DateFormat.getDateInstance(DateFormat.SHORT).format(project.getCreatedOn()),
				DateFormat.getDateInstance(DateFormat.SHORT).format(project.getChangedOn()));
		return s;
	}
}
