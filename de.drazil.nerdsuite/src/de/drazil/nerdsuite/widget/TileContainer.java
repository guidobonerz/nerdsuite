package de.drazil.nerdsuite.widget;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.drazil.nerdsuite.model.ProjectMetaData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TileContainer {

	private ProjectMetaData metadata = null;
	@JsonProperty(value = "tiles")
	private List<Tile> tileList = null;
	@JsonProperty(value = "tileIndexOrder")
	private List<Integer> tileIndexOrderList = null;
	@JsonProperty(value = "selectedTiles")
	private List<Integer> selectedTileIndexList = null;
	private String referenceRepositoryLocation = null;

}
