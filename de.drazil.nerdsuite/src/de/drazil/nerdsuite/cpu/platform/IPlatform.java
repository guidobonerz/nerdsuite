package de.drazil.nerdsuite.cpu.platform;

import java.util.List;

import de.drazil.nerdsuite.cpu.ICPU;
import de.drazil.nerdsuite.cpu.decode.dialect.IDialect;
import de.drazil.nerdsuite.model.Address;
import de.drazil.nerdsuite.model.DisassemblingRange;
import de.drazil.nerdsuite.model.PlatformData;
import de.drazil.nerdsuite.model.Value;
import de.drazil.nerdsuite.widget.IContentProvider;

public interface IPlatform {
    public IDialect getDialect();

    public void setDialect(IDialect dialect);

    public abstract boolean supportsSpecialStartSequence();

    public abstract boolean supportsBasic();

    public abstract void parseStartSequence(byte byteArray[], Value programCounter);

    public boolean isIgnoreStartAddressBytes();

    public void setIgnoreStartAddressBytes(boolean ignoreStartAddressBytes);

    public ICPU getCPU();

    public void setCPU(ICPU cpu);

    public void setProgrammCounter(Value pc);

    public Value getProgrammCounter();

    public void handlePlatformSpecific(byte byteArray[], int offset);

    public void init(IContentProvider contentProvider);

    public void parseBinary(IContentProvider contentProvider, List<DisassemblingRange> ranges);

    public PlatformData getPlatFormData();

    public Value checkAdress(byte content[], int start);

    public void handleAddress(Address address, Value value);

}
