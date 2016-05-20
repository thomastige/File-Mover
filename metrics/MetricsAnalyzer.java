package metrics;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricsAnalyzer {

	File dir;

	public MetricsAnalyzer(String dir) {
		this.dir = new File(dir);
	}

	public Map<String, MetricsData> parse() throws IOException {
		Map<String, MetricsData> result = new HashMap<String, MetricsData>();
		File[] dirs = dir.listFiles();
		for (int i = 0; i < dirs.length; ++i) {
			if (dirs[i].isDirectory()) {
				MetricsData metricsData = new MetricsData();
				File[] files = dirs[i].listFiles();
				for (int j = 0; j < files.length; ++j) {
					if (files[j].isFile()) {
						List<String> lines = Files.readAllLines(files[j].toPath());
						String firstLine = lines.get(0);
						if (firstLine.startsWith("[") && firstLine.endsWith("]")) {
							String[] splitLine = firstLine.split("__");
							for (int k = 1; k < splitLine.length - 1; ++k) {
								String datum = splitLine[k];
								metricsData.add(datum);
							}
						}
					}
				}
				result.put(dirs[i].getName(), metricsData);
			}
		}
		return result;
	}
}
