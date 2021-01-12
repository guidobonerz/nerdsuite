package de.drazil.nerdsuite.disassembler;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.drazil.nerdsuite.disassembler.cpu.AbstractCPU;
import de.drazil.nerdsuite.disassembler.dialect.KickAssemblerDialect;
import de.drazil.nerdsuite.disassembler.platform.C64Platform;
import de.drazil.nerdsuite.disassembler.platform.IPlatform;
import de.drazil.nerdsuite.model.ConversionType;
import de.drazil.nerdsuite.model.InstructionType;
import de.drazil.nerdsuite.model.Opcode;
import de.drazil.nerdsuite.model.Range;
import de.drazil.nerdsuite.model.ReferenceType;
import de.drazil.nerdsuite.util.NumericConverter;

public class Disassembler {
	private byte byteArray[] = null;
	private IPlatform platform = null;

	public Disassembler() {
		this(new C64Platform(new KickAssemblerDialect(), false));
	}

	public Disassembler(IPlatform platform) {
		this.platform = platform;
	}

	public void start(String file) {

		try {
			byteArray = BinaryFileHandler.readFile(new File(file), 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//byteArray = platform.parseBinary(byteArray);

		Map<String, ConversionType> conversionMap = new HashMap<String, ConversionType>();
		// conversionMap.put("099a", ConversionType.CharsetData);
		// conversionMap.put("119a", ConversionType.MulticolorSpriteData);
		// print the result

		int byteCount = 0;
		for (InstructionLine instructionLine : platform.getCPU().getInstructionLineList()) {
			// System.out.printf("%s: %s\n",
			// instructionLine.getProgramCounter(),
			// instructionLine.getReferenceType());
			byteCount += instructionLine.getRange().getLen();
			printDiasassembly(instructionLine, conversionMap);
		}

		System.out.println(byteCount + ": bytes");

	}

	private void printDiasassembly(InstructionLine instructionLine, Map<String, ConversionType> conversionMap) {
		Range range = instructionLine.getRange();
		Opcode opcode = platform.getCPU().getOpcodeByIndex(byteArray, range.getOffset());
		String pc = NumericConverter.toHexString(instructionLine.getProgramCounter().getValue(), 4);

		String s1 = "";
		String s2 = "";
		String s3 = " ";
		String s0 = "";
		if (opcode != null && instructionLine.getInstructionType() == InstructionType.Asm) {
			s1 = opcode.getMnemonic();
			s2 = AbstractCPU.getMnemonicArgument(opcode, range, byteArray);
			System.out.printf("%s: %3s %8s %30s len:%s %s\n", pc, s1, s2,
					(instructionLine.hasReferenceValue() ? instructionLine.getReferenceValue() : ""),
					instructionLine.getRange().getLen(),
					instructionLine.getReferenceType() == ReferenceType.JumpMark ? "JumpMark" : "");
		} else {
			ConversionType ct = conversionMap.get(pc);
			if (ct == null) {
				for (int i = (int) range.getOffset(); i < (range.getOffset() + range.getLen()); i++) {
					s1 += NumericConverter.toHexString(NumericConverter.getByteAsInt(byteArray, i), 2) + " ";
				}
				System.out.printf("%s: %s %s \n", pc, s1, instructionLine.getReferenceType());
			} else if (ct == ConversionType.MulticolorSpriteData) {
				int index = 0;
				while (index < range.getLen()) {
					System.out.println(NumericConverter.toBinaryString(byteArray, range.getOffset() + index, 3));
					index += 3;
				}
			} else if (ct == ConversionType.CharsetData) {

				int index = 0;
				while (index < range.getLen()) {
					if (range.getOffset() + index > byteArray.length - 1)
						break;
					System.out.println(NumericConverter.toBinaryString(byteArray, range.getOffset() + index, 1));
					index++;
				}

			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// InstructionSet.init("/Users/drazil/Documents/workspace/rcp/de.drazil.NerdSuite/");
		new Disassembler().start("/Users/drazil/LocalApplications/step74/jmain.prg");
		// new
		// Disassembler().start("/Users/drazil/Documents/retro_computing/c64_stuff/prg/for.prg");
		// new Disassembler().start("/Users/drazil/Downloads/scroll-#6.prg");

		// new Disassembler().start("c:\\data\\scroll-#6.prg");
		// new Disassembler().start("c:\\data\\jmain.prg");
	}
}
