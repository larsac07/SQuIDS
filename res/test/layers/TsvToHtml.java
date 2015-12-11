package no.uib.lca092.rtms;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import no.uib.lca092.rtms.gui.GUI;
import no.uib.lca092.rtms.gui.ThemeManager;
import no.uib.lca092.rtms.io.Parser;

public class TsvToHtml {

	private Preferences prefs = Preferences.userNodeForPackage(this.getClass());
	private final String COPY_SETTING = "copy_on_page";
	private final String SAVE_SETTING = "save_on_load";
	private final String THEME_SETTING = "theme";

	private GUI gui;
	private Parser parser;
	private int index = 0;
	private boolean autoCopyPage;
	private boolean autoSaveFile;
	private String currentTheme;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new TsvToHtml();
			}
		});
	}

	public TsvToHtml() {

		this.gui = GUI.getInstance();
		this.gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.parser = new Parser();

		loadPrefs();

		setupActionListeners();
	}

	private void loadPrefs() {
		this.autoCopyPage = this.prefs.getBoolean(this.COPY_SETTING, false);
		this.autoSaveFile = this.prefs.getBoolean(this.SAVE_SETTING, false);
		this.currentTheme = this.prefs.get(this.THEME_SETTING, "Standard");
		this.gui.getSettings().getThemeBox().setSelectedItem(this.currentTheme);
		ThemeManager.buildTheme(this.currentTheme, this.gui);
	}

	private void savePrefs() {
		this.prefs.put(this.THEME_SETTING, this.currentTheme);
		this.prefs.putBoolean(this.COPY_SETTING, this.autoCopyPage);
		this.prefs.putBoolean(this.SAVE_SETTING, this.autoSaveFile);
	}

	private void setupActionListeners() {

		this.gui.getNextButton().setMnemonic(KeyEvent.VK_RIGHT);
		this.gui.getPreviousButton().setMnemonic(KeyEvent.VK_LEFT);

		this.gui.getOpenMenuItem().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		this.gui.getOpenMenuItem().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				loadFile();
				if (TsvToHtml.this.autoSaveFile) {
					saveFile();
				}
			}
		});

		this.gui.getSaveMenuItem().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		this.gui.getSaveMenuItem().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				saveFile();
			}
		});

		this.gui.getCopyMenuItem().setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK));
		this.gui.getCopyMenuItem().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				copyTextArea();
			}
		});

		this.gui.getPrefsMenuItem().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
		this.gui.getPrefsMenuItem().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				TsvToHtml.this.gui.toggleSettingsWindow();
				TsvToHtml.this.gui.getSettings().setSelectedCheckBoxes(TsvToHtml.this.autoCopyPage,
						TsvToHtml.this.autoSaveFile);
			}
		});

		this.gui.getPreviousButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				update(-1);
			}
		});

		this.gui.getNextButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				update(1);

			}
		});

		this.gui.getSettings().getCancelButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				TsvToHtml.this.gui.getSettings().getSaveCheck().setSelected(TsvToHtml.this.autoSaveFile);
				TsvToHtml.this.gui.getSettings().getCopyCheck().setSelected(TsvToHtml.this.autoCopyPage);
				TsvToHtml.this.gui.getSettings().setVisible(false);
			}
		});

		this.gui.getSettings().getApplyButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateSettings();
			}
		});

		this.gui.getSettings().getOkButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateSettings();
				TsvToHtml.this.gui.getSettings().setVisible(false);
			}
		});

	}

	/**
	 * Copies the entire text of the textArea to the system clipboard
	 */
	public void copyTextArea() {
		String selectedText = TsvToHtml.this.gui.getTextArea().getText();
		StringSelection stringSelection = new StringSelection(selectedText);
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
	}

	/**
	 * updates the gui, with the correct page index and controls the jButtons
	 * accordingly.
	 */
	public void update(int indexMod) {

		TsvToHtml.this.index += indexMod;

		int size = this.parser.getRowHTMLList().size();
		String page = "Page ";
		String of = " of ";

		updateButtons(size);

		TsvToHtml.this.gui.getTextArea().setText(TsvToHtml.this.parser.getRowHTMLList().get(TsvToHtml.this.index));
		TsvToHtml.this.gui.getTextArea().setCaretPosition(0);
		this.gui.getPageLabel().setText(page + (this.index + 1) + of + size);

		autoCopy();
	}

	private void updateButtons(int size) {
		this.gui.getPreviousButton().setEnabled(true);
		this.gui.getNextButton().setEnabled(true);
		if (this.index <= 0) {
			this.index = 0;
			this.gui.getPreviousButton().setEnabled(false);
		} else if (this.index >= size - 1) {
			this.index = size - 1;
			this.gui.getNextButton().setEnabled(false);
		}
	}

	private void autoCopy() {
		if (TsvToHtml.this.autoCopyPage) {
			copyTextArea();
		}
	}

	protected void updateSettings() {
		this.autoSaveFile = TsvToHtml.this.gui.getSettings().getSaveCheck().isSelected();
		this.autoCopyPage = TsvToHtml.this.gui.getSettings().getCopyCheck().isSelected();
		String newTheme = this.gui.getSettings().getThemeBox().getSelectedItem().toString();
		if (!this.currentTheme.equals(newTheme)) {
			ThemeManager.buildTheme(newTheme, this.gui);
			this.currentTheme = newTheme;
		}
		savePrefs();
	}

	private void loadFile() {
		JFileChooser fileChooser = new JFileChooser(new File(""));
		int returnVal = fileChooser.showOpenDialog(TsvToHtml.this.gui);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			TsvToHtml.this.parser.setInputTSV(file);
			try {
				TsvToHtml.this.parser.parse();
			} catch (IOException ioe) {
				System.err.println("Something went wrong when trying to read from file  \""
						+ TsvToHtml.this.parser.getInputTSV() + "\"");
				ioe.printStackTrace();
			}

			TsvToHtml.this.parser.toRowHTML();
			update(0);
		}
	}

	protected void saveFile() {
		JFileChooser fileChooser = new JFileChooser(new File("./res"));
		int returnVal = fileChooser.showSaveDialog(TsvToHtml.this.gui);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			TsvToHtml.this.parser.setOutputTXT(file);
			try {
				TsvToHtml.this.parser.write();
			} catch (IOException ioe) {
				System.err.println("Something went wrong when trying to write to file  \""
						+ TsvToHtml.this.parser.getOutputTXT() + "\"");
				ioe.printStackTrace();
			}
		}
	}
}
