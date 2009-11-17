package org.purc.purcforms.client.widget;

import java.util.Date;

import org.purc.purcforms.client.util.FormUtil;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;


/**
 * 
 * @author daniel
 *
 */
public class DateTimeWidget extends Composite{

	private HorizontalPanel panel = new HorizontalPanel();
	private DatePickerWidget dateWidget = new DatePickerWidget();
	private TimeWidget timeWidget = new TimeWidget();

	public DateTimeWidget(){

		initWidget(panel);

		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		panel.setSpacing(0);

		panel.add(dateWidget);
		panel.add(timeWidget);
		
		panel.setCellWidth(timeWidget, "48%");
		panel.setCellWidth(dateWidget, "52%");
		
		dateWidget.setWidth("100%");
		timeWidget.setWidth("100%");
		
		dateWidget.setHeight("100%");
		timeWidget.setHeight("100%");

		sinkEvents(Event.getTypeInt(KeyDownEvent.getType().getName()));
		
		addEventHandlers();
	}


	public void addEventHandlers(){
		((TextBox)dateWidget).addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event){
				if(getParent().getParent() instanceof RuntimeWidgetWrapper)
					timeWidget.setFocus(true); //((RuntimeWidgetWrapper)getParent().getParent()).moveToNextWidget();
			}
		});
	}


	@Override
	public void onBrowserEvent(Event event){
		if(DOM.eventGetType(event) == Event.ONKEYDOWN){
			if(event.getKeyCode() == KeyCodes.KEY_ENTER ){
				if(event.getTarget() != timeWidget.getElement()){
					dateWidget.close();
					event.preventDefault();
					event.stopPropagation();
					timeWidget.setFocus(true);
					return;
				}
				else if(getParent().getParent() instanceof RuntimeWidgetWrapper)
					((RuntimeWidgetWrapper)getParent().getParent()).moveToNextWidget();
			}
		}

		super.onBrowserEvent(event);
	}

	public void setTabIndex(int index) {
		dateWidget.setTabIndex(index);
		timeWidget.setTabIndex(index);
	}

	public int getTabIndex(){
		return dateWidget.getTabIndex();
	}

	public String getText(){
		if(dateWidget.getText().trim().length() == 0 && timeWidget.getText().trim().length() == 0)
			return "";

		return dateWidget.getText() + " " + timeWidget.getTextWithMask();
	}

	public void setText(String text){
		if(text == null || text.trim().length() == 0){
			dateWidget.setText(null);
			timeWidget.setText(null);
		}
		else{
			Date date = FormUtil.getDateTimeSubmitFormat().parse(text);
			dateWidget.setText(FormUtil.getDateDisplayFormat().format(date));
			timeWidget.setText(FormUtil.getTimeDisplayFormat().format(date));
		}
	}

	public void setFocus(boolean focused){
		dateWidget.setFocus(true);
	}

	public void setEnabled(boolean enabled){
		dateWidget.setEnabled(enabled);
		timeWidget.setEnabled(enabled);
	}
	
	public void setStyle(String name, String value){
		DOM.setStyleAttribute(dateWidget.getElement(), "cursor", value);
		DOM.setStyleAttribute(timeWidget.getElement(), "cursor", value);
	}
	
	public boolean isEnabled(){
		return dateWidget.isEnabled();
	}
}