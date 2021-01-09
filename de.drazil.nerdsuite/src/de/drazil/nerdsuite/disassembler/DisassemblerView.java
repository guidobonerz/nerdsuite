
package de.drazil.nerdsuite.disassembler;

import java.nio.file.Path;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.configuration.Configuration;
import de.drazil.nerdsuite.widget.HexViewWidget;

public class DisassemblerView {

	@Inject
	public DisassemblerView() {

	}

	@PostConstruct
	public void postConstruct(Composite parent) {

		byte[] content = null;
		try {
			content = BinaryFileHandler.readFile(Path.of(Configuration.WORKSPACE_PATH.toString(), "jmain.prg").toFile(),
					0);
		} catch (Exception e) {

			e.printStackTrace();
		}

		HexViewWidget hvw = new HexViewWidget(parent, SWT.V_SCROLL);
		hvw.setContent(content);

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