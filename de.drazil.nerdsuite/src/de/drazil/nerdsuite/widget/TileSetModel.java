package de.drazil.nerdsuite.widget;

import java.util.List;

import lombok.Data;

@Data
public class TileSetModel {
	private String name = null;
	private int activeSet = 0;
	private List<TileModel> tileModelList = null;
}
