package tablebuilder;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import metrics.MetricsAnalyzer;
import metrics.MetricsData;
import table.Alignment;
import table.Table;

public class TableBuilder {

	String dir;
	
	public TableBuilder() {
		
	}
	
	public TableBuilder(String loc) {
		dir = loc;
	}
	public String build() throws IOException, ParseException {
		FolderParser parser = new FolderParser(dir);
		Map<String, Map<String, List<String>>> map = parser.parseFolder();
		return build(map);
	}

	public String build(Map<String, Map<String, List<String>>> map) throws IOException, ParseException {
		StringBuilder tables = new StringBuilder();
		Table table = new Table();
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String date = it.next();
			Map<String, List<String>> sprintMap = map.get(date);
			Iterator<String> it2 = sprintMap.keySet().iterator();
			DateFormat df = new SimpleDateFormat("dd MMMM yyyy");
			  
			String formattedDate = df.format(new Date(Long.valueOf(date)));
			table.addCell(formattedDate, Alignment.CENTER);
			table.newRow();
			while (it2.hasNext()) {
				String sprint = it2.next();
				table.addCell(sprint);
				table.newRow();
				List<String> bugs = sprintMap.get(sprint);
				Iterator<String> it3 = bugs.iterator();
				while (it3.hasNext()){
					String bug = it3.next();
					table.addCell("");
					table.addCell(bug);
					table.newRow();
				}
			}

		}
		table.closeTable();

		tables.append(table.render());

		Table table2 = new Table();
		table2.addCell("Sprint", Alignment.CENTER);
		table2.addCell("Metrics", Alignment.CENTER);
		table2.newRow();
		MetricsAnalyzer analyzer = new MetricsAnalyzer(dir);
		Map<String, MetricsData> results = analyzer.parse();
		Iterator<String> it2 = results.keySet().iterator();
		while (it2.hasNext()) {
			String key = it2.next();
			String value = results.get(key).getStringValue();
			table2.addCell(key);
			table2.addCell(value, Alignment.RIGHT);
			table2.newRow();
		}
		table2.closeTable();

		tables.append(table2.render());

		return tables.toString();
	}
	
}
