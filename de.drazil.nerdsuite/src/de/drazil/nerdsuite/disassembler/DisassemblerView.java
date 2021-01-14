
package de.drazil.nerdsuite.disassembler;

import java.nio.file.Path;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.configuration.Configuration;
import de.drazil.nerdsuite.widget.HexViewWidget;

public class DisassemblerView {

	@Inject
	private MPart part;

	private HexViewWidget hvw;

	public DisassemblerView() {

	}

	@PostConstruct
	public void postConstruct(Composite parent, EMenuService menuService) {

		byte[] content = null;
		try {
			content = BinaryFileHandler.readFile(Path.of(Configuration.WORKSPACE_PATH.toString(), "jmain.prg").toFile(),
					0);
		} catch (Exception e) {

			e.printStackTrace();
		}

		hvw = new HexViewWidget(parent, SWT.V_SCROLL);
		hvw.setContent(content);
		menuService.registerContextMenu(hvw.getDisassemblyView(), "de.drazil.nerdsuite.popupmenu.disassemblyView");
		menuService.registerContextMenu(hvw.getBinaryView(), "de.drazil.nerdsuite.popupmenu.binaryView");

	}

	@Inject
	@Optional
	public void jumpToAddress(@UIEventTopic("JumpToAddress") Object o) {
		hvw.jumpToAddress();
	}

	@Inject
	@Optional
	public void returnToCaller(@UIEventTopic("ReturnToOrigin") Object o) {
		hvw.returnToOrigin();
	}

	@Inject
	@Optional
	public void setLabel(@UIEventTopic("SetLabel") String name) {
		hvw.setLabel(name);
	}

	@PreDestroy
	public void preDestroy() {

	}

	@Focus
	public void onFocus() {

	}

	@Persist
	public void save() {

	}

}