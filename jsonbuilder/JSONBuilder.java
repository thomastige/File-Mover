package jsonbuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
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
import table.Table;

public class JSONBuilder {

	private Map<String, Map<String, List<String>>> map;
	public static final float hoursPerDay = (float) 7.5;
	public static final float hoursIncrement = (float) 0.25;
	private static final String DEFAULT_ROLE = "DEV";
	private static final String DEFAULT_DESC = "Investigation";
	private static final String DEFAULT_OVERRIDE = "false";

	private String separator;
	private String destPath;

	// bug id, then date gives the value
	private Map<String, Map<String, List<String>>> overrideMap;

	public JSONBuilder(Map<String, Map<String, List<String>>> map, String separator, String destPath) {
		this.map = map;
		this.separator = separator;
		this.destPath = destPath;
		this.overrideMap = new HashMap<String, Map<String, List<String>>>();
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
				List<JSONBug> bugList = new ArrayList<JSONBug>();
				while (bugIterator.hasNext()) {
					String bug = getKey(bugIterator.next());
					JSONBug jsonBug = new JSONBug();
					// jsonBug.setWorked(workedQueue.remove() + "");
					jsonBug.setBilled("0");
					jsonBug.setBugNumber(bug);
					jsonBug.setDate(formattedDate);
					jsonBug.setDescription(descriptionMap.get(bug));
					jsonBug.setRole(DEFAULT_ROLE);
					jsonBug.setOverride(DEFAULT_OVERRIDE);

					Map<String, List<String>> bugOverrides = overrideMap.get(bug);
					DateFormat dfoverride = new SimpleDateFormat("dd MMMM yyyy");
					String overrideDate = dfoverride.format(entryDate).toUpperCase();
					if (bugOverrides != null && bugOverrides.get(overrideDate) != null && !bugOverrides.get(overrideDate).isEmpty()) {
						List<String> overrides = bugOverrides.get(overrideDate);
						Iterator<String> overrideIterator = overrides.iterator();
						while (overrideIterator.hasNext()) {
							String or = overrideIterator.next();
							String[] split = or.split(":");
							if (split.length == 2 && !"".equals(split[1])) {
								if ("WORKED".equals(split[0].toUpperCase())) {
									jsonBug.setWorked(split[1]);
									jsonBug.setOverride("true");
								}
							}
						}
					}
					bugList.add(jsonBug);
				}
				bugList = calculateWork(bugList);
				Iterator bugIt = bugList.iterator();
				while (bugIt.hasNext()) {
					result.append(bugIt.next().toString());
					result.append(",\n");
				}
			}
		}
		result.setLength(result.length() - 2);
		return "[" + result.toString() + "]";
	}

	private List<JSONBug> calculateWork(List<JSONBug> bugList) {
		List<JSONBug> result = new ArrayList<JSONBug>();
		Map<String, List<JSONBug>> map = new HashMap<String, List<JSONBug>>();
		Iterator<JSONBug> it = bugList.iterator();
		while (it.hasNext()) {
			JSONBug bug = it.next();
			if (map.get(bug.getDate()) == null) {
				map.put(bug.getDate(), new ArrayList<JSONBug>());
			}
			map.get(bug.getDate()).add(bug);
		}
		Iterator<String> dateIt = map.keySet().iterator();
		while (dateIt.hasNext()) {
			List<JSONBug> overridden = new ArrayList<JSONBug>();
			String key = dateIt.next();
			List<JSONBug> listPerDate = map.get(key);
			float alreadyRegistered = 0;
			Iterator<JSONBug> bugsForDate = listPerDate.iterator();
			// set overridden time calculation
			while (bugsForDate.hasNext()) {
				JSONBug bug = bugsForDate.next();
				if ("true".equals(bug.getOverride())) {
					alreadyRegistered += Float.valueOf(bug.getWorked());
					overridden.add(bug);
					bugsForDate.remove();
				}
			}
			// calculate the rest
			bugsForDate = listPerDate.iterator();
			Float[] values = new Float[listPerDate.size()];
			float max = (hoursPerDay - alreadyRegistered) / hoursIncrement;
			for (int i = 0; i < max; ++i) {
				if (values[i % values.length] == null) {
					values[i % values.length] = (float) 0;
				}
				values[i % values.length] += hoursIncrement;
			}
			//add to list
			int counter = 0;
			while (bugsForDate.hasNext()) {
				JSONBug bug = bugsForDate.next();
				if ("false".equals(bug.getOverride())) {
					bug.setWorked(""+values[counter++]);
				}
				result.add(bug);
			}
			result.addAll(overridden);
			
		}
		return result;

	}

	// TODO: Extract in a new class, add a second parameter for a map<String,
	// Float> that will indicate whether an element was overridden. If so, use
	// that. Otherwise, dispatch the rest. To be used in the JSON Configurer as
	// well.
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
						String positionProp = PropertyManager.readProperty("commentPosition");
						if (positionProp != null) {
							int position = Integer.parseInt(positionProp);
							if (position != 0 && position <= lines.length) {
								if (!"".equals(lines[position - 1])) {
									desc = lines[position - 1];
								}
							}
						}
					}

					Iterator<String> lineIt = contents.iterator();
					while (lineIt.hasNext()) {
						Map<String, List<String>> overrideMap = getOverrides(lineIt);
						if (this.overrideMap.get(getKey(bug)) == null || this.overrideMap.get(getKey(bug)).isEmpty()) {
							this.overrideMap.put(getKey(bug), overrideMap);
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

	private Map<String, List<String>> getOverrides(Iterator<String> it) {
		String line = it.next();
		Map<String, List<String>> overrideMap = new HashMap<String, List<String>>();
		if (line.length() > 1 && line.startsWith(Table.CORNER) && line.endsWith(Table.CORNER)
				&& line.substring(1, line.length() - 1).matches("[" + Table.HORIZONTAL + "]*")) {
			StringBuilder potentialTimeBlock = new StringBuilder();
			potentialTimeBlock.append(line + "\n");
			if (it.hasNext()) {
				String secondLine = it.next();
				// check if timestamp date
				if (secondLine.length() > 1 && secondLine.startsWith(Table.VERTICAL) && secondLine.endsWith(Table.VERTICAL)) {
					String trimmedLine = secondLine.substring(1, secondLine.length() - 1).trim();
					DateFormat df = new SimpleDateFormat("dd MMM yyyy");
					Date date = null;
					try {
						date = df.parse(trimmedLine);
					} catch (ParseException e) {
						// TODO: fix this, it's just a check to see if the line
						// is a date or not
					}
					if (date != null) {
						String nextLine = it.next();
						// check if time override
						if (nextLine.length() > 1 && nextLine.startsWith(Table.VERTICAL) && secondLine.endsWith(Table.VERTICAL)) {
							potentialTimeBlock.append(nextLine + "\n");
							while (!nextLine.startsWith(Table.CORNER)) {
								String override = nextLine.substring(1, nextLine.length() - 2).trim();
								if (!overrideMap.containsKey(trimmedLine)) {
									overrideMap.put(trimmedLine, new ArrayList<String>());
								}
								overrideMap.get(trimmedLine).add(override);
								nextLine = it.next();
							}
							nextLine = it.next();
						}
					}
				}
			}
		}
		return overrideMap;
	}
}
