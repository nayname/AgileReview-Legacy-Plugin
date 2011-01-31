package de.tukl.cs.softech.agilereview.views.detail;

import org.apache.xmlbeans.XmlObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbenchPart;

import de.tukl.cs.softech.agilereview.plugincontrol.CommentController;

/**
 * Abstract class of a Comment or Review representation, which automatically provides IPartListener
 * and FocusListener to save all modified data persistently. Furthermore a listener implementation is provided
 * for a revert action by setting the object Data to "revert" and for a save action by setting the object Data to "save"
 * @param <E> type which would be displayed by this AbstractDetail
 */
public abstract class AbstractDetail<E extends XmlObject> extends Composite implements FocusListener, Listener {
	
	/**
	 * Button which should save the changes of the current displayed object
	 */
	protected Button saveButton;
	/**
	 * Button which should revert the changes of the current displayed object
	 */
	protected Button revertButton;
	
	/**
	 * current displayed object which will be modified
	 */
	protected E editedObject;
	/**
	 * backup of the current displayed object
	 */
	protected E backupObject;

	/**
	 * Creates a new AbstractDetail Composite onto the given parent with the specified SWT styles
	 * @param parent onto the ReviewDetail Composite will be added
	 * @param style with which this Composite will be styled
	 */
	protected AbstractDetail(Composite parent, int style) {
		super(parent, style);
		initUI();
		
		revertButton.setText("Revert");
	    revertButton.setData("revert");
	    revertButton.setEnabled(false);
	    revertButton.addListener(SWT.Selection, this);
	    
	    saveButton.setText("Apply");
	    saveButton.setData("save");
	    saveButton.addListener(SWT.Selection, CommentController.getInstance());
	}

	/**
	 * this method will be automatically called by the constructor and should
	 * contain the initialization of the UI especially of the saveButton and revertButton
	 */
	protected abstract void initUI();
	
	/**
	 * saveChanges will be called by the IPartListener and FocusListener and should contain
	 * a save routine on object level of the current modified data
	 * @return true, if changes are done and write back is necessary,<br>false, if no changes have been made
	 */
	protected abstract boolean saveChanges();
	
	/**
	 * fills all contents of the given input into the detail view
	 * @param input which should be displayed
	 */
	protected abstract void fillContents(E input);
	
	/**
	 * saves every changes made in the current Detail View
	 * @param part will be forwarded from the {@link DetailView}
	 * @see org.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.IWorkbenchPartReference)
	 */
	public void partClosedOrDeactivated(IWorkbenchPart part) {
		saveChanges();
		//fire "save" event for persistent storage
		saveButton.notifyListeners(SWT.Selection, new Event());
		revertButton.setEnabled(false);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Composite#setFocus()
	 */
	public abstract boolean setFocus();
	
	/**
	 * not in use
	 * @see org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events.FocusEvent)
	 */
	@Override
	public void focusGained(FocusEvent e) {
		
	}

	/**
	 * save current changes in objects
	 * @see org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events.FocusEvent)
	 */
	@Override
	public void focusLost(FocusEvent e) {
		if(saveChanges()) {
			revertButton.setEnabled(true);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
	 */
	@Override/*?agilereview|reuter|c10?*/
	public void handleEvent(Event event) {
		if(event.widget.getData().equals("revert")) {
			@SuppressWarnings("unchecked")
			E copy = (E)backupObject.copy();
			this.editedObject.set(copy);
			fillContents(backupObject);
			revertButton.setEnabled(false);
		}
	}
}