package autocisq.properties;

import java.util.LinkedList;
import java.util.List;

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
	public static final QualifiedName KEY_MEASURES = new QualifiedName("measures", "measures");

	private static final String LABEL_LAYER_MAP = "&Layer map:";
	public static final QualifiedName KEY_LAYER_MAP = new QualifiedName("layer_map", "layer_map");

	private static final String LABEL_DB_OR_IO_CLASSES = "&Classes with database or io operations:";
	public static final QualifiedName KEY_DB_OR_IO_CLASSES = new QualifiedName("db_or_io_classes", "db_or_io_classes");

	private static final int TEXT_AREA_HEIGHT = 5;

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
		this.measuresText = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.RESIZE);
		GridData gdm = new GridData(GridData.FILL_HORIZONTAL);
		gdm.heightHint = convertHeightInCharsToPixels(TEXT_AREA_HEIGHT);
		this.measuresText.setLayoutData(gdm);

		// LayerMap Label
		Label layerMapLabel = new Label(composite, SWT.NONE);
		layerMapLabel.setText(LABEL_LAYER_MAP);

		// LayerMap textfield
		this.layerMapText = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.RESIZE);
		GridData gdlm = new GridData(GridData.FILL_HORIZONTAL);
		gdlm.heightHint = convertHeightInCharsToPixels(TEXT_AREA_HEIGHT);
		this.layerMapText.setLayoutData(gdlm);

		// DbOrIoClasses Label
		Label dbOrIoClassesLabel = new Label(composite, SWT.NONE);
		dbOrIoClassesLabel.setText(LABEL_DB_OR_IO_CLASSES);

		// DbOrIoClasses textfield
		this.dbOrIoClassesText = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.RESIZE);
		GridData gddb = new GridData(GridData.FILL_HORIZONTAL);
		gddb.heightHint = convertHeightInCharsToPixels(TEXT_AREA_HEIGHT);
		this.dbOrIoClassesText.setLayoutData(gddb);

		// Populate fields
		try {
			String measures = ((IResource) getElement()).getPersistentProperty(KEY_MEASURES);
			this.measuresText.setText((measures != null) ? measures : "");
		} catch (CoreException e) {
			this.measuresText.setText("");
		}

		try {
			String layerMap = ((IResource) getElement()).getPersistentProperty(KEY_LAYER_MAP);
			this.layerMapText.setText((layerMap != null) ? layerMap : "");
		} catch (CoreException e) {
			this.layerMapText.setText("");
		}

		try {
			String dbOrIoClasses = ((IResource) getElement()).getPersistentProperty(KEY_DB_OR_IO_CLASSES);
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
		Composite composite = new Composite(parent, SWT.RESIZE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		GridData data = new GridData(GridData.BEGINNING);
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

		List<String> measureList = new LinkedList<>();
		measureList.add("autocisq.measure.maintainability.ClassTooManyChildren");
		measureList.add("autocisq.measure.maintainability.ContinueOrBreakOutsideSwitch");
		measureList.add("autocisq.measure.maintainability.FileLOC");
		measureList.add("autocisq.measure.maintainability.FileDuplicateTokens");
		measureList.add("autocisq.measure.maintainability.MethodCommentedOutInstructions");
		measureList.add("autocisq.measure.maintainability.MethodFanOut");
		measureList.add("autocisq.measure.maintainability.MethodParameters");
		measureList.add("autocisq.measure.maintainability.HardCodedLiteral");
		measureList.add("autocisq.measure.maintainability.HorizontalLayers");
		measureList.add("autocisq.measure.maintainability.LayerSkippingCall");
		measureList.add("autocisq.measure.maintainability.MethodTooManyDataOrFileOperations");
		measureList.add("autocisq.measure.maintainability.MethodDirectlyUsingFieldFromOtherClass");
		measureList.add("autocisq.measure.maintainability.VariableDeclaredPublic");
		measureList.add("autocisq.measure.maintainability.MethodCyclomaticComplexity");
		measureList.add("autocisq.measure.maintainability.MethodUnreachable");
		measureList.add("autocisq.measure.maintainability.ClassInheritanceLevel");
		measureList.add("autocisq.measure.maintainability.ClassCoupling");
		measureList.add("autocisq.measure.maintainability.CyclicCallBetweenPackages");
		measureList.add("autocisq.measure.maintainability.IndexModifiedWithinLoop");
		measureList.add("autocisq.measure.reliability.EmptyExceptionHandlingBlock");

		List<String> dbOrIoClassList = new LinkedList<>();
		dbOrIoClassList.add("java.io.File");
		dbOrIoClassList.add("java.nio.file.Files");
		dbOrIoClassList.add("java.sql.Connection");
		dbOrIoClassList.add("java.sql.DriverManager");
		dbOrIoClassList.add("java.sql.PreparedStatement");
		dbOrIoClassList.add("java.sql.Statement");
		dbOrIoClassList.add("com.github.javaparser.JavaParser");

		String nl = System.lineSeparator();

		String measures = "";
		for (String measure : measureList) {
			measures += measure + nl;
		}

		String dbOrIoClasses = "";
		for (String dbOrIoClass : dbOrIoClassList) {
			dbOrIoClasses += dbOrIoClass + nl;
		}

		this.measuresText.setText(measures);
		this.layerMapText.setText("");
		this.dbOrIoClassesText.setText(dbOrIoClasses);
	}

	@Override
	public boolean performOk() {
		// store the values
		try {
			((IResource) getElement()).setPersistentProperty(KEY_MEASURES, this.measuresText.getText());
			((IResource) getElement()).setPersistentProperty(KEY_LAYER_MAP, this.layerMapText.getText());
			((IResource) getElement()).setPersistentProperty(KEY_DB_OR_IO_CLASSES, this.dbOrIoClassesText.getText());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return true;
	}

}