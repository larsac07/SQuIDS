package autocisq.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import autocisq.Activator;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private final static String KEY_MEASURES = "measures";
	private final static String KEY_LAYER_MAP = "layer_map";
	private final static String KEY_DB_OR_IO_CLASSES = "db_or_io_classes";

	public PreferencePage() {
		super(GRID);

	}

	@Override
	public void createFieldEditors() {
		// addField(new DirectoryFieldEditor("PATH", "&Directory preference:",
		// getFieldEditorParent()));
		// addField(
		// new BooleanFieldEditor("BOOLEAN_VALUE", "&An example of a boolean
		// preference", getFieldEditorParent()));
		//
		// addField(new RadioGroupFieldEditor("CHOICE", "An example of a
		// multiple-choice preference", 1,
		// new String[][] { { "&Choice 1", "choice1" }, { "C&hoice 2", "choice2"
		// } }, getFieldEditorParent()));
		// addField(new StringFieldEditor("MySTRING1", "A &text preference:",
		// getFieldEditorParent()));
		// addField(new StringFieldEditor("MySTRING2", "A &text preference:",
		// getFieldEditorParent()));
		addField(new MultilineStringFieldEditor(KEY_MEASURES, "Measures", getFieldEditorParent()));
		addField(new MultilineStringFieldEditor(KEY_LAYER_MAP, "Layer map", getFieldEditorParent()));
		addField(
				new MultilineStringFieldEditor(KEY_DB_OR_IO_CLASSES, "Database or IO classes", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("A demonstration of a preference page implementation");
	}
}