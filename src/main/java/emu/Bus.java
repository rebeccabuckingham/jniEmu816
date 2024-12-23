package emu;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Bus {
	private static final boolean TRACE_MEMORY_READ = false;
	private static final boolean TRACE_MEMORY_WRITE = false;

	@Expose
	public ArrayList<BusDevice> deviceList;

	public short readMemory(int address) {
		short value = 0xFF;
		boolean found = false;
		String deviceName = null;

		for (BusDevice device : deviceList) {
			if (device.contains(address)) {
				value = device.readMemory(address);
				deviceName = device.getName();
				found = true;
				break;
			}
		}

		if (TRACE_MEMORY_READ && (address >= 0xD000 && address < 0xD100)) {
			if (found)
				System.out.printf("readMemory: [%s] %04x = %02x\n", deviceName, address, value);
			else
				System.out.printf("readMemory: [no name] %04x = bus error!\n", address);
		}

		return value;
	}

	public void writeMemory(int address, short value) {
		boolean found = false;
		String deviceName = null;

		for (BusDevice device : deviceList) {
			if (device.contains(address)) {
				deviceName = device.getName();
				device.writeMemory(address, value);
				found = true;
				break;
			}
		}

		if (TRACE_MEMORY_WRITE && (address >= 0xD000 && address < 0xD100)) {
			if (found)
				System.out.printf("writeMemory: [%s] %04x = %02x\n", deviceName, address, value);
			else
				System.out.printf("writeMemory: [no name]  %04x = bus error!\n", address);
		}
	}

	public void add(BusDevice d) {
		deviceList.add(d);
	}

	public Bus() {
		deviceList = new ArrayList<>();
	}
}
