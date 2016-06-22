package tablebuilder;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import adt.BugEntry;
import metrics.MetricsAnalyzer;
import metrics.MetricsData;
import table.Alignment;
import table.Columns;
import table.Table;

public class TableBuilder {

	String dir;

	public TableBuilder() {
	}

	public TableBuilder(String loc) {
		dir = loc;
	}

	public String build(List<BugEntry> bugs) throws IOException {
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
			if (!bug.getSprint().equals(sprint)){
				sprint = bug.getSprint();
				table.addCell(sprint);
				table.newRow();
			}
			table.addCell("");
			table.addCell(bug.getFileName());
		}
		table.closeTable();
		result.append(table.render());
		
		Table table2 = new Table(Columns.FIXED);
		MetricsAnalyzer analyzer = new MetricsAnalyzer(dir);
		Map<String, List<MetricsData>> results = analyzer.parse();
		Iterator<String> it2 = results.keySet().iterator();
		while (it2.hasNext()) {
			String key = it2.next();
			List<MetricsData> metrics = results.get(key);
			Iterator<MetricsData> metricsIt = metrics.iterator();
			table2.addCell(key);
			while (metricsIt.hasNext()) {
				String value = metricsIt.next().getStringValue();
				table2.addCell(value, Alignment.RIGHT);
			}
			table2.newRow();
		}
		table2.closeTable();

		result.append(table2.render());
		
		
		return result.toString();
	}

}
