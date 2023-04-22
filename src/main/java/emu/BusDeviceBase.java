package emu;

public abstract class BusDeviceBase implements BusDevice {
	protected int size;
	protected int startAddress;
	protected String name;

	public String getName() { return name; }

	public static String addr24(int address) {
		String tmp = String.format("%06x", address);
		return tmp.substring(0, 2) + ":" + tmp.substring(2);
	}

	public static String addr16(int address) {
		return String.format("%04x", address);
	}

	public int getStartAddress() {
		return startAddress;
	}

	public int getSize() {
		return size;
	}

	public int effectiveAddress(int address) {
		return address - getStartAddress();
	}

	public boolean contains(int address) {
		int ea = effectiveAddress(address);
		return ea >= 0 && ea < size;
	}

	public BusDeviceBase(String name, int startAddress, int size) {
		this.name = name;
		this.startAddress = startAddress;
		this.size = size;
	}
}
