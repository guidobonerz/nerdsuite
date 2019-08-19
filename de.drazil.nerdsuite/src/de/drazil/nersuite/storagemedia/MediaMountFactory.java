package de.drazil.nersuite.storagemedia;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MediaMountFactory {

	private static Map<String, IMediaProvider> mediaStore = new HashMap<>();

	public static boolean isMountable(File file) {
		return file != null && file.isFile() && file.getName().matches(".*\\.[dD]64");
	}

	public static IMediaProvider mount(File file, File parent) throws Exception {
		IMediaProvider mediaProvider = mediaStore.get(file.getName());
		if (mediaProvider == null && isMountable(file)) {
			mediaProvider = new D64MediaProvider();
			mediaProvider.read(file);
			mediaStore.put(file.getName(), mediaProvider);
		}
		return mediaProvider;
	}

	public static void unmount(File file) {

	}
}
