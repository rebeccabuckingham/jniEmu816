package emu;

import java.util.Arrays;

public class Main {
	public native void print();
	public native void initCpu();
	public native void runCpu();

	static {
		System.loadLibrary("lib65816");
	}

	//public MainScreen mainScreen;
	public Bus bus;
	public ConsoleDevice consoleDevice;
	TerminalDevice terminalDevice;

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
//		javax.swing.SwingUtilities.invokeLater(new Runnable() {
//				public void run() {
//					MainScreen.getConsole().println("Hello from sayHelloJava()!");
//				}
//		});
	}

	public Main() throws Exception {
		bus = new Bus();

		Ram highCode = new Ram("highCode", 0x010000, 0x8000);
		Arrays.fill(highCode.memory, (short) 0x42);
		bus.add(highCode);

		Ram highData = new Ram("highData", 0x018000, 0x8000);
		Arrays.fill(highData.memory, (short) 0x42);
		bus.add(highData);

		Ram lowCode =  new Ram("lowCode", 0x00E000, 0x2000);
		Arrays.fill(lowCode.memory, (short) 0x42);
		bus.add(lowCode);

		Ram lowData =  new Ram("lowData", 0x00D100, 0x0F00);
		Arrays.fill(lowData.memory, (short) 0x42);
		bus.add(lowData);

		//consoleDevice = new ConsoleDevice((int) 0xD000, 4);
		//bus.add(consoleDevice);
		terminalDevice = new TerminalDevice((int) 0xD000, 4);
		bus.add(terminalDevice);

		Ram stack = new Ram("stack", 0x00C800, 0x0800);
		Arrays.fill(stack.memory, (short) 0x00);
		bus.add(stack);

		Ram base = new Ram("base", 0x000000, 0xC800);
		Arrays.fill(base.memory, (short) 0x42);
		bus.add(base);

		//consoleDevice.showGUI(consoleDevice);

	}

	public static void main(String[] args) throws Exception {
		Main m = new Main();

		if (args.length == 1 && args[0].equalsIgnoreCase("--test-terminal")) {
			// turn console echo and noCpu modes on so we can just test the terminal
			m.consoleDevice.echoMode = true;
			m.consoleDevice.noCpuMode = true;
		} else {
			PgzLoader.loadPgz("/Users/rebecca/Developer/calypsi-minimal-example/test.pgz", m.bus);
			m.initCpu();

//			System.out.println("waiting 3 seconds for gui to be ready...");
//			try {
//				Thread.sleep(3000);
//			} catch (Exception e) {
//
//			}
			m.terminalDevice.runServer();
			while (! m.terminalDevice.connected()) {
				System.out.println("waiting for terminal device to connect over telnet...");
				try {
					Thread.sleep(10000);
				} catch (Exception e) {

				}
			}

			System.out.println("starting cpu...");
			m.runCpu();
		}


	}


}
