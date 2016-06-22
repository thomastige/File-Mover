package parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import adt.BugEntry;

public class HourCalculator {

	public static final float hoursPerDay = (float) 7.5;
	public static final float hoursIncrement = (float) 0.25;
	
	List<BugEntry> bugs;

	public HourCalculator(List<BugEntry> bugs) {
		this.bugs = bugs;
	}

	public String getCalculationAsString() {
		StringBuilder result = new StringBuilder();
		calculateHours();
		Iterator<BugEntry> it = bugs.iterator();
		while (it.hasNext()) {
			BugEntry entry = it.next();
			result.append(entry.toString());
			if (it.hasNext()){
				result.append(",\n");
			}
		}
		return result.toString();
	}

	public List<BugEntry> calculateHours() {
		List<BugEntry> result = new ArrayList<BugEntry>();

		Map<Date, Float> overridesForDate = new HashMap<Date, Float>();
		Iterator<BugEntry> bugIterator = bugs.iterator();
		while (bugIterator.hasNext()) {
			BugEntry bug = bugIterator.next();
			if (bug.getOverride()) {
				if (overridesForDate.get(bug.getDate()) == null) {
					overridesForDate.put(bug.getDate(), (float) 0.0);
				}
				float currentValue = overridesForDate.get(bug.getDate());
				overridesForDate.put(bug.getDate(), currentValue + Float.valueOf(bug.getWorked()));
				result.add(bug);
				bugIterator.remove();
			}
		}
		Map<Date, List<BugEntry>> mappedPerDate = splitBugsIntoList();
		Iterator<Date> it = mappedPerDate.keySet().iterator();
		while (it.hasNext()) {
			Date date = it.next();
			List<BugEntry> bugsPerDate = mappedPerDate.get(date);
			Float[] values = new Float[bugsPerDate.size()];
			for (int i = 0; i < values.length; ++i) {
				values[i] = (float) 0.0;
			}
			float overriddenTotal = 0;
			if (overridesForDate.get(date) != null) {
				overriddenTotal = overridesForDate.get(date);
			}
			float max = (hoursPerDay - overriddenTotal) / hoursIncrement;
			for (int i = 0; i < max; ++i) {
				if (values[i % values.length] == null) {
					values[i % values.length] = (float) 0;
				}
				values[i % values.length] += hoursIncrement;
			}
			int counter = 0;
			Iterator<BugEntry> it2 = bugsPerDate.iterator();
			while (it2.hasNext()) {
				BugEntry bug = it2.next();
				bug.setWorked(values[counter++] + "");
			}
			result.addAll(bugsPerDate);
		}
		this.bugs = result;
		return result;
	}

	private Map<Date, List<BugEntry>> splitBugsIntoList() {
		Map<Date, List<BugEntry>> result = new TreeMap<Date, List<BugEntry>>();
		Iterator<BugEntry> it = bugs.iterator();
		while (it.hasNext()) {
			BugEntry key = it.next();

			if (result.get(key.getDate()) == null) {
				result.put(key.getDate(), new ArrayList<BugEntry>());
			}

			result.get(key.getDate()).add(key);
		}
		return result;
	}

	

}