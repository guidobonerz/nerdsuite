package de.drazil.nerdsuite.imaging.service;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.enums.AnimationMode;
import lombok.Setter;

public class AnimationService extends AbstractImagingService {
	public final static int SET_DELAY = 4;
	private Animator animator = null;
	@Setter
	private int delay = 100;
	private int pos = 0;
	private boolean animationIsRunning = false;
	private Composite composite;
	private List<Integer> animationIndex;
	private boolean forward = true;

	public AnimationService() {
		animator = new Animator();
		animationIndex = new ArrayList<>();
		animationIndex.add(0);

	}

	public class Animator implements Runnable {
		public synchronized void run() {

			if (forward) {
				if (pos >= selectedTileIndexList.size()) {
					pos = 0;
				}
			} else {
				if (pos < 0) {
					pos = selectedTileIndexList.size() - 1;
				}
			}
			int index = selectedTileIndexList.get(pos);
			pos += (forward ? 1 : -1);
			animationIndex.set(0, index);
			service.redrawTileViewer(animationIndex, false, true);
			System.out.println("animate");
			/*
			 * callback.onRunService(imagingWidgetConfiguration.computeTileOffset(tl.x,
			 * tl.y, navigationOffset), tl.x, tl.y, true);
			 */
			composite.getDisplay().timerExec(delay, this);

		}
	}

	public void startAnimation() {
		if (selectedTileIndexList.size() < 1) {
			// showNotification(null, null, "You have to select an animation range first.",
			// null);
		} else if (selectedTileIndexList.size() == 1) {
			// showNotification(null, null, "You have to select at least two tiles to start
			// the animation.", null);
		} else {
			animationIsRunning = true;
			/*
			 * showNotification(ImagingServiceDescription.Animation,
			 * ImagingServiceAction.Start, isAnimationRunning() ? "Stop Animation (" +
			 * (animationTimerDelay) + " ms)" : "Start Animation", animationIsRunning);
			 */
			composite.getDisplay().timerExec(0, animator);
		}
	}

	public void stopAnimation() {
		animationIsRunning = false;
		/*
		 * showNotification(ImagingServiceDescription.Animation,
		 * ImagingServiceAction.Start, isAnimationRunning() ? "Stop Animation (" +
		 * (animationTimerDelay) + " ms)" : "Start Animation", animationIsRunning);
		 */
		composite.getDisplay().timerExec(-1, animator);
		// doDrawAllTiles();
	}

	public boolean isAnimationRunning() {
		return animationIsRunning;
	}

	public boolean isAnimatable() {
		return selectedTileIndexList.size() > 1;
	}

	/*
	 * public void changeAnimationTimerDelay(int delay) { animationTimerDelay =
	 * delay; if (isAnimationRunning()) {
	 * showNotification(ImagingServiceDescription.Animation,
	 * ImagingServiceAction.Start, isAnimationRunning() ? "Stop Animation (" +
	 * (animationTimerDelay) + " ms)" : "Start Animation", animationIsRunning);
	 * composite.getDisplay().timerExec(delay, animator); } }
	 */
	public void execute(AnimationMode animationMode) {
		service = ServiceFactory.getService(owner, TileRepositoryService.class);
		selectedTileIndexList = service.getSelectedTileIndexList();
		if (animationMode == AnimationMode.Forward) {
			delay = 100;
			forward = true;
			stopAnimation();
			startAnimation();
		} else if (animationMode == AnimationMode.ForwardFast) {
			delay = 50;
			forward = true;
			stopAnimation();
			startAnimation();
		} else if (animationMode == AnimationMode.Stop) {
			pos = 0;
			stopAnimation();
		} else if (animationMode == AnimationMode.Backward) {
			delay = 100;
			forward = false;
			stopAnimation();
			startAnimation();
		} else if (animationMode == AnimationMode.BackwardFast) {
			delay = 50;
			forward = false;
			stopAnimation();
			startAnimation();
		}
	}

	public void setComposite(Composite composite) {
		this.composite = composite;
	}
}
