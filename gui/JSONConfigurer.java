package gui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import jsonbuilder.JSONBug;

public class JSONConfigurer extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Map<String, List<JSONBug>> bugs;

	public JSONConfigurer(String filePath) throws IOException {
		super();
		this.bugs = readBugFile(filePath);
		add(getConfigurationPanel());

		pack();
		setVisible(true);
	}

	private JScrollPane getConfigurationPanel() {
		JScrollPane pane = new JScrollPane();

		return pane;
	}

	private Map<String, List<JSONBug>> readBugFile(String filePath) throws IOException {
		Map<String, List<JSONBug>> list = new HashMap<String, List<JSONBug>>();

		List<String> lines = Files.readAllLines(Paths.get(filePath));
		lines.set(0, lines.get(0).substring(1));
		lines.set(lines.size() - 1, lines.get(lines.size() - 1).substring(0, lines.get(lines.size() - 1).length() - 1));

		Iterator<String> it = lines.iterator();
		while (it.hasNext()) {
			String line = it.next();
			line = line.substring(1, line.length() - 2);
			String[] splitLine = line.split(",");
			Map<String, String> mapping = new HashMap<String, String>();
			for (int i = 0; i < splitLine.length; ++i) {
				String[] keyValue = splitLine[i].split(":");
				mapping.put(keyValue[0].replaceAll("\"", "").trim(), keyValue[1].replaceAll("\"", "").trim());
			}
			JSONBug bug = new JSONBug();
			bug.setBilled(mapping.get("billed").replaceAll("h", ""));
			bug.setBugNumber(mapping.get("bugNumber"));
			bug.setDate(mapping.get("date"));
			bug.setDescription(mapping.get("description"));
			bug.setRole(mapping.get("Role"));
			bug.setWorked(mapping.get("worked").replaceAll("h", ""));
			if (list.get(mapping.get("date")) == null){
				list.put(mapping.get("date"), new ArrayList<JSONBug>());
			}
			list.get(mapping.get("date")).add(bug);

		}

		return list;
	}

}
