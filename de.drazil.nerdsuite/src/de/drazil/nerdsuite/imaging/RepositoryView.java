
package de.drazil.nerdsuite.imaging;

import java.io.IOException;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;

import de.drazil.nerdsuite.assembler.InstructionSet;
import de.drazil.nerdsuite.disassembler.BinaryFileReader;
import de.drazil.nerdsuite.widget.ImageSelector;

public class RepositoryView {

	private byte binaryData[] = null;
	private byte blankData[] = null;

	@Inject
	public RepositoryView() {

	}

	@PostConstruct
	public void postConstruct(Composite parent) {
		ImageSelector selector = new ImageSelector(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.V_SCROLL);
		selector.getConf().setWidgetName("Selector:");
		selector.getConf().setWidth(8);
		selector.getConf().setHeight(8);
		selector.getConf().setPixelSize(3);
		selector.getConf().setPixelGridEnabled(false);
		selector.getConf().setTileGridEnabled(true);
		selector.getConf().setTileSubGridEnabled(false);
		selector.getConf().setTileCursorEnabled(true);
		selector.getConf().setSeparatorEnabled(false);
		selector.setSelectedTileOffset(0, 0, false);
		selector.setBitplane(getBlankData());
		// selector.setImagePainterFactory(imagePainterFactory);
		selector.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
		selector.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(1).getColor());
		selector.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(2).getColor());
		selector.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(3).getColor());
		selector.setSelectedColor(1);
		selector.recalc();
	}

	private byte[] getBlankData() {
		if (blankData == null) {
			blankData = new byte[0x1f40];
			for (int i = 0; i < blankData.length; i++)
				blankData[i] = 32;// (byte) (Math.random() * 80);
		}
		return blankData;
	}

	private byte[] getBinaryData() {
		if (binaryData == null) {

			Bundle bundle = Platform.getBundle("de.drazil.nerdsuite");
			URL url = bundle.getEntry("fonts/c64_lower.64c");

			try {
				binaryData = BinaryFileReader.readFile(url.openConnection().getInputStream(), 2);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return binaryData;
	}

}