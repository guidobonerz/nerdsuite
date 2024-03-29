package de.drazil.nerdsuite.storagemedia;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;

import com.google.common.collect.ComparisonChain;

import de.drazil.nerdsuite.cpu.Endianness;
import de.drazil.nerdsuite.util.BinaryFileHandler;
import de.drazil.nerdsuite.util.NumericConverter;

public abstract class AbstractBaseMediaContainer implements IMediaContainer {

    public byte[] content;
    private MediaEntry root;
    private File container;

    public AbstractBaseMediaContainer(File file) {
        container = file;
        root = new MediaEntry();
        root.setRoot(true);
        root.setUserObject(file);
    }

    public File getContainer() {
        return container;
    }

    @Override
    public MediaEntry[] getEntries(Object parentEntry) {
        MediaEntry[] list = new MediaEntry[] {};
        MediaEntry mediaEntry = getRoot();
        if (parentEntry instanceof MediaEntry) {
            mediaEntry = (MediaEntry) parentEntry;
        }
        readEntries(mediaEntry);

        Collections.sort(mediaEntry.getChildrenList(), new Comparator<MediaEntry>() {
            @Override
            public int compare(MediaEntry me1, MediaEntry me2) {
                return ComparisonChain.start().compareTrueFirst(me1.isDirectory(), me2.isDirectory())
                        .compare(me1.getName(), me2.getName()).compare(me1.getType(), me2.getType()).result();
            }
        });
        list = mediaEntry.getChildrenList().toArray(new MediaEntry[mediaEntry.getChildrenCount()]);
        return list;
    }

    @Override
    public boolean hasEntries(Object entry) {
        boolean hasChildren = false;
        if (entry instanceof MediaEntry) {
            MediaEntry me = (MediaEntry) entry;
            hasChildren = me.hasChildren();
        }
        return hasChildren;
    }

    public MediaEntry getRoot() {
        return root;
    }

    @Override
    public byte[] read(File file) throws Exception {
        getRoot().clear();
        content = BinaryFileHandler.readFile(file, 0);
        readHeader();
        return content;
    }

    protected int getWord(int start) {
        return getWord(start, Endianness.LittleEndian);
    }

    protected int getWord(int start, Endianness endianess) {
        return NumericConverter.getWordAsInt(content, start, endianess);
    }

    protected int getByte(int start) {
        return NumericConverter.getByteAsInt(content, start);
    }

    protected String getString(int start, int end, boolean skipCharCheck) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i <= end; i++) {
            char c = (char) (content[i] & 0x7f);
            if (Character.isLetter(c) || Character.isDigit(c) || skipCharCheck) {
                sb.append(Character.toString(c));
            }
        }
        return sb.toString();
    }

    public boolean isEmptyEntry(int base, int maxCount) {
        int lastValue = content[base];
        int count = 0;
        for (int i = base; i < base + maxCount; i++) {
            if (i > 0) {
                count += (content[i] == lastValue ? 1 : 0);
            }
            lastValue = content[i];
        }
        return count == maxCount;
    }

    public boolean isEmptyEntry(int base, int maxCount, int checkValue) {
        int count = 0;
        for (int i = base; i < base + maxCount; i++) {
            count += ((content[i] & 0xff) == checkValue ? 1 : 0);
        }
        return count == maxCount;
    }

    public void exportEntry(MediaEntry entry, File file) throws Exception {
        try {
            final OutputStream os = new FileOutputStream(file);
            readContent(entry, new IMediaEntryWriter() {
                @Override
                public void write(MediaEntry entry, int start, int len, boolean finished) throws Exception {
                    BinaryFileHandler.write(os, content, start, len, finished);
                }

                @Override
                public byte[] getData() {
                    // TODO Auto-generated method stub
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] exportEntry(MediaEntry entry) throws Exception {
        byte[] data = null;

        try {

            data = readContent(entry, new IMediaEntryWriter() {
                byte[] result = null;

                @Override
                public void write(MediaEntry entry, int start, int len, boolean finished) throws Exception {
                    if (result == null) {
                        result = new byte[len];
                        System.arraycopy(content, start, result, 0, len);
                    } else {
                        byte[] temp = new byte[result.length + len];
                        int tempLength = result.length;
                        System.arraycopy(result, 0, temp, 0, result.length);
                        result = temp;
                        System.arraycopy(content, start, result, tempLength, len);
                    }
                }

                @Override
                public byte[] getData() {
                    return result;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    protected abstract void readHeader();

    @Override
    public abstract byte[] readContent(MediaEntry entry, IMediaEntryWriter writer) throws Exception;

    @Override
    public abstract void readEntries(MediaEntry parent);

    @Override
    public void getAvailabilityMap() {
        // TODO Auto-generated method stub

    }

}
