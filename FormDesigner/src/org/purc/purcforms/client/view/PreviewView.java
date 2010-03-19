package org.purc.purcforms.client.view;

import org.purc.purcforms.client.CenterPanel;
import org.purc.purcforms.client.Toolbar;
import org.purc.purcforms.client.controller.SubmitListener;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.util.FormDesignerUtil;
import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.xforms.XformBuilder;
import org.purc.purcforms.client.xforms.XformUtil;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;


/**
 * This widget is used to preview a form in the form designer.
 * 
 * @author daniel
 *
 */
public class PreviewView extends FormRunnerView {

	public interface Images extends FormRunnerView.Images,Toolbar.Images {
		AbstractImagePrototype error();
	}

	/** Popup for displaying the context menu for the preview. */
	private PopupPanel popup;
	
	/** Reference to the design surface for getting layout xml during refresh. */
	private DesignSurfaceView designSurfaceView;
	
	/** Reference to the center panel for committing edit changes and getting the current form. */
	private CenterPanel centerPanel;

	
	/**
	 * Creates a new instance of the preview widget.
	 * 
	 * @param images the images for the preview context menu.
	 */
	public PreviewView(Images images){
		super(images);

		popup = new PopupPanel(true,true);
		MenuBar menuBar = new MenuBar(true);
		menuBar.addItem(FormDesignerUtil.createHeaderHTML(images.loading(),LocaleText.get("refresh")),true,new Command(){
			public void execute() {popup.hide(); refresh();}});

		menuBar.addSeparator();
		menuBar.addItem(FormDesignerUtil.createHeaderHTML(images.save(),LocaleText.get("submit")),true,new Command(){
			public void execute() {popup.hide(); submit();}});

		popup.setWidget(menuBar);

		addNewTab(LocaleText.get("page")+"1");

		DOM.sinkEvents(getElement(),DOM.getEventsSunk(getElement()) | Event.ONMOUSEDOWN);

		//This is needed for IE
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				setHeight(getHeight());
			}
		});
	}

	//TODO These two should bind to interfaces.
	public void setDesignSurface(DesignSurfaceView designSurfaceView){
		this.designSurfaceView = designSurfaceView;
	}

	public void setCenterPanel(CenterPanel centerPanel){
		this.centerPanel = centerPanel;
	}

	/**
	 * Sets up the preview widget.
	 */
	protected void initPanel(){
		AbsolutePanel panel = new AbsolutePanel();
		//FormDesignerUtil.maximizeWidget(panel);
		selectedPanel = panel;

		//This is needed for IE
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				//onWindowResized(Window.getClientWidth(), Window.getClientHeight());
				setHeight(getHeight());
			}
		});
	}

	@Override
	protected void submit(){
		if(formDef != null){
			if(formDef.getDoc() == null)
				XformBuilder.fromFormDef2Xform(formDef);

			saveValues();

			if(!isValid(false))
				return;

			String xml = XformUtil.getInstanceDataDoc(formDef.getDoc()).toString();
			xml = FormDesignerUtil.formatXml(/*"<?xml version='1.0' encoding='UTF-8' ?> " +*/ xml);
			submitListener.onSubmit(xml);
		}
	}
	

	/**
	 * Sets the listener for form submission events.
	 * 
	 * @param submitListener the listener.
	 */
	public void setSubmitListener(SubmitListener submitListener){
		this.submitListener = submitListener;
	}

	/**
	 * Checks if the preview surface has any widgets.
	 * 
	 * @return true if yes, else false.
	 */
	public boolean isPreviewing(){
		return tabs.getWidgetCount() > 0 && selectedPanel != null && selectedPanel.getWidgetCount() > 0;
	}

	@Override
	public void onBrowserEvent(Event event) {
		int type = DOM.eventGetType(event);

		switch (type) {
		case Event.ONMOUSEDOWN:
			if( (event.getButton() & Event.BUTTON_RIGHT) != 0){
				if(event.getTarget().getClassName().length() == 0){
					popup.setPopupPosition(event.getClientX(), event.getClientY());
					popup.show();
					FormDesignerUtil.disableContextMenu(popup.getElement());
				}
			}
			break;
		}	
	}

	/**
	 * Reloads widgets on the preview surface.
	 */
	public void refresh(){
		FormUtil.dlg.setText(LocaleText.get("refreshingPreview"));
		FormUtil.dlg.center();

		DeferredCommand.addCommand(new Command(){
			public void execute() {
				try{
					centerPanel.commitChanges();
					loadForm(centerPanel.getFormDef(), designSurfaceView.getLayoutXml(),null);
					FormUtil.dlg.hide();
				}
				catch(Exception ex){
					FormUtil.dlg.hide();
					FormUtil.displayException(ex);
				}
			}
		});
	}

	/**
	 * Removes all widgets from the preview surface.
	 */
	public void clearPreview(){
		tabs.clear();
	}
}
