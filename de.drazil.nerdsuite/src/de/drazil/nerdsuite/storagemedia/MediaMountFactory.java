package de.drazil.nerdsuite.storagemedia;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaMountFactory {

	private static Map<String, IMediaManager> mediaStore = new HashMap<>();
	public static Pattern pattern = Pattern.compile(".*\\.(([dD]64|71|81)|[dD][sS][kK]|[aA][tT][rR])");

	public static IMediaManager mount(File file) {
		IMediaManager mediaProvider = null;
		String fileName = file.getName();

		Matcher matcher = pattern.matcher(fileName);
		if (matcher.find()) {
			String suffix = matcher.group(1);
			mediaProvider = mediaStore.get(suffix);
			if (mediaProvider == null) {
				if (suffix.equalsIgnoreCase("d64")) {
					mediaProvider = new D64_MediaManager();
				} else if (suffix.equalsIgnoreCase("d71")) {
					mediaProvider = new D71_MediaManager();
				} else if (suffix.equalsIgnoreCase("d81")) {
					mediaProvider = new D81_MediaManager();
				} else if (suffix.equalsIgnoreCase("dsk")) {
					mediaProvider = new DSK_MediaManager();
				} else if (suffix.equalsIgnoreCase("atr")) {
					mediaProvider = new ATR_MediaManager();
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
}
