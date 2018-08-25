package de.drazil.nerdsuite.widget;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.mouse.AdvancedMouseAdaper;
import de.drazil.nerdsuite.mouse.IAdvancedMouseListener;
import de.drazil.nerdsuite.mouse.IAdvancedMouseMoveListener;
import de.drazil.nerdsuite.mouse.IAdvancedMouseTrackListener;
import de.drazil.nerdsuite.mouse.IAdvancedMouseWheelListener;
import lombok.Getter;

public abstract class BaseImagingWidget extends Canvas implements IAdvancedMouseListener, IAdvancedMouseMoveListener,
		IAdvancedMouseTrackListener, IAdvancedMouseWheelListener, IConfigurationListener {

	protected AdvancedMouseAdaper ama = null;
	protected int modifierMask = 0;

	@Getter
	protected ImagingWidgetConfiguration conf = null;

	protected int width = 0;
	protected int height = 0;
	protected int tileColumns = 0;
	protected int tileRows = 0;
	protected int columns = 0;
	protected int rows = 0;
	protected int currentWidth;

	private final static class DefaultImageingWidgetConfiguration extends ImagingWidgetConfiguration {

	}

	public BaseImagingWidget(Composite parent, int style, ImagingWidgetConfiguration configuration) {
		super(parent, style);
		conf = configuration == null ? new DefaultImageingWidgetConfiguration() : configuration;
		conf.setConfigurationListener(this);
		ama = new AdvancedMouseAdaper(this);
		ama.addMouseListener(this);
		ama.addMouseMoveListener(this);
		ama.addMouseTrackListener(this);
		ama.addMouseWheelListener(this);
	}

	public void setMouseActionEnabled(boolean mouseActionEnabled) {
		ama.setMouseActionEnabled(mouseActionEnabled);
	}

	@Override
	public void rightMouseButtonClicked(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void rightMouseButtonReleased(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void rightMouseButtonPressed(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void leftMouseButtonDoubleClicked(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void rightMouseButtonDoubleClicked(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void leftMouseButtonClicked(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void mouseMove(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void mouseExit(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void mouseEnter(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void mouseDropped(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void mouseDragged(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void middleMouseButtonReleased(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void middleMouseButtonPressed(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void leftMouseButtonReleased(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void leftMouseButtonPressed(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void leftMouseButtonTimesClicked(int modifierMask, int x, int y, int count) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void rightMouseButtonTimesClicked(int modifierMask, int x, int y, int count) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void mouseScrolled(int modifierMask, int x, int y, int count) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void configurationChanged(int width, int height, int tileColumns, int tileRows, int painterPixelSize,
			int selectorPixelSize, int columns, int rows, int currentWidth) {
		this.width = width;
		this.height = height;
		this.tileColumns = tileColumns;
		this.tileRows = tileRows;
		this.columns = columns;
		this.rows = rows;
		this.currentWidth = currentWidth;
	}
}
