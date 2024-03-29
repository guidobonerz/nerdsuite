package de.drazil.nerdsuite.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.drazil.nerdsuite.sourceeditor.DocumentPartition;
import lombok.Data;

@Data
public class BasicInstruction implements IWordMatcher {
	private String instruction;
	//private String prefix;
	//private String token;
	private String description;
	private String purpose;
	private boolean includesOpenBrace;
	@JsonProperty(required = false)
	private boolean isComment;
	private int minParameters;
	private int maxParameters;
	private List<String> inParameter;
	private List<BasicToken> tokens;
	private String outValue;
	@JsonIgnore
	private int currentParameter = 0;
	@JsonIgnore
	private int offset = 0;
	@JsonIgnore
	private int selectedTokenIndex = 0;
	@JsonIgnore
	private int selectedVersion = 0;

	@JsonIgnore
	public void reset() {
		currentParameter = 0;
	}

	@JsonIgnore
	public DocumentPartition hasMatch(String text, int offset) {
		DocumentPartition partition = null;
		if (text.charAt(offset) == instruction.charAt(0) && purpose.equals("C")) {
			partition = new DocumentPartition(offset, 1);
		} else {
			int matchIndex = text.indexOf(instruction, offset);
			if (offset == matchIndex) {
				int len = instruction.length();
				if (includesOpenBrace) {
					len--;
				}
				partition = new DocumentPartition(offset, len);
			}
		}
		return partition;
	}

	@JsonIgnore
	public int getTokenControl() {
		if (purpose.equals("I")) {
			return 0;
		} else if (purpose.equals("F")) {
			return 1;
		} else if (purpose.equals("O")) {
			return 2;
		} else if (purpose.equals("C")) {
			return 3;
		} else if (purpose.equals("R")) {
			return 4;
		}
		return 0;
	}
}
