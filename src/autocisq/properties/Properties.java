package autocisq.properties;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
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

	private static final String LABEL_APPLY_TO_ALL = "Apply to all projects";

	private static final int TEXT_AREA_HEIGHT = 5;

	private static final String NL = System.lineSeparator();

	private Map<String, PropertiesField> properties;
	private List<String> measures;
	private List<String> dbOrIoClasses;

	/**
	 * Constructor for SamplePropertyPage.
	 */
	public Properties() {
		super();
		this.properties = new LinkedHashMap<>();
		createDefaults();
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
		PropertiesField measures = new PropertiesChecklistField(composite, LABEL_MEASURES, KEY_MEASURES, height,
				this.measures);

		// LayerMap properties
		PropertiesField layerMap = new PropertiesTextField(composite, LABEL_LAYER_MAP, KEY_LAYER_MAP, height);

		// DbOrIoClasses properties
		PropertiesField dbOrIoClasses = new PropertiesTextField(composite, LABEL_DB_OR_IO_CLASSES, KEY_DB_OR_IO_CLASSES,
				height);

		// IgnoreFilter properties
		PropertiesField ignoreFilter = new PropertiesTextField(composite, LABEL_IGNORE_FILTER, KEY_IGNORE_FILTER,
				height);

		Button applyToAll = new Button(composite, SWT.PUSH);
		applyToAll.setText(LABEL_APPLY_TO_ALL);
		applyToAll.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				switch (event.type) {
					case SWT.Selection:
						applyToAll();
				}
			}
		});

		this.properties.put(measures.getId(), measures);
		this.properties.put(layerMap.getId(), layerMap);
		this.properties.put(dbOrIoClasses.getId(), dbOrIoClasses);
		this.properties.put(ignoreFilter.getId(), ignoreFilter);

		// Populate fields
		for (String key : this.properties.keySet()) {
			PropertiesField property = this.properties.get(key);
			try {
				String text = ((IResource) getElement()).getPersistentProperty(property.getqName());
				property.setValues(Arrays.asList(text.split(NL)));
			} catch (CoreException | IllegalArgumentException e) {
				property.setValues(new LinkedList<>());
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

		this.properties.get(KEY_MEASURES).setValues(this.measures);
		this.properties.get(KEY_LAYER_MAP).setValues(new LinkedList<>());
		this.properties.get(KEY_DB_OR_IO_CLASSES).setValues(this.dbOrIoClasses);
		this.properties.get(KEY_IGNORE_FILTER).setValues(new LinkedList<>());
	}

	@Override
	public boolean performOk() {
		// store the values
		IResource resource = (IResource) getElement();
		applySettings(resource);
		return true;
	}

	protected void applyToAll() {
		boolean apply = MessageDialog.openConfirm(getShell(), "Apply settings to all projects",
				"Apply settings to all projects?");
		if (apply) {
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for (IProject project : projects) {
				applySettings(project);
			}
		}
	}

	private void applySettings(IResource resource) {
		for (String key : this.properties.keySet()) {
			try {
				PropertiesField property = this.properties.get(key);
				String propertyValue = String.join(NL, property.getValues());
				resource.setPersistentProperty(property.getqName(), propertyValue);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	private void createDefaults() {
		this.measures = new LinkedList<>();
		this.measures.add("autocisq.measure.maintainability.CISQMM04FileDuplicateTokens");
		this.measures.add("autocisq.measure.maintainability.CISQMM05MethodUnreachable");
		this.measures.add("autocisq.measure.maintainability.CISQMM06ClassInheritanceLevel");
		this.measures.add("autocisq.measure.maintainability.CISQMM07ClassChildren");
		this.measures.add("autocisq.measure.maintainability.CISQMM09MethodDirectlyUsingFieldFromOtherClass");
		this.measures.add("autocisq.measure.maintainability.CISQMM10VariableDeclaredPublic");
		this.measures.add("autocisq.measure.maintainability.CISQMM11MethodFanOut");
		this.measures.add("autocisq.measure.maintainability.CISQMM12ClassCoupling");
		this.measures.add("autocisq.measure.maintainability.CISQMM13CyclicCallBetweenPackages");
		this.measures.add("autocisq.measure.maintainability.CISQMM14MethodCommentedOutInstructions");
		this.measures.add("autocisq.measure.maintainability.CISQMM15FileLOC");
		this.measures.add("autocisq.measure.maintainability.CISQMM16IndexModifiedWithinLoop");
		this.measures.add("autocisq.measure.maintainability.CISQMM17ContinueOrBreakOutsideSwitch");
		this.measures.add("autocisq.measure.maintainability.CISQMM18MethodCyclomaticComplexity");
		this.measures.add("autocisq.measure.maintainability.CISQMM20MethodParameters");
		this.measures.add("autocisq.measure.maintainability.CISQMM21HardCodedLiteral");

		this.dbOrIoClasses = new LinkedList<>();
		this.dbOrIoClasses.add("java.io.*");
		this.dbOrIoClasses.add("java.nio.*");
		this.dbOrIoClasses.add("java.sql.*");
		this.dbOrIoClasses.add("com.github.javaparser.JavaParser");
	}

}