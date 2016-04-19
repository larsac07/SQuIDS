package autocisq.properties;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

public class PropertiesChecklistField extends PropertiesField {
	private Group checklist;

	public PropertiesChecklistField(Composite parent, String label, String id, int height, List<String> checkboxes) {
		super(parent, label, id);
		this.checklist = new Group(parent, SWT.SHADOW_IN | SWT.RESIZE);
		this.checklist.setLayout(new RowLayout(SWT.VERTICAL));
		for (String checkbox : checkboxes) {
			new Button(this.checklist, SWT.CHECK).setText(checkbox);
		}
	}

	@Override
	public List<String> getValues() {
		List<String> selected = new LinkedList<>();
		for (Control control : this.checklist.getChildren()) {
			if (control instanceof Button) {
				Button checkbox = (Button) control;
				if (checkbox.getSelection())
					selected.add(((Button) control).getText());
			}
		}
		return selected;
	}

	@Override
	public void setValues(List<String> values) {
		for (String value : values) {
			for (Control control : this.checklist.getChildren()) {
				if (control instanceof Button) {
					Button checkbox = (Button) control;
					if (checkbox.getText().equals(value)) {
						checkbox.setSelection(true);
					}
				}
			}
		}
	}

}
