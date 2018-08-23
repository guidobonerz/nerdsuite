package de.drazil.nerdsuite.imaging.service;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;

public class AnimationService extends AbstractService {
	public final static int START = 1;
	public final static int STOP = 2;
	public final static int SET_DELAY = 4;
	private Animator animator = null;
	private int animationDelay = 0;

	public AnimationService() {

		animator = new Animator();
	}

	public class Animator implements Runnable {
		public synchronized void run() {
			Collections.rotate(tileLocationList, -1);
			TileLocation tl = tileLocationList.get(0);
			callback.onRunService(conf.computeTileOffset(tl.x, tl.y, navigationOffset));
			((Composite) source).getDisplay().timerExec(animationDelay, this);
		}
	}

	@Override
	public void runService(int action, List<TileLocation> tileLocationList, int offset, byte[] bitplane) {
		this.navigationOffset = offset;
		this.tileLocationList = tileLocationList;
		callback.beforeRunService();
		if (action == START) {
			((Composite) source).getDisplay().timerExec(0, animator);
		} else if (action == STOP) {
			((Composite) source).getDisplay().timerExec(-1, animator);
		}
		callback.afterRunService();
	}

	@Override
	public void setValue(int action, Object data) {
		if (action == SET_DELAY) {
			animationDelay = (int) data;
		}
	}

	@Override
	public boolean needsConfirmation() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void sendResponse(String message, Object data) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isReadyToRun(List<TileLocation> tileLocationList, ImagingWidgetConfiguration configuration) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProcessConfirmed(boolean confirmAnyProcess) {
		// TODO Auto-generated method stub
		return false;
	}
	/*
	 * public void startAnimation() { if (tileSelectionList.size() < 1) {
	 * showNotification(null, null,
	 * "You have to select an animation range first.", null); } else if
	 * (tileSelectionList.size() == 1) { showNotification(null, null,
	 * "You have to select at least two tiles to start the animation.", null); }
	 * else { animationIsRunning = true;
	 * showNotification(ImagingServiceDescription.Animation,
	 * ImagingServiceAction.Start, isAnimationRunning() ? "Stop Animation (" +
	 * (animationTimerDelay) + " ms)" : "Start Animation", animationIsRunning);
	 * getDisplay().timerExec(0, animator); } }
	 * 
	 * public void stopAnimation() { animationIsRunning = false;
	 * showNotification(ImagingServiceDescription.Animation,
	 * ImagingServiceAction.Start, isAnimationRunning() ? "Stop Animation (" +
	 * (animationTimerDelay) + " ms)" : "Start Animation", animationIsRunning);
	 * getDisplay().timerExec(-1, animator); doDrawAllTiles(); }
	 * 
	 * public boolean isAnimationRunning() { return animationIsRunning; }
	 * 
	 * public boolean isAnimatable() { return tileSelectionList.size() > 1; }
	 * 
	 * public void changeAnimationTimerDelay(int delay) { animationTimerDelay =
	 * delay; if (isAnimationRunning()) {
	 * showNotification(ImagingServiceDescription.Animation,
	 * ImagingServiceAction.Start, isAnimationRunning() ? "Stop Animation (" +
	 * (animationTimerDelay) + " ms)" : "Start Animation", animationIsRunning);
	 * getDisplay().timerExec(delay, animator); } }
	 */
}
