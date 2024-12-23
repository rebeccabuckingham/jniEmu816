package emu;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@SuppressWarnings("ALL")
public class Configuration {

	public static void configureFromFile(String filename, Bus bus) throws IOException {
		Gson gson = new Gson();
		try (FileReader reader = new FileReader(filename)) {
			Map data = gson.fromJson(reader, Map.class);
			ArrayList list = (ArrayList) data.get("deviceList");

			list.forEach(o -> {
				Map<String, Object> map = (Map<String, Object>) o;
				int size = ((Double) map.get("size")).intValue();
				int startAddress = ((Double) map.get("startAddress")).intValue();
				String name = (String) map.get("name");
				String className = (String) map.get("className");

				//System.out.println("size: " + size + ", startAddress: " + startAddress + ", name: " + name + ", className: " + className);

				if (className.equals(emu.Ram.class.getName())) {
					bus.add(new Ram(name, startAddress, size));
				} else if (className.equals(emu.ConsoleDevice.class.getName())) {
					bus.add(new ConsoleDevice(startAddress, size));
				} else {
					throw new RuntimeException("Unknown class " + className);
				}

			});

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void writeConfigurationToFile(String filename, Bus bus) {

	}
}
