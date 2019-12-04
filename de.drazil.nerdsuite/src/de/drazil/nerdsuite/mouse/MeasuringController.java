package de.drazil.nerdsuite.mouse;

import java.util.ArrayList;
import java.util.List;

public class MeasuringController {

	private List<IMeasuringListener> list;
	private long[] triggerMillis;
	private volatile Thread thread;
	private Measure measure;
	private volatile boolean running = false;

	public MeasuringController() {
		list = new ArrayList<>();
	}

	public void setTriggerMillis(long... triggerMillis) {
		this.triggerMillis = triggerMillis;
	}

	public void start() {
		if (triggerMillis != null) {
			running = true;
			measure = new Measure(System.currentTimeMillis(), triggerMillis);
			thread = new Thread(measure);
			thread.start();
		}
	}

	public void stop() {
		running = false;
		thread = null;
	}

	public void addMeasuringListener(IMeasuringListener listener) {
		list.add(listener);
	}

	public void removeMeasuringListener(IMeasuringListener listener) {
		list.remove(listener);
	}

	private void fireTimeReached(long triggerMillis) {
		list.forEach(l -> l.onTriggerTimeReached(triggerMillis));
	}

	private class Measure implements Runnable {
		private long startMillies;
		private int mc = 0;
		private long[] triggerMillis;

		public Measure(long startMillies, long[] triggerMillis) {
			mc = 0;
			this.startMillies = startMillies;
			this.triggerMillis = triggerMillis;
		}

		@Override
		public synchronized void run() {
			Thread currentMeasurment = Thread.currentThread();
			if (running) {
				while (currentMeasurment == thread) {
					long diff = System.currentTimeMillis() - startMillies;
					if (running && mc < triggerMillis.length && diff > triggerMillis[mc]) {
						fireTimeReached(triggerMillis[mc]);
						mc++;
					}
				}
			}
		}
	}
}
