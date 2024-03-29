package de.drazil.nerdsuite.toolchain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.exec.OS;

import de.drazil.nerdsuite.log.Console;

public class ExternalRunnerToolchainStage implements IToolchainStage<Object> {
	private String name;
	private boolean isRunning = false;
	private ProcessBuilder processbuilder;
	private Process process;

	public ExternalRunnerToolchainStage(String name, String... parameters) {
		this(name, Arrays.asList(parameters));
	}

	public ExternalRunnerToolchainStage(String name, List<String> parameters) {
		this.name = name;
		this.processbuilder = new ProcessBuilder(patchStartCommand(parameters));
	}

	@Override
	public void start() {
		Console.println(name);
		for (String command : processbuilder.command()) {
			Console.print(command);
		}
		Console.println();

		try {
			process = processbuilder.start();

			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line = null;
			while (null != (line = br.readLine())) {
				Console.println(line);
			}
			br.close();

			int errorCode = process.waitFor();
			if (errorCode == 1) {
				line = null;
				br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				while (null != (line = br.readLine())) {
					Console.println(line);
				}
				br.close();
				Console.println("External Process stopped with errors");
			} else if (errorCode == 0) {
				Console.println("External Process stopped normally");
			}

			process.destroy();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}

	@Override
	public Object getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	private List<String> patchStartCommand(List<String> parameters) {
		if (parameters != null && parameters.size() > 0) {
			for (int i = 0; i < parameters.size(); i++) {
				String argument = parameters.get(i);
				if (OS.isFamilyMac() && argument.endsWith(".app")) {
					parameters.add(i, "open");
					i += 1;
					parameters.add(i + 1, "--args");
				}
			}
		}
		return parameters;
	}
}
