package de.drazil.nerdsuite.widget;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.Constants;

public class MemoryViewWidget extends Canvas implements PaintListener {

    private Image image = null;
    private int width = 660;
    private int height = 960;
    private Image[] imageCache;

    public MemoryViewWidget(Composite parent, int style) {
        super(parent, style);
        imageCache = new Image[512];
        prepareImage(width, height);
        prepareImageCache();
        addPaintListener(this);
    }

    private void prepareImageCache() {
        for (int i = 0; i < 256; i++) {
            Image imgGreen = new Image(getDisplay(), 8, 1);
            Image imgRed = new Image(getDisplay(), 8, 1);

            GC gcGreen = new GC(imgGreen);
            GC gcRed = new GC(imgRed);

            int v = 1;
            int x = 7;
            while (v < 256) {
                if ((i & v) == v) {
                    gcGreen.setForeground(Constants.GREEN);
                    gcRed.setForeground(Constants.RED);

                } else {
                    gcGreen.setForeground(Constants.BLACK);
                    gcRed.setForeground(Constants.BLACK);
                }
                gcGreen.drawPoint(x, 0);
                gcRed.drawPoint(x, 0);
                v = v << 1;
                x--;
            }
            imageCache[i] = imgGreen;
            imageCache[i + 256] = imgRed;
            gcGreen.dispose();
            gcRed.dispose();
        }
    }

    private void prepareImage(int width, int height) {
        image = new Image(getDisplay(), width, 960);
        GC gc = new GC(image);
        gc.setBackground(Constants.BLACK);
        gc.fillRectangle(0, 0, width, 960);
        gc.setForeground(Constants.WHITE);
        int from = 0;
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 4; y++) {
                String s = String.format("%04x-%04x", from, from + 0x1fff);
                gc.drawString(s, x * 330, 210 + y * 230);
                from += 0x2000;
            }
        }
        gc.dispose();
    }

    public void paintControl(PaintEvent e) {
        e.gc.drawImage(image, 0, 0);
    }

    public void setByte(int address, int value, boolean rw) {

        int segmentStartY = ((address / 0x2000) % 4) * 230;
        int segmentStartX = (address / 0x8000) * 330;
        int si = address % 0x2000;
        int x = segmentStartX + ((si / 8) % 40 * 8);
        int y = segmentStartY + ((si % 8) + (si / 320) * 8);
        GC gc = new GC(image);

        // System.out.printf("adr:%04x , %02x image: %8s\n", address, value,
        // Integer.toBinaryString(value + (rw ? 0 : 256)));

        gc.drawImage(imageCache[value + (rw ? 0 : 256)], x, y);
        gc.dispose();
        redraw(x, y, 8, 1, false);

    }

    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
        return new Point(width, height);
    }
}
