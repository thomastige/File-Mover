package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.border.Border;

import props.PropertyManager;
import reader.Reader;

public class Gui {

	private JFrame frame;
	private JButton chooseSourceBtn;
	private JButton chooseDestBtn;
	private JButton startBtn;
	private JButton stopBtn;
	private JTextArea sourceText;
	private JTextArea destText;
	private JTextArea consoleText;
	private JTextArea timeText;
	private Timer timer;

	public void buildGui() {
		frame = new JFrame();
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 2));

		buildComponents();

		panel.add(sourceText);
		panel.add(chooseSourceBtn);
		panel.add(destText);
		panel.add(chooseDestBtn);
		panel.add(timeText);
		panel.add(startBtn);
		panel.add(stopBtn);

		frame.add(panel, BorderLayout.NORTH);

		consoleText = new JTextArea(15, 0);
		consoleText.setEditable(false);
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
		chooseSourceBtn = new JButton("Source");
		chooseSourceBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent paramActionEvent) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					sourceText.setText(fileChooser.getSelectedFile().toString());
				}
			}
		});
		destText = new JTextArea();
		destText.setText(PropertyManager.readProperty("dest"));
		destText.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		chooseDestBtn = new JButton("Destination");
		chooseDestBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent paramActionEvent) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					destText.setText(fileChooser.getSelectedFile().toString());
				}

			}
		});
		startBtn = new JButton("start");
		startBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent paramActionEvent) {
				if (!"".equals(timeText) && !"".equals(sourceText.getText()) && !"".equals(destText.getText())) {
					timer = new Timer(Integer.parseInt(timeText.getText()), new ActionListener() {
						public void actionPerformed(ActionEvent evt) {

							Reader reader = new Reader(sourceText.getText(), destText.getText(), "JIRANOTE__", "__");
							try {
								DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
								Date date = new Date();
								println(dateFormat.format(date));
								print(reader.parseFolder());

							} catch (IOException e) {
								e.printStackTrace();
							} catch (NumberFormatException e) {
								e.printStackTrace();
							}

						}
					});
					timer.setInitialDelay(0);
					timer.setRepeats(true);
					timer.start();
				} else {
					JOptionPane.showMessageDialog(frame, "Please make sure all values are set.");
				}

			}

		});
		
		stopBtn = new JButton("stop");
		stopBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				timer.stop();
			}
		});
		
		timeText = new JTextArea();
		timeText.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		timeText.setText(PropertyManager.readProperty("time"));

		frame.addWindowListener(new java.awt.event.WindowAdapter() {
	        public void windowClosing(WindowEvent winEvt) {
	        	PropertyManager.set("source", sourceText.getText());
	        	PropertyManager.set("dest", destText.getText());
	        	PropertyManager.set("time", timeText.getText());
	        	
	            try {
					PropertyManager.dump();
				} catch (IOException e) {
					e.printStackTrace();
				}
	            System.exit(0);
	        }
	    });
		
	}

	void print(String msg) {
		consoleText.append(msg);
	}

	void println(String msg) {
		consoleText.append(msg + "\n");
	}
	
	

}
