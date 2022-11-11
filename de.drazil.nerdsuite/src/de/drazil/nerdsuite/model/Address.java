package de.drazil.nerdsuite.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Address {
    @JsonIgnore
    private Value value;
    private String address;
    private String constName;
    private String description;
    @JsonProperty(value = "readBitMask")
    private List<BitMask> readBitMaskList;
    @JsonProperty(value = "writeBitMask")
    private List<BitMask> writeBitMaskList;

    public Address(String address, String constName, String description) {
        this.address = address;
        this.constName = constName;
        this.description = description;
    }

    @JsonIgnore
    public int getAddressValue() {
        return Integer.parseInt(address, 16);
    }

    public boolean matches(int value) {
        return getAddressValue() == value;
    }

    public boolean hasWriteBitmaskConfiguration() {
        return writeBitMaskList != null && writeBitMaskList.size() > 0;
    }

    public boolean hasReadBitmaskConfiguration() {
        return readBitMaskList != null && readBitMaskList.size() > 0;
    }
}
