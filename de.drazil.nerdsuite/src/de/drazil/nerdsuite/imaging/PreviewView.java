
package de.drazil.nerdsuite.imaging;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.assembler.InstructionSet;
import de.drazil.nerdsuite.widget.ImageViewer;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration.GridStyle;

public class PreviewView {
	@Inject
	public PreviewView() {

	}

	@PostConstruct
	public void postConstruct(Composite parent) {
		ImageViewer previewer = new ImageViewer(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED);
		previewer.getConf().setWidgetName("Preview :");
		previewer.getConf().setWidth(8);
		previewer.getConf().setHeight(8);
		previewer.getConf().setPixelSize(3);
		previewer.getConf().setTileRows(1);
		previewer.getConf().setTileColumns(1);

		previewer.getConf().setPixelGridEnabled(false);
		previewer.getConf().setGridStyle(GridStyle.Dot);
		previewer.getConf().setTileGridEnabled(false);
		previewer.getConf().setTileCursorEnabled(false);
		previewer.getConf().setSeparatorEnabled(false);
		previewer.setSelectedTileOffset(0, 0, false);
		previewer.setBitplane(new byte[65535]);
		previewer.setImagePainterFactory(null);
		previewer.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
		previewer.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(1).getColor());
		previewer.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(2).getColor());
		previewer.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(3).getColor());
		previewer.setSelectedColor(1);
		previewer.recalc();
	}

}