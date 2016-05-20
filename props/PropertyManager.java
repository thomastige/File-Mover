package props;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PropertyManager {

	private static String propLocation = getPropLocation();
	private static Map<String, String> props = new HashMap<String, String>();

	public static void initialize() throws IOException {
		File file = new File(propLocation);
		if (file.exists()) {
			List<String> lines = Files.readAllLines(Paths.get(propLocation));
			for (String line : lines) {
				String[] brokenString = line.split("=");
				if (brokenString.length == 2) {
					props.put(brokenString[0], brokenString[1]);
				}
			}
		}

	}

	public static String readProperty(String key) {
		return props.get(key);
	}

	public static void set(String key, String value) {
		props.put(key, value);
	}

	public static void dump() throws IOException {
		File file = new File(getPropLocation());

		if (file.exists()) {
			file.delete();
		}
		file.getParentFile().mkdirs();
		file.createNewFile();
		Iterator<String> it = props.keySet().iterator();
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		while (it.hasNext()) {
			String key = it.next();
			writer.write(key + "=" + props.get(key) + "\n");
		}
		writer.close();

	}

	private static String getPropLocation() {
		String loc = System.getProperty("user.dir");
		File folder = new File(loc);
		return folder.toString() + File.separator + "props" + "";

	}

}
