package de.drazil.nerdsuite.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class BitMask {
    private int mask;
    private String description;
}
