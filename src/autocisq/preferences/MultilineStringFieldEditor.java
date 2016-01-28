package autocisq.preferences;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class MultilineStringFieldEditor extends FieldEditor {

	private static final String ERROR_MESSAGE = "Multiline.error.message"; //$NON-NLS-1$
	public static final int VALIDATE_ON_KEY_STROKE = 0;
	public static final int VALIDATE_ON_FOCUS_LOST = 1;
	public static int UNLIMITED = -1;

	private boolean isValid;
	private String oldValue;
	private String compTitle;
	private Label title;
	private Text textField;
	private int textLimit = UNLIMITED;
	private String errorMessage;
	private boolean emptyStringAllowed = true;
	private int validateStrategy = VALIDATE_ON_KEY_STROKE;

	protected MultilineStringFieldEditor() {
	}

	public MultilineStringFieldEditor(String name, String labelText, int width, int strategy, Composite parent) {
		init(name, labelText);
		setTextLimit(width);
		setValidateStrategy(strategy);
		this.isValid = false;
		this.errorMessage = ERROR_MESSAGE;
		createControl(parent);
	}

	public MultilineStringFieldEditor(String name, String labelText, int width, Composite parent) {
		this(name, labelText, width, VALIDATE_ON_KEY_STROKE, parent);
		this.compTitle = labelText;
	}

	public MultilineStringFieldEditor(String name, String labelText, Composite parent) {
		this(name, labelText, UNLIMITED, parent);
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {
		GridData gd = (GridData) this.textField.getLayoutData();
		gd.horizontalSpan = numColumns - 1;
		// We only grab excess space if we have to
		// If another field editor has more columns then
		// we assume it is setting the width.
		gd.grabExcessHorizontalSpace = gd.horizontalSpan == 1;
	}

	protected boolean checkState() {
		boolean result = false;
		if (this.emptyStringAllowed)
			result = true;

		if (this.textField == null)
			result = false;

		String txt = this.textField.getText();

		if (txt == null)
			result = false;

		result = (txt.trim().length() > 0) || this.emptyStringAllowed;

		// call hook for subclasses
		result = result && doCheckState();

		if (result)
			clearErrorMessage();
		else
			showErrorMessage(this.errorMessage);

		return result;
	}

	protected boolean doCheckState() {
		return true;
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		this.title = new Label(parent, SWT.UP);
		this.title.setFont(parent.getFont());
		this.compTitle = getLabelText();
		this.title.setText(this.compTitle);
		this.title.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		this.textField = getTextControl(parent);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 100;
		gd.heightHint = 150;
		this.textField.setLayoutData(gd);

	}

	@Override
	protected void doLoad() {
		if (this.textField != null) {
			String value = getPreferenceStore().getString(getPreferenceName());
			this.textField.setText(value);
			this.oldValue = value;
		}
	}

	@Override
	protected void doLoadDefault() {
		if (this.textField != null) {
			String value = getPreferenceStore().getDefaultString(getPreferenceName());
			this.textField.setText(value);
		}
		valueChanged();
	}

	@Override
	protected void doStore() {
		getPreferenceStore().setValue(getPreferenceName(), this.textField.getText());
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

	@Override
	public int getNumberOfControls() {
		return 2;
	}

	public String getStringValue() {
		if (this.textField != null)
			return this.textField.getText();
		else
			return getPreferenceStore().getString(getPreferenceName());
	}

	protected Text getTextControl() {
		return this.textField;
	}

	public Text getTextControl(Composite parent) {
		if (this.textField == null) {
			this.textField = new Text(parent, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER | SWT.WRAP);
			this.textField.setFont(parent.getFont());
			switch (this.validateStrategy) {
				case VALIDATE_ON_KEY_STROKE:
					this.textField.addKeyListener(new KeyAdapter() {
						@Override
						public void keyPressed(KeyEvent e) {
							valueChanged();
						}
					});

					this.textField.addFocusListener(new FocusAdapter() {
						@Override
						public void focusGained(FocusEvent e) {
							refreshValidState();
						}

						@Override
						public void focusLost(FocusEvent e) {
							clearErrorMessage();
						}
					});
					break;
				case VALIDATE_ON_FOCUS_LOST:
					this.textField.addKeyListener(new KeyAdapter() {
						@Override
						public void keyPressed(KeyEvent e) {
							clearErrorMessage();
						}
					});
					this.textField.addFocusListener(new FocusAdapter() {
						@Override
						public void focusGained(FocusEvent e) {
							refreshValidState();
						}

						@Override
						public void focusLost(FocusEvent e) {
							valueChanged();
							clearErrorMessage();
						}
					});
					break;
				default:
					Assert.isTrue(false, "Unknown validate strategy"); //$NON-NLS-1$
			}
			this.textField.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent event) {
					MultilineStringFieldEditor.this.textField = null;
				}
			});
			if (this.textLimit > 0) { // Only set limits above 0 - see SWT spec
				this.textField.setTextLimit(this.textLimit);
			}
		} else {
			checkParent(this.textField, parent);
		}
		return this.textField;
	}

	public boolean isEmptyStringAllowed() {
		return this.emptyStringAllowed;
	}

	@Override
	public boolean isValid() {
		return this.isValid;
	}

	@Override
	protected void refreshValidState() {
		this.isValid = checkState();
	}

	public void setEmptyStringAllowed(boolean b) {
		this.emptyStringAllowed = b;
	}

	public void setErrorMessage(String message) {
		this.errorMessage = message;
	}

	@Override
	public void setFocus() {
		if (this.textField != null) {
			this.textField.setFocus();
		}
	}

	public void setStringValue(String value) {
		if (this.textField != null) {
			if (value == null)
				value = ""; //$NON-NLS-1$
			this.oldValue = this.textField.getText();
			if (!this.oldValue.equals(value)) {
				this.textField.setText(value);
				valueChanged();
			}
		}
	}

	public void setTextLimit(int limit) {
		this.textLimit = limit;
		if (this.textField != null)
			this.textField.setTextLimit(limit);
	}

	public void setValidateStrategy(int value) {
		Assert.isTrue(value == VALIDATE_ON_FOCUS_LOST || value == VALIDATE_ON_KEY_STROKE);
		this.validateStrategy = value;
	}

	public void showErrorMessage() {
		showErrorMessage(this.errorMessage);
	}

	protected void valueChanged() {
		setPresentsDefaultValue(false);
		boolean oldState = this.isValid;
		refreshValidState();

		if (this.isValid != oldState)
			fireStateChanged(IS_VALID, oldState, this.isValid);

		String newValue = this.textField.getText();
		if (!newValue.equals(this.oldValue)) {
			fireValueChanged(VALUE, this.oldValue, newValue);
			this.oldValue = newValue;
		}
	}

}