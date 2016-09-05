package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.border.Border;

import adt.UIDataBean;
import constants.Constants;
import processor.Processor;
import props.PropertyManager;

//TODO: REFACTOR THE ENTIRE THING (YES, THE ENTIRE PROJECT)
public class Gui {

	private final class ExitWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent winEvt) {
			new Processor(getUIDataBean()).setProperties();
			System.exit(0);
		}
	}

	private final class StopButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			timer.stop();
		}
	}

	private final class ChooseSourceButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent paramActionEvent) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnValue = fileChooser.showOpenDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				sourceText.setText(fileChooser.getSelectedFile().toString());
			}
		}
	}

	private final class TimerListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			new Processor(getUIDataBean()).executeTimerAction();
		}
	}

	private final class StartButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent paramActionEvent) {
			if (!"".equals(timeText) && !"".equals(sourceText.getText()) && !"".equals(destText.getText())) {
				if (timer != null) {
					timer.stop();
				}
				timer = new Timer(Integer.parseInt(timeText.getText()), new TimerListener());
				timer.setInitialDelay(0);
				timer.setRepeats(true);
				timer.start();
			} else {
				JOptionPane.showMessageDialog(frame, "Please make sure all values are set.");
			}

		}
	}

	private final class ChooseDestBtnListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent paramActionEvent) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnValue = fileChooser.showOpenDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				destText.setText(fileChooser.getSelectedFile().toString());
			}

		}
	}

	/**
	 * http://stackoverflow.com/questions/19834155/jtextarea-as-console
	 *
	 */
	public class TextAreaOutputStream extends OutputStream {
	    private JTextArea textControl;

	    /**
	     * Creates a new instance of TextAreaOutputStream which writes
	     * to the specified instance of javax.swing.JTextArea control.
	     *
	     * @param control   A reference to the javax.swing.JTextArea
	     *                  control to which the output must be redirected
	     *                  to.
	     */
	    public TextAreaOutputStream( JTextArea control ) {
	        textControl = control;
	    }

	    /**
	     * Writes the specified byte as a character to the
	     * javax.swing.JTextArea.
	     *
	     * @param   b   The byte to be written as character to the
	     *              JTextArea.
	     */
	    public void write( int b ) throws IOException {
	        // append the data as characters to the JTextArea control
	        textControl.append( String.valueOf( ( char )b ) );
	    }  
	}
	
	private JFrame frame;
	private JButton chooseSourceBtn;
	private JButton chooseDestBtn;
	private JButton startBtn;
	private JButton stopBtn;
	private JButton timeBtn;
	private JButton prefixBtn;
	private JButton separatorBtn;
	private JTextArea sourceText;
	private JTextArea destText;
	private JTextArea consoleText;
	private JTextArea timeText;
	private JTextArea prefixText;
	private JTextArea separatorText;
	private Timer timer;

	public void buildGui() {
		JPanel corePanel = new JPanel(new GridLayout(1, 1));
		frame = new JFrame("File Mover");
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 2));
		JTabbedPane tabbedPane = new JTabbedPane();

		buildComponents();
		frame.setJMenuBar(buildMenu());

		panel.add(chooseSourceBtn);
		panel.add(sourceText);
		panel.add(chooseDestBtn);
		panel.add(destText);
		panel.add(timeBtn);
		panel.add(timeText);
		panel.add(prefixBtn);
		panel.add(prefixText);
		panel.add(separatorBtn);
		panel.add(separatorText);
		panel.add(startBtn);
		panel.add(stopBtn);

		tabbedPane.add("File Mover", panel);
		corePanel.add(tabbedPane);
		frame.add(corePanel, BorderLayout.NORTH);

		tabbedPane.add("JSON generator", getJSONPanel());

		consoleText = new JTextArea(15, 0);
		consoleText.setEditable(false);
		PrintStream out = new PrintStream( new TextAreaOutputStream( consoleText ) );
		System.setOut( out );
		System.setErr( out );

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JScrollPane scrollPane = new JScrollPane(consoleText);
		frame.add(scrollPane, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
	}

	private void buildComponents() {
		Border border = BorderFactory.createLineBorder(Color.BLACK);

		sourceText = new JTextArea(1, 0);
		sourceText
				.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		sourceText.setText(PropertyManager.readProperty("source"));
		getChooseSourceBtn();
		destText = new JTextArea();
		destText.setText(PropertyManager.readProperty("dest"));
		destText.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		getChooseDestBtn();
		getStartBtn();

		getStopBtn();
		timeBtn = new JButton("Delay (in ms)");
		timeBtn.setEnabled(false);
		timeText = new JTextArea();
		timeText.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		timeText.setText(PropertyManager.readProperty("time"));

		frame.addWindowListener(new ExitWindowListener());
		prefixBtn = new JButton("Prefix");
		prefixBtn.setEnabled(false);
		prefixText = new JTextArea();
		prefixText.setText(PropertyManager.readProperty("prefix"));
		prefixText
				.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		separatorBtn = new JButton("Separator");
		separatorBtn.setEnabled(false);
		separatorText = new JTextArea();
		separatorText.setText(PropertyManager.readProperty("separator"));
		separatorText
				.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));

	}

	private void getChooseSourceBtn() {
		chooseSourceBtn = new JButton("Source");
		chooseSourceBtn.addActionListener(new ChooseSourceButtonListener());
	}

	private void getChooseDestBtn() {
		chooseDestBtn = new JButton("Destination");
		chooseDestBtn.addActionListener(new ChooseDestBtnListener());
	}

	private void getStopBtn() {
		stopBtn = new JButton("stop");
		stopBtn.addActionListener(new StopButtonListener());
	}

	private void getStartBtn() {
		startBtn = new JButton("start");
		startBtn.addActionListener(new StartButtonListener());
	}

	public String getSeparator() {
		return separatorText.getText();
	}

	private JMenuBar buildMenu() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(getActionMenu());
		return menuBar;
	}

	private JMenu getActionMenu() {
		JMenu menu = new JMenu("Actions");
		menu.add(getCreateTableItem());
		return menu;
	}

	private JMenuItem getCreateTableItem() {
		JMenuItem menuItem = new JMenuItem("Create note tables");
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					new Processor(getUIDataBean()).createTables();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		});
		return menuItem;
	}

	private JTextArea fromDateText;
	private JTextArea toDateText;
	private JTextArea defaultCommentText;
	private JTextArea commentText;
	private JTextArea roleText;

	private JPanel getJSONPanel() {
		JPanel panel = new JPanel(new GridLayout(0, 2));
		Border border = BorderFactory.createLineBorder(Color.BLACK);

		panel.add(getDisabledJButton("From date (yyyymmdd)"));
		fromDateText = new JTextArea(PropertyManager.readProperty("fromDateJSON"));
		fromDateText
				.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		panel.add(fromDateText);

		panel.add(getDisabledJButton("To date (yyyymmdd)"));
		toDateText = new JTextArea(PropertyManager.readProperty("toDateJSON"));
		toDateText
				.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		panel.add(toDateText);

		panel.add(getDisabledJButton("Default Comment"));
		defaultCommentText = new JTextArea(PropertyManager.readProperty("defaultComment"));
		defaultCommentText.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		panel.add(defaultCommentText);
		panel.add(getDisabledJButton("Comment"));
		commentText = new JTextArea(PropertyManager.readProperty("commentPosition"));
		commentText.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		panel.add(commentText);
		panel.add(getDisabledJButton("Role"));
		roleText = new JTextArea(PropertyManager.readProperty("role"));
		roleText.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		panel.add(roleText);
		
		panel.add(getGenerateButton());
		panel.add(getJSONConfigureButton());

		return panel;
	}

	private JButton getGenerateButton() {
		JButton button = new JButton();
		button.setText("GENERATE");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new Processor(getUIDataBean()).generate();
			}

		});
		return button;
	}

	private JButton getDisabledJButton(String label) {
		JButton button = new JButton(label);
		button.setEnabled(false);
		return button;
	}

	private JButton getJSONConfigureButton() {
		JButton button = new JButton();
		button.setText("CONFIGURE");

		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					new JSONConfigurer(destText.getText() + File.separator + Constants.JSON_TEMPO);
				} catch (IOException | ParseException e) {
					e.printStackTrace();
				}
			}

		});
		return button;
	}

	private UIDataBean getUIDataBean() {
		UIDataBean result = new UIDataBean();
		result.setDelay(timeText.getText());
		result.setDestination(destText.getText());
		result.setPrefix(prefixText.getText());
		result.setSeparator(separatorText.getText());
		result.setSource(sourceText.getText());

		result.setGeneratorFromDate(fromDateText.getText());
		result.setGeneratorToDate(toDateText.getText());
		result.setGeneratorRole(roleText.getText());
		result.setGeneratorCommentValue(commentText.getText());
		result.setGeneratorDefaultComment(defaultCommentText.getText());
		return result;
	}

}
