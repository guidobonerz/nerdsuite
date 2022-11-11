package de.drazil.nerdsuite.cpu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.drazil.nerdsuite.Constants;
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
	// private static List<Opcode> opcodeList = new ArrayList<Opcode>(256);
	private static List<AssemblerDirective> directiveList = null;
	// private static List<CpuInstruction> cpuInstructionList = null;

	private static PlatformData platformData = null;
	private static CpuInstructions cpuInstructions = null;
	private static Map<String, Map<String, List<Opcode>>> platformOpcodeMap = new HashMap<String, Map<String, List<Opcode>>>();

	public static void init(PlatformData platformData) throws Exception {
		Bundle bundle = Platform.getBundle(Constants.APP_ID);
		createPlatformspecificData(bundle, platformData);
		// createDirectiveList(bundle, "configuration/kickass_syntax.json");
	}

	public static List<AssemblerDirective> getDirectiveList() {
		return directiveList;
	}

	public static void createDirectiveList(Bundle bundle, String file) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			AssemblerDirectives directives = mapper.readValue(bundle.getEntry(file), AssemblerDirectives.class);
			directiveList = directives.getDirectives();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<CpuInstruction> getCpuInstructionList(String platformId) {
		return null;// cpuInstructionList;
	}

	public static List<Opcode> getOpcodeList(String platformId, String prefix) {
		return platformOpcodeMap.get(platformId).get(prefix);
	}

	public static PlatformData getPlatformData() {
		return platformData;
	}

	public static List<Address> getPlaformAddressList() {
		return platformData.getPlatformAddressList();
	}

	public static void createPlatformspecificData(Bundle bundle, PlatformData platformData) {

		ObjectMapper mapper = new ObjectMapper();

		Map<String, List<Opcode>> opcodeListMap = platformOpcodeMap.get(platformData.getPlatformId());
		if (opcodeListMap == null) {
			opcodeListMap = new HashMap<String, List<Opcode>>();
			try {
				cpuInstructions = mapper.readValue(bundle.getEntry(platformData.getCpuInstructionSource()),
						CpuInstructions.class);
				Collection<CpuInstruction> cpuInstructionList = cpuInstructions.getCpuInstructionList();

				// setup aliases

				for (CpuInstruction cpuInstruction : cpuInstructionList) {
					for (Opcode opcode : cpuInstruction.getOpcodeList()) {
						String prefix = opcode.getPrefix();
						if (prefix == null) {
							prefix = "";
							opcode.setPrefix("");
						}
						List<Opcode> opcodeList = opcodeListMap.get(prefix);
						if (opcodeList == null) {
							opcodeList = new ArrayList<Opcode>(256);
							for (int i = 0; i < 256; i++) {
								opcodeList.add(null);
							}
							opcodeListMap.put(prefix, opcodeList);
						}

						String opcodeName = opcode.getHex();
						int index = Integer.parseInt(opcodeName, 16);
						opcode.setMnemonic(cpuInstruction.getId());
						opcode.setType(cpuInstruction.getType());
						opcode.setAddressingMode(findAddressingMode(opcode.getAddressingModeId()));
						opcodeList.set(index, opcode);
					}

					/*
					 * List<String> aliasNameList = cpuInstruction.getAlias(); if (aliasNameList !=
					 * null) { aliasNameList.add(cpuInstruction.getId()); for (String alias :
					 * aliasNameList) { if (!cpuInstruction.getId().equals(alias)) { CpuInstruction
					 * aliasInstruction = new CpuInstruction(aliasNameList,
					 * cpuInstruction.getType(), cpuInstruction.getFlags(),
					 * cpuInstruction.getCategory(), cpuInstruction.isIllegal(),
					 * cpuInstruction.isStable(), cpuInstruction.getOpcodeList());
					 * aliasInstruction.setId(alias);
					 * aliasInstruction.setDescription(cpuInstruction.getDescription());
					 * aliasOpcodeList.add(aliasInstruction); } } }
					 */
				}
				// cpuInstructionList.addAll(aliasOpcodeList);
			} catch (Exception e) {
				e.printStackTrace();
			}
			platformOpcodeMap.put(platformData.getPlatformId(), opcodeListMap);
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
