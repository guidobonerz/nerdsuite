
package de.drazil.nerdsuite.imaging;

import java.io.IOException;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;

import de.drazil.nerdsuite.disassembler.BinaryFileReader;
import de.drazil.nerdsuite.model.GraphicFormat;
import de.drazil.nerdsuite.widget.ImageRepository;

public class RepositoryView {
	private ImageRepository repository = null;
	private Composite parent = null;
	private byte binaryData[] = null;
	private byte blankData[] = null;

	public RepositoryView() {

	}

	@Inject
	EMenuService menuService;
	

	@PostConstruct
	public void postConstruct(Composite parent) {
		this.parent = parent;
		parent.addListener(SWT.Resize, e -> {
			computeVisibility();
		});
		getWidget();
	}

	private void computeVisibility() {
		Rectangle r = parent.getClientArea();
		int columns = (r.width - getWidget().getVerticalBar().getSize().x) / getWidget().getConf().getTileWidthPixel();
		columns = columns == 0 ? 1 : columns;
		int tileCount = blankData.length / getWidget().getConf().getTileSize();
		int rows = (tileCount / columns) + 1;
		System.out.println(tileCount);

		getWidget().getConf().setColumns(columns);
		getWidget().getConf().setRows(rows);
		getWidget().recalc();
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

	private ImageRepository getWidget() {
		if (repository == null) {
			repository = new ImageRepository(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.V_SCROLL) {

				/*
				 * 
				 * @Override protected void setHasTileSelection(int count) {
				 * getStartAnimation().setEnabled(count > 1);
				 * getAnimationTimerDelayScale().setEnabled(count > 1); }
				 * 
				 * @Override protected void showNotification(ImagingServiceDescription type,
				 * ImagingServiceAction mode, String notification, Object data) { if (type ==
				 * ImagingServiceDescription.Animation) {
				 * getStartAnimation().setText(notification); } else {
				 * MessageDialog.openInformation(parent.getShell(), "Information",
				 * notification); } }
				 * 
				 * @Override protected boolean isConfirmed(ImagingServiceDescription type,
				 * ImagingServiceAction mode, int tileCount) { boolean confirmation = false; if
				 * (type == ImagingServiceDescription.Rotate) { confirmation =
				 * MessageDialog.openQuestion(parent.getShell(), "Question",
				 * "Rotating these tile(s) causes data loss, because it is/they are not squarish.\n\nDo you want to rotate anyway?"
				 * ); } if (type == ImagingServiceDescription.All) { confirmation =
				 * MessageDialog.openQuestion(parent.getShell(), "Question",
				 * MessageFormat.format( "Do you really want to process {0} ?", (tileCount > 1)
				 * ? "all selected tiles" : "this tile")); } return confirmation; }
				 * 
				 * @Override protected void setNotification(int offset, int tileSize) {
				 * 
				 * getNotification().setText(MessageFormat.format(
				 * "Offset: ${0} tile:{1} bytes", String.format("%04X", offset), tileSize)); }
				 */
			};
			repository.getConf().setWidgetName("Selector:");
			repository.getConf().setColumns(4);
			repository.getConf().setRows(4);
			repository.getConf().setPixelSize(3);
			repository.getConf().setPixelGridEnabled(false);
			repository.getConf().setTileGridEnabled(true);
			repository.getConf().setTileSubGridEnabled(false);
			repository.getConf().setTileCursorEnabled(true);
			repository.getConf().setSeparatorEnabled(false);
			// repository.setSelectedTileOffset(0, 0, false);
			repository.setBitplane(getBlankData());
			repository.setImagePainterFactory(null);

			repository.setSelectedColor(1);
			repository.recalc();
			// selector.addDrawListener(getPainter());
			// selector.addDrawListener(getPreviewer());

		}
		menuService.registerContextMenu(repository, "de.drazil.nerdsuite.popupmenu.popupmenu");
		return repository;

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