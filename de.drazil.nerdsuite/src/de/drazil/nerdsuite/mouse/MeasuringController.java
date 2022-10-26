package de.drazil.nerdsuite.mouse;

import java.util.ArrayList;
import java.util.List;

public class MeasuringController {

    public static enum Trigger {
        LEFT, MIDDLE, RIGHT
    }

    private List<IMeasuringListener> list;
    private long triggerMillis;
    private volatile Thread thread;
    private Measure measure;
    private volatile boolean running = false;
    private int timerId = -1;
    private Object payload;

    public MeasuringController() {
        list = new ArrayList<>();
    }

    public void setTriggerMillis(long triggerMillis) {
        this.triggerMillis = triggerMillis;
    }

    public void start(int id, Object payload) {
        if (triggerMillis != -1) {
            timerId = id;
            this.payload = payload;
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
        list.forEach(l -> l.onTriggerTimeReached(triggerMillis, timerId, payload));
    }

    private class Measure implements Runnable {
        private long startMillies;
        private long triggerMillis;

        public Measure(long startMillies, long triggerMillis) {
            this.startMillies = startMillies;
            this.triggerMillis = triggerMillis;
        }

        @Override
        public synchronized void run() {
            Thread currentMeasurment = Thread.currentThread();
            if (running) {
                while (currentMeasurment == thread) {
                    long diff = System.currentTimeMillis() - startMillies;
                    if (running && diff > triggerMillis) {
                        fireTimeReached(triggerMillis);
                    }
                }
            }
        }
    }
}
