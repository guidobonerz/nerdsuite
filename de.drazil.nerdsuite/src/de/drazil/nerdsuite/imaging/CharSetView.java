
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
import de.drazil.nerdsuite.widget.ImageReferenceSelector;

public class CharSetView {
	private byte binaryData[] = null;

	@Inject
	public CharSetView() {

	}

	@PostConstruct
	public void postConstruct(Composite parent) {
		ImageReferenceSelector referenceSelector = new ImageReferenceSelector(parent,
				SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.V_SCROLL);
		referenceSelector.getConf().setWidgetName("ReferenceSelector:");
		referenceSelector.getConf().setWidth(8);
		referenceSelector.getConf().setHeight(8);
		referenceSelector.getConf().setTileColumns(1);
		referenceSelector.getConf().setTileRows(1);
		referenceSelector.getConf().setColumns(16);
		referenceSelector.getConf().setRows(16);
		referenceSelector.getConf().setPixelSize(2);
		referenceSelector.getConf().setPixelGridEnabled(false);
		referenceSelector.getConf().setTileGridEnabled(true);
		referenceSelector.getConf().setTileSubGridEnabled(false);
		referenceSelector.getConf().setTileCursorEnabled(true);
		referenceSelector.getConf().setSeparatorEnabled(false);
		referenceSelector.setSelectedTileOffset(0, 0, true);
		referenceSelector.setBitplane(getBinaryData());
		referenceSelector.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
		referenceSelector.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(1).getColor());
		referenceSelector.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(2).getColor());
		referenceSelector.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(3).getColor());
		referenceSelector.setSelectedColor(1);
		referenceSelector.recalc();
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