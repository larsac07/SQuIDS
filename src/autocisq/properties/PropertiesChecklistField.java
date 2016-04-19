package autocisq.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

public class PropertiesChecklistField extends PropertiesField {
	private final static String NL = System.lineSeparator();
	private Group checklist;

	public PropertiesChecklistField(Composite parent, String label, String id, int height) {
		super(parent, label, id);
		this.checklist = new Group(parent, SWT.SHADOW_IN);
		this.checklist.setLayout(new RowLayout(SWT.VERTICAL));
		GridData gdm = new GridData(GridData.FILL_HORIZONTAL);
		gdm.heightHint = height;
		this.checklist.setLayoutData(gdm);
	}

	@Override
	public String getText() {
		String text = "";
		Control[] controls = this.checklist.getChildren();
		System.out.println(controls.length);
		for (int i = 1; i < controls.length; i++) {
			Control control = controls[i];
			if (control instanceof Button) {
				text += ((Button) control).getText();
			}
		}
		return text;
	}

	@Override
	public void setText(String text) {
		String[] lines = text.split(NL);
		for (String line : lines) {
			new Button(this.checklist, SWT.CHECK).setText(line);
		}
	}

}
