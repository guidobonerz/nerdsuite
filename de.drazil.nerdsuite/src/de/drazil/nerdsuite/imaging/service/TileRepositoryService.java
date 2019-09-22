package de.drazil.nerdsuite.imaging.service;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.widget.Tile;

public class TileRepositoryService extends AbstractImagingService {

	@JsonProperty(value = "tiles")
	private List<Tile> tileList = null;
	@JsonProperty(value = "tileIndexOrder")
	private List<Integer> tileIndexOrderList = null;
	@JsonIgnore
	private List<ITileManagementListener> tileServiceManagementListener = null;
	@JsonIgnore
	private List<ITileSelectionListener> tileServiceSelectionListener = null;
	@JsonIgnore
	private ImagePainterFactory imagePainterFactory;
	@JsonProperty(value = "selectedTile")
	private int selectedTileIndex = 0;

	public TileRepositoryService() {
		tileList = new ArrayList<>();
		tileIndexOrderList = new ArrayList<>();
		tileServiceManagementListener = new ArrayList<>();
		tileServiceSelectionListener = new ArrayList<>();
		imagePainterFactory = new ImagePainterFactory();
	}

	public void addTile(int size) {
		addTile("tile_" + (tileList.size() + 1), size);
	}

	public void addTile(String name, int size) {
		System.out.println("Add Tile");
		Tile tile = new Tile(name, size);
		tileList.add(tile);
		tileIndexOrderList.add(tileList.indexOf(tile));
		setSelectedTile(tileIndexOrderList.get(getSize() - 1));
		fireTileAdded();
	}

	public void removeLast() {
		if (tileIndexOrderList.size() > 0) {
			removeTile(tileIndexOrderList.size() - 1);
		}
	}

	public void removeSelected() {
		removeTile(selectedTileIndex);
	}

	public void removeTile(int index) {
		if (tileIndexOrderList.size() > 0) {
			System.out.println("Remove Tile");
			int tileIndex = tileIndexOrderList.get(index);
			tileList.remove(tileIndex);
			tileIndexOrderList.remove(index);
			fireTileRemoved();
		}
	}

	public void setSelectedTile(int index) {
		selectedTileIndex = index;
		fireTileSelected(getTile(index));
	}

	@JsonIgnore
	public Tile getSelectedTile() {
		return getTile(selectedTileIndex);
	}

	@JsonIgnore
	public int getSelectedTileIndex() {
		return tileList.indexOf(getSelectedTile());
	}

	public Tile getTile(int index) {
		return tileList.get(tileIndexOrderList.get(index));
	}

	@JsonIgnore
	public int getSize() {
		return tileList.size();
	}

	public ImagePainterFactory getImagePainterFactory() {
		return imagePainterFactory;
	}

	public void addTileManagementListener(ITileManagementListener... listeners) {
		for (ITileManagementListener listener : listeners) {
			addTileManagementListener(listener);
		}
	}

	public void addTileManagementListener(ITileManagementListener listener) {
		tileServiceManagementListener.add(listener);
	}

	public void removeTileManagementListener(ITileManagementListener listener) {
		tileServiceManagementListener.remove(listener);
	}

	public void addTileSelectionListener(ITileSelectionListener... listeners) {
		for (ITileSelectionListener listener : listeners) {
			addTileSelectionListener(listener);
		}
	}

	public void addTileSelectionListener(ITileSelectionListener listener) {
		tileServiceSelectionListener.add(listener);
	}

	public void removeTileSelectionListener(ITileSelectionListener listener) {
		tileServiceSelectionListener.remove(listener);
	}

	private void fireTileAdded() {
		tileServiceManagementListener.forEach(listener -> listener.tileAdded(getSelectedTile()));
	}

	private void fireTileRemoved() {
		tileServiceManagementListener.forEach(listener -> listener.tileRemoved());
	}

	private void fireTileReordered() {
		tileServiceManagementListener.forEach(listener -> listener.tileReordered());
	}

	public void fireTileSelected(Tile tile) {
		tileServiceSelectionListener.forEach(listener -> listener.tileSelected(tile));
	}

	public static void load(File fileName, String owner, Project project) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			TileRepositoryService service = mapper.readValue(fileName, TileRepositoryService.class);
			ServiceFactory.addService(owner, service, false);
			service.fireTileSelected(service.getSelectedTile());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void save(File fileName, TileRepositoryService service, Project project) {
		try {
			FileWriter fw = new FileWriter(fileName);
			fw.write("// Nerdsuite Project by drazil 2017-2019\n");
			fw.write("// Projectname : " + project.getName() + "\n");
			fw.write("// Targetplatform : " + project.getTargetPlatform() + "\n");
			fw.write("// Type : " + project.getProjectType() + "\n");
			fw.write("// Variant : " + project.getProjectSubType() + "\n");

			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			mapper.writeValue(fw, service);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
