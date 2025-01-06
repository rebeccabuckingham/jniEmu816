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

public class Loader {

	public static String hexDump(Bus b, int address, int count) {
		String line = String.format("%06x:", address);
		for (int i = 0; i < count; i++) {
			line += String.format(" %02x", b.readMemory(address + i));
		}
		return line;
	}

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

	private static int getAddr16(byte[] content, int pointer) {
		int a1 = (int) (content[pointer] & 0xFF);
		int a2 = (int) (content[pointer + 1] & 0xFF);

		return a1 + (a2 * 256);
	}

	public static int loadSegment(byte[] content, int pointer, Bus bus) {
		int address = getAddr24(content, pointer);  pointer += 3;
		int length = getAddr24(content, pointer);  pointer += 3;

		//System.out.printf("loadSegment address: %06x, length: %d\n", address, length);

		for (int i = 0; i < length; i++) {
			bus.writeMemory(address + i,
				(short) (content[pointer + i] & 0xFF)
				);
		}

		pointer += length;

		return pointer;
	}


	public static int loadPgz(String filename, Bus bus) throws IOException {
		byte[] content = Files.readAllBytes(Paths.get(filename));
		System.out.println("content size is: " + content.length);
		hexDump(content, 0, 16);

		if (content[0] != 0x5a) {
			throw new IOException(filename + " file doesn't start with a 'Z'!");
		}

		int pointer = 1;
		int executionAddress = 0;

		int segment = 0;
		while (pointer < content.length) {
			int tmpPointer = pointer;
			int address = getAddr24(content, tmpPointer);  tmpPointer += 3;
			int length = getAddr24(content, tmpPointer);  tmpPointer += 3;

			System.out.printf("loadSegment address: %06x, length: %d\n", address, length);
			if (length == 0) {
				executionAddress = address;
			}

			pointer = loadSegment(content, pointer, bus);
			System.out.println("after loading segment " + segment + ", pointer is now: " + pointer);
			segment++;
		}

		System.out.println("reset vector: " + hexDump(bus, 0xfffc, 2));

		return executionAddress;
	}

	public static int loadPrg(String filename, Bus bus) throws IOException {
		byte[] content = Files.readAllBytes(Paths.get(filename));
		System.out.println("content size is: " + content.length);
		hexDump(content, 0, 16);

		int baseAddress = getAddr16(content, 0);
		int address = baseAddress;
		for (int i = 2; i < content.length; i++) {
			bus.writeMemory(address, content[i]);
			address++;
		}

		return address;
	}

}
