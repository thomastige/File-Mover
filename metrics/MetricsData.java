package metrics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MetricsData {

	private Map<String, Integer> mappings;

	public MetricsData() {
		mappings = new HashMap<String, Integer>();
	}

	public void add(String string) {
		Integer value = mappings.get(string);
		if (value == null) {
			mappings.put(string, 1);
		} else {
			mappings.put(string, value + 1);
		}
	}

	public String getStringValue() {
		StringBuilder result = new StringBuilder();
		Iterator<String> it = mappings.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (!"".equals(key.trim())) {
				result.append(key + ":" + mappings.get(key) + "\n");
			}
		}
		return result.toString();

	}

}
