package de.drazil.nerdsuite.toolchain;

import java.util.ArrayList;
import java.util.List;

public class Toolchain {
	private List<IToolchainStage<?>> toolChainStageList = null;

	public Toolchain() {
		toolChainStageList = new ArrayList<>();
	}

	public <RESULT> void addToolchainStage(IToolchainStage<RESULT> toolChainItem) {
		toolChainStageList.add(toolChainItem);
	}
	
	

	public void start() {
		for (IToolchainStage<?> toolChainStage : toolChainStageList) {
			toolChainStage.start();
			while (toolChainStage.isRunning()) {
			}
		}
	}
}
