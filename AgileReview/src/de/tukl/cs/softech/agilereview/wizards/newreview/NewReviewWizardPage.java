package de.tukl.cs.softech.agilereview.wizards.newreview;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.tukl.cs.softech.agilereview.dataaccess.ReviewAccess;
import de.tukl.cs.softech.agilereview.tools.PropertiesManager;

/**
 * The single page of the NewReview Wizard
 */
public class NewReviewWizardPage extends WizardPage implements ModifyListener {
    
    /**
     * the text field for retrieving the id
     */
    private Text id;
    /**
     * the text field for retrieving the reference
     */
    private Text reference;
    /**
     * the text field for retrieving the responsible person
     */
    private Text responsibility;
    /**
     * the text field for retrieving the description
     */
    private StyledText description;
    
    /**
     * Creates a new page
     */
    protected NewReviewWizardPage() {
        super("New Review");
        setTitle("New Review");
        setDescription("This wizard creates a new Review.");
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 2;
        Label lReviewId = new Label(container, SWT.NULL);
        lReviewId.setText("Review-Name*:");
        
        id = new Text(container, SWT.BORDER | SWT.SINGLE);
        id.setText("");
        id.setToolTipText("Review-Name must be set.");
        id.addModifyListener(this);
        
        // external reference
        Label lReference = new Label(container, SWT.NULL);
        lReference.setText("Reference:");
        reference = new Text(container, SWT.BORDER | SWT.SINGLE);
        
        // responsibility
        Label lResponsibility = new Label(container, SWT.NULL);
        lResponsibility.setText("Responsibility:");
        responsibility = new Text(container, SWT.BORDER | SWT.SINGLE);
        
        // description
        Label lDescription = new Label(container, SWT.NULL);
        lDescription.setText("Description:");
        description = new StyledText(container, SWT.PUSH | SWT.V_SCROLL | SWT.BORDER);
        description.setEditable(true);
        description.setWordWrap(true);
        
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        id.setLayoutData(gd);
        reference.setLayoutData(gd);
        responsibility.setLayoutData(gd);
        description.setLayoutData(new GridData(GridData.FILL_BOTH));
        // Required to avoid an error in the system
        setControl(container);
        this.modifyText(null);
        setErrorMessage(null);
    }
    
    /**
     * @return the review ID entered
     */
    protected String getReviewID() {
        return this.id.getText().trim();
    }
    
    /**
     * @return the review reference entered
     */
    protected String getReviewReference() {
        return this.reference.getText().trim();
    }
    
    /**
     * @return the responsible person entered
     */
    protected String getReviewResponsibility() {
        return this.responsibility.getText().trim();
    }
    
    /**
     * @return the review description entered
     */
    protected String getReviewDescription() {
        return this.description.getText().trim();
    }
    
    @Override
    public void modifyText(ModifyEvent e) {
        String validMessage = PropertiesManager.getInstance().isValid(id.getText());
        if (validMessage == null) {
            // Try if reviewId is already existent
            if (!ReviewAccess.getInstance().reviewExists(this.id.getText())) {
                setErrorMessage(null);
                setPageComplete(true);
            } else {
                setErrorMessage("ReviewId already in use");
                setPageComplete(false);
            }
        } else {
            setErrorMessage("* " + validMessage);
            setPageComplete(false);
        }
    }
}