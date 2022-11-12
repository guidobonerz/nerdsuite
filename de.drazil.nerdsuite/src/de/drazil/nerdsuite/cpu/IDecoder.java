package de.drazil.nerdsuite.cpu;

import de.drazil.nerdsuite.model.DisassemblingRange;
import de.drazil.nerdsuite.model.PlatformData;
import de.drazil.nerdsuite.model.Value;
import de.drazil.nerdsuite.widget.IContentProvider;

public interface IDecoder {
    public void decode(IContentProvider contentProvider, Value pc,
            PlatformData platformData,
            DisassemblingRange discoverableRange, int stage);
}
