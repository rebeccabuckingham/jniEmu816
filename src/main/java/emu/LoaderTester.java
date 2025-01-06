package emu;

public class LoaderTester {
	public Bus bus;

	public LoaderTester() {
		bus = new Bus();
		bus.add(new Ram("ram", 0, 8 * 1024 * 1024));
	}

	public static void main(String[] args) throws Exception {
		LoaderTester tester = new LoaderTester();

		var filename = "/Users/rebecca/Developer/Archive/calypsi-minimal-example/test.pgz";
		int address = 0;
		if (filename.toLowerCase().endsWith(".prg")) {
			address = Loader.loadPrg(filename, tester.bus);
		} else if (filename.toLowerCase().endsWith(".pgz")) {
			address = Loader.loadPgz(filename, tester.bus);
		}

		System.out.printf("execution address: %06x\n", address);
	}
}
