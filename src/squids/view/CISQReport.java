package squids.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class CISQReport extends ViewPart {

	public final static String ID = "squids.view.cisqreport";

	private StyledText text;
	private final static String NL = System.lineSeparator();

	public CISQReport() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		this.text = new StyledText(parent, SWT.V_SCROLL);
		this.text.setText("");
		this.text.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		this.text.setEditable(false);
		this.text.setCaret(null);
	}

	@Override
	public void setFocus() {
		this.text.setFocus();
	}

	public void setString(String string) {
		this.text.setText(string);
	}

	public void add(String string) {
		String textContent = this.text.getText();
		textContent += string + NL;
		this.text.setText(textContent);
	}

}
