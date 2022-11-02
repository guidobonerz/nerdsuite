package de.drazil.nerdsuite.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class DisassemblingRange {
    private int offset = 0;
    private int len = 0;
    private boolean dirty;
    private RangeType rangeType = RangeType.Unspecified;
}
