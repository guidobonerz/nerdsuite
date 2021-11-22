package de.drazil.nerdsuite.imaging;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.handler.BrokerObject;
import de.drazil.nerdsuite.model.Key;
import de.drazil.nerdsuite.model.PlatformColor;
import de.drazil.nerdsuite.widget.IHitKeyListener;
import de.drazil.nerdsuite.widget.PlatformFactory;
import de.drazil.nerdsuite.widget.VirtualKeyboard;

public class VirtualKeyboardView implements IHitKeyListener {

	@Inject
	IEventBroker broker;

	private VirtualKeyboard virtualKeyboard;

	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout(1, true));
		List<PlatformColor> colorList = PlatformFactory.getPlatformColors("C64");
		virtualKeyboard = new VirtualKeyboard(parent, 0, colorList);
		virtualKeyboard.addHitKeyListener(this);
		virtualKeyboard.setLayoutData(new GridData(GridData.CENTER, GridData.BEGINNING, true, true));
		parent.setBackground(Constants.GREY3);
	}

	@Override
	public void keyPressed(Key key) {
		broker.send("KeyboardSequence", new BrokerObject("VIRTUAL_KEYBOARD", key));
	}
}
