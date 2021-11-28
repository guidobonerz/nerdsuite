package de.drazil.nerdsuite.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.drazil.nerdsuite.sourceeditor.DocumentPartition;
import lombok.Data;

@Data
public class BasicDirective implements IWordMatcher {
	private String name;

	private int offset = 0;

	public BasicDirective(String name) {
		this.name = name;
	}

	public DocumentPartition hasMatch(String text, int offset) {
		DocumentPartition partition = null;

		int matchIndex = text.indexOf(name, offset);
		if (offset == matchIndex) {
			int len = name.length();

			partition = new DocumentPartition(offset, len);
		}

		return partition;
	}

	@JsonIgnore
	public int getTokenControl() {
		return 4;
	}
}
