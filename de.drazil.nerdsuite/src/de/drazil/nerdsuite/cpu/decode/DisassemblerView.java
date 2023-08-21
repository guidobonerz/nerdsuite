
package de.drazil.nerdsuite.cpu.decode;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.cpu.decode.dialect.KickAssemblerDialect;
import de.drazil.nerdsuite.cpu.platform.C64Platform;
import de.drazil.nerdsuite.cpu.platform.IPlatform;
import de.drazil.nerdsuite.widget.HexViewWidget;

public class DisassemblerView {

    private HexViewWidget hvw;

    public DisassemblerView() {

    }

    @PostConstruct
    public void postConstruct(Composite parent, EMenuService menuService) {
        IPlatform platform = new C64Platform(new KickAssemblerDialect(), false);
        hvw = new HexViewWidget(parent, SWT.V_SCROLL, platform);
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
    
    @Inject
    @Optional
    public void setRangeTag(@UIEventTopic("SetRangeTag") String name) {
        hvw.setRangeTag();
    }
    
    @Inject
    @Optional
    public void clarAllRanges(@UIEventTopic("ClearAllRanges") String name) {
        hvw.clearAllRanges();
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