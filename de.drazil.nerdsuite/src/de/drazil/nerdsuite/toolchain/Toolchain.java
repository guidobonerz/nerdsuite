package de.drazil.nerdsuite.toolchain;

import java.util.ArrayList;
import java.util.List;

public class Toolchain {
	private List<IToolchainItem<?>> toolChainItemList = null;

	public Toolchain() {
		toolChainItemList = new ArrayList<>();
	}

	public <RESULT> void addToolChainItem(IToolchainItem<RESULT> toolChainItem) {
		toolChainItemList.add(toolChainItem);
	}

	public void start() {
		for (IToolchainItem<?> toolChainItem : toolChainItemList) {
			toolChainItem.start();
			while (toolChainItem.isRunning()) {
			}
		}
	}
}
