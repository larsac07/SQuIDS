package no.uib.lca092.rtms.gui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

public class ThemeManager {

	public static void buildTheme(String name, GUI gui) {
		switch(name) {
		case "Standard":
			setStandardTheme(gui);
			break;
		case "Dark":
			setDarkTheme(gui);
			break;
		case "Nimbus":
			setBuiltInJavaTheme(gui, name);
			break;
		case "Console":
			setConsoleTheme(gui);
			break;
		case "Metal":
			setBuiltInJavaTheme(gui, name);
		default:
			break;
		}
	}

	/**
	 * Sets the look and feel to a built in java one
	 * @param gui the gui to be updated
	 * @param name the name of the look and feel
	 */
	private static void setBuiltInJavaTheme(GUI gui, String name) {
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if (name.equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Theme is not available, do nothing.
		}
		SwingUtilities.updateComponentTreeUI(gui);
		SwingUtilities.updateComponentTreeUI(gui.getSettings());
	}

	/**
	 * Sets the look and feel to a old school console-like color scheme
	 * @param gui the gui to be modified
	 */
	private static void setConsoleTheme(GUI gui) {
//		gui.getTextArea().setBackground(Color.BLACK);
//		gui.getTextArea().setForeground(Color.GREEN);
//		gui.getTextArea().setFont(new Font(Font.MONOSPACED, Font.PLAIN, gui.getTextArea().getFont().getSize()));
	}

	/**
	 * Sets the look and feel to a dark color scheme
	 * @param gui the gui to be modified
	 */
	private static void setDarkTheme(GUI gui) {
		// TODO Auto-generated method stub

	}

	/**
	 * Sets the look and feel to system standard
	 * @param gui the gui to be modified
	 */
	private static void setStandardTheme(GUI gui) {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		SwingUtilities.updateComponentTreeUI(gui);
		SwingUtilities.updateComponentTreeUI(gui.getSettings());
	}
}
