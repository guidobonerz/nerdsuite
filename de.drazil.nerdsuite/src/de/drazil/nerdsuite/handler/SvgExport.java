
package de.drazil.nerdsuite.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.graphics.Point;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.imaging.service.ServiceFactory;
import de.drazil.nerdsuite.imaging.service.TileRepositoryService;
import de.drazil.nerdsuite.model.ProjectMetaData;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class SvgExport {

	int width = 30;
	int height = 30;
	boolean firstEntry = true;

	public final static int TOP = 1;
	public final static int LEFT = 2;
	public final static int BOTTOM = 4;
	public final static int RIGHT = 8;

	private Map<String, List<SvgPoint>> pathMap;

	public SvgExport() {
		pathMap = new HashMap<String, List<SvgExport.SvgPoint>>();
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	private class SvgPoint {
		int x1;
		int y1;
		int x2;
		int y2;
		boolean isStartPoint;
	}

	@Execute
	public void execute(MPart part, IEventBroker broker) {
		String owner = (String) part.getTransientData().get(Constants.OWNER);
		TileRepositoryService service = ServiceFactory.getService(owner, TileRepositoryService.class);
		ProjectMetaData metadata = service.getMetadata();
		ImagingWidgetConfiguration conf = metadata.getViewerConfig().get(ProjectMetaData.PAINTER_CONFIG);
		Map<String, SvgPoint> map = new HashMap<String, SvgPoint>();

		int[] content = service.getActiveLayerFromSelectedTile().getContent();
		for (int i = 0; i < content.length; i++) {
			String key = String.valueOf(content[i]);
			List<SvgPoint> pathList = pathMap.get(key);
			if (pathList == null) {
				pathList = new ArrayList<SvgExport.SvgPoint>();
				pathMap.put(key, pathList);
			}
			Point pc = getPoint(i, conf);

			int borders = getOutlineRanges(pc.x, pc.y, content, conf);
			int x1 = pc.x * width;
			int y1 = pc.y * height;
			int x2 = x1 + width;
			int y2 = y1 + height;
			if ((borders & TOP) == TOP) {
				SvgPoint p = new SvgPoint(x1, y1, x2, y1, false);
				pathList.add(p);
			}
			if ((borders & BOTTOM) == BOTTOM) {
				SvgPoint p = new SvgPoint(x1, y2, x2, y2, false);
				pathList.add(p);
			}
			if ((borders & LEFT) == LEFT) {
				SvgPoint p = new SvgPoint(x1, y1, x1, y2, false);
				pathList.add(p);
			}
			if ((borders & RIGHT) == RIGHT) {
				SvgPoint p = new SvgPoint(x2, y1, x2, y2, false);
				pathList.add(p);
			} else {

			}
		}

		String key = String.valueOf(1);
		List<SvgPoint> pathList = pathMap.get(key);

		// left to right order
		for (SvgPoint p : pathList) {
			if (p.x1 > p.x2) {
				int x = p.x2;
				p.x2 = p.x1;
				p.x1 = x;
			}
			if (p.y1 > p.y2) {
				int y = p.y2;
				p.y2 = p.y1;
				p.y1 = y;
			}
		}

		// sort
		// pathList.sort(Comparator.comparingInt(Point::getX1).thenComparingInt(Point::getY1));
		int a = 0;

	}

	private int getOutlineRanges(int x, int y, int[] content, ImagingWidgetConfiguration conf) {
		int borders = 0;
		int iCenter = getIndex(x, y, conf);
		int iTop = getIndex(x, y - 1, conf);
		int iBottom = getIndex(x, y + 1, conf);
		int iLeft = getIndex(x - 1, y, conf);
		int iRight = getIndex(x + 1, y, conf);
		int vc = getValueAtIndex(iCenter, content);
		borders |= vc != getValueAtIndex(iTop, content) ? TOP : 0;
		borders |= vc != getValueAtIndex(iBottom, content) ? BOTTOM : 0;
		borders |= vc != getValueAtIndex(iLeft, content) ? LEFT : 0;
		borders |= vc != getValueAtIndex(iRight, content) ? RIGHT : 0;
		return borders;
	}

	private int getValueAtIndex(int i, int[] content) {
		return i == -1 || i > content.length - 1 ? -1 : content[i];
	}

	private int getIndex(int x, int y, ImagingWidgetConfiguration conf) {
		if (x < 0 || y < 0 || x > conf.getTileWidth() || y > conf.getTileHeight()) {
			return -1;
		} else {
			return y * conf.getTileWidth() + x;
		}
	}

	private Point getPoint(int i, ImagingWidgetConfiguration conf) {
		return new Point(i % conf.getTileHeight(), i / conf.getTileWidth());
	}

}