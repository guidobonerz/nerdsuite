package de.drazil.nerdsuite.enums;

import lombok.Getter;

public enum U64RestApiCommand {

	Version("v1", "GET", "version"), SidPlay("v1", "POST", "runners:sidplay"),
	SidPlayFile("v1", "PUT", "runners:sidplay"), ModPlay("v1", "POST", "runners:modplay"),
	ModPlayFile("v1", "PUT", "runners:modplay"), LoadProgram("v1", "POST", "runners:load_prg"),
	LoadProgramFile("v1", "PUT", "runners:load_prg"), RunProgram("v1", "POST", "runners:run_prg"),
	RunProgramFile("v1", "PUT", "runners:run_prg"), RunCrt("v1", "POST", "runners:run_crt"),
	RunCrtFile("v1", "PUT", "runners:run_crt"), MachineReset("v1", "PUT", "machine:reset"),
	MachineReboot("v1", "PUT", "machine:reboot"), MachinePause("v1", "PUT", "machine:pause"),
	MachineResume("v1", "PUT", "machine:resume"), MachinePowerOff("v1", "PUT", "machine:poweroff"),
	MachineWriteMem("v1", "PUT", "machine:writemem"), MachineRest("v1", "POST", "machine:writemem");

	@Getter
	private String version;
	@Getter
	private String method;
	@Getter
	private String command;

	private U64RestApiCommand(String version, String method, String command) {
		this.version = version;
		this.method = method;
		this.command = command;
	}

}
