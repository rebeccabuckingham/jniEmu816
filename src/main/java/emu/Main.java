package emu;

import java.util.Arrays;

public class Main {
	public native void print();
	public native void initCpu(int address);
	public native void runCpu();

	static {
		System.loadLibrary("lib65816");
	}

	//public MainScreen mainScreen;
	public Bus bus;
	public ConsoleDevice consoleDevice;

	// methods required by lib65816
	public short readMemory(int address, long timestamp) {
		return bus.readMemory(address);
	}

	public void writeMemory(int address, short value, long timestamp) {
		bus.writeMemory(address, value);
	}

	public void hardwareUpdate(long timestamp) { }

	// called from bridge.c Java_emu_Main_initCpu()
	public void sayHelloJava() {
	}

	public Main() {
		bus = new Bus();

//		Ram highCode = new Ram("highCode", 0x010000, 0x8000);
//		Arrays.fill(highCode.memory, (short) 0x42);
//		bus.add(highCode);
//
//		Ram highData = new Ram("highData", 0x018000, 0x8000);
//		Arrays.fill(highData.memory, (short) 0x42);
//		bus.add(highData);
//
//		Ram lowCode =  new Ram("lowCode", 0x00E000, 0x2000);
//		Arrays.fill(lowCode.memory, (short) 0x42);
//		bus.add(lowCode);
//
//		Ram lowData =  new Ram("lowData", 0x00D100, 0x0F00);
//		Arrays.fill(lowData.memory, (short) 0x42);
//		bus.add(lowData);
//
//		consoleDevice = new ConsoleDevice((int) 0xD000, 4);
//		bus.add(consoleDevice);
//
//		Ram stack = new Ram("stack", 0x00C800, 0x0800);
//		Arrays.fill(stack.memory, (short) 0x00);
//		bus.add(stack);
//
//		Ram base = new Ram("base", 0x000000, 0xC800);
//		Arrays.fill(base.memory, (short) 0x42);
//		bus.add(base);

		Ram low_ram = new Ram("low_ram", 0, 0xdf00);
		bus.add(low_ram);

		Ram rom = new Ram("rom", 0xe000, 0x2000);
		bus.add(rom);

		Ram high_ram = new Ram("high_ram", 0x10000, 0x70000);
		bus.add(high_ram);

		Ram high_rom = new Ram("high_rom", 0xc00000, 0x80000);
		bus.add(high_rom);

//		consoleDevice.showGUI(consoleDevice);
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.out.println("need to specify the file to load and run.");
		} else {
			System.out.println("will load and run: " + args[0]);

			Main m = new Main();

			int runAddress = 0;

			if (args[0].toLowerCase().endsWith(".bin")) {
				BinLoader.loadBin(args[0], m.bus);
				runAddress = 0xc08004;
			} else {
				PgzLoader.loadPgz(args[0], m.bus);
			}

			m.initCpu(runAddress);

//			System.out.println("waiting 3 seconds for gui to be ready...");
//			try {
//				Thread.sleep(3000);
//			} catch (Exception e) {
//
//			}

			System.out.println("starting cpu...");
			m.runCpu();
		}
	}


}
