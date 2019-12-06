package de.drazil.nerdsuite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Test {
	private static boolean moveGraphActive = false;
	private static Point moveStartPos = new Point(0, 0);
	private static ScrolledComposite sc;
	private static Point startOrigin;

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		sc = new ScrolledComposite(shell, SWT.V_SCROLL|SWT.H_SCROLL);

		Text text = new Text(sc, SWT.MULTI | SWT.READ_ONLY);
		String multiLineText = "";
		for (int i = 0; i < 400; i++) {
			multiLineText += "Hello World SWT dsfjkghsdlfkgjhsdgfklshdfgklsjhdfgklsjdhfgskldjfhgskldfjhgsdklfjghsdklfjhgsdklfgjsdklfjghsdklfjghsdklfghjsdkfhgsdkfjghsdkjfghsdkfjghsdkjfghsdkjfghsdkjfghsdkjfghsdkjflghsdkfjghsdkfjlghs" + i + System.lineSeparator();
		}
		text.setText(multiLineText);
		text.setEnabled(false);
		text.pack();
		sc.setContent(text);

		sc.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(final MouseEvent e) {
				moveGraphActive = e.button == 1;
				if (moveGraphActive) {
					moveStartPos.x = e.x;
					moveStartPos.y = e.y;
					startOrigin = sc.getOrigin();
				}
			}

			@Override
			public void mouseUp(final MouseEvent e) {
				moveGraphActive = false;
				startOrigin = null;
			}
		});

		sc.addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(final MouseEvent e) {
				if (moveGraphActive) {
					// FIXED
					final int newX = startOrigin.x + moveStartPos.x - e.x;
					final int newY = startOrigin.y + moveStartPos.y - e.y;
					sc.setOrigin(newX, newY);
				}
			}
		});

		shell.setSize(500, 150);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

}
