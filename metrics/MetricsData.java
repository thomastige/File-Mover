package metrics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MetricsData {

	String dataName;
	String folderPath;
	private Map<String, Integer> mappings;

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public String getDataName() {
		return dataName;
	}

	public void setDataName(String dataName) {
		this.dataName = dataName;
	}

	public MetricsData(String name) {
		mappings = new HashMap<String, Integer>();
		dataName = name;
	}

	public MetricsData() {
		this(null);
	}

	public void add(String string) {
		Integer value = mappings.get(string);
		if (value == null) {
			mappings.put(string, 1);
		} else {
			mappings.put(string, value + 1);
		}
	}

	public String getStringValue(boolean includeKey) {
		StringBuilder result = new StringBuilder();
		Iterator<String> it = mappings.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (!"".equals(key.trim())) {
				if (includeKey){
					result.append(key + ":");
				}
				result.append(mappings.get(key) + "\n");
			}
		}
		return result.toString();
	}
	
}
