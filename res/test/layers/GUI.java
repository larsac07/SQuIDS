package no.uib.lca092.rtms.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class GUI extends JFrame {
	/**
	 *
	 */
	private static final long serialVersionUID = -8444329352701950508L;

	private static GUI instance;

	private JMenuItem openMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem copyMenuItem;
	private JMenuItem prefsMenuItem;

	private JTextArea textArea;

	private JButton previousButton;
	private JButton nextButton;

	private JLabel pageLabel;
	private JList<String> list;
	private SettingsGUI settings;
	private int width;
	private int height;

	public synchronized static GUI getInstance() {
		if (instance == null) {
			instance = new GUI();
		}

		return instance;
	}

	private GUI() {
		setTitle("Feedback-utility");
		ImageIcon ico = new ImageIcon("res/grade.png");
		setIconImage(ico.getImage());

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.width = screenSize.width / 2;
		this.height = screenSize.height;
		setBounds(0, 0, this.width, this.height);

		this.settings = new SettingsGUI(this.width / 2, this.height / 2);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		JMenu editMenu = new JMenu("Edit");
		menuBar.add(editMenu);

		JMenu settingsMenu = new JMenu("Settings");
		menuBar.add(settingsMenu);

		this.openMenuItem = new JMenuItem("Open");
		fileMenu.add(this.openMenuItem);

		this.saveMenuItem = new JMenuItem("Save");
		fileMenu.add(this.saveMenuItem);

		this.copyMenuItem = new JMenuItem("Copy to clipboard");
		editMenu.add(this.copyMenuItem);

		this.prefsMenuItem = new JMenuItem("Preferences");
		settingsMenu.add(this.prefsMenuItem);

		this.textArea = new JTextArea();
		this.textArea.setWrapStyleWord(true);
		this.textArea.setLineWrap(true);
		this.textArea.setEditable(false);

		JScrollPane scroller = new JScrollPane(this.textArea);

		getContentPane().add(scroller, BorderLayout.CENTER);

		JPanel navPanel = new JPanel();
		getContentPane().add(navPanel, BorderLayout.SOUTH);

		this.previousButton = new JButton("Previous");
		this.previousButton.setEnabled(false);
		navPanel.add(this.previousButton);

		this.pageLabel = new JLabel("Page");
		navPanel.add(this.pageLabel);

		this.nextButton = new JButton("Next");
		this.nextButton.setEnabled(false);
		navPanel.add(this.nextButton);

		this.list = new JList<>();
		getContentPane().add(this.list, BorderLayout.WEST);

		setVisible(true);
	}

	public SettingsGUI getSettings() {
		return this.settings;
	}

	public void toggleSettingsWindow() {
		if (!this.settings.isVisible()) {
			this.settings.setVisible(true);
		} else {
			this.settings.requestFocus();
			this.settings.toFront();
		}
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public JTextArea getTextArea() {
		return this.textArea;
	}

	public JButton getPreviousButton() {
		return this.previousButton;
	}

	public JLabel getPageLabel() {
		return this.pageLabel;
	}

	public JButton getNextButton() {
		return this.nextButton;
	}

	public JMenuItem getOpenMenuItem() {
		return this.openMenuItem;
	}

	public JMenuItem getSaveMenuItem() {
		return this.saveMenuItem;
	}

	public JMenuItem getCopyMenuItem() {
		return this.copyMenuItem;
	}

	public JList<String> getList() {
		return this.list;
	}

	public JMenuItem getPrefsMenuItem() {
		return this.prefsMenuItem;
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return this.height;
	}

}
