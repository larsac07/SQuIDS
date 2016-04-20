package autocisq.properties;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class PropertiesTextField extends PropertiesField {
	private final static String NL = System.lineSeparator();
	private Text text;

	public PropertiesTextField(Composite parent, String label, String id, int height) {
		super(parent, label, id);
		this.text = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.RESIZE);
		GridData gdm = new GridData(GridData.FILL_HORIZONTAL);
		gdm.heightHint = height;
		this.text.setLayoutData(gdm);
	}

	@Override
	public List<String> getValues() {
		return Arrays.asList(this.text.getText().split(NL));
	}

	@Override
	public void setValues(List<String> values) {
		this.text.setText(String.join(NL, values));
	}

}
