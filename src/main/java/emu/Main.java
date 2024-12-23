package emu;

public class Main {
	public native void print();
	public native void initCpu();
	public native void runCpu();

	static {
		System.loadLibrary("lib65816");
	}

	public Bus bus;
	public ConsoleDevice consoleDevice;

	// methods required by lib65816
	public short readMemory(int address, long timestamp) {
		return bus.readMemory(address);
	}

	public void writeMemory(int address, short value, long timestamp) {
		bus.writeMemory(address, value);
	}

	// note: timestamp is a long (64 bit) value in Java, but only a
	//       32 bit unsigned int in C.  That means it will wrap over to 0
	//    	 after 4,294,967,295 cycles, or a little over 7 minutes.
	public void hardwareUpdate(long timestamp) {

	}

	// called from bridge.c Java_emu_Main_initCpu()
	public void sayHelloJava() {
	}

	public Main() {
		bus = new Bus();
	}

	public static ConsoleDevice findConsoleDevice(Bus bus) {
		return bus.deviceList.stream()
				.filter(device -> device instanceof ConsoleDevice)
				.map(device -> (ConsoleDevice) device)
				.findFirst()
				.orElse(null);
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println("Usage: [config file] [program file]");
		} else {
			String configFile = args[0];
			System.out.println("Using config file: " + configFile);

			String filename = args[1];
			System.out.println("will load and run: " + filename);

			Main m = new Main();
			Configuration.configureFromFile(configFile, m.bus);

			m.consoleDevice = findConsoleDevice(m.bus);
			if (m.consoleDevice == null) {
				System.out.println("no console device specified");
			} else {
				m.consoleDevice.showGUI(m.consoleDevice);
			}

			if (filename.toLowerCase().endsWith(".prg")) {
				Loader.loadPrg(filename, m.bus);
			} else {
				Loader.loadPgz(filename, m.bus);
			}

			m.initCpu();

			if (m.consoleDevice != null) {
				System.out.println("waiting 3 seconds for gui to be ready...");
				try {
					Thread.sleep(3000);
				} catch (Exception e) {

				}
			}

			System.out.println("starting cpu...");
			m.runCpu();
		}
	}


}
