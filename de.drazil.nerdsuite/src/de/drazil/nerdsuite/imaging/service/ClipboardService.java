package de.drazil.nerdsuite.imaging.service;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Rectangle;

import de.drazil.nerdsuite.enums.Selection;
import de.drazil.nerdsuite.widget.Layer;

public class ClipboardService implements IService {

    public final static int OFF = 0;
    public final static int CUT = 1;
    public final static int COPY = 2;
    public final static int PASTE = 4;
    private int initialAction;
    private List<Integer> selectionList;
    private Rectangle rangeSelection;
    private Selection selectionType = Selection.Unkown;
    private String sourceOwner;

    @Override
    public void setOwner(String owner) {

    }

    public void clipboardAction(int action, Selection selection, String owner) {

        if (action == CUT || action == COPY) {
            sourceOwner = owner;
            TileRepositoryService service = ServiceFactory.getService(owner, TileRepositoryService.class);
            initialAction = action;
            selectionType = selection;
            if (selection == Selection.List) {
                selectionList = new ArrayList<Integer>(service.getSelectedTileIndexList());
            } else if (selection == Selection.Range) {
                rangeSelection = service.getSelection();
            } else {

            }

        } else if (action == PASTE) {
            TileRepositoryService sourceService = ServiceFactory.getService(sourceOwner, TileRepositoryService.class);
            TileRepositoryService targetService = ServiceFactory.getService(owner, TileRepositoryService.class);
            if (selectionType == Selection.List) {
                List<Integer> targetSelectionList = targetService.getSelectedTileIndexList();
                for (int i = 0; i < selectionList.size(); i++) {
                    Layer sourceLayer = sourceService.getActiveLayerFromTile(selectionList.get(i));
                    Layer targetLayer = targetService.getActiveLayerFromTile(targetSelectionList.get(i));

                    targetLayer.setColorPalette(sourceLayer.getColorPalette());
                    for (int j = 0; j < sourceLayer.getContent().length; j++) {
                        targetLayer.getContent()[j] = sourceLayer.getContent()[j];
                        String ri = sourceService.getMetadata().getReferenceId();
                        if (ri != null) {
                            targetLayer.getBrush()[j] = sourceLayer.getBrush()[j];
                        }

                    }
                    targetLayer.setDirty(true);

                    if (initialAction == CUT) {
                        sourceService.getActiveLayerFromTile(selectionList.get(i)).reset(0, 0);
                    }
                    targetService.redrawTileViewer(targetSelectionList, ImagePainterFactory.UPDATE, false);
                }
            } else if (selectionType == Selection.Range) {
                int width = sourceService.getMetadata().getWidth();
                Layer sourceLayer = sourceService.getActiveLayerFromTile(sourceService.getSelectedTileIndex());
                Layer targetLayer = targetService.getActiveLayerFromTile(targetService.getSelectedTileIndex());
                for (int y = rangeSelection.y, c = 0; y < rangeSelection.y + rangeSelection.height; y++) {
                    for (int x = rangeSelection.x; x < rangeSelection.x + rangeSelection.width; x++, c++) {
                        targetLayer.getContent()[c] = sourceLayer.getContent()[x + (y * width)];
                        targetLayer.getBrush()[c] = sourceLayer.getBrush()[x + (y * width)];
                    }
                }
            }
        } else {
        }
    }
}
