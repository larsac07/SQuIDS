package autocisq.properties;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class PropertiesTextField {
	private Label label;
	private QualifiedName qName;
	private String id;
	private Text text;

	public PropertiesTextField(Composite parent, String label, String id, int height) {
		this.label = new Label(parent, SWT.NONE);
		this.label.setText(label);
		this.id = id;
		this.qName = new QualifiedName(id, id);
		this.text = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.RESIZE);
		GridData gdm = new GridData(GridData.FILL_HORIZONTAL);
		gdm.heightHint = height;
		this.text.setLayoutData(gdm);
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

	public String getText() {
		return this.text.getText();
	}

	public void setText(String text) {
		this.text.setText(text);
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
