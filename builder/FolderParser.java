package builder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FolderParser {

	private File fileToParse;

	public FolderParser() {
		this(System.getProperty("system.dir"));
	}

	public FolderParser(String dir) {
		fileToParse = new File(dir);
	}

	public Map<String, List<String>> parseFolder() throws IOException {
		return parseFolder(fileToParse);
	}

	private Map<String, List<String>> parseFolder(File dir) throws IOException {
		Map<String, List<String>> result = new TreeMap<String, List<String>>();

		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; ++i) {
			if (files[i].isDirectory()) {
				result = joinMaps(result, parseFolder(files[i]));
			} else {
				result = joinMaps(result, mapDates(files[i]));
			}
		}
		return result;
	}

	private Map<String, List<String>> mapDates(File file) throws IOException {
		Map<String, List<String>> result = new TreeMap<String, List<String>>();

		List<String> lines = Files.readAllLines(file.toPath());
		Iterator<String> it = lines.iterator();
		while (it.hasNext()) {
			String line = it.next();
			String date = extractDate(line);
			if (date != null) {
				Map<String, List<String>> currDateMap = new TreeMap<String, List<String>>();
				List<String> issues = new ArrayList<String>();
				issues.add(file.getName());
				currDateMap.put(date + " | " + file.getParentFile().getName(), issues);
				result = joinMaps(result, currDateMap);
			}
		}
		return result;
	}

	// TODO: write parsing algorithm?
	private String extractDate(String line) {
		String result = null;
		String[] split = line.split(" ");
		if (split.length == 3) {
			if (isInteger(split[0]) && isInteger(split[2])) {
				result = split[0] + "/" + split[1] + "/" + split[2];
			}
		}
		if (split.length == 5) {
			if (isInteger(split[1]) && isInteger(split[3])) {
				result = split[1] + "/" + split[2] + "/" + split[3];
			}
		}
		return result;
	}

	private Map<String, List<String>> joinMaps(Map<String, List<String>> map1, Map<String, List<String>> map2) {
		if (map1 == null || map1.isEmpty()) {
			return map2;
		} else if (map2 == null || map2.isEmpty()) {
			return map1;
		}
		Map<String, List<String>> result = new TreeMap<String, List<String>>(new MonthComparator());
		result.putAll(map1);
		Iterator<String> it = map2.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			List<String> value = result.get(key);
			if (value == null) {
				result.put(key, map2.get(key));
			} else {
				List<String> resultList = new ArrayList<String>(map2.get(key));
				for (String str : result.get(key)) {
					if (!resultList.contains(str))
						resultList.add(str);
				}
				result.put(key, resultList);
			}
		}

		return result;
	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		return true;
	}

	private class MonthComparator implements Comparator<String> {

		@Override
		public int compare(String arg0, String arg1) {
			String[] left = arg0.split("/");
			left[2] = left[2].substring(0,3);
			String[] right = arg1.split("/");
			right[2] = right[2].substring(0,3);
			if (!left[0].equals(right[0])) {
				return (Integer.parseInt(left[0]) - Integer.parseInt(right[0]));
			}

			if (!left[1].equals(right[1])) {
				Map<String, Integer> monthMappings = getMonthMap();
				return monthMappings.get(left[1].toUpperCase()) - monthMappings.get(right[1].toUpperCase());
			}

			if (!left[2].equals(right[2])) {
				return (Integer.parseInt(left[0]) - Integer.parseInt(right[0]));
			}

			return 0;
		}

		private Map<String, Integer> getMonthMap() {
			Map<String, Integer> result = new HashMap<String, Integer>();
			result.put("JANUARY", 0);
			result.put("FEBRUARY", 1);
			result.put("MARCH", 2);
			result.put("APRIL", 3);
			result.put("MAY", 4);
			result.put("JUNE", 5);
			result.put("JULY", 6);
			result.put("AUGUST", 7);
			result.put("SEPTEMBER", 8);
			result.put("OCTOBER", 9);
			result.put("NOVEMBER", 10);
			result.put("DECEMBER", 11);
			return result;
		}

	}
}
