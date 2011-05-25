package de.tukl.cs.softech.agilereview.preferences.lang;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;

import de.tukl.cs.softech.agilereview.tools.PropertiesManager;

/**
 * The TableFieldEditor represents the configurable table of languages supported by AgileReview
 */
public class TableFieldEditor extends FieldEditor {
	
	/**
	 * Instance of PropertiesManager
	 */
	private static PropertiesManager pm = PropertiesManager.getInstance();
	/**
	 * TableViewer managing the table
	 */
	private TableViewer table;
	
	/**
	 * Creates a new TableFieldEditor
	 * @param name
	 * @param labelText
	 * @param parent
	 */
	public TableFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
	}

	/**
	 * not yet used
	 * @see org.eclipse.jface.preference.FieldEditor#adjustForNumColumns(int)
	 */
	@Override
	protected void adjustForNumColumns(int numColumns) {
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {		
		Label label = new Label(parent, SWT.WRAP);
		label.setText("More than one fileendings in one cell should be managed by comma separation." +
				"The begin and end tag should specify the tags of a multiline comment in the corresponding programming " +
				"language.");
		GridData gd = new GridData();
        gd.horizontalSpan = numColumns;
        gd.horizontalAlignment = GridData.FILL;
        gd.widthHint = parent.getSize().x;
        label.setLayoutData(gd);
		
		table = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		createColumn("fileendings",200,0);
        createColumn("begin tag",100,1);
        createColumn("end tag",100,2);
        table.setColumnProperties(new String[]{"fileendings","begin tag", "end tag"});
        table.getTable().setLinesVisible(true);
        table.getTable().setHeaderVisible(true);
        table.setContentProvider(new FileendingContentProvider());
        
        gd = new GridData();
        gd.horizontalSpan = numColumns;
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        table.getTable().setLayoutData(gd);
	}

	@Override
	protected void doLoad() {
		table.setInput(pm.getParserFileendingsAndTags());
	}

	@Override
	protected void doLoadDefault() {
		table.setInput(pm.getParserFileendingsAndTags());
	}

	@Override
	protected void doStore() {
		//TODO
	}

	@Override
	public int getNumberOfControls() {
		// TODO Auto-generated method stub
		return 1;
	}

	/**
	 * Creates a single column of the viewer with given parameters 
	 * @param title The title to be set
	 * @param bound The width of the column
	 * @param colNumber The columns number
	 */
	private void createColumn(String title, int bound, final int colNumber) {
		TableViewerColumn viewerColumn = new TableViewerColumn(table, SWT.NONE);
		viewerColumn.setEditingSupport(new FileendingEditingSupport(table));
		TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		
		viewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				String result = "";
				if(element instanceof SupportedLanguageEntity) {
					switch(colNumber) {
					case 0:
						result = ((SupportedLanguageEntity)element).getFileendingsAsString();
						break;
					case 1:
						result = ((SupportedLanguageEntity)element).getBeginTag();
						break;
					case 2:
						result = ((SupportedLanguageEntity)element).getEndTag();
						break;
					}
				}
				return result;
			}
		});
	}
}
