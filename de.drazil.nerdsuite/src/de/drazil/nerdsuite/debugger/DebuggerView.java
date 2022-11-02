
package de.drazil.nerdsuite.debugger;

import java.util.ArrayList;

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

import de.drazil.nerdsuite.disassembler.dialect.KickAssemblerDialect;
import de.drazil.nerdsuite.disassembler.platform.C64Platform;
import de.drazil.nerdsuite.disassembler.platform.IPlatform;
import de.drazil.nerdsuite.network.TcpHandler;
import de.drazil.nerdsuite.network.TcpHandler.Response;
import de.drazil.nerdsuite.toolchain.Toolchain;
import de.drazil.nerdsuite.widget.HexViewWidget;

public class DebuggerView {

    @Inject
    private MPart part;

    private HexViewWidget hvw;

    public DebuggerView() {

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

    @PreDestroy
    public void preDestroy() {

    }

    @Focus
    public void onFocus() {

    }

    @Persist
    public void save() {

    }

    public static void main(String argv[]) {
        TcpHandler th = new TcpHandler("127.0.0.1", 6502);
        Toolchain toolChain = new Toolchain();

        ArrayList<String> parameters2 = new ArrayList<>();

        parameters2.add("\\Users\\drazil\\applications\\GTK3VICE-3.5-win64\\bin\\x64sc.exe");
        parameters2.add("-binarymonitor");
        // parameters2.add("-binarymonitoraddress");
        // parameters2.add("localhost");
        // parameters2.add("\\Users\\drazil\\.nerdsuiteWorkspace\\teleporter64.prg");

        // toolChain.addToolChainItem(new DefaultToolchainItem("Run", parameters2));
        // toolChain.start();

        // TimeUnit.SECONDS.sleep(4);
        // System.out.println("main done.");
        th.openSocket();
        Response response = th.write(th.buildCommand(new byte[] { (byte) 0xbb }));
        int a = 0;

    }

}
