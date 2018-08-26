package de.drazil.nerdsuite.imaging;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;

import de.drazil.nerdsuite.assembler.InstructionSet;
import de.drazil.nerdsuite.disassembler.BinaryFileReader;
import de.drazil.nerdsuite.widget.ImagingWidget;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration.PixelConfig;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration.WidgetMode;

public class BitmapEditor {

	@Inject
	public BitmapEditor() {
		// TODO Your code here
	}

	@PostConstruct
	public void postConstruct(Composite parent) {
		Bundle bundle = Platform.getBundle("de.drazil.nerdsuite");
		URL url = bundle.getEntry("images/picrambo.prg");
		File file = null;

		try {
			file = new File(FileLocator.resolve(url).toURI());
			URL resolvedUrl = FileLocator.toFileURL(url);
			URI resolvedUri = new URI(resolvedUrl.getProtocol(), resolvedUrl.getPath(), null);
			file = new File(resolvedUri);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte binaryData[] = null;
		try {
			binaryData = BinaryFileReader.readFile(file, 2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parent.setLayout(new FillLayout(SWT.HORIZONTAL | SWT.VERTICAL));

		ImagingWidgetConfiguration ic = new ImagingWidgetConfiguration();
		ic.setWidgetName("Viewer:");
		ic.setWidgetMode(WidgetMode.BitmapViewer);
		ic.setWidth(8);
		ic.setHeight(8);
		ic.setColumns(40);
		ic.setRows(25);
		ic.setPixelSize(3);
		ic.setPixelGridEnabled(false);
		ic.setTileSubGridEnabled(false);
		ic.setTileGridEnabled(true);
		ImagingWidget painter = new ImagingWidget(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED, ic);

		painter.setColorProvider(new KoalaColorProvider());
		painter.setBitlane(binaryData);
		painter.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(7).getColor());
		painter.setSelectedColor(0);

		painter.recalc();
		parent.setLayout(new RowLayout(SWT.HORIZONTAL));

		Button multicolor = new Button(parent, SWT.CHECK);
		multicolor.setSelection(ic.getPixelConfig() == PixelConfig.BC2);
		multicolor.setText("MultiColor");
		multicolor.addListener(SWT.Selection, e -> {
			painter.getConf().setPixelConfig(multicolor.getSelection() ? PixelConfig.BC2 : PixelConfig.BC1);
			painter.recalc();
		});
		Button grid = new Button(parent, SWT.CHECK);
		grid.setSelection(ic.isTileGridEnabled());
		grid.setText("Grid");
		grid.addListener(SWT.Selection, e -> {
			ic.setTileGridEnabled(grid.getSelection());
			painter.recalc();
		});

	}

}