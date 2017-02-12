package de.drazil.nerdsuite.assembler;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.drazil.nerdsuite.model.Address;
import de.drazil.nerdsuite.model.AddressingMode;
import de.drazil.nerdsuite.model.AssemblerDirective;
import de.drazil.nerdsuite.model.AssemblerDirectives;
import de.drazil.nerdsuite.model.CpuInstruction;
import de.drazil.nerdsuite.model.CpuInstructions;
import de.drazil.nerdsuite.model.Opcode;
import de.drazil.nerdsuite.model.PlatformData;

public final class InstructionSet {

	private static List<String> labelList = new ArrayList<String>(30);
	private static List<Opcode> opcodeList = new ArrayList<Opcode>(256);
	private static List<AssemblerDirective> directiveList = null;
	private static List<CpuInstruction> cpuInstructionList = null;

	private static PlatformData platformData = null;
	private static CpuInstructions cpuInstructions = null;

	public static void init(String baseUrl) throws Exception {
		createPlatformspecificData(baseUrl, "configuration/c64_platform.json");
		createDirectiveList(baseUrl, "configuration/kickass_syntax.json");
	}

	public static List<AssemblerDirective> getDirectiveList() {
		return directiveList;
	}

	public static void createDirectiveList(String baseUrl, String file) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			AssemblerDirectives directives = mapper.readValue(new URL(baseUrl + file), AssemblerDirectives.class);
			directiveList = directives.getDirectives();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<CpuInstruction> getCpuInstructionList() {
		return cpuInstructionList;
	}

	public static List<Opcode> getOpcodeList() {
		return opcodeList;
	}

	public static PlatformData getPlatformData() {
		return platformData;
	}

	public static List<Address> getPlaformAddressList() {
		return platformData.getPlatformAddressList();
	}

	public static void createPlatformspecificData(String baseUrl, String file) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			platformData = mapper.readValue(new URL(baseUrl + file), PlatformData.class);
			cpuInstructions = mapper.readValue(new URL(baseUrl + platformData.getCpuInstructionSource()),
					CpuInstructions.class);
			cpuInstructionList = cpuInstructions.getCpuInstructionList();

			// setup aliases
			List<CpuInstruction> aliasOpcodeList = new ArrayList<CpuInstruction>();
			for (int i = 0; i < 256; i++)
				opcodeList.add(null);

			for (CpuInstruction cpuInstruction : cpuInstructionList) {
				for (Opcode opcode : cpuInstruction.getOpcodeList()) {
					String opcodeName = opcode.getHex();
					int index = Integer.parseInt(opcodeName, 16);
					opcode.setMnemonic(cpuInstruction.getId());
					opcode.setType(cpuInstruction.getType());
					opcode.setAddressingMode(findAddressingMode(opcode.getAddressingModeId()));
					opcodeList.set(index, opcode);
				}

				List<String> aliasNameList = cpuInstruction.getAlias();
				if (aliasNameList != null) {
					aliasNameList.add(cpuInstruction.getId());
					for (String alias : aliasNameList) {
						if (!cpuInstruction.getId().equals(alias)) {
							CpuInstruction aliasInstruction = new CpuInstruction(aliasNameList,
									cpuInstruction.getType(), cpuInstruction.getFlags(), cpuInstruction.getCategory(),
									cpuInstruction.isIllegal(), cpuInstruction.isStable(),
									cpuInstruction.getOpcodeList());
							aliasInstruction.setId(alias);
							aliasInstruction.setDescription(cpuInstruction.getDescription());
							aliasOpcodeList.add(aliasInstruction);
						}
					}
				}
			}
			cpuInstructionList.addAll(aliasOpcodeList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<String> getLabelList() {
		return labelList;
	}

	public static void addLabel(String label) {
		labelList.add(label);
	}

	public static void clearLabelList() {
		while (labelList.size() > 0) {
			labelList.remove(0);
		}
	}

	public static AddressingMode findAddressingMode(String id) {
		for (AddressingMode syntax : cpuInstructions.getAddressingModeList()) {
			if (id.equals(syntax.getId()))
				return syntax;
		}
		return null;
	}
}
