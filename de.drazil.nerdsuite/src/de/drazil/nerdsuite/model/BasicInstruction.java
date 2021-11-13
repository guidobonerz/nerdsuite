package de.drazil.nerdsuite.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.drazil.nerdsuite.sourceeditor.Token;
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
	@Override
	public boolean hasMatch(String value, Token token, int offset) {
		boolean hasMatch = false;
		this.offset = offset;
		int matchIndex = value.indexOf(instruction, offset);
		if (matchIndex != -1) {
			token.setStart(matchIndex);
			int length = instruction.length();
			if (includesOpenBrace) {
				length--;
			}
			this.offset = matchIndex + length + 1;
			token.setLength(length);
			hasMatch = true;
		}
		token.setValid(hasMatch);
		return hasMatch;
	}
}
