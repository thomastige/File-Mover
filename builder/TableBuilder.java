package builder;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import metrics.MetricsAnalyzer;
import metrics.MetricsData;
import table.Alignment;
import table.Table;

public class TableBuilder {

	String dir;

	public TableBuilder(String loc) {
		dir = loc;
	}

	public String build() throws IOException {
		StringBuilder tables = new StringBuilder();
		Table table = new Table();
		FolderParser parser = new FolderParser(dir);
		Map<String, List<String>> result = parser.parseFolder();
		Iterator<String> it = result.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			List<String> list = result.get(key);
			Iterator<String> it2 = list.iterator();
			table.addCell(key);
			table.newRow();
			while (it2.hasNext()) {
				String value = it2.next();
				table.addCell(" ");
				table.addCell(value);
				table.newRow();
			}

		}
		table.closeTable();

		tables.append(table.render());

		Table table2 = new Table();
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
