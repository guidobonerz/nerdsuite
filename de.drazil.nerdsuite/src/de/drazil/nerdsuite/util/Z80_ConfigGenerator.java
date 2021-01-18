package de.drazil.nerdsuite.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.drazil.nerdsuite.model.AddressingMode;
import de.drazil.nerdsuite.model.CpuInstruction;
import de.drazil.nerdsuite.model.CpuInstructions;
import de.drazil.nerdsuite.model.Opcode;

public class Z80_ConfigGenerator {

	private static int getValueSize(String[] bytes) {
		int count = 0;
		for (String s : bytes) {
			if (s.equals("XX")) {
				count++;
			}
		}

		return count;
	}

	private static int getFirstValuePos(String[] bytes) {
		int count = 0;
		for (String s : bytes) {
			if (s.equals("XX")) {
				count++;
				break;
			}
		}
		return count;
	}

	public static void main(String args[]) {

		try {

			Map<String, CpuInstruction> cpuInstructionMap = new HashMap<>();
			Map<String, Opcode> opcodeMap = new HashMap<>();
			Map<String, AddressingMode> addressingMap = new HashMap<>();
			Collection<String> prefixList = new ArrayList();
			prefixList.add("DDCB");
			prefixList.add("DD");
			prefixList.add("FDCB");
			prefixList.add("FB");
			prefixList.add("CB");
			prefixList.add("ED");
			File inFile = new File("c:\\Users\\drazil\\.nerdsuiteWorkspace\\z80.csv");
			BufferedReader br = new BufferedReader(new FileReader(inFile));
			String line = null;
			CpuInstructions instructions = new CpuInstructions();

			while ((line = br.readLine()) != null) {
				String item[] = line.split(";");
				item[0] = item[0].trim();
				item[1] = item[1].trim();
				String instruction[] = item[0].split(" ");
				String bytes[] = item[1].split(" ");

				CpuInstruction cpuInstruction = cpuInstructionMap.get(instruction[0]);
				if (cpuInstruction == null) {
					cpuInstruction = new CpuInstruction();
					cpuInstruction.setId(instruction[0]);
					cpuInstructionMap.put(instruction[0], cpuInstruction);
				}

				Opcode opcode = new Opcode();
				String prefix = "";
				int hexPos = 0;
				int prefixSize = 0;
				int valueStartPosition = 1;
				if (item[1].startsWith("DD CB")) {
					prefix = "DDCB";
					prefixSize = 2;
					hexPos = 3;
					valueStartPosition = 2;
				} else if (item[1].startsWith("DD")) {
					prefixSize = 1;
					prefix = "DD";
					hexPos = 1;
					valueStartPosition = 2;
				} else if (item[1].startsWith("FD CB")) {
					prefixSize = 2;
					prefix = "FDCB";
					hexPos = 3;
					valueStartPosition = 2;
				} else if (item[1].startsWith("FD")) {
					prefixSize = 1;
					prefix = "FD";
					hexPos = 1;
					valueStartPosition = 2;
				} else if (item[1].startsWith("CB")) {
					prefixSize = 1;
					prefix = "CB";
					hexPos = 1;
					valueStartPosition = -1;
				} else if (item[1].startsWith("ED")) {
					prefixSize = 1;
					prefix = "ED";
					hexPos = 1;
					valueStartPosition = 2;
				}

				int length = getValueSize(bytes) + 1;

				opcode.setHex(bytes[hexPos]);
				opcode.setPrefix(prefix);
				opcode.setValueStartPos(valueStartPosition);
				cpuInstruction.getOpcodeList().add(opcode);

				String mode0 = instruction[0];
				String mode1 = instruction[0];
				if (instruction.length > 1) {
					if (instruction[1].contains("$+2")) {
						length += 1;
					} else if (instruction[1].contains("$+3")) {
						length += 2;
					}

					mode0 = instruction[1];
					mode1 = instruction[1].replace("NN", "{WORD}").replace("N", "{BYTE}");
					mode1 = mode1.replace("$+2", "{BYTE}").replace("$+3", "{WORD}");
				}
				AddressingMode am = addressingMap.get(mode0);
				if (am == null) {
					am = new AddressingMode();
					am.setId(mode0);
					am.setLen(length);
					am.setAddressingMode(mode0);
					am.setArgumentTemplate(mode1.equals(cpuInstruction.getId()) ? "" : mode1);
					addressingMap.put(mode0, am);
				}
				opcode.setAddressingModeId(mode0);

			}
			instructions.setAddressingModeList(addressingMap.values());
			instructions.setCpuInstructionList(cpuInstructionMap.values());
			instructions.setPrefixList(prefixList);
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			mapper.setSerializationInclusion(Include.NON_NULL);
			mapper.writeValue(new File("c:\\\\Users\\\\drazil\\\\.nerdsuiteWorkspace\\\\z80_instructions.json"),
					instructions);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
