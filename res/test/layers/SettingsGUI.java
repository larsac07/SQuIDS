package no.uib.lca092.rtms.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SettingsGUI extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8517891157441806944L;
	private JButton cancelButton;
	private JButton applyButton;
	private JButton okButton;
	private JCheckBox copyCheck;
	private JCheckBox saveCheck;
	private JComboBox<String> themeBox;

	public SettingsGUI(int width, int height){

		setTitle("Preferences");
		setBounds(200,100,width,height);
		JPanel settingsPanel = new JPanel(new BorderLayout());
		
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.PAGE_AXIS));
		
		JLabel copyLabel = new JLabel("Copy Settings");
		copyLabel.setFont(new Font(copyLabel.getFont().getFontName(), Font.BOLD, copyLabel.getFont().getSize()));
		innerPanel.add(copyLabel);
		copyCheck = new JCheckBox("Copy on pageTurn");
		innerPanel.add(copyCheck);
		saveCheck = new JCheckBox("Save on file-open");
		innerPanel.add(saveCheck);
		JLabel themeLabel = new JLabel("Theme Settings");
		themeLabel.setFont(new Font(themeLabel.getFont().getFontName(), Font.BOLD, themeLabel.getFont().getSize()));
		
		innerPanel.add(themeLabel);
		themeBox = new JComboBox<String>();
		int themeWidth = themeBox.getMaximumSize().width;
		themeBox.setMaximumSize(new Dimension(themeWidth, height/12));
		themeBox.setEditable(false);
		themeBox.addItem("Standard");
		themeBox.addItem("Dark");
		themeBox.addItem("Metal");
		themeBox.addItem("Nimbus");
		themeBox.addItem("Console");
		innerPanel.add(themeBox);
		
		JPanel buttonPanel = new JPanel();
		cancelButton = new JButton("Cancel");
		applyButton = new JButton("Apply");
		okButton = new JButton("OK");

		buttonPanel.add(cancelButton);
		buttonPanel.add(applyButton);
		buttonPanel.add(okButton);
		
		settingsPanel.add(innerPanel, BorderLayout.CENTER);
		settingsPanel.add(buttonPanel,BorderLayout.SOUTH);
		add(settingsPanel);
		
		getRootPane().setDefaultButton(okButton);
		okButton.requestFocus();
	}

	public JComboBox<String> getThemeBox() {
		return themeBox;
	}

	public void setSelectedCheckBoxes(boolean autoCopyPage, boolean autoSaveFile) {
		copyCheck.setSelected(autoCopyPage);
		saveCheck.setSelected(autoSaveFile);
	}

	public JButton getCancelButton() {
		return cancelButton;
	}


	public JButton getApplyButton() {
		return applyButton;
	}


	public JButton getOkButton() {
		return okButton;
	}


	public JCheckBox getCopyCheck() {
		return copyCheck;
	}


	public JCheckBox getSaveCheck() {
		return saveCheck;
	}
}
