package de.drazil.nerdsuite.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BasicInstruction implements IWordMatcher {
	private String instruction;
	private String prefix;
	private String token;
	private String description;
	private String purpose;
	private boolean includesOpenBrace;
	@JsonProperty(required = false)
	private boolean isComment;
	private int minParameters;
	private int maxParameters;
	private int minVersion;
	private List<String> inParameter;
	private String outValue;
	@JsonIgnore
	private int currentParameter = 0;
	@JsonIgnore
	private int offset = 0;

	@JsonIgnore
	public void reset() {
		currentParameter = 0;
	}

	@JsonIgnore
	public Range hasMatch(String text, int offset) {
		Range range = null;
		int matchIndex = text.indexOf(instruction, offset);
		if (offset == matchIndex) {
			int len = instruction.length();
			if (includesOpenBrace) {
				len--;
			}
			range = new Range(offset, len);
		}
		return range;
	}

	@JsonIgnore
	public int getTokenControl() {
		if (purpose.equals("C")) {
			return 0;
		} else if (purpose.equals("F")) {
			return 1;
		} else if (purpose.equals("O")) {
			return 2;
		}
		return 0;
	}
}
