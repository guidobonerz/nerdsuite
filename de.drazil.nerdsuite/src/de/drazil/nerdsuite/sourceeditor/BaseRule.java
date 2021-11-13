package de.drazil.nerdsuite.sourceeditor;

import lombok.Getter;
import lombok.Setter;
import de.drazil.nerdsuite.model.SourceRules;

public abstract class BaseRule implements IRule {

	protected boolean hasMatch;
	protected boolean hasMore = false;

	protected char prefixBuffer[];
	protected char suffixBuffer[];
	protected boolean foundPrefix = false;
	protected boolean foundSuffix = false;
	@Getter
	@Setter
	private String prefix;
	@Getter
	@Setter
	private String suffix;
	@Getter
	@Setter
	private Marker marker;
	@Getter
	@Setter
	private Token token;
	@Getter
	@Setter
	private int priority;
	@Getter
	@Setter
	public int offset;

	public BaseRule(SourceRules sourceRule) {
	}

	public BaseRule(String prefix, String suffix, Marker marker, Token token) {
		setPrefix(prefix);
		setSuffix(suffix);
		setToken(token);
		setMarker(marker);
		reset();
	}

	public String getPrefixBufferString(char c, int index) {
		return getBufferString(prefixBuffer, c, index);
	}

	public String getSuffixBufferString(char c, int index) {
		return getBufferString(suffixBuffer, c, index);
	}

	private String getBufferString(char buffer[], char c, int index) {
		if (index >= buffer.length) {
			System.arraycopy(buffer, 1, buffer, 0, buffer.length - 1);
		}
		buffer[(index < buffer.length ? index : buffer.length - 1)] = c;
		return String.valueOf(buffer);
	}

	@Override
	public void reset() {
		prefixBuffer = createCharArray(getPrefix());
		suffixBuffer = createCharArray(getSuffix());
	}

	private char[] createCharArray(String pattern) {
		if (pattern == null)
			return new char[] {};
		return new char[pattern.length()];
	}
}
