package de.drazil.nerdsuite.disassembler.platform;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.drazil.nerdsuite.basic.BasicParser;
import de.drazil.nerdsuite.disassembler.InstructionLine;
import de.drazil.nerdsuite.disassembler.cpu.CPU_Z80;
import de.drazil.nerdsuite.disassembler.dialect.IDialect;
import de.drazil.nerdsuite.model.Address;
import de.drazil.nerdsuite.model.BasicInstruction;
import de.drazil.nerdsuite.model.BasicInstructions;
import de.drazil.nerdsuite.model.DisassemblingRange;
import de.drazil.nerdsuite.model.InstructionType;
import de.drazil.nerdsuite.model.RangeType;
import de.drazil.nerdsuite.model.ReferenceType;
import de.drazil.nerdsuite.model.Value;
import de.drazil.nerdsuite.widget.IContentProvider;

public class CPC6128Platform extends AbstractPlatform {

    private BasicInstructions basicInstructions;

    public CPC6128Platform(IDialect dialect, boolean ignoreStartAddressBytes) {
        super(dialect, new CPU_Z80(), ignoreStartAddressBytes, "/configuration/platform/cpc6128_platform.json");
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
            ObjectMapper mapper = new ObjectMapper();
            Map<String, BasicInstruction> basicTokenMap = new HashMap<String, BasicInstruction>();
            try {
                System.out.println("read BasicV2 instructions");
                basicInstructions = mapper.readValue(
                        new File("/Users/drazil/Documents/workspace/rcp/de.drazil.NerdSuite/config/basic_v2.json"),
                        BasicInstructions.class);
                for (BasicInstruction bs : basicInstructions.getBasicInstructionList()) {
                    // basicTokenMap.put(bs.getToken(), bs);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            BasicParser basicParser = new BasicParser(programCounter, getCPU(), basicTokenMap);
            String basicCode = basicParser.start(byteArray, programCounter);
            System.out.println(basicCode);
            Value asmStart = basicParser.getLastBasicLineAddress(byteArray, 0).add(2);
            InstructionLine instructionLine = getCPU().getInstructionLineList().get(0);
            instructionLine.setPassed(true);
            instructionLine.setInstructionType(InstructionType.Basic);
            getCPU().splitInstructionLine(instructionLine, programCounter, asmStart.sub(programCounter).add(2),
                    RangeType.Unspecified, ReferenceType.NoReference);
        }
    }

    @Override
    public void parseBinary(IContentProvider contentProvider, List<DisassemblingRange> ranges) {

        System.out.println("init   : build memory map");
        setProgrammCounter(getProgrammCounter());
        init(contentProvider);
        // System.out.println("stage 1: parse header information");
        // parseStartSequence(byteArray, pc);
        System.out.println("stage 2: parse instructions");

        long start = System.currentTimeMillis();
        try {
            for (DisassemblingRange dr : ranges) {
                getCPU().decode(contentProvider, getProgrammCounter(), getPlatFormData(), dr, 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.printf("line: %d\n", getCPU().getLine());
            System.out.printf("%d ms\n", System.currentTimeMillis() - start);
            System.out.println("ready.");
        }
        // System.out.println("stage 3: compress ranges");
        // getCPU().compressRanges();

    }
}
