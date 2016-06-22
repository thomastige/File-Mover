package processor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import adt.BugEntry;
import adt.UIDataBean;
import constants.Constants;
import mover.Mover;
import parser.FolderParser;
import parser.HourCalculator;
import props.PropertyManager;
import tablebuilder.TableBuilder;

public class Processor {

	UIDataBean ui;

	public Processor(UIDataBean ui) {
		this.ui = ui;
	}

	public void generate() {
		PrintWriter out;
		Long time = System.currentTimeMillis();
		try {
			FolderParser parser = new FolderParser(ui.getDestination());
			parser.setCommentPosition(Integer.parseInt(ui.getGeneratorCommentValue()));
			parser.setDefaultComment(ui.getGeneratorDefaultComment());
			parser.setRole(ui.getGeneratorRole());
			if (!"".equals(ui.getGeneratorFromDate())) {
				parser.setFromDate(ui.getGeneratorFromDate());
			}
			if (!"".equals(ui.getGeneratorToDate())) {
				parser.setToDate(ui.getGeneratorToDate());
			}
			List<BugEntry> bugs = parser.parseToList();
			String jsonContent = new HourCalculator(bugs).getCalculationAsString();
			out = new PrintWriter(ui.getDestination() + File.separator + Constants.JSON_TEMPO);
			out.print(jsonContent);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		time = System.currentTimeMillis() - time;
		System.out.println("Generated JSON load file in " + time + " ms.");
	}

	public void createTable() {
		TableBuilder builder = new TableBuilder(ui.getDestination());
		PrintWriter out;
		try {
			Long time = System.currentTimeMillis();
			out = new PrintWriter(ui.getDestination() + File.separator + Constants.NOTE_LOG);
			FolderParser parser = new FolderParser(ui.getDestination());
			out.print(builder.build(parser.parseToList()));
			time = System.currentTimeMillis() - time;
			out.close();
			System.out.println("Generated a table in " + time + " ms.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void executeTimerAction() {
		Mover reader = new Mover(ui.getSource(), ui.getDestination(), ui.getPrefix(), ui.getSeparator());
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			System.out.println(dateFormat.format(date));
			String parseResult = reader.parseFolder();
			System.out.print(parseResult);
			if (parseResult != null && !"".equals(parseResult)) {
				createTable();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	public void setProperties() {
		PropertyManager.set("source", ui.getSource());
		PropertyManager.set("dest", ui.getDestination());
		PropertyManager.set("time", ui.getDelay());
		PropertyManager.set("prefix", ui.getPrefix());
		PropertyManager.set("separator", ui.getSeparator());
		PropertyManager.set("fromDateJSON", ui.getGeneratorFromDate());
		PropertyManager.set("toDateJSON", ui.getGeneratorToDate());
		PropertyManager.set("commentPosition", ui.getGeneratorCommentValue());
		PropertyManager.set("role", ui.getGeneratorRole());
		PropertyManager.set("defaultComment", ui.getGeneratorDefaultComment());
		try {
			PropertyManager.dump();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
