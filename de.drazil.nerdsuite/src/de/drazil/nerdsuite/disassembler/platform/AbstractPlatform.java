package de.drazil.nerdsuite.disassembler.platform;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.assembler.InstructionSet;
import de.drazil.nerdsuite.basic.BasicParser;
import de.drazil.nerdsuite.disassembler.InstructionLine;
import de.drazil.nerdsuite.disassembler.cpu.ICPU;
import de.drazil.nerdsuite.disassembler.dialect.IDialect;
import de.drazil.nerdsuite.model.BasicInstruction;
import de.drazil.nerdsuite.model.BasicInstructions;
import de.drazil.nerdsuite.model.InstructionType;
import de.drazil.nerdsuite.model.PlatformData;
import de.drazil.nerdsuite.model.Range;
import de.drazil.nerdsuite.model.ReferenceType;
import de.drazil.nerdsuite.model.Value;
import de.drazil.nerdsuite.widget.IContentProvider;

public abstract class AbstractPlatform implements IPlatform {
    private IDialect dialect;
    private boolean ignoreStartAddressBytes = false;
    private boolean supportsBasic = false;
    private ICPU cpu;
    private PlatformData platformData;
    private BasicParser basicParser;
    private BasicInstructions basicInstructions;
    private Value pc;

    public AbstractPlatform(IDialect dialect, ICPU cpu, boolean ignoreStartAddressBytes, String addressFileName) {
        setDialect(dialect);
        setCPU(cpu);
        setIgnoreStartAddressBytes(ignoreStartAddressBytes);
        readPlatformData(addressFileName);
        readBasicTokens();
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

    public void init(IContentProvider contentProvider) {
        getCPU().addInstructionLine(new InstructionLine(getProgrammCounter(),
                new Range(0, contentProvider.getContentLength()), InstructionType.Undefined,
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

    private void readBasicTokens() {

        String basicInstructionSource = platformData.getBasicInstructionSource();
        if (basicInstructionSource != null && supportsBasic()) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, BasicInstruction> basicTokenMap = new HashMap<String, BasicInstruction>();
            try {
                System.out.printf("read basic instructions from : %s", basicInstructionSource);
                Bundle bundle = Platform.getBundle(Constants.APP_ID);
                URL url = bundle.getEntry(basicInstructionSource);
                File file = new File(FileLocator.resolve(url).toURI());
                basicInstructions = mapper.readValue(file, BasicInstructions.class);
                for (BasicInstruction bs : basicInstructions.getBasicInstructionList()) {
                    // basicTokenMap.put(bs.getToken(), bs);
                }
                BasicParser basicParser = new BasicParser(getProgrammCounter(), getCPU(), basicTokenMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    public Value checkAdress(byte[] content, int start) {
        Value value = new Value(0);
        List<String> commonStartAdress = getPlatFormData().getCommonStartAdresses();
        if (commonStartAdress != null) {
            for (String address : commonStartAdress) {
                int adr = Integer.parseInt(address, 16);
                if (adr == getCPU().getWord(content, start)) {
                    value = new Value(adr);
                    break;
                }
            }
        }
        return value;
    }
}
