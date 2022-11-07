package de.drazil.nerdsuite.widget;

public interface IContentProvider {
    public byte[] getContentArray();

    public int getContentStart();

    public int getContentLength();

    public byte getContentAtOffset(int offset);

}
