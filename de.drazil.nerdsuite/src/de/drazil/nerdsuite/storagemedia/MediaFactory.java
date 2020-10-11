package de.drazil.nerdsuite.storagemedia;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaFactory {

	private static Map<String, IMediaContainer> mediaStore = new HashMap<>();
	public static String SOURCE_PATTERN = "(file|ftp)@(.*)";
	public static String FILE_PATTERN = "(.*\\.(([dD]64|71|81)|[dD][sS][kK]|[aA][tT][rR]))";
	public static Pattern sourcePattern = Pattern.compile(SOURCE_PATTERN);
	public static Pattern filePattern = Pattern.compile(FILE_PATTERN);

	public static boolean isMountable(File file) {
		return file.toString().matches(SOURCE_PATTERN);
	}

	public static IMediaContainer mount(File sourceFile) {
		IMediaContainer mediaProvider = null;
		String fileName = sourceFile.toString();

		Matcher sourceMatcher = sourcePattern.matcher(fileName);
		if (sourceMatcher.find()) {
			String type = sourceMatcher.group(1);
			String source = sourceMatcher.group(2);
			Matcher fileMatcher = filePattern.matcher(source);
			String suffix = "";
			if (fileMatcher.find()) {
				suffix = fileMatcher.group(2);
			}
			File file = new File(source);
			mediaProvider = mediaStore.get(fileName);
			if (mediaProvider == null) {
				if (suffix.equalsIgnoreCase("d64")) {
					mediaProvider = new D64_MediaContainer(sourceFile);
				} else if (suffix.equalsIgnoreCase("d71")) {
					mediaProvider = new D71_MediaContainer(sourceFile);
				} else if (suffix.equalsIgnoreCase("d81")) {
					mediaProvider = new D81_MediaContainer(sourceFile);
				} else if (suffix.equalsIgnoreCase("dsk")) {
					mediaProvider = new DSK_MediaContainer(sourceFile);
				} else if (suffix.equalsIgnoreCase("atr")) {
					mediaProvider = new ATR_MediaContainer(sourceFile);
				} else if (type.equalsIgnoreCase("ftp")) {
					mediaProvider = new FtpMediaContainer(sourceFile);
				} else {

				}
				mediaStore.put(fileName, mediaProvider);
				try {
					mediaProvider.read(file);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		return mediaProvider;
	}

	public static void unmount(File file) {
	}

	public static void addChildEntry(MediaEntry parent, MediaEntry child) {
		child.setParent(parent);
		parent.addChildrenEntry(child);
	}

	public static MediaEntry[] getChildren(MediaEntry entry) {
		IMediaContainer mediaManager = MediaFactory.mount((File) entry.getUserObject());
		return mediaManager.getEntries(entry);
	}
}
