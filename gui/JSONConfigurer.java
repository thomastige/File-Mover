package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

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
		setContentPane(getPanel());
		setVisible(true);
		pack();
	}

	private JScrollPane getPanel() {
		JScrollPane scrollPane = new JScrollPane(getConfigurationPanel(), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setPreferredSize(new Dimension(800, 600));
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		return scrollPane;
	}

	private JPanel getConfigurationPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 7));
		
		Iterator<String> dateIt = bugs.keySet().iterator();
		while (dateIt.hasNext()){
			String date = dateIt.next();
			List<JSONBug> bugList = bugs.get(date);
			Iterator<JSONBug> bugIt = bugList.iterator();
			while (bugIt.hasNext()){
				JSONBug bug = bugIt.next();
				panel.add(new JTextArea(bug.getBugNumber()));
				panel.add(new JTextArea(bug.getDate()));
				panel.add(new JTextArea(bug.getWorked()));
				panel.add(new JTextArea(bug.getBilled()));
				panel.add(new JTextArea(bug.getDescription()));
				panel.add(new JTextArea(bug.getRole()));
				JCheckBox overridden = new JCheckBox();
				overridden.setEnabled(false);
				panel.add(overridden);
			}
		}
		
		return panel;
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
			String date = mapping.get("date");
			bug.setBilled(mapping.get("billed").replaceAll("h", ""));
			bug.setBugNumber(mapping.get("bugNumber"));
			bug.setDate(date);
			bug.setDescription(mapping.get("description"));
			bug.setRole(mapping.get("Role"));
			bug.setWorked(mapping.get("worked").replaceAll("h", ""));
			if (list.get(date) == null) {
				list.put(date, new ArrayList<JSONBug>());
			}
			list.get(date).add(bug);

		}

		return list;
	}

	private JButton getDisabledButton(String label) {
		JButton button = new JButton(label);
		button.setEnabled(false);
		return button;
	}

}
