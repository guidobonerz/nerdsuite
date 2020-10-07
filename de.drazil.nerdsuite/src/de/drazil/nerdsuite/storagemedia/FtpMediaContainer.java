package de.drazil.nerdsuite.storagemedia;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.Collections;
import java.util.Comparator;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.google.common.collect.ComparisonChain;

public class FtpMediaContainer implements IMediaContainer {

	private FTPClient client;
	private MediaEntry root;
	private File file;

	public FtpMediaContainer(File file) {
		this.file = file;
		root = new MediaEntry();
		root.setName("");
		root.setFullName("");
		root.setRoot(true);
		root.setUserObject(file);
		root.setDirectory(true);
	}

	@Override
	public MediaEntry getRoot() {
		return root;
	}

	@Override
	public byte[] read(File file) throws Exception {
		try {
			client = new FTPClient();
			client.connect(file.getName().split("@")[1]);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean hasEntries(Object entry) {
		return true;
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
	public void readEntries(MediaEntry parent) {

		try {
			System.out.println(parent.getName());
			FTPFile files[] = client.listFiles(parent.getName());

			for (FTPFile file : files) {

				MediaEntry entry = new MediaEntry(1, file.getName(), parent.getName() + "/" + file.getName(), "",
						(int) file.getSize(), 0, 0, null, null);
				entry.setDirectory(file.getType() == FTPFile.DIRECTORY_TYPE);
				entry.setUserObject(this.file);
				MediaFactory.addChildEntry(parent, entry);
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	@Override
	public byte[] readContent(MediaEntry entry, IMediaEntryWriter writer) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void exportEntry(MediaEntry entry, File file) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public byte[] exportEntry(MediaEntry entry) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getAvailabilityMap() {
		// TODO Auto-generated method stub

	}

}
