package de.tukl.cs.softech.agilereview.views;

import java.util.HashSet;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener3;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

import de.tukl.cs.softech.agilereview.tools.PluginLogger;
import de.tukl.cs.softech.agilereview.views.commenttable.CommentTableView;
import de.tukl.cs.softech.agilereview.views.detail.DetailView;


/**
 * The ViewControl provides information about the current displayed ViewPart of
 * this plugin. Furthermore the ViewControl provides and forwards events of the
 * following Listener: {@link ISelectionListener}, {@link IPartListener2}, {@link IPerspectiveListener3}
 */
public class ViewControl implements ISelectionChangedListener, IPartListener2, IPerspectiveListener3 {
	
	/**
	 * Set of all active Views
	 */
	private static HashSet<Class<? extends IWorkbenchPart>> activeViews = new HashSet<Class<? extends IWorkbenchPart>>();
	/**
	 * contextActivation for later deactivating
	 */
	private static IContextActivation contextActivation;
	/**
	 * Instance of ViewControl in order to add all listeners
	 */
	private static ViewControl instance = new ViewControl();
	
	/**
	 * Singleton Pattern
	 * @return the only instance of ViewControl
	 */
	public static ViewControl getInstance() {
		return instance;
	}
	
	/**
	 * Creates a new instance of ViewControl
	 */
	private ViewControl() {
		Display.getCurrent().asyncExec(new Runnable() {
			@Override
			public void run() {
				//wait until the active page is created, then register all listeners
				while(PlatformUI.getWorkbench() == null) {}
				while(PlatformUI.getWorkbench().getActiveWorkbenchWindow() == null) {}
				while(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() == null) {}
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().addPartListener(ViewControl.this);
				// PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().addSelectionListener(ViewControl.this);
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(ViewControl.this);
				// If our perspective is loaded, activate its context
				if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getPerspective().getId().equals("de.tukl.cs.softech.agilereview.view.AgileReviewPerspective")) {
					IContextService contextService = (IContextService)PlatformUI.getWorkbench().getService(IContextService.class);
					contextActivation = contextService.activateContext("de.tukl.cs.softech.agilereview.perspective.open");
					PluginLogger.log(ViewControl.class.toString(), "Constructor", "Context \"de.tukl.cs.softech.agilereview.perspective.open\" activated");
					// TODO: Should not be done this way
//					IBindingService bindingService = (IBindingService)PlatformUI.getWorkbench().getService(IBindingService.class);
//					CommandManager commandManager = new CommandManager();
//					ContextManager contextManager = new ContextManager();
//					BindingManager bindingManager = new BindingManager(contextManager, commandManager);
//					Scheme scheme = bindingService.getScheme("de.tukl.cs.softech.agilereview.keyscheme");
//					if (scheme.isDefined()) {
//						try {
//							bindingManager.setActiveScheme(scheme);
//						} catch (NotDefinedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
				}
			}
		});
	}
	
	/**
	 * Registers a new {@link ViewPart} for the plugin
	 * @param c Class of the {@link ViewPart} to register
	 * @return true if the registration was successful
	 */
	public static boolean registerView(Class<? extends IWorkbenchPart> c) {
		PluginLogger.log(ViewControl.class.toString(), "registerView", c.getName());
		return activeViews.add(c);
		
	}
	
	/**
	 * Unregisters a given {@link ViewPart} for the plugin. Views will be unregistered automatically when closed
	 * @param c Class of the {@link ViewPart} to unregister
	 * @return true if the given {@link ViewPart} was unregistered successfully
	 */
	private static boolean unregisterView(Class<? extends IWorkbenchPart> c) {
		PluginLogger.log(ViewControl.class.toString(), "unregisterView", c.getName());
		return activeViews.remove(c);
	}
	
	/**
	 * Checks whether the given {@link ViewPart} has been registered
	 * @param c Class of the {@link ViewPart} to be checked
	 * @return true if the given {@link ViewPart} has been registered
	 */
	public static boolean isOpen(Class<? extends ViewPart> c) {
		return activeViews.contains(c);
	}
	
	//****************************************
	//****** ISelectionChangedListener *******
	//****************************************
	
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if(isOpen(CommentTableView.class)) {
			CommentTableView.getInstance().selectionChanged(event);
		}
		if(isOpen(DetailView.class)) {
			DetailView.getInstance().selectionChanged(event);
		}
	}
	
//	/** listens for selectionChanged events and forwards these to following methods:<br>
//	 *  {@link CommentTableView#selectionChanged(IWorkbenchPart, ISelection)}<br>
//	 *  {@link DetailView#selectionChanged(IWorkbenchPart, ISelection)}
//	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
//	 */
//	@Override
//	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
//		if(selection != null && part != null) {
//			// TODO: the following logging line makes reading logs impossible 
//			// PluginLogger.log(this.getClass().toString(), "selectionChanged", "fired with selection != null && part != null");
//			if(isOpen(CommentTableView.class)) {
//				CommentTableView.getInstance().selectionChanged(part, selection);
//			}
//			if(isOpen(DetailView.class)) {
//				DetailView.getInstance().selectionChanged(part, selection);
//			}
//		}
//	}
	
	//****************************************
	//********** IPartListener2 **************
	//****************************************
	
	/** not yet used
	 * @see org.eclipse.ui.IPartListener2#partActivated(org.eclipse.ui.IWorkbenchPartReference)
	 */
	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
		
	}

	/**
	 * listens whether a part was brought to top and forwards this to the following methods:<br>
	 * {@link CommentTableView#partBroughtToTop(IWorkbenchPartReference)}
	 * @see org.eclipse.ui.IPartListener2#partBroughtToTop(org.eclipse.ui.IWorkbenchPartReference)
	 */
	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {
		PluginLogger.log(this.getClass().toString(), "partBroughtToTop", partRef.getPart(false).getTitle());
		if(isOpen(CommentTableView.class)) {
			CommentTableView.getInstance().partBroughtToTop(partRef);
		}
	}

	/**
	 * listens whether a part was closed and forwards this to the following methods:<br>
	 * {@link CommentTableView#partClosed(IWorkbenchPartReference)}<br>
	 * {@link DetailView#partClosedOrDeactivated(IWorkbenchPart)}
	 * @see org.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.IWorkbenchPartReference)
	 */
	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		if(ViewControl.unregisterView(partRef.getPart(false).getClass())) {
			PluginLogger.log(this.getClass().toString(), "partClosed", "unregister: "+partRef.getPart(false).getTitle());
		}
		
		if(isOpen(DetailView.class)) {
			DetailView.getInstance().partClosedOrDeactivated(partRef.getPart(false));
		}
		if(isOpen(CommentTableView.class)) {
			CommentTableView.getInstance().partClosed(partRef);
		}
	}

	/**
	 * listens whether a part was deactivated and forwards this to the following methods:<br>
	 * {@link DetailView#partClosedOrDeactivated(IWorkbenchPart)}
	 * @see org.eclipse.ui.IPartListener2#partDeactivated(org.eclipse.ui.IWorkbenchPartReference)
	 */
	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
		PluginLogger.log(this.getClass().toString(), "partDeactivated", partRef.getPart(false).getTitle());
		if(isOpen(DetailView.class)) {
			DetailView.getInstance().partClosedOrDeactivated(partRef.getPart(false));
		}
	}
	
	/**
	 * not yet used
	 * @see org.eclipse.ui.IPartListener2#partOpened(org.eclipse.ui.IWorkbenchPartReference)
	 */
	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
		
	}

	/**
	 * not yet used
	 * @see org.eclipse.ui.IPartListener2#partHidden(org.eclipse.ui.IWorkbenchPartReference)
	 */
	@Override
	public void partHidden(IWorkbenchPartReference partRef) {
		
	}

	/**
	 * not yet used
	 * @see org.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.IWorkbenchPartReference)
	 */
	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
				
	}

	/**
	 * not yet used
	 * @see org.eclipse.ui.IPartListener2#partInputChanged(org.eclipse.ui.IWorkbenchPartReference)
	 */
	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {
				
	}
	
	//****************************************
	//****** IPerspectiveListener3 ***********
	//****************************************

	/** 
	 * not yet used
	 * @see org.eclipse.ui.IPerspectiveListener2#perspectiveChanged(org.eclipse.ui.IWorkbenchPage, org.eclipse.ui.IPerspectiveDescriptor, org.eclipse.ui.IWorkbenchPartReference, java.lang.String)
	 */
	@Override
	public void perspectiveChanged(IWorkbenchPage page,	IPerspectiveDescriptor perspective,
			IWorkbenchPartReference partRef, String changeId) {
		// PluginLogger.log(this.getClass().toString(), "perspectiveChanged1", perspective.getLabel());
	}

	/**
	 * listens whether a perspective was activated and forwards this to the following methods:<br>
	 * {@link CommentTableView#perspectiveActivated(IWorkbenchPage, IPerspectiveDescriptor)}
	 * @see org.eclipse.ui.IPerspectiveListener#perspectiveActivated(org.eclipse.ui.IWorkbenchPage, org.eclipse.ui.IPerspectiveDescriptor)
	 */
	@Override
	public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		PluginLogger.log(this.getClass().toString(), "perspectiveActivated", perspective.getLabel());
		if (perspective.getId().equals("de.tukl.cs.softech.agilereview.view.AgileReviewPerspective")) {
			IContextService contextService = (IContextService)PlatformUI.getWorkbench().getService(IContextService.class);
			contextActivation = contextService.activateContext("de.tukl.cs.softech.agilereview.perspective.open");
			PluginLogger.log(ViewControl.class.toString(), "perspectiveActivated", "Context \"de.tukl.cs.softech.agilereview.perspective.open\" activated");
		} else {
			IContextService contextService = (IContextService)PlatformUI.getWorkbench().getService(IContextService.class);
			contextService.deactivateContext(contextActivation);
			PluginLogger.log(ViewControl.class.toString(), "perspectiveActivated", "Context \"de.tukl.cs.softech.agilereview.perspective.open\" deactivated");
		}
		
		if(isOpen(CommentTableView.class)) {
			CommentTableView.getInstance().perspectiveActivated(page, perspective);
		}
	}

	/**
	 * not yet used
	 * @see org.eclipse.ui.IPerspectiveListener#perspectiveChanged(org.eclipse.ui.IWorkbenchPage, org.eclipse.ui.IPerspectiveDescriptor, java.lang.String)
	 */
	@Override
	public void perspectiveChanged(IWorkbenchPage page,	IPerspectiveDescriptor perspective, String changeId) {
		// PluginLogger.log(this.getClass().toString(), "perspectiveChanged2", perspective.getLabel());
	}

	/**
	 * not yet used
	 * @see org.eclipse.ui.IPerspectiveListener3#perspectiveOpened(org.eclipse.ui.IWorkbenchPage, org.eclipse.ui.IPerspectiveDescriptor)
	 */
	@Override
	public void perspectiveOpened(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		// PluginLogger.log(this.getClass().toString(), "perspectiveOpened", perspective.getLabel());
	}

	/**
	 * not yet used
	 * @see org.eclipse.ui.IPerspectiveListener3#perspectiveClosed(org.eclipse.ui.IWorkbenchPage, org.eclipse.ui.IPerspectiveDescriptor)
	 */
	@Override
	public void perspectiveClosed(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		// PluginLogger.log(this.getClass().toString(), "perspectiveClosed", perspective.getLabel());
	}

	/**
	 * not yet used
	 * @see org.eclipse.ui.IPerspectiveListener3#perspectiveDeactivated(org.eclipse.ui.IWorkbenchPage, org.eclipse.ui.IPerspectiveDescriptor)
	 */
	@Override
	public void perspectiveDeactivated(IWorkbenchPage page,	IPerspectiveDescriptor perspective) {
		// PluginLogger.log(this.getClass().toString(), "perspectiveDeactivated", perspective.getLabel());
	}

	/**
	 * not yet used
	 * @see org.eclipse.ui.IPerspectiveListener3#perspectiveSavedAs(org.eclipse.ui.IWorkbenchPage, org.eclipse.ui.IPerspectiveDescriptor, org.eclipse.ui.IPerspectiveDescriptor)
	 */
	@Override
	public void perspectiveSavedAs(IWorkbenchPage page,	IPerspectiveDescriptor oldPerspective, IPerspectiveDescriptor newPerspective) {
		// PluginLogger.log(this.getClass().toString(), "perspectiveSavedAs", oldPerspective.getLabel()+"-->"+newPerspective.getLabel());
	}
}
