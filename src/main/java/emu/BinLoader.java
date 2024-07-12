package emu;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BinLoader {
	public static void loadBin(String filename, Bus bus) throws IOException {
		int address = 0xc0_0000;
		byte[] content = Files.readAllBytes(Paths.get(filename));
		System.out.println("content size is: " + content.length);
		for (int i = 0; i < content.length; i++) {
			bus.writeMemory(address, content[i]);
			address++;
		}
	}
}
