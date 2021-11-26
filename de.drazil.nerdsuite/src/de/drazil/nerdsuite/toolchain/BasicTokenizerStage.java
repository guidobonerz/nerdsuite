package de.drazil.nerdsuite.toolchain;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import de.drazil.nerdsuite.basic.BasicTokenizer;
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

	public BasicTokenizerStage(String name, String platform, String content, BasicInstructions basicInstructions,
			String fileName) {
		this.platform = platform;
		this.content = content;
		this.fileName = fileName;
		this.name = name;
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
		Console.println(name);
		CharMap charMap = PlatformFactory.getCharMap(platform);

		List<CharObject> charMapList = charMap.getCharMap().stream().filter(e -> e.isUpper() == true)
				.collect(Collectors.toList());
		byte[] bytecode = BasicTokenizer.tokenize(content.toUpperCase(), basicInstructions, charMapList);
		byte[] payload = new byte[] {};
		payload = ArrayUtil.grow(payload, NumericConverter.getWord(2049));
		payload = ArrayUtil.grow(payload, bytecode);

		try {
			Files.write(new File(fileName).toPath(), payload);
			Console.printf("%s (%d bytes) written\n", fileName, payload.length);
		} catch (IOException e1) {
			Console.printf("write file %s failed", fileName);
			e1.printStackTrace();
		}

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}
}
