package emu;

/*
 load a .PGZ format file
 format:

 Header = "Z" character.
 Rest of file = Segments, one after another, until we hit EOF:
	address: 3 bytes, little endian.
	length: 3 bytes, little endian.
	data: [length] bytes.
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PgzLoader {

	private static void hexDump(byte[] array, int startIndex, int count) {
		String line = String.format("%06x:", startIndex);
		for (int i = 0; i < count; i++) {
			if (startIndex + i < array.length)
				line += String.format(" %02x", array[startIndex + i]);
		}
		System.out.println(line);
	}

	private static int getAddr24(byte[] content, int pointer) {
		//hexDump(content, pointer, 8);
		int a1 = (int) (content[pointer] & 0xFF);
		int a2 = (int) (content[pointer + 1] & 0xFF);
		int a3 = (int) (content[pointer + 2] & 0xFF);

		//System.out.printf("a1: %02x, a2: %02x, a3: %02x --> %06x\n", a1, a2, a3, value);
		return a1 + (a2 * 256) + (a3 * 65536);
	}


	public static int loadSegment(byte[] content, int pointer, Bus bus) {
		int address = getAddr24(content, pointer);  pointer += 3;
		int length = getAddr24(content, pointer);  pointer += 3;

		System.out.printf("loadSegment address: %06x, length: %d\n", address, length);

		for (int i = 0; i < length; i++) {
			bus.writeMemory(address + i,
				(short) (content[pointer + i] & 0xFF)
				);
		}

		pointer += length;

		return pointer;
	}


	public static void loadPgz(String filename, Bus bus) throws IOException {
		byte[] content = Files.readAllBytes(Paths.get(filename));
		System.out.println("content size is: " + content.length);
		hexDump(content, 0, 16);

		if (content[0] != 0x5a) {
			throw new IOException(filename + " file doesn't start with a 'Z'!");
		}

		int pointer = 1;

		int segment = 0;
		while (pointer < content.length) {
			pointer = loadSegment(content, pointer, bus);
			System.out.println("after loading segment " + segment + ", pointer is now: " + pointer);
			segment++;
		}
	}
}
