package metrics;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MetricsAnalyzer {

	File dir;

	public MetricsAnalyzer(String dir) {
		this.dir = new File(dir);
	}

	public Map<String, List<MetricsData>> parse() throws IOException {
		Map<String, List<MetricsData>> result = new TreeMap<String, List<MetricsData>>();
		File[] dirs = dir.listFiles();
		for (int i = 0; i < dirs.length; ++i) {
			if (dirs[i].isDirectory()) {
				List<MetricsData> metricsData = new ArrayList<MetricsData>();
				File[] files = dirs[i].listFiles();
				for (int j = 0; j < files.length; ++j) {
					if (files[j].isFile()) {
						List<String> lines = Files.readAllLines(files[j].toPath());
						String firstLine = lines.get(0);
						if (firstLine.startsWith("[") && firstLine.endsWith("]")) {
							String[] splitLine = firstLine.split("__");
							for (int k = 1; k < splitLine.length - 1; ++k) {
								if (metricsData.size() < (k)){
									metricsData.add(new MetricsData());
								}
								String datum = splitLine[k];
								metricsData.get(k-1).add(datum);
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
