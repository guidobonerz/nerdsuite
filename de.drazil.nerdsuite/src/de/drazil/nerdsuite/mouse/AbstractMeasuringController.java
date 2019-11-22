package de.drazil.nerdsuite.mouse;

import java.util.ArrayList;
import java.util.List;

public class AbstractMeasuringController implements IMeasuringController {

	private List<IMeasuringListener> list;
	private long[] triggerMillis = new long[] { 2000 };
	private volatile Thread thread;
	private int mc = 0;
	private Measure measure;
	private volatile boolean running = false;

	public AbstractMeasuringController() {
		list = new ArrayList<>();
	}

	@Override
	public void setTriggerMillis(long... triggerMillis) {
		this.triggerMillis = triggerMillis;
	}

	@Override
	public void start() {
		System.out.println("start");
		running = true;
		measure = new Measure(System.currentTimeMillis(), triggerMillis);
		thread = new Thread(measure);
		thread.start();
	}

	@Override
	public void stop() {
		System.out.println("stop");
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
		list.forEach(l -> l.onTimeReached(triggerMillis));
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
						System.out.println(mc);
						fireTimeReached(triggerMillis[mc]);
						mc++;
					}
				}
			}
		}
	}
}
