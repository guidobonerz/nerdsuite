package de.drazil.nerdsuite.storagemedia;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaFactory {

	private static Map<String, IMediaContainer> mediaStore = new HashMap<>();
	public static String FILE_PATTERN = ".*\\.(([dD]64|71|81)|[dD][sS][kK]|[aA][tT][rR])";
	public static Pattern pattern = Pattern.compile(FILE_PATTERN);

	public static boolean isMountable(File file) {
		return file.getName().matches(FILE_PATTERN);
	}

	public static IMediaContainer mount(File file) {
		IMediaContainer mediaProvider = null;
		String fileName = file.getName();

		Matcher matcher = pattern.matcher(fileName);
		if (matcher.find()) {
			String suffix = matcher.group(1);
			mediaProvider = mediaStore.get(suffix);
			if (mediaProvider == null) {
				if (suffix.equalsIgnoreCase("d64")) {
					mediaProvider = new D64_MediaContainer(file);
				} else if (suffix.equalsIgnoreCase("d71")) {
					mediaProvider = new D71_MediaContainer(file);
				} else if (suffix.equalsIgnoreCase("d81")) {
					mediaProvider = new D81_MediaContainer(file);
				} else if (suffix.equalsIgnoreCase("dsk")) {
					mediaProvider = new DSK_MediaContainer(file);
				} else if (suffix.equalsIgnoreCase("atr")) {
					mediaProvider = new ATR_MediaContainer(file);
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
