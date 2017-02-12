package de.drazil.nerdsuite.handler;

import java.util.ArrayList;

import org.eclipse.e4.core.di.annotations.Execute;

import de.drazil.nerdsuite.log.Console;
import de.drazil.nerdsuite.toolchain.DefaultToolchainItem;
import de.drazil.nerdsuite.toolchain.Toolchain;

public class BuildHandler
{
	@Execute
	public void execute()
	{
		Console.clear();
		Console.setOutputStream(System.out);
		ArrayList<String> parameters1 = new ArrayList<>();
		parameters1.add("java");
		parameters1.add("-jar");
		parameters1.add("/Users/drazil/Downloads/KickAssembler1/KickAss.jar");
		parameters1.add("/Users/drazil/Documents/coding/c64asm/irq1.asm");
		parameters1.add("-showmem");
		parameters1.add("-symbolfiledir");
		parameters1.add("/Users/drazil/.nerdsuiteWorkspace/test1/symbol/");
		parameters1.add("-o");
		parameters1.add("/Users/drazil/.nerdsuiteWorkspace/test1/bin/irq2.prg");
		Toolchain toolChain = new Toolchain();
		toolChain.addToolChainItem(new DefaultToolchainItem("Build", parameters1));

		ArrayList<String> parameters2 = new ArrayList<>();
		parameters2.add("/Users/drazil/LocalApplications/vice/x64sc.app");
		parameters2.add("/Users/drazil/.nerdsuiteWorkspace/test1/bin/irq2.prg");
		toolChain.addToolChainItem(new DefaultToolchainItem("Run", parameters2));
		toolChain.execute();
	}
}