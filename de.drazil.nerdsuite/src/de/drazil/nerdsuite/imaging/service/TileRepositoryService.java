package de.drazil.nerdsuite.imaging.service;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.util.List;

import org.eclipse.swt.graphics.Rectangle;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.configuration.Initializer;
import de.drazil.nerdsuite.model.GraphicMetadata;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.util.FileUtil;
import de.drazil.nerdsuite.widget.Layer;
import de.drazil.nerdsuite.widget.Tile;
import de.drazil.nerdsuite.widget.TileContainer;
import lombok.Getter;
import lombok.Setter;

public class TileRepositoryService implements IService {
	@Getter
	@Setter
	private String owner = null;
	@Getter
	@Setter
	private Rectangle selection;
	private TileContainer container;

	public TileRepositoryService getReferenceRepository() {
		TileRepositoryService service = null;
		if (ServiceFactory.checkService(getMetadata().getReferenceId())) {
			service = ServiceFactory.getService(getMetadata().getReferenceId(), TileRepositoryService.class);
		}
		return service;
	}

	public TileRepositoryService() {
		container = new TileContainer();
	}

	public void init(int tileCount) {
		container.addInitialTiles(tileCount);
	}

	public boolean hasReference() {
		return getReferenceRepository() != null;
	}

	public List<Tile> getTileList() {
		return container.getTileList();
	}

	public void setMetadata(GraphicMetadata metadata) {
		container.setMetadata(metadata);
	}

	public GraphicMetadata getMetadata() {
		return container.getMetadata();
	}

	public String getSelectedTileName() {
		return getSelectedTile().getName();
	}

	public String getTileName(int index) {
		return getTile(index).getName();
	}

	public Tile getSelectedTile() {
		return container.getSelectedTile();
	}

	public Layer getActiveLayerFromSelectedTile() {
		return getSelectedTile().getActiveLayer();
	}

	public Layer getActiveLayerFromTile(int index) {
		return getTile(index).getActiveLayer();
	}

	public int getSelectedTileIndex() {
		return getSelectedTileIndex(false);
	}

	public int getSelectedTileIndex(boolean natural) {
		return container.getSelectedTileIndex(natural);
	}

	public void setSelectedTileIndex(int index) {
		container.setSelectedTileIndex(index);
	}

	public void setSelectedTileIndexList(List<Integer> tileIndexList) {
		container.setSelectedTileIndexList(tileIndexList);
	}

	public List<Integer> getSelectedTileIndexList() {
		return container.getSelectedTileIndexList();
	}

	public void moveTile(int from, int to) {
		container.moveTile(from, to);
	}

	public void setDirty(boolean dirty) {
		container.setDirty(dirty);
	}

	public int getSize() {
		return container.getSize();
	}

	public Tile getTile(int index) {
		return container.getTile(index);
	}

	public Tile getTile(int index, boolean naturalOrder) {
		return container.getTile(index, naturalOrder);
	}

	public int getTileIndex(int index) {
		return container.getTileIndex(index);
	}

	public Tile addTile() {
		return container.addTile();
	}

	public void addTileManagementListener(ITileManagementListener... listeners) {
		container.addTileManagementListener(listeners);
	}

	public void addTileUpdateListener(ITileUpdateListener... listeners) {
		container.addTileUpdateListener(listeners);
	}

	public void redrawTileViewer(List<Integer> selectedTileIndexList, int action, boolean temporary) {
		container.redrawTileViewer(selectedTileIndexList, action, temporary);
	}

	public TileContainer load(String id) {
		return load(id, false);
	}

	public TileContainer load(String id, boolean isReference) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
		try {
			Project project = Initializer.getConfiguration().getWorkspace().getProjectById(id);
			File file = FileUtil.getFileFromProject(project);
			container = mapper.readValue(file, TileContainer.class);

			String referenceId = container.getMetadata().getReferenceId();
			if (null != referenceId) {
				Project referenceProject = Initializer.getConfiguration().getWorkspace().getProjectById(referenceId);
				TileRepositoryService referenceRepository = ServiceFactory.getService(referenceId,
						TileRepositoryService.class);
				referenceRepository.load(referenceProject.getId(), true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return container;
	}

	public void save(Project project) {
		try {
			/*
			 * FileOutputStream fos = new
			 * FileOutputStream("c:/Users/drazil/compressed_file.ns_file"); GZIPOutputStream
			 * gzipout = new GZIPOutputStream(fos); gzipout.write(getHeaderText(project,
			 * container.getMetadata()).getBytes()); ObjectMapper mapper1 = new
			 * ObjectMapper(); mapper1.writeValue(gzipout, container);
			 */
			File file = FileUtil.getFileFromProject(project);
			FileWriter fw = new FileWriter(file);
			fw.write(getHeaderText(project, container.getMetadata()));
			ObjectMapper mapper2 = new ObjectMapper();
			mapper2.enable(SerializationFeature.INDENT_OUTPUT);
			mapper2.writeValue(fw, container);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String getHeaderText(Project project, GraphicMetadata metadata) {
		String s = String.format(Constants.PROJECT_FILE_INFO_HEADER, project.getName(),
				DateFormat.getDateInstance(DateFormat.SHORT).format(project.getCreatedOn()),
				DateFormat.getDateInstance(DateFormat.SHORT).format(project.getChangedOn()));
		return s;
	}
}
