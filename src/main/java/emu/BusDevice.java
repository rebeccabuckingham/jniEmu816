package emu;

public interface BusDevice {
	public int getStartAddress();
	public int getSize();
	public short readMemory(int address);
	public void writeMemory(int address, short value);
	public boolean contains(int address);
	public int effectiveAddress(int address);
	public String getName();
}
