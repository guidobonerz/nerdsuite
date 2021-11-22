package de.drazil.nerdsuite.util;

public class ArrayUtil {

	public final static byte[] update(byte[] target, byte[] source, int offset) {
		for (int i = 0; i < source.length; i++) {
			target[offset + i] = source[i];
		}
		return target;
	}

	public final static byte[] grow(byte[] target, byte source) {
		return grow(target, new byte[] { source });
	}

	public final static byte[] grow(byte[] target, byte[] source) {
		byte[] oldTarget = target;
		if (target == null) {
			target = new byte[source.length];
			System.arraycopy(source, 0, target, 0, source.length);
		} else {
			int offset = target.length;
			target = new byte[target.length + source.length];
			System.arraycopy(oldTarget, 0, target, 0, oldTarget.length);
			System.arraycopy(source, 0, target, offset, source.length);
		}
		return target;

	}
}
