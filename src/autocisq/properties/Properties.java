package autocisq.properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

public class Properties extends PropertyPage {

	private static final String PROJECT_LABEL = "Project:";

	private static final String LABEL_MEASURES = "&Measures:";
	public static final String KEY_MEASURES = "measures";

	private static final String LABEL_LAYER_MAP = "&Layer map:";
	public static final String KEY_LAYER_MAP = "layer_map";

	private static final String LABEL_DB_OR_IO_CLASSES = "&Classes with database or io operations:";
	public static final String KEY_DB_OR_IO_CLASSES = "measures";

	private static final int TEXT_FIELD_WIDTH = 50;
	private static final int TEXT_FIELD_HEIGHT = 10;

	private Text measuresText;
	private Text layerMapText;
	private Text dbOrIoClassesText;

	/**
	 * Constructor for SamplePropertyPage.
	 */
	public Properties() {
		super();
	}

	private void addHeaderSection(Composite parent) {
		Composite composite = createDefaultComposite(parent);

		// Label for path field
		Label pathLabel = new Label(composite, SWT.NONE);
		pathLabel.setText(PROJECT_LABEL);

		// Path text field
		Text pathValueText = new Text(composite, SWT.WRAP | SWT.READ_ONLY);
		pathValueText.setText(((IResource) getElement()).getFullPath().toString());
	}

	private void addSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		separator.setLayoutData(gridData);
	}

	private void addInputSection(Composite parent) {
		Composite composite = createDefaultComposite(parent);

		// Measures Label
		Label measuresLabel = new Label(composite, SWT.NONE);
		measuresLabel.setText(LABEL_MEASURES);

		// Measures textfield
		this.measuresText = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gd = new GridData();
		gd.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
		gd.heightHint = convertHeightInCharsToPixels(TEXT_FIELD_HEIGHT);
		this.measuresText.setLayoutData(gd);

		// LayerMap Label
		Label layerMapLabel = new Label(composite, SWT.NONE);
		layerMapLabel.setText(LABEL_LAYER_MAP);

		// LayerMap textfield
		this.layerMapText = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gdlm = new GridData();
		gdlm.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
		gdlm.heightHint = convertHeightInCharsToPixels(TEXT_FIELD_HEIGHT);
		this.layerMapText.setLayoutData(gdlm);

		// DbOrIoClasses Label
		Label dbOrIoClassesLabel = new Label(composite, SWT.NONE);
		dbOrIoClassesLabel.setText(LABEL_DB_OR_IO_CLASSES);

		// DbOrIoClasses textfield
		this.dbOrIoClassesText = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gddb = new GridData();
		gddb.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
		gddb.heightHint = convertHeightInCharsToPixels(TEXT_FIELD_HEIGHT);
		this.dbOrIoClassesText.setLayoutData(gddb);

		// Populate fields
		try {
			String measures = ((IResource) getElement())
					.getPersistentProperty(new QualifiedName(KEY_MEASURES, KEY_MEASURES));
			this.measuresText.setText((measures != null) ? measures : "");
		} catch (CoreException e) {
			this.measuresText.setText("");
		}

		try {
			String layerMap = ((IResource) getElement())
					.getPersistentProperty(new QualifiedName(KEY_LAYER_MAP, KEY_LAYER_MAP));
			this.layerMapText.setText((layerMap != null) ? layerMap : "");
		} catch (CoreException e) {
			this.layerMapText.setText("");
		}

		try {
			String dbOrIoClasses = ((IResource) getElement())
					.getPersistentProperty(new QualifiedName(KEY_DB_OR_IO_CLASSES, KEY_DB_OR_IO_CLASSES));
			this.dbOrIoClassesText.setText((dbOrIoClasses != null) ? dbOrIoClasses : "");
		} catch (CoreException e) {
			this.dbOrIoClassesText.setText("");
		}
	}

	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		addHeaderSection(composite);
		addSeparator(composite);
		addInputSection(composite);
		return composite;
	}

	private Composite createDefaultComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);

		return composite;
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		this.measuresText.setText("");
		this.layerMapText.setText("");
		this.dbOrIoClassesText.setText("");
	}

	@Override
	public boolean performOk() {
		// store the values
		try {
			((IResource) getElement()).setPersistentProperty(new QualifiedName(KEY_MEASURES, KEY_MEASURES),
					this.measuresText.getText());
			((IResource) getElement()).setPersistentProperty(new QualifiedName(KEY_LAYER_MAP, KEY_LAYER_MAP),
					this.layerMapText.getText());
			((IResource) getElement()).setPersistentProperty(
					new QualifiedName(KEY_DB_OR_IO_CLASSES, KEY_DB_OR_IO_CLASSES), this.dbOrIoClassesText.getText());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return true;
	}

}