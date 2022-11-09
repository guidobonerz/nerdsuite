package de.drazil.nerdsuite.widget;

public interface IContentProvider {
    public byte[] getContentArray();

    public int getContentOffset();

    public int getContentLength();

    public byte getContentAtOffset(int offset);

}
