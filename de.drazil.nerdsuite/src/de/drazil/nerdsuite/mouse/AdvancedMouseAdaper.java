package de.drazil.nerdsuite.mouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.mouse.MeasuringController.Trigger;
import lombok.Getter;
import lombok.Setter;

public class AdvancedMouseAdaper implements IMeasuringListener {

    public final static int MOUSE_BUTTON_LEFT = 1;
    public final static int MOUSE_BUTTON_MIDDLE = 2;
    public final static int MOUSE_BUTTON_RIGHT = 3;
    public final static int SET_LEFT_BUTTON_PRESSED = 1;
    public final static int SET_LEFT_BUTTON_RELEASED = 2;
    public final static int SET_MIDDLE_BUTTON_PRESSED = 4;
    public final static int SET_MIDDLE_BUTTON_RELEASED = 8;
    public final static int SET_RIGHT_BUTTON_PRESSED = 16;
    public final static int SET_RIGHT_BUTTON_RELEASED = 32;
    public final static int SET_MOUSE_MOVE = 64;
    public final static int SET_MOUSE_DRAGGED = 128;
    public final static int SET_MOUSE_DROPPED = 256;

    private int mouseState = SET_LEFT_BUTTON_RELEASED + SET_RIGHT_BUTTON_RELEASED;

    public static enum MouseButton {
        Left, Middle, Right
    }

    private enum MouseTrack {
        Enter, Exit
    }

    private enum MouseMove {
        Drag, DragDelayed, Drop, Move
    }

    private InternalMouseAdapter lma = null;
    private int leftClickTimeStart = 0;
    private int middleClickTimeStart = 0;
    private int rightClickTimeStart = 0;
    @Getter
    @Setter
    private int leftClickTimeMillis = 800;
    @Getter
    @Setter
    private int leftDoubleClickTimeMillis = 800;
    @Getter
    @Setter
    private int leftTimesClickTimeMillis = 1200;
    @Getter
    @Setter
    private int rightClickTimeMillis = 800;
    @Getter
    @Setter
    private int rightDoubleClickTimeMillis = 800;
    @Getter
    @Setter
    private int rightTimesClickTimeMillis = 1200;

    @Getter
    @Setter
    private boolean mouseActionEnabled = true;
    private boolean canDragDelayed = false;
    private boolean isAlreadyDragging = false;

    private List<IAdvancedMouseListener> mouseListenerList = null;
    private List<IAdvancedMouseMoveListener> mouseMoveListenerList = null;
    private List<IAdvancedMouseTrackListener> mouseTrackListenerList = null;
    private List<IAdvancedMouseWheelListener> mouseWheelListenerList = null;
    private Map<String, MeasuringController> measuringControllerMap = null;

    private final class InternalMouseAdapter
            implements MouseMoveListener, MouseTrackListener, MouseWheelListener, MouseListener {

        public InternalMouseAdapter(Composite composite, AdvancedMouseAdaper advancedMouseAdapter) {
            composite.addMouseListener(this);
            composite.addMouseMoveListener(this);
            composite.addMouseTrackListener(this);
            composite.addMouseWheelListener(this);
        }

        @Override
        public void mouseDoubleClick(MouseEvent e) {

        }

        @Override
        public void mouseDown(MouseEvent e) {
            int modifierMask = getModifierMask(e);
            if (e.button == MOUSE_BUTTON_LEFT) {
                leftClickTimeStart = e.time;
                setMouseState(SET_LEFT_BUTTON_PRESSED, true);
                setMouseState(SET_LEFT_BUTTON_RELEASED, false);
                fireMouseButtonPressed(MouseButton.Left, modifierMask, e.x, e.y);
            } else if (e.button == MOUSE_BUTTON_MIDDLE) {
                middleClickTimeStart = e.time;
                setMouseState(SET_MIDDLE_BUTTON_PRESSED, true);
                setMouseState(SET_MIDDLE_BUTTON_RELEASED, false);
                fireMouseButtonPressed(MouseButton.Middle, modifierMask, e.x, e.y);
            } else if (e.button == MOUSE_BUTTON_RIGHT) {
                rightClickTimeStart = e.time;
                setMouseState(SET_RIGHT_BUTTON_PRESSED, true);
                setMouseState(SET_RIGHT_BUTTON_RELEASED, false);
                fireMouseButtonPressed(MouseButton.Right, modifierMask, e.x, e.y);
            }
        }

        @Override
        public void mouseEnter(MouseEvent e) {
            int modifierMask = getModifierMask(e);
            fireMouseTrack(MouseTrack.Enter, modifierMask, e.x, e.y);
        }

        @Override
        public void mouseExit(MouseEvent e) {
            int modifierMask = getModifierMask(e);
            fireMouseTrack(MouseTrack.Exit, modifierMask, e.x, e.y);

        }

        @Override
        public void mouseHover(MouseEvent e) {
            // int modifierMask = getModifierMask(e);
            // fireMouseHover(modifierMask, e.x, e.y);
        }

        @Override
        public void mouseMove(MouseEvent e) {
            int modifierMask = getModifierMask(e);
            setMouseState(SET_MOUSE_MOVE, true);
            if (isMouseState(SET_LEFT_BUTTON_PRESSED + SET_MOUSE_MOVE, true)) {
                setMouseState(SET_MOUSE_DRAGGED, true);
                setMouseState(SET_MOUSE_DROPPED, false);

                fireMouseMove(MouseMove.Drag, modifierMask, e.x, e.y);
            } else {
                fireMouseMove(MouseMove.Move, modifierMask, e.x, e.y);
            }
            setMouseState(SET_MOUSE_MOVE, false);
        }

        @Override
        public void mouseScrolled(MouseEvent e) {
            if ((e.stateMask & SWT.CONTROL) == SWT.CONTROL) {
                fireMouseScrolled(getModifierMask(e), e.x, e.y, e.count);
            }
        }

        @Override
        public void mouseUp(MouseEvent e) {
            int modifierMask = getModifierMask(e);

            if (isMouseState(SET_MOUSE_DRAGGED, true)) {
                setMouseState(SET_MOUSE_DROPPED, true);
                setMouseState(SET_MOUSE_DRAGGED, false);
                fireMouseMove(MouseMove.Drop, modifierMask, e.x, e.y);
            }
            if (e.button == MOUSE_BUTTON_LEFT) {
                setMouseState(SET_LEFT_BUTTON_RELEASED, true);
                setMouseState(SET_LEFT_BUTTON_PRESSED, false);
                int diff = e.time - leftClickTimeStart;
                if (diff < leftClickTimeMillis && e.count == 1) {
                    fireMouseButtonClicked(MouseButton.Left, modifierMask, e.x, e.y);
                }
                if (diff < leftDoubleClickTimeMillis && e.count == 2) {
                    fireMouseButtonDoubleClicked(MouseButton.Left, modifierMask, e.x, e.y);
                }
                if (diff < leftTimesClickTimeMillis) {
                    fireMouseButtonTimesClicked(MouseButton.Left, modifierMask, e.x, e.y, e.count);
                }
                fireMouseButtonReleased(MouseButton.Left, modifierMask, e.x, e.y);
            } else if (e.button == MOUSE_BUTTON_MIDDLE) {
                setMouseState(SET_MIDDLE_BUTTON_RELEASED, true);
                setMouseState(SET_MIDDLE_BUTTON_PRESSED, false);
                fireMouseButtonReleased(MouseButton.Middle, modifierMask, e.x, e.y);
            } else if (e.button == MOUSE_BUTTON_RIGHT) {
                setMouseState(SET_RIGHT_BUTTON_RELEASED, true);
                setMouseState(SET_RIGHT_BUTTON_PRESSED, false);
                int diff = e.time - rightClickTimeStart;
                if (diff < rightClickTimeMillis && e.count == 1) {
                    fireMouseButtonClicked(MouseButton.Right, modifierMask, e.x, e.y);
                }
                if (diff < rightDoubleClickTimeMillis && e.count == 2) {
                    fireMouseButtonDoubleClicked(MouseButton.Right, modifierMask, e.x, e.y);
                }
                if (diff < rightTimesClickTimeMillis) {
                    fireMouseButtonTimesClicked(MouseButton.Right, modifierMask, e.x, e.y, e.count);
                }
                fireMouseButtonReleased(MouseButton.Right, modifierMask, e.x, e.y);
            }
        }

        private int getModifierMask(MouseEvent e) {
            return e.stateMask & SWT.MODIFIER_MASK;
        }

        private void setMouseState(int state, boolean set) {
            if (set) {
                mouseState |= state;
            } else {
                mouseState &= (state ^ 0xff);
            }
            mouseState &= 0xff;
        }

        private boolean isMouseState(int state, boolean set) {
            if (set) {
                return (mouseState & state) == state;
            } else {
                return ((mouseState & (state ^ 0xff)) & 0xff) == 0;
            }
        }
    }

    public AdvancedMouseAdaper(Composite composite) {
        lma = new InternalMouseAdapter(composite, this);
        mouseListenerList = new ArrayList<>();
        mouseMoveListenerList = new ArrayList<>();
        mouseTrackListenerList = new ArrayList<>();
        mouseWheelListenerList = new ArrayList<>();
        measuringControllerMap = new HashMap<>();
    }

    public void disableDelayTrigger(Trigger trigger) {
        if (trigger == Trigger.LEFT) {
            MeasuringController mc = measuringControllerMap.get(Trigger.LEFT.toString());
            if (mc != null) {
                mc.stop();
            }
        }
        if (trigger == Trigger.MIDDLE) {
            MeasuringController mc = measuringControllerMap.get(Trigger.MIDDLE.toString());
            if (mc != null) {
                mc.stop();
            }
        }
        if (trigger == Trigger.RIGHT) {
            MeasuringController mc = measuringControllerMap.get(Trigger.RIGHT.toString());
            if (mc != null) {
                mc.stop();
            }
        }
    }

    public void enableDelayTrigger(Trigger trigger, long delay) {
        MeasuringController mc = null;
        if (trigger == Trigger.LEFT && delay != -1) {
            mc = new MeasuringController();
            mc.setTriggerMillis(delay);
            mc.addMeasuringListener(this);
            measuringControllerMap.put(Trigger.LEFT.toString(), mc);
        }
        if (trigger == Trigger.MIDDLE && delay != -1) {
            mc = new MeasuringController();
            mc.setTriggerMillis(delay);
            mc.addMeasuringListener(this);
            measuringControllerMap.put(Trigger.MIDDLE.toString(), mc);
        }
        if (trigger == Trigger.RIGHT && delay != -1) {
            mc = new MeasuringController();
            mc.setTriggerMillis(delay);
            mc.addMeasuringListener(this);
            measuringControllerMap.put(Trigger.RIGHT.toString(), mc);
        }
    }

    public void addMouseListener(IAdvancedMouseListener l) {
        mouseListenerList.add(l);
    }

    public void removeMouseListener(IAdvancedMouseListener l) {
        mouseListenerList.remove(l);
    }

    public void addMouseMoveListener(IAdvancedMouseMoveListener l) {
        mouseMoveListenerList.add(l);
    }

    public void removeMouseMoveListener(IAdvancedMouseMoveListener l) {
        mouseMoveListenerList.remove(l);
    }

    public void addMouseTrackListener(IAdvancedMouseTrackListener l) {
        mouseTrackListenerList.add(l);
    }

    public void removeMouseTrackListener(IAdvancedMouseTrackListener l) {
        mouseTrackListenerList.remove(l);
    }

    public void addMouseWheelListener(IAdvancedMouseWheelListener l) {
        mouseWheelListenerList.add(l);
    }

    public void removeMouseWheelListener(IAdvancedMouseWheelListener l) {
        mouseWheelListenerList.remove(l);
    }

    @Override
    public void onTriggerTimeReached(long triggerTime, int timerId, Object payload) {
        canDragDelayed = true;
        AdvancedMouseAdapterEvent amae = (AdvancedMouseAdapterEvent) payload;
        fireMouseButtonPressedDelayed(amae.getButton(), amae.getModifierMask(), amae.getX(), amae.getY());
    }

    public void fireMouseButtonPressed(MouseButton button, int modifierMask, int x, int y) {
        if (isMouseActionEnabled()) {
            mouseListenerList.forEach(ml -> {
                if (button == MouseButton.Left) {
                    MeasuringController mc = measuringControllerMap.get(Trigger.LEFT.toString());
                    if (mc != null) {
                        mc.start(1, new AdvancedMouseAdapterEvent(button, modifierMask, x, y));
                    }
                    ml.leftMouseButtonPressed(modifierMask, x, y);
                } else if (button == MouseButton.Middle) {
                    MeasuringController mc = measuringControllerMap.get(Trigger.MIDDLE.toString());
                    if (mc != null) {
                        mc.start(1, new AdvancedMouseAdapterEvent(button, modifierMask, x, y));
                    }
                    ml.middleMouseButtonPressed(modifierMask, x, y);
                } else if (button == MouseButton.Right) {
                    MeasuringController mc = measuringControllerMap.get(Trigger.RIGHT.toString());
                    if (mc != null) {
                        mc.start(1, new AdvancedMouseAdapterEvent(button, modifierMask, x, y));
                    }
                    ml.rightMouseButtonPressed(modifierMask, x, y);
                }
            });
        }
    }

    public void fireMouseButtonReleased(MouseButton button, int modifierMask, int x, int y) {
        if (isMouseActionEnabled()) {
            canDragDelayed = false;
            isAlreadyDragging = false;
            mouseListenerList.forEach(ml -> {
                if (button == MouseButton.Left) {
                    MeasuringController mc = measuringControllerMap.get(Trigger.LEFT.toString());
                    if (mc != null) {
                        mc.stop();
                    }
                    ml.leftMouseButtonReleased(modifierMask, x, y);
                } else if (button == MouseButton.Middle) {
                    MeasuringController mc = measuringControllerMap.get(Trigger.MIDDLE.toString());
                    if (mc != null) {
                        mc.stop();
                    }
                    ml.middleMouseButtonReleased(modifierMask, x, y);
                } else if (button == MouseButton.Right) {
                    MeasuringController mc = measuringControllerMap.get(Trigger.RIGHT.toString());
                    if (mc != null) {
                        mc.stop();
                    }
                    ml.rightMouseButtonReleased(modifierMask, x, y);
                }
            });
        }
    }

    public void fireMouseButtonTimesClicked(MouseButton button, int modifierMask, int x, int y, int count) {
        if (isMouseActionEnabled()) {
            mouseListenerList.forEach(ml -> {
                if (button == MouseButton.Left) {
                    ml.leftMouseButtonTimesClicked(modifierMask, x, y, count);
                } else if (button == MouseButton.Middle) {

                } else if (button == MouseButton.Right) {
                    ml.rightMouseButtonTimesClicked(modifierMask, x, y, count);
                }
            });
        }
    }

    public void fireMouseButtonPressedDelayed(MouseButton button, int modifierMask, int x, int y) {
        if (isMouseActionEnabled()) {
            mouseListenerList.forEach(ml -> {
                if (button == MouseButton.Left) {
                    ml.leftMouseButtonPressedDelayed(modifierMask, x, y);
                } else if (button == MouseButton.Middle) {
                    ml.middleMouseButtonPressedDelayed(modifierMask, x, y);
                } else if (button == MouseButton.Right) {
                    ml.rightMouseButtonPressedDelayed(modifierMask, x, y);
                }
            });
        }
    }

    public void fireMouseButtonClicked(MouseButton button, int modifierMask, int x, int y) {
        if (isMouseActionEnabled()) {
            mouseListenerList.forEach(ml -> {
                if (button == MouseButton.Left) {
                    ml.leftMouseButtonClicked(modifierMask, x, y);
                } else if (button == MouseButton.Middle) {

                } else if (button == MouseButton.Right) {
                    ml.rightMouseButtonClicked(modifierMask, x, y);
                }
            });
        }
    }

    public void fireMouseButtonDoubleClicked(MouseButton button, int modifierMask, int x, int y) {
        if (isMouseActionEnabled()) {
            mouseListenerList.forEach(ml -> {
                if (button == MouseButton.Left) {
                    ml.leftMouseButtonDoubleClicked(modifierMask, x, y);
                } else if (button == MouseButton.Middle) {

                } else if (button == MouseButton.Right) {
                    ml.rightMouseButtonDoubleClicked(modifierMask, x, y);
                }
            });
        }
    }

    public void fireMouseTrack(MouseTrack mouseTrack, int modifierMask, int x, int y) {
        if (isMouseActionEnabled()) {
            mouseTrackListenerList.forEach(ml -> {
                if (mouseTrack == MouseTrack.Enter) {
                    ml.mouseEnter(modifierMask, x, y);
                } else if (mouseTrack == MouseTrack.Exit) {
                    ml.mouseExit(modifierMask, x, y);
                }
            });
        }
    }

    public void fireMouseMove(MouseMove move, int modifierMask, int x, int y) {
        if (isMouseActionEnabled()) {
            mouseMoveListenerList.forEach(ml -> {
                if (move == MouseMove.Move) {
                    ml.mouseMove(modifierMask, x, y);
                } else if (move == MouseMove.Drag) {
                    if (canDragDelayed && !isAlreadyDragging) {
                        ml.mouseDraggedDelayed(modifierMask, x, y);
                    } else {
                        canDragDelayed = false;
                        isAlreadyDragging = true;
                        ml.mouseDragged(modifierMask, x, y);
                    }
                } else if (move == MouseMove.Drop) {
                    ml.mouseDropped(modifierMask, x, y);
                }
            });
        }
    }

    public void fireMouseScrolled(int modifierMask, int x, int y, int count) {
        if (isMouseActionEnabled()) {
            mouseWheelListenerList.forEach(ml -> {
                ml.mouseScrolled(modifierMask, x, y, count);
            });
        }
    }
}
