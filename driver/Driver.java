package driver;

import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import gui.Gui;
import props.PropertyManager;

public class Driver {

	public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		gui();
	}

	static void gui() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		PropertyManager.initialize();
		Gui gui = new Gui();
		gui.buildGui();
	}
}
