
package de.drazil.nerdsuite.imaging;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.assembler.InstructionSet;
import de.drazil.nerdsuite.model.GraphicFormat;
import de.drazil.nerdsuite.widget.ImageViewer;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration.GridStyle;

public class PreviewView {

	private ImageViewer previewer = null;
	private Composite parent = null;
	private byte blankData[] = null;

	public PreviewView() {

	}

	@PostConstruct
	public void postConstruct(Composite parent) {
		this.parent = parent;
		parent.addListener(SWT.Resize, e -> {
			System.out.println(parent.getClientArea());

		});
		getWidget();
	}

	@Inject
	@Optional
	void eventReceived(@UIEventTopic("gfxFormat") GraphicFormat gf) {
		getWidget().getConf().setWidth(gf.getMetadata().getWidth());
		getWidget().getConf().setHeight(gf.getMetadata().getHeight());
		getWidget().getConf().setTileRows(gf.getMetadata().getTileRows());
		getWidget().getConf().setTileColumns(gf.getMetadata().getTileColumns());
		getWidget().recalc();
	}

	public ImageViewer getWidget() {
		if (previewer == null) {
			previewer = new ImageViewer(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED);
			previewer.getConf().setWidgetName("Preview :");
			previewer.getConf().setPixelSize(1);
			previewer.getConf().setRows(1);
			previewer.getConf().setColumns(1);
			previewer.getConf().setPixelGridEnabled(false);
			previewer.getConf().setGridStyle(GridStyle.Dot);
			previewer.getConf().setTileGridEnabled(false);
			previewer.getConf().setTileCursorEnabled(false);
			previewer.getConf().setSeparatorEnabled(false);
			previewer.setSelectedTileOffset(0, 0, false);
			previewer.setBitplane(getBlankData());
			previewer.setImagePainterFactory(null);
			previewer.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
			previewer.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(1).getColor());
			previewer.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(2).getColor());
			previewer.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(3).getColor());
			previewer.setSelectedColor(1);
			previewer.recalc();
		}
		return previewer;
	}

	private byte[] getBlankData() {
		if (blankData == null) {
			blankData = new byte[0x1f40];
			for (int i = 0; i < blankData.length; i++)
				blankData[i] = 32;// (byte) (Math.random() * 80);
		}
		return blankData;
	}
}