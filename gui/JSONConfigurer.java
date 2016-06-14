package gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import jsonbuilder.JSONBug;
import jsonbuilder.JSONBuilder;

public class JSONConfigurer extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Map<String, List<JSONBug>> bugs;
	String filePath;
	boolean refresh = false;

	public JSONConfigurer(String filePath) throws IOException {
		super();
		this.filePath = filePath;
		this.bugs = readBugFile(filePath);
		setContentPane(getPanel());

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				String recalculated = recalculate();
				PrintWriter out;
				try {
					out = new PrintWriter(filePath);
					out.print(recalculated);
					out.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				evt.getWindow().dispose();
			}
		});

		setVisible(true);
		pack();
	}

	public void refresh() throws IOException{
		refresh = true;
		removeAll();
		this.bugs = readBugFile(filePath);
		setContentPane(getPanel());
		refresh = false;
	}
	
	private JScrollPane getPanel() {
		JScrollPane scrollPane = new JScrollPane(getConfigurationPanel(),
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setPreferredSize(new Dimension(800, 600));
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		return scrollPane;
	}

	private JPanel getConfigurationPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 7));

		Iterator<String> dateIt = bugs.keySet().iterator();
		while (dateIt.hasNext()) {
			String date = dateIt.next();
			List<JSONBug> bugList = bugs.get(date);
			Iterator<JSONBug> bugIt = bugList.iterator();
			while (bugIt.hasNext()) {
				JSONBug bug = bugIt.next();
				panel.add(new CustomTextArea(bug.getBugNumber()));
				panel.add(new CustomTextArea(bug.getDate()));
				panel.add(new CustomTextArea(bug.getWorked()));
				panel.add(new CustomTextArea(bug.getBilled()));
				panel.add(new CustomTextArea(bug.getDescription()));
				panel.add(new CustomTextArea(bug.getRole()));
				JCheckBox overridden = new JCheckBox();
				// overridden.setEnabled(false);
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
			if (!"".equals(line)) {
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
		}

		return list;
	}

	private String recalculate() {
		StringBuilder output = new StringBuilder();
		List<JSONBug> list = new ArrayList<JSONBug>();
		Map<String, String> overriddenHours = new HashMap<String, String>();
		for (int i = 0; i < ((Container) ((Container) (Container) getContentPane().getComponent(0)).getComponent(0))
				.getComponentCount(); ++i) {
			String number = "";
			String date = "";
			String worked = "";
			String billed = "";
			String description = "";
			String role = "";
			boolean overridden = false;

			if (((Container) ((Container) getContentPane().getComponent(0)).getComponent(0))
					.getComponent(i) instanceof CustomTextArea) {
				number = ((CustomTextArea) ((Container) ((Container) getContentPane().getComponent(0)).getComponent(0))
						.getComponent(i++)).getText();
				date = ((CustomTextArea) ((Container) ((Container) getContentPane().getComponent(0)).getComponent(0))
						.getComponent(i++)).getText();
				worked = ((CustomTextArea) ((Container) ((Container) getContentPane().getComponent(0)).getComponent(0))
						.getComponent(i++)).getText();
				billed = ((CustomTextArea) ((Container) ((Container) getContentPane().getComponent(0)).getComponent(0))
						.getComponent(i++)).getText();
				description = ((CustomTextArea) ((Container) ((Container) getContentPane().getComponent(0))
						.getComponent(0)).getComponent(i++)).getText();
				role = ((CustomTextArea) ((Container) ((Container) getContentPane().getComponent(0)).getComponent(0))
						.getComponent(i++)).getText();
			}
			if (((Container) ((Container) getContentPane().getComponent(0)).getComponent(0))
					.getComponent(i) instanceof JCheckBox) {
				overridden = ((JCheckBox) ((Container) ((Container) getContentPane().getComponent(0)).getComponent(0))
						.getComponent(i)).isSelected();
				if (overridden) {
					overriddenHours.put(number, worked);
				}
			}
			JSONBug bug = new JSONBug();
			bug.setBugNumber(number);
			bug.setDate(date);
			bug.setWorked(worked);
			bug.setBilled(billed);
			bug.setDescription(description);
			bug.setRole(role);
			list.add(bug);
		}
		list = getworkedQueue(overriddenHours);
		Iterator<JSONBug> it = list.iterator();
		while (it.hasNext()) {
			output.append(it.next());
			if (it.hasNext()) {
				output.append(",\n");
			}
		}
		return "[" + output.toString() + "]";
	}

	private JButton getDisabledButton(String label) {
		JButton button = new JButton(label);
		button.setEnabled(false);
		return button;
	}

	private List<JSONBug> getworkedQueue(Map<String, String> overriddenHours) {
		List<JSONBug> list = new ArrayList<JSONBug>();
		Iterator<String> it = bugs.keySet().iterator();
		while (it.hasNext()) {
			String date = it.next();
			List<JSONBug> bugsPerDate = bugs.get(date);
			Float[] values = new Float[bugsPerDate.size() - overridesForDate(overriddenHours, bugsPerDate)];
			for (int i = 0; i < values.length; ++i) {
				values[i] = (float) 0.0;
			}
			float max = (JSONBuilder.hoursPerDay - getOverriddenTotal(overriddenHours, bugsPerDate))
					/ JSONBuilder.hoursIncrement;
			for (int i = 0; i < max; ++i) {
				if (values[i % values.length] == null) {
					values[i % values.length] = (float) 0;
				}
				values[i % values.length] += JSONBuilder.hoursIncrement;
			}
			int counter = 0;
			Iterator<JSONBug> it2 = bugsPerDate.iterator();
			while (it2.hasNext()) {
				JSONBug bug = it2.next();
				if (overriddenHours.containsKey(bug.getBugNumber())) {
					bug.setWorked(overriddenHours.get(bug.getBugNumber()));
				} else {
					bug.setWorked(values[counter++] + "");
				}
			}
			list.addAll(bugsPerDate);
			// bugs.replace(date, bugsPerDate);
		}
		return list;
	}

	private float getOverriddenTotal(Map<String, String> overriddenMap, List<JSONBug> bugs) {
		float result = 0;
		Iterator<JSONBug> it = bugs.iterator();
		while (it.hasNext()) {
			JSONBug bug = it.next();
			if (overriddenMap.containsKey(bug.getBugNumber())) {
				result += Float.valueOf(overriddenMap.get(bug.getBugNumber()));
			}
		}
		return result;
	}

	private int overridesForDate(Map<String, String> overriddenMap, List<JSONBug> bugList) {
		int counter = 0;

		Iterator<JSONBug> it = bugList.iterator();
		while (it.hasNext()) {
			if (overriddenMap.containsKey(it.next().getBugNumber())) {
				counter++;
			}
		}

		return counter;
	}

	private class CustomTextArea extends JTextArea {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public CustomTextArea() {
			this("");
		}

		public CustomTextArea(String label) {
			super(label);
			this.addFocusListener(new FocusListener() {

				@Override
				public void focusGained(FocusEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void focusLost(FocusEvent arg0) {
					if (!refresh && !((JTextArea) arg0.getComponent()).getText().startsWith("PMO")) {
						recalculate();
//						 try {
//						 refresh();
//						 } catch (IOException e) {
//						 e.printStackTrace();
//						 }
					}
				}

			});
		}

	}
}
