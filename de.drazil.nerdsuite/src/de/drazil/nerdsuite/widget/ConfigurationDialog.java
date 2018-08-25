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

public class ConfigurationDialog extends TitleAreaDialog implements SelectionListener {

	private Spinner tileWidthSpinner;
	private Spinner tileHeightSpinner;
	private Spinner tileColumnsSpinner;
	private Spinner tileRowsSpinner;
	private Spinner painterPixelSizeSpinner;
	private Spinner selectorPixelSizeSpinner;

	private int tileWidth;
	private int tileHeight;
	private int tileColumns;
	private int tileRows;
	private int painterPixelSize;
	private int selectorPixelSize;

	private List<IConfigurationListener> configurationListenerList = null;

	public ConfigurationDialog(Shell parentShell) {
		super(parentShell);
		configurationListenerList = new ArrayList<>();
	}

	@Override
	public int open() {
		// TODO Auto-generated method stub
		return super.open();
	}

	@Override
	public void create() {
		super.create();
		setTitle("Custom Image Configuration");

		// setMessage("");
		tileWidthSpinner.setSelection(tileWidth);
		tileHeightSpinner.setSelection(tileHeight);
		tileColumnsSpinner.setSelection(tileColumns);
		tileRowsSpinner.setSelection(tileRows);
		painterPixelSizeSpinner.setSelection(painterPixelSize);
		selectorPixelSizeSpinner.setSelection(selectorPixelSize);
	}

	public void addConfigurationListener(IConfigurationListener l) {
		configurationListenerList.add(l);
	}

	public void removeConfigurationListener(IConfigurationListener l) {
		configurationListenerList.remove(l);
	}

	private void fireConfigurationChanged() {

		for (IConfigurationListener cl : configurationListenerList) {
			cl.configurationChanged(tileWidthSpinner.getSelection(), tileHeightSpinner.getSelection(),
					tileColumnsSpinner.getSelection(), tileRowsSpinner.getSelection(),
					painterPixelSizeSpinner.getSelection(), selectorPixelSizeSpinner.getSelection(), 0, 0, 0);
		}
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
		createPainterPixelSize(container);
		createSelectorPixelSize(container);
		return area;

	}

	private void createTileWidth(Composite container) {
		Label widthLabel = new Label(container, SWT.NONE);
		widthLabel.setText("Tile Width");

		GridData dataWidth = new GridData();
		dataWidth.grabExcessHorizontalSpace = true;
		dataWidth.horizontalAlignment = GridData.FILL;

		tileWidthSpinner = new Spinner(container, SWT.BORDER);
		tileWidthSpinner.setMinimum(8);
		tileWidthSpinner.setMaximum(1000);
		tileWidthSpinner.setSelection(8);
		tileWidthSpinner.setIncrement(8);
		tileWidthSpinner.setPageIncrement(8);
		tileWidthSpinner.setLayoutData(dataWidth);
		tileWidthSpinner.addSelectionListener(this);
	}

	private void createTileHeight(Composite container) {
		Label heightLabel = new Label(container, SWT.NONE);
		heightLabel.setText("Tile Height");

		GridData dataHeight = new GridData();
		dataHeight.grabExcessHorizontalSpace = true;
		dataHeight.horizontalAlignment = GridData.FILL;

		tileHeightSpinner = new Spinner(container, SWT.BORDER);
		tileHeightSpinner.setMinimum(8);
		tileHeightSpinner.setMaximum(1000);
		tileHeightSpinner.setSelection(8);
		tileHeightSpinner.setIncrement(1);
		tileHeightSpinner.setPageIncrement(1);
		tileHeightSpinner.setLayoutData(dataHeight);
		tileHeightSpinner.addSelectionListener(this);
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

	private void createPainterPixelSize(Composite container) {
		Label painterPixelSizeLabel = new Label(container, SWT.NONE);
		painterPixelSizeLabel.setText("Painter Pixel Size");

		GridData dataPainterPixelSize = new GridData();
		dataPainterPixelSize.grabExcessHorizontalSpace = true;
		dataPainterPixelSize.horizontalAlignment = GridData.FILL;

		painterPixelSizeSpinner = new Spinner(container, SWT.BORDER);
		painterPixelSizeSpinner.setMinimum(8);
		painterPixelSizeSpinner.setMaximum(40);
		painterPixelSizeSpinner.setSelection(1);
		painterPixelSizeSpinner.setIncrement(1);
		painterPixelSizeSpinner.setPageIncrement(1);
		painterPixelSizeSpinner.setLayoutData(dataPainterPixelSize);
		painterPixelSizeSpinner.addSelectionListener(this);
	}

	private void createSelectorPixelSize(Composite container) {
		Label selectorPixelSizeLabel = new Label(container, SWT.NONE);
		selectorPixelSizeLabel.setText("Selector Pixel Size");

		GridData dataSelectorPixelSize = new GridData();
		dataSelectorPixelSize.grabExcessHorizontalSpace = true;
		dataSelectorPixelSize.horizontalAlignment = GridData.FILL;

		selectorPixelSizeSpinner = new Spinner(container, SWT.BORDER);
		selectorPixelSizeSpinner.setMinimum(2);
		selectorPixelSizeSpinner.setMaximum(16);
		selectorPixelSizeSpinner.setSelection(1);
		selectorPixelSizeSpinner.setIncrement(1);
		selectorPixelSizeSpinner.setPageIncrement(1);
		selectorPixelSizeSpinner.setLayoutData(dataSelectorPixelSize);
		selectorPixelSizeSpinner.addSelectionListener(this);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	// save content of the Text fields because they get disposed
	// as soon as the Dialog closes
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

	public void setConfiguration(int tileWidth, int tileHeight, int tileColumns, int tileRows, int painterPixelSize,
			int selectorPixelSize) {
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.tileColumns = tileColumns;
		this.tileRows = tileRows;
		this.painterPixelSize = painterPixelSize;
		this.selectorPixelSize = selectorPixelSize;
	}

}
