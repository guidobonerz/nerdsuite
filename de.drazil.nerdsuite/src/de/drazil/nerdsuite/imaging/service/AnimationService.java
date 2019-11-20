package de.drazil.nerdsuite.imaging.service;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.enums.AnimationMode;
import de.drazil.nerdsuite.imaging.service.ITileUpdateListener.UpdateMode;
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

	public AnimationService() {
		animator = new Animator();
		animationIndex = new ArrayList<>();
		animationIndex.add(0);

	}

	public class Animator implements Runnable {
		public synchronized void run() {

			if (pos >= selectedTileIndexList.size()) {
				pos = 0;
			}
			int index = selectedTileIndexList.get(pos);
			pos++;
			animationIndex.set(0, index);
			service.updateTileViewer(animationIndex, UpdateMode.Animation);
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
		// serviceCallback.beforeRunService();
		if (animationMode == AnimationMode.Forward) {
			pos = 0;
			startAnimation();
			// ((Composite) source).getDisplay().timerExec(0, animator);
		} else if (animationMode == AnimationMode.Stop) {
			stopAnimation();
			// ((Composite) source).getDisplay().timerExec(-1, animator);
		}
		// serviceCallback.afterRunService();
	}

	public void setComposite(Composite composite) {
		this.composite = composite;
	}
}
