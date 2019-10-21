package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import de.drazil.nerdsuite.model.CustomSize;

public class CustomFormatDialog extends TitleAreaDialog implements SelectionListener {

	private Spinner tileWidthSpinner;
	private Spinner tileHeightSpinner;
	private Spinner tileColumnsSpinner;
	private Spinner tileRowsSpinner;

	private CustomSize customSize;
	private boolean supportCustomSize;

	private List<IConfigurationListener> configurationListenerList = null;

	public CustomFormatDialog(Shell parentShell) {
		super(parentShell);
		configurationListenerList = new ArrayList<>();
	}

	public int open(CustomSize customSize, boolean supportCustomSize) {
		this.customSize = customSize;
		this.supportCustomSize = supportCustomSize;
		return super.open();
	}

	@Override
	public void create() {
		super.create();
		setTitle("Custom Format Setup");

		tileWidthSpinner.setSelection(customSize.getWidth());
		tileHeightSpinner.setSelection(customSize.getHeight());
		tileColumnsSpinner.setSelection(customSize.getTileColumns());
		tileRowsSpinner.setSelection(customSize.getTileRows());
	}

	public void addConfigurationListener(IConfigurationListener l) {
		configurationListenerList.add(l);
	}

	public void removeConfigurationListener(IConfigurationListener l) {
		configurationListenerList.remove(l);
	}

	private void fireConfigurationChanged() {
		customSize.setTileColumns(tileColumnsSpinner.getSelection());
		customSize.setTileRows(tileRowsSpinner.getSelection());
		customSize.setWidth(tileWidthSpinner.getSelection());
		customSize.setHeight(tileHeightSpinner.getSelection());
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		createTileWidth(container);
		createTileHeight(container);
		createTileColumns(container);
		createTileRows(container);
		return area;
	}

	private void createTileWidth(Composite container) {
		Label widthLabel = new Label(container, SWT.NONE);
		widthLabel.setText("Tile Width");

		GridData dataWidth = new GridData();
		dataWidth.grabExcessHorizontalSpace = true;
		dataWidth.horizontalAlignment = GridData.FILL;

		tileWidthSpinner = new Spinner(container, SWT.BORDER);
		tileWidthSpinner.setMinimum(customSize.getStorageEntity());
		tileWidthSpinner.setMaximum(1000);
		tileWidthSpinner.setSelection(customSize.getWidth());
		tileWidthSpinner.setIncrement(customSize.getStorageEntity());
		tileWidthSpinner.setPageIncrement(customSize.getStorageEntity());
		tileWidthSpinner.setLayoutData(dataWidth);
		tileWidthSpinner.addSelectionListener(this);
		tileWidthSpinner.setEnabled(supportCustomSize);
	}

	private void createTileHeight(Composite container) {
		Label heightLabel = new Label(container, SWT.NONE);
		heightLabel.setText("Tile Height");

		GridData dataHeight = new GridData();
		dataHeight.grabExcessHorizontalSpace = true;
		dataHeight.horizontalAlignment = GridData.FILL;

		tileHeightSpinner = new Spinner(container, SWT.BORDER);
		tileHeightSpinner.setMinimum(customSize.getHeight());
		tileHeightSpinner.setMaximum(1000);
		tileHeightSpinner.setSelection(customSize.getHeight());
		tileHeightSpinner.setIncrement(1);
		tileHeightSpinner.setPageIncrement(1);
		tileHeightSpinner.setLayoutData(dataHeight);
		tileHeightSpinner.addSelectionListener(this);
		tileHeightSpinner.setEnabled(supportCustomSize);
	}

	private void createTileColumns(Composite container) {
		Label tileColumnsLabel = new Label(container, SWT.NONE);
		tileColumnsLabel.setText("Tile Columns");

		GridData dataTileColumns = new GridData();
		dataTileColumns.grabExcessHorizontalSpace = true;
		dataTileColumns.horizontalAlignment = GridData.FILL;

		tileColumnsSpinner = new Spinner(container, SWT.BORDER);
		tileColumnsSpinner.setMinimum(1);
		tileColumnsSpinner.setMaximum(16);
		tileColumnsSpinner.setSelection(1);
		tileColumnsSpinner.setIncrement(1);
		tileColumnsSpinner.setPageIncrement(1);
		tileColumnsSpinner.setLayoutData(dataTileColumns);
		tileColumnsSpinner.addSelectionListener(this);
	}

	private void createTileRows(Composite container) {
		Label tileRowsLabel = new Label(container, SWT.NONE);
		tileRowsLabel.setText("Tile Rows");

		GridData dataTileRows = new GridData();
		dataTileRows.grabExcessHorizontalSpace = true;
		dataTileRows.horizontalAlignment = GridData.FILL;

		tileRowsSpinner = new Spinner(container, SWT.BORDER);
		tileRowsSpinner.setMinimum(1);
		tileRowsSpinner.setMaximum(16);
		tileRowsSpinner.setSelection(1);
		tileRowsSpinner.setIncrement(1);
		tileRowsSpinner.setPageIncrement(1);
		tileRowsSpinner.setLayoutData(dataTileRows);
		tileRowsSpinner.addSelectionListener(this);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	private void saveInput() {

	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		fireConfigurationChanged();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub

	}
}