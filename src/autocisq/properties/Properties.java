package autocisq.properties;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
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
	public static final String KEY_DB_OR_IO_CLASSES = "db_or_io_classes";

	private static final String LABEL_IGNORE_FILTER = "&Files/folders to ignore:";
	public static final String KEY_IGNORE_FILTER = "ignore_filter";

	private static final int TEXT_AREA_HEIGHT = 5;

	private Map<String, PropertiesTextField> properties;

	/**
	 * Constructor for SamplePropertyPage.
	 */
	public Properties() {
		super();
		this.properties = new LinkedHashMap<>();
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

		int height = convertHeightInCharsToPixels(TEXT_AREA_HEIGHT);

		// Measures properties
		PropertiesTextField measures = new PropertiesTextField(composite, LABEL_MEASURES, KEY_MEASURES, height);

		// LayerMap properties
		PropertiesTextField layerMap = new PropertiesTextField(composite, LABEL_LAYER_MAP, KEY_LAYER_MAP, height);

		// DbOrIoClasses properties
		PropertiesTextField dbOrIoClasses = new PropertiesTextField(composite, LABEL_DB_OR_IO_CLASSES,
				KEY_DB_OR_IO_CLASSES, height);

		// IgnoreFilter properties
		PropertiesTextField ignoreFilter = new PropertiesTextField(composite, LABEL_IGNORE_FILTER, KEY_IGNORE_FILTER,
				height);

		this.properties.put(measures.getId(), measures);
		this.properties.put(layerMap.getId(), layerMap);
		this.properties.put(dbOrIoClasses.getId(), dbOrIoClasses);
		this.properties.put(ignoreFilter.getId(), ignoreFilter);

		// Populate fields
		for (String key : this.properties.keySet()) {
			PropertiesTextField property = this.properties.get(key);
			try {
				String text = ((IResource) getElement()).getPersistentProperty(property.getqName());
				property.setText(text);
			} catch (CoreException e) {
				property.setText("");
			}
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

		this.properties.get(KEY_MEASURES).setText(measures);
		this.properties.get(KEY_LAYER_MAP).setText("");
		this.properties.get(KEY_DB_OR_IO_CLASSES).setText(dbOrIoClasses);
	}

	@Override
	public boolean performOk() {
		// store the values
		for (String key : this.properties.keySet()) {
			try {
				PropertiesTextField property = this.properties.get(key);
				((IResource) getElement()).setPersistentProperty(property.getqName(), property.getText());
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

}