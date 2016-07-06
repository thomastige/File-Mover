package metrics;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import constants.Constants;

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
						if (firstLine.startsWith(Constants.METADATA_START)
								&& firstLine.endsWith(Constants.METADATA_END)) {
							String[] splitLine = firstLine.replace(Constants.METADATA_START, "")
									.replace(Constants.METADATA_END, "").split(Constants.METADATA_SEPARATOR);
							
							for (int k = 0; k < splitLine.length; ++k) {
								String datum = splitLine[k];
								String[] keyValuePair = datum.split(Constants.METADATA_NAME_SEPARATOR);
								if (metricsData.size() < (k + 1)) {
									metricsData.add(new MetricsData(keyValuePair.length == 2 ? keyValuePair[0] : null));
								}
								metricsData.get(k).setFolderPath(dirs[i].getPath());
								metricsData.get(k).add(keyValuePair.length == 2 ? keyValuePair[1] : datum);
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
