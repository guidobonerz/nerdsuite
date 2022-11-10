package de.drazil.nerdsuite.disassembler.platform;

import java.util.List;

import de.drazil.nerdsuite.basic.BasicParser;
import de.drazil.nerdsuite.disassembler.InstructionLine;
import de.drazil.nerdsuite.disassembler.cpu.CPU_6510;
import de.drazil.nerdsuite.disassembler.dialect.IDialect;
import de.drazil.nerdsuite.model.Address;
import de.drazil.nerdsuite.model.DisassemblingRange;
import de.drazil.nerdsuite.model.InstructionType;
import de.drazil.nerdsuite.model.RangeType;
import de.drazil.nerdsuite.model.ReferenceType;
import de.drazil.nerdsuite.model.Value;
import de.drazil.nerdsuite.widget.IContentProvider;

public class C64Platform extends AbstractPlatform {

    public C64Platform(IDialect dialect, boolean ignoreStartAddressBytes) {
        super(dialect, new CPU_6510(), ignoreStartAddressBytes, "/configuration/platform/c64_platform.json");
    }

    @Override
    public boolean supportsBasic() {
        return true;
    }

    @Override
    public boolean supportsSpecialStartSequence() {
        return true;
    }

    @Override
    public void handlePlatformSpecific(byte[] byteArray, int offset) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleAddress(Address address, Value value) {
        // TODO Auto-generated method stub

    }

    @Override
    public void parseStartSequence(byte byteArray[], Value programCounter) {
        // is basic start/
        if (programCounter.getValue() == 2049) {

            
            // String basicCode = basicParser.start(byteArray, programCounter);
            // System.out.println(basicCode);
            //Value asmStart = basicParser.getLastBasicLineAddress(byteArray, 2);

            InstructionLine instructionLine = getCPU().getInstructionLineList().get(0);
            // instructionLine = getCPU().splitInstructionLine(instructionLine,
            // programCounter, asmStart);
            instructionLine.setPassed(true);
            instructionLine.setInstructionType(InstructionType.Basic);
            // getCPU().splitInstructionLine(instructionLine, programCounter,
            // asmStart.sub(programCounter).add(2), RangeType.Unspecified,
            // ReferenceType.NoReference);
        }
    }

    @Override
    public void parseBinary(IContentProvider contentProvider, List<DisassemblingRange> ranges) {

        System.out.println("init   : build memory map");
        setProgrammCounter(getProgrammCounter());
        init(contentProvider);
        System.out.println("stage 1: parse header information");
        parseStartSequence(contentProvider.getContentArray(), getProgrammCounter());
        System.out.println("stage 2: parse instructions");

        long start = System.currentTimeMillis();
        for (DisassemblingRange dr : ranges) {
            getCPU().decode(contentProvider, getProgrammCounter(), getPlatFormData(), dr, 2);
        }
        long duration = (System.currentTimeMillis() - start);
        System.out.printf("%d Seconds", duration);
        // System.out.println("stage 3: compress ranges");
        // getCPU().compressRanges();
        System.out.println("ready.");
    }

}
