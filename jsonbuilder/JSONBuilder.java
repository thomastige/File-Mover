package jsonbuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import props.PropertyManager;

public class JSONBuilder {

	private Map<String, Map<String, List<String>>> map;
	private static final float hoursPerDay = (float) 7.5;
	private static final float hoursIncrement = (float) 0.25;
	private static final String DEFAULT_ROLE = "DEV";
	private static final String DEFAULT_DESC = "Investigation";

	private String separator;
	private String destPath;

	public JSONBuilder(Map<String, Map<String, List<String>>> map, String separator, String destPath) {
		this.map = map;
		this.separator = separator;
		this.destPath = destPath;
	}

	public String buildJSON() {
		return buildJSON(new Date(0));
	}

	public String buildJSON(Date fromDate) {
		return buildJSON(fromDate, new Date());
	}

	public String buildJSON(Date fromDate, Date toDate) {
		StringBuilder result = new StringBuilder();
		Map<String, String> descriptionMap;
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String unformattedDate = it.next();
			DateFormat df = new SimpleDateFormat("dd/MMM/yy");
			Date entryDate = new Date(Long.valueOf(unformattedDate));
			if (!entryDate.before(fromDate) && !entryDate.after(toDate)) {
				String formattedDate = df.format(entryDate);
				// sprintmap is pretty useless in this case
				Map<String, List<String>> sprintMap = map.get(unformattedDate);
				descriptionMap = getDescriptionMap(sprintMap);
				List<String> allBugs = new ArrayList<String>();
				Iterator<String> it2 = sprintMap.keySet().iterator();
				while (it2.hasNext()) {
					List<String> bugs = sprintMap.get(it2.next());
					allBugs.addAll(bugs);
				}
				Iterator<String> bugIterator = allBugs.iterator();
				Queue<Float> workedQueue = getworkedQueue(allBugs);
				while (bugIterator.hasNext()) {
					String bug = getKey(bugIterator.next());
					JSONBug jsonBug = new JSONBug();
					jsonBug.setWorked(workedQueue.remove() + "");
					jsonBug.setBilled("0");
					jsonBug.setBugNumber(bug);
					jsonBug.setDate(formattedDate);
					jsonBug.setDescription(descriptionMap.get(bug));
					jsonBug.setRole(DEFAULT_ROLE);
					result.append(jsonBug.toString());
					result.append(",\n");
				}
			}
		}
		result.setLength(result.length() - 2);
		return "[" + result.toString() + "]";
	}

	private Queue<Float> getworkedQueue(List<String> allBugs) {
		Queue<Float> queue = new LinkedList<Float>();
		Float[] values = new Float[allBugs.size()];
		Iterator it = allBugs.iterator();
		float max = hoursPerDay / hoursIncrement;
		for (int i = 0; i < max; ++i) {
			if (values[i % values.length] == null) {
				values[i % values.length] = (float) 0;
			}
			values[i % values.length] += hoursIncrement;
		}
		List<Float> list = Arrays.asList(values);
		return new LinkedList<Float>(list);
	}

	private String getKey(String bug) {
		return bug.split(" - ")[0];
	}

	private Map<String, String> getDescriptionMap(Map<String, List<String>> map) {
		Map<String, String> descriptionMap = new HashMap<String, String>();
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String sprint = it.next();
			List<String> bugs = map.get(sprint);
			Iterator<String> bugIt = bugs.iterator();
			while (bugIt.hasNext()) {
				String desc = DEFAULT_DESC;
				String bug = bugIt.next();
				String path = destPath + (destPath.endsWith(File.separator) ? "" : File.separator) + sprint + File.separator + bug;
				try {
					List<String> contents = Files.readAllLines(Paths.get(path));
					String firstLine = contents.get(0);
					if (firstLine.startsWith("[__") && firstLine.endsWith("__]")) {
						firstLine = firstLine.replace("[__", "").replace("__]", "");
						String[] lines = firstLine.split(separator);
						int position = Integer.parseInt(PropertyManager.readProperty("commentPosition"));
						if (position <= lines.length) {
							if (!"".equals(lines[position - 1])) {
								desc = lines[position - 1];
							}
						}
					}
				} catch (IOException | NumberFormatException e) {
					e.printStackTrace();
				}
				descriptionMap.put(getKey(bug), desc);
			}
		}
		return descriptionMap;
	}
}
