package emu;

public class Ram extends BusDeviceBase {
	public short[] memory;

	@Override
	public short readMemory(int address) {
		return memory[effectiveAddress(address)];
	}

	@Override
	public void writeMemory(int address, short value)
	{
		int ea = effectiveAddress(address);
		memory[ea] = value;
	}

	public Ram(String name, int startAddress, int size) {
		super("RAM " + name + " (" + BusDeviceBase.addr24(startAddress) + "," + BusDeviceBase.addr16(size) + ")", startAddress, size);
		memory = new short[size];
	}

	public String toString() {
		return String.format(getName() + " [%06x:%d]", startAddress, size);
	}
}
