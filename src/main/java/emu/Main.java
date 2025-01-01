package emu;

public class Main {
	public native void print();
	public native void initCpu();
	public native void runCpu();
	public native void setRunAddress(long address);

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
			System.out.println("Usage: [config file] [program files]");
		} else {
			String configFile = args[0];
			System.out.println("Using config file: " + configFile);

			Main m = new Main();
			Configuration.configureFromFile(configFile, m.bus);

			// TODO console device will change to TCP/IP port.
			m.consoleDevice = findConsoleDevice(m.bus);
			if (m.consoleDevice == null) {
				System.out.println("no console device specified");
			} else {
				m.consoleDevice.showGUI(m.consoleDevice);
			}

			for (int i = 1; i < args.length; i++) {
				String filename = args[i];
				System.out.println("will load and run: " + filename);

				if (filename.toLowerCase().endsWith(".prg")) {
					Loader.loadPrg(filename, m.bus);
				} else {
					Loader.loadPgz(filename, m.bus);
				}
			}

			m.initCpu();

			// TODO: this would have to be an option specified before the filenames.
			// m.setRunAddress(0);

			// TODO: won't need this sleep once console device is TCP/IP.
			//       but! will need an optional wait for connection feature.
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
