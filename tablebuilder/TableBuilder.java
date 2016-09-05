package tablebuilder;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import adt.BugEntry;
import metrics.MetricsAnalyzer;
import metrics.MetricsData;
import parser.HourCalculator;
import table.Alignment;
import table.Columns;
import table.Table;

public class TableBuilder {

	private static final String SPRINT_HEADER = "Sprint #";
	private static final String BUG_NUMBER_HEADER = "Bug #";
	private static final String COUNTER_HEADER = "Number of bugs";
	private static final String TIME_HEADER = "Hours";
	private static final String AVERAGE_HEADER = "Average hours";
	private static final String TOTAL_HEADER = "Total";
	
	
	String dir;

	public TableBuilder() {
	}

	public TableBuilder(String loc) {
		dir = loc;
	}

	public String buildCalendarLog(List<BugEntry> bugs) throws IOException {
		StringBuilder result = new StringBuilder();

		Table table = new Table();
		Iterator<BugEntry> it = bugs.iterator();

		DateFormat df = new SimpleDateFormat("dd MMMM yyyy");
		Date date = null;
		String sprint = null;
		while (it.hasNext()) {
			BugEntry bug = it.next();
			table.newRow();
			if (!bug.getDate().equals(date)) {
				date = bug.getDate();
				sprint = null;
				table.addCell(df.format(date), Alignment.CENTER);
				table.newRow();
			}
			if (!bug.getSprint().equals(sprint)) {
				sprint = bug.getSprint();
				table.addCell(sprint);
				table.newRow();
			}
			table.addCell("");
			table.addCell(bug.getFileName());
		}
		table.closeTable();
		result.append(table.render());

		return result.toString();
	}

	public String buildMetricsLog(List<BugEntry> bugs) throws IOException {
		StringBuilder result = new StringBuilder();

		Table table = new Table(Columns.FIXED);
		MetricsAnalyzer analyzer = new MetricsAnalyzer(dir);
		Map<String, List<MetricsData>> results = analyzer.parse();
		Iterator<String> it2 = results.keySet().iterator();
		table.addHeader(SPRINT_HEADER);
		while (it2.hasNext()) {
			List<Integer> totals = new ArrayList<Integer>();
			String key = it2.next();
			List<MetricsData> metrics = results.get(key);
			Iterator<MetricsData> metricsIt = metrics.iterator();
			table.addCell(key);
			while (metricsIt.hasNext()) {
				MetricsData data = metricsIt.next();
				String value = data.getStringValue(true);
				table.addCell(value, data.getDataName(), Alignment.RIGHT);
				totals.add(new File(data.getFolderPath()).listFiles().length);
			}
			if (!totals.isEmpty()) {
				Iterator<Integer> totalIt = totals.iterator();
				while (totalIt.hasNext()) {
					Integer total = totalIt.next();
					table.addCell(total.toString(), TOTAL_HEADER, Alignment.RIGHT);
				}
			}
			table.newRow();
		}
		table.closeTable();

		result.append(table.render());
		return result.toString();
	}

	public String buildHoursLog(List<BugEntry> bugs) {
		StringBuilder result = new StringBuilder();
		Table table = new Table(Columns.FIXED);

		bugs = new HourCalculator(bugs).calculateHours();
		Map<String, Map<String, String>> metadataMap = new HashMap<String, Map<String, String>>();
		Map<String, Float> timeMap = new HashMap<String, Float>();
		Iterator<BugEntry> it = bugs.iterator();
		while (it.hasNext()) {
			BugEntry bug = it.next();
			Float currentTime = timeMap.get(bug.getBugNumber());
			if (currentTime == null) {
				timeMap.put(bug.getBugNumber(), new Float(0));
				currentTime = new Float(0);
			}
			timeMap.put(bug.getBugNumber(),
					Float.valueOf(currentTime + (bug.getWorked() == null ? 0 : Float.valueOf(bug.getWorked()))));
			metadataMap.put(bug.getBugNumber(), bug.getMetadata());
		}
		SortedSet<Entry<String, Float>> ss = entriesSortedByValues(timeMap);

		Iterator<Entry<String, Float>> setIterator = ss.iterator();

		table.addHeader(BUG_NUMBER_HEADER);
		table.addHeader(TIME_HEADER);

		while (setIterator.hasNext()) {
			Entry<String, Float> entry = setIterator.next();
			table.addCell(entry.getKey(), BUG_NUMBER_HEADER, Alignment.LEFT);
			table.addCell(entry.getValue().toString(), TIME_HEADER, Alignment.LEFT);
			Map<String, String> metadata = metadataMap.get(entry.getKey());
			Iterator<String> bugMeta = metadata.keySet().iterator();
			while (bugMeta.hasNext()) {
				String key = bugMeta.next();
				table.addCell(metadata.get(key), key, Alignment.RIGHT);
			}
			table.newRow();
		}
		table.closeTable();
		result.append(table.render());
		
		Map<String, Map<String, Float>> metadataTableMappings = new HashMap<String, Map<String, Float>>();
		Map<String, Map<String, Integer>> counter = new HashMap<String, Map<String, Integer>>();
		
		Iterator<String> bugIt = timeMap.keySet().iterator();
		while (bugIt.hasNext()) {
			String key = bugIt.next();
			Map<String, String> metadata = metadataMap.get(key);
			Iterator<String> metaIt = metadata.keySet().iterator();
			while (metaIt.hasNext()) {
				String metaKey = metaIt.next();
				String metaValue = metadata.get(metaKey);

				Map<String, Float> currentMap = metadataTableMappings.get(metaKey);
				Map<String, Integer> currentMapCounter = counter.get(metaKey);
				if (currentMap == null) {
					currentMap = new HashMap<String, Float>();
				}
				if (currentMapCounter == null) {
					currentMapCounter = new HashMap<String, Integer>();
				}
				
				Float time = currentMap.get(metaValue);
				Integer currentCounter = currentMapCounter.get(metaValue);
				if (time == null) {
					currentMap.put(metaValue, new Float(0));
					time = new Float(0);
				}
				if (currentCounter == null) {
					currentMapCounter.put(metaValue, new Integer(0));
					currentCounter = new Integer(0);
				}
				
				time += timeMap.get(key);
				currentCounter++;
				currentMap.put(metaValue, time);
				currentMapCounter.put(metaValue, currentCounter);
				metadataTableMappings.put(metaKey, currentMap);
				counter.put(metaKey, currentMapCounter);
			}
		}
		
		Iterator<String> metadataIterator = metadataTableMappings.keySet().iterator();
		while (metadataIterator.hasNext()){
			String tag = metadataIterator.next();
			Map<String, Float> tableMap = metadataTableMappings.get(tag);
			Table metaTable = new Table();
			Iterator<String> metaTableIt = tableMap.keySet().iterator();
			while (metaTableIt.hasNext()){
				String metadataEntry = metaTableIt.next();
				Float time = tableMap.get(metadataEntry);
				Integer ctr = counter.get(tag).get(metadataEntry);
				Float average = (time.floatValue()/ctr.floatValue());
				metaTable.addCell(metadataEntry, tag, Alignment.LEFT);
				metaTable.addCell(time.toString(), TIME_HEADER, Alignment.RIGHT);
				metaTable.addCell(ctr.toString(), COUNTER_HEADER, Alignment.RIGHT);
				metaTable.addCell(average.toString(), AVERAGE_HEADER, Alignment.RIGHT);
				metaTable.newRow();
			}
			metaTable.closeTable();
			result.append(metaTable.render());
		}

		
		return result.toString();
	}

	static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
		SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
				int res = e2.getValue().compareTo(e1.getValue());
				return res != 0 ? res : 1;
			}
		});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}
}
