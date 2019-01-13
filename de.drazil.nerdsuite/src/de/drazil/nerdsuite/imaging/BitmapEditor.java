package de.drazil.nerdsuite.imaging;

import java.io.IOException;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;

import de.drazil.nerdsuite.assembler.InstructionSet;
import de.drazil.nerdsuite.constants.PixelConfig;
import de.drazil.nerdsuite.disassembler.BinaryFileReader;
import de.drazil.nerdsuite.widget.ImageViewer;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;

public class BitmapEditor {

	@Inject
	public BitmapEditor() {
		// TODO Your code here
	}

	@PostConstruct
	public void postConstruct(Composite parent) {

		byte binaryData[] = null;

		Bundle bundle = Platform.getBundle("de.drazil.nerdsuite");
		URL url = bundle.getEntry("images/Image by Almighty God.koa");

		try {
			binaryData = BinaryFileReader.readFile(url.openConnection().getInputStream(), 2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		parent.setLayout(new FillLayout(SWT.HORIZONTAL | SWT.VERTICAL));

		ImagingWidgetConfiguration ic = new ImagingWidgetConfiguration();
		ic.setWidgetName("Viewer:");
		ic.setWidth(8);
		ic.setHeight(8);
		ic.setColumns(40);
		ic.setRows(25);
		ic.setPixelSize(3);
		ic.setPixelGridEnabled(false);
		ic.setTileSubGridEnabled(false);
		ic.setTileGridEnabled(true);
		ic.setPixelConfig(PixelConfig.BC2);
		ImageViewer viewer = new ImageViewer(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED, ic);

		viewer.setColorProvider(new KoalaColorProvider());

		viewer.setSelectedColor(0);

		viewer.recalc();
		parent.setLayout(new RowLayout(SWT.HORIZONTAL));

		Button multicolor = new Button(parent, SWT.CHECK);
		multicolor.setSelection(ic.getPixelConfig() == PixelConfig.BC2);
		multicolor.setText("MultiColor");
		multicolor.addListener(SWT.Selection, e -> {
			viewer.getConf().setPixelConfig(multicolor.getSelection() ? PixelConfig.BC2 : PixelConfig.BC1);
			viewer.recalc();
		});
		Button grid = new Button(parent, SWT.CHECK);
		grid.setSelection(ic.isTileGridEnabled());
		grid.setText("Grid");
		grid.addListener(SWT.Selection, e -> {
			ic.setTileGridEnabled(grid.getSelection());
			viewer.recalc();
		});

	}

}