package de.drazil.nerdsuite.toolchain;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import de.drazil.nerdsuite.basic.CbmBasicTokenizer;
import de.drazil.nerdsuite.log.Console;
import de.drazil.nerdsuite.model.BasicInstructions;
import de.drazil.nerdsuite.model.CharMap;
import de.drazil.nerdsuite.model.CharObject;
import de.drazil.nerdsuite.util.ArrayUtil;
import de.drazil.nerdsuite.util.NumericConverter;
import de.drazil.nerdsuite.widget.PlatformFactory;

public class BasicTokenizerStage implements IToolchainStage<Object> {

	private String platform;
	private String content;
	private String fileName;
	private BasicInstructions basicInstructions;
	private List<CharObject> charMap;
	private String name;
	private boolean debug;

	public BasicTokenizerStage(String name, String platform, String content, BasicInstructions basicInstructions,
			String fileName, boolean debug) {
		this.platform = platform;
		this.content = content;
		this.fileName = fileName;
		this.name = name;
		this.debug = debug;
		this.basicInstructions = basicInstructions;
	}

	@Override
	public Object getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void start() {
		long startTime = System.currentTimeMillis();
		Console.println(name + (debug ? " in Debug Mode" : ""));

		CharMap charMap = PlatformFactory.getCharMap(platform);

		List<CharObject> charMapList = charMap.getCharMap().stream().filter(e -> e.isUpper() == true)
				.collect(Collectors.toList());
		byte[] bytecode = CbmBasicTokenizer.tokenize(content.toUpperCase(), basicInstructions, charMapList);
		byte[] payload = new byte[] {};
		payload = ArrayUtil.grow(payload, NumericConverter.getWord(2049));
		payload = ArrayUtil.grow(payload, bytecode);

		try {
			Files.write(new File(fileName).toPath(), payload);
			float diff = (System.currentTimeMillis() - startTime) / 1000f;
			Console.printf("%s (%d bytes) written in %f seconds\n", fileName, payload.length, diff);
		} catch (IOException e1) {
			Console.printf("write file %s failed", fileName);
			e1.printStackTrace();
		} finally {

		}
	}
}
