package de.drazil.nerdsuite.cpu.platform;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.drazil.nerdsuite.basic.BasicParser;
import de.drazil.nerdsuite.cpu.CPU_Z80;
import de.drazil.nerdsuite.cpu.decode.InstructionLine;
import de.drazil.nerdsuite.cpu.decode.dialect.IDialect;
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
    public boolean supportsBasic() {
        return false;
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
