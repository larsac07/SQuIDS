package autocisq.properties;

import java.util.List;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public abstract class PropertiesField {

	private Label label;
	private QualifiedName qName;
	private String id;

	public PropertiesField(Composite parent, String label, String id) {
		super();
		this.label = new Label(parent, SWT.NONE);
		this.label.setText(label);
		this.id = id;
		this.qName = new QualifiedName(this.id, this.id);
	}

	public Label getLabel() {
		return this.label;
	}

	public void setLabel(Label label) {
		this.label = label;
	}

	public QualifiedName getqName() {
		return this.qName;
	}

	public void setqName(QualifiedName qName) {
		this.qName = qName;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public abstract void setValues(List<String> values);

	public abstract List<String> getValues();

}