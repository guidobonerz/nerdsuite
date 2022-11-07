package de.drazil.nerdsuite.disassembler.platform;

import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.assembler.InstructionSet;
import de.drazil.nerdsuite.disassembler.InstructionLine;
import de.drazil.nerdsuite.disassembler.cpu.ICPU;
import de.drazil.nerdsuite.disassembler.dialect.IDialect;
import de.drazil.nerdsuite.model.InstructionType;
import de.drazil.nerdsuite.model.PlatformData;
import de.drazil.nerdsuite.model.Range;
import de.drazil.nerdsuite.model.RangeType;
import de.drazil.nerdsuite.model.ReferenceType;
import de.drazil.nerdsuite.model.Value;

public abstract class AbstractPlatform implements IPlatform {
    private IDialect dialect;
    private boolean ignoreStartAddressBytes = false;
    private ICPU cpu;
    private PlatformData platformData;
    private Value pc;

    public AbstractPlatform(IDialect dialect, ICPU cpu, boolean ignoreStartAddressBytes, String addressFileName) {
        setDialect(dialect);
        setCPU(cpu);
        setIgnoreStartAddressBytes(ignoreStartAddressBytes);
        readPlatformData(addressFileName);

    }

    public IDialect getDialect() {
        return dialect;
    }

    public void setDialect(IDialect dialect) {
        this.dialect = dialect;
    }

    public boolean isIgnoreStartAddressBytes() {
        return ignoreStartAddressBytes;
    }

    public void setIgnoreStartAddressBytes(boolean ignoreStartAddressBytes) {
        this.ignoreStartAddressBytes = ignoreStartAddressBytes;
    }

    public ICPU getCPU() {
        return cpu;
    }

    public void setCPU(ICPU cpu) {
        this.cpu = cpu;
    }

    public void init(byte byteArray[], Range range, RangeType rangeType) {
        getCPU().addInstructionLine(new InstructionLine(getProgrammCounter(), range,
                rangeType == RangeType.Code ? InstructionType.Asm : InstructionType.Data,
                ReferenceType.NoReference));
    }

    @Override
    public Value getProgrammCounter() {
        return pc;
    }

    @Override
    public void setProgrammCounter(Value pc) {
        this.pc = pc;
    }

    @Override
    public PlatformData getPlatFormData() {
        return platformData;
    }

    private void readPlatformData(String fileName) {

        try {
            Bundle bundle = Platform.getBundle(Constants.APP_ID);
            URL url = bundle.getEntry(fileName);
            File file = new File(FileLocator.resolve(url).toURI());
            ObjectMapper mapper = new ObjectMapper();
            platformData = mapper.readValue(file, PlatformData.class);
            InstructionSet.init(platformData);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
