package driver;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import gui.Gui;
import props.PropertyManager;
import reader.Reader;

public class Driver {

	public static void main(String[] args) throws InterruptedException, IOException {
		gui();
	}

	static void gui() throws IOException {
		PropertyManager.initialize();
		Gui gui = new Gui();
		gui.buildGui();
	}
}
