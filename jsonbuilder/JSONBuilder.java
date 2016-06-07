package jsonbuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class JSONBuilder {

	private Map<String, Map<String, List<String>>> map;
	private static final float hoursPerDay = (float) 7.5;
	private static final float hoursIncrement = (float) 0.25;
	private static final String DEFAULT_ROLE = "Developer";

	public JSONBuilder(Map<String, Map<String, List<String>>> map) {
		this.map = map;
	}

	public String buildJSON() {
		StringBuilder result = new StringBuilder();

		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String unformattedDate = it.next();
			DateFormat df = new SimpleDateFormat("d/mmm/yyyy");
			String formattedDate = df.format(new Date(Long.valueOf(unformattedDate)));
			// sprintmap is pretty useless in this case
			Map<String, List<String>> sprintMap = map.get(unformattedDate);
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
				jsonBug.setBilled("");
				jsonBug.setBugNumber(bug);
				jsonBug.setDate(formattedDate);
				jsonBug.setDescription("");
				jsonBug.setRole(DEFAULT_ROLE);
				result.append(jsonBug.toString());
				result.append(",");
			}

		}
		result.setLength(result.length()-1);
		return "\""+result.toString()+"\"";
	}

	private Queue<Float> getworkedQueue(List<String> allBugs) {
		Queue<Float> queue = new LinkedList<Float>();
		Float[] values = new Float[allBugs.size()];
		Iterator it = allBugs.iterator();
		float max = hoursPerDay / hoursIncrement;
		for (int i = 0; i < max; ++i) {
			values[i%values.length] += hoursIncrement;
		}
		List<Float> list = Arrays.asList(values);
		return  new LinkedList<Float>(list);
	}

	private String getKey(String bug) {
		return bug.split(" - ")[0];

	}
}
