package de.tukl.cs.softech.agilereview.dataaccess;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlTokenSource;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;

import de.tukl.cs.softech.agilereview.tools.PluginLogger;

import agileReview.softech.tukl.de.CommentsDocument;
import agileReview.softech.tukl.de.ReviewDocument;

/**
 * Model which holds the files in which the comments and reviews are stored and provides saving functions
 */
class ReviewFileModel {
	
	/**
	 * Maps the files to the corresponding review document (for saving)
	 */
	private HashMap<IFile, ReviewDocument> xmlReviewDocuments = new HashMap<IFile, ReviewDocument>();
	
	/**
	 * Maps the file-paths to the corresponding comment document (for saving)
	 */
	private HashMap<IFile, CommentsDocument> xmlCommentDocuments = new HashMap<IFile, CommentsDocument>(); 

	
	/**
	 * Saving method for a given XML document / File pair 
	 * @param document
	 * @param filePath
	 * @throws IOException
	 */
	private void save(XmlTokenSource document, IFile filePath) throws IOException
	{
		document.save(filePath.getLocation().toFile(), new XmlOptions().setSavePrettyPrint());
		try {
			filePath.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			PluginLogger.logError(ReviewAccess.class.toString(), "save", "CoreException while saving "+filePath.getLocation().toOSString(), e);
		}
	}
	
	/**
	 * Deletes the given file
	 * @param delFile
	 */
	private void deleteResource(IResource delFile)
	{
		try {
			delFile.delete(true, null);
		} catch (CoreException e) {
			MessageDialog.openWarning(null, "Warning: Could not delete file or folder", "File \""+delFile.getLocation().toOSString()+"\" could not be deleted");
			System.out.println("File \""+delFile.getLocation().toOSString()+"\" could not be deleted");
		}
	}
	
	////////////
	// Setter //
	////////////
	
	/**
	 * Adds the given Xml document / File pair to this model
	 * @param doc 
	 * @param path 
	 */
	protected void addXmlDocument(XmlTokenSource doc, IFile path)
	{
		if (doc instanceof ReviewDocument)
		{
			this.xmlReviewDocuments.put(path, (ReviewDocument)doc);
		}
		else if (doc instanceof CommentsDocument)
		{
			this.xmlCommentDocuments.put(path, (CommentsDocument)doc);
		}
		
	}
	
	/**
	 * Removes this file from the model
	 * @param file
	 */
	protected void removeXmlDocument(IFile file)
	{
		// Delete the given file
		this.deleteResource(file);
		// Try the comments first
		if (this.xmlCommentDocuments.remove(file)==null && this.xmlReviewDocuments.remove(file)!=null)
		{
			// Get the parent Folder
			IResource delFolder = file.getParent();
			
			// Delete all files in the review folder
			if (delFolder instanceof IFolder) {
				try {
					for (IResource f : ((IFolder)delFolder).members())
					{
						if (f instanceof IFile)
						this.removeXmlDocument((IFile)f);
					}
				} catch (CoreException e) {
					PluginLogger.logError(ReviewAccess.class.toString(), "removeXmlDocument", "CoreException while removing "+file.getLocation().toOSString()+" from model", e);
				}

				// Delete the folder afterwards
				this.deleteResource(delFolder);
			}
		}
	}
	
	/**
	 * Clears this model
	 */
	protected void clearModel()
	{
		PluginLogger.log(this.getClass().toString(), "clearModel", "Review and Comment file model cleared");
		this.xmlReviewDocuments.clear();
		this.xmlCommentDocuments.clear();
	}
	
	/**
	 * Saves the given File
	 * @param f
	 * @throws IOException
	 */
	protected void save(IFile f) throws IOException
	{
		XmlTokenSource document = null;
		// Try comment-file
		document = this.xmlCommentDocuments.get(f);
		if (document == null)
		{
			document = this.xmlReviewDocuments.get(f);
		}
		
		if (document != null)
		{
			this.save(document, f);	
		}
	}
	
	/**
	 * Saves all files of this model
	 * @throws IOException
	 */
	protected void saveAll() throws IOException
	{
		// First the reviews
		for (Entry<IFile, ReviewDocument> currEntry :this.xmlReviewDocuments.entrySet())
		{
			this.save(currEntry.getValue(), currEntry.getKey());
		}
		
		// Then the comments
		for (Entry<IFile, CommentsDocument> currEntry :this.xmlCommentDocuments.entrySet())
		{
			this.save(currEntry.getValue(), currEntry.getKey());
		}
	}
	
	////////////
	// Getter //
	////////////
	/**
	 * Returns the Comments document which is represented by the given file
	 * @param file 
	 * @return Comments document represented by this file
	 */
	protected CommentsDocument getCommentsDoc(IFile file)
	{
		return this.xmlCommentDocuments.get(file);
	}
	
	/**
	 * Checks whether this file is stored in this model
	 * @param file
	 * @return <i>true</i> if this file is stored in this model, <i>false</i> otherwise
	 */
	protected boolean containsFile(IFile file)
	{
		return this.xmlCommentDocuments.containsKey(file) || this.xmlReviewDocuments.containsKey(file);
	}
	
	/**
	 * Returns all stored CommentsDocuments
	 * @return all stored CommentsDocuments
	 */
	protected Collection<CommentsDocument> getAllCommentsDocument()
	{
		return this.xmlCommentDocuments.values();
	}
	
	/**
	 * Returns all stored ReviewDocuments
	 * @return all stored ReviewDocuments
	 */
	protected Collection<ReviewDocument> getAllReviewDocument()
	{
		return this.xmlReviewDocuments.values();
	}
}
