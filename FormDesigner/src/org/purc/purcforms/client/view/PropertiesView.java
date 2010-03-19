package org.purc.purcforms.client.view;

import org.purc.purcforms.client.Context;
import org.purc.purcforms.client.controller.IFormActionListener;
import org.purc.purcforms.client.controller.IFormChangeListener;
import org.purc.purcforms.client.controller.IFormSelectionListener;
import org.purc.purcforms.client.controller.ItemSelectionListener;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.FormDef;
import org.purc.purcforms.client.model.OptionDef;
import org.purc.purcforms.client.model.PageDef;
import org.purc.purcforms.client.model.QuestionDef;
import org.purc.purcforms.client.model.RepeatQtnsDef;
import org.purc.purcforms.client.util.FormDesignerUtil;
import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.widget.DescTemplateWidget;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;


/**
 * View responsible for displaying and hence allow editing of 
 * form, page, question, or question option properties.
 * 
 * @author daniel
 *
 */
public class PropertiesView extends Composite implements IFormSelectionListener,ItemSelectionListener{

	/** List box index for no selected data type. */
	private static final byte DT_INDEX_NONE = -1;
	
	/** List box index for text data type. */
	private static final byte DT_INDEX_TEXT = 0;
	
	/** List box index for number data type. */
	private static final byte DT_INDEX_NUMBER = 1;
	
	/** List box index for decimal data type. */
	private static final byte DT_INDEX_DECIMAL = 2;
	
	/** List box index for date data type. */
	private static final byte DT_INDEX_DATE = 3;
	
	/** List box index for time data type. */
	private static final byte DT_INDEX_TIME = 4;
	
	/** List box index for dateTime data type. */
	private static final byte DT_INDEX_DATE_TIME = 5;
	
	/** List box index for boolean data type. */
	private static final byte DT_INDEX_BOOLEAN = 6;
	
	/** List box index for single select data type. */
	private static final byte DT_INDEX_SINGLE_SELECT = 7;
	
	/** List box index for multiple select data type. */
	private static final byte DT_INDEX_MULTIPLE_SELECT = 8;
	
	/** List box index for repeat data type. */
	private static final byte DT_INDEX_REPEAT = 9;
	
	/** List box index for image data type. */
	private static final byte DT_INDEX_IMAGE = 10;
	
	/** List box index for video data type. */
	private static final byte DT_INDEX_VIDEO = 11;
	
	/** List box index for audio data type. */
	private static final byte DT_INDEX_AUDIO = 12;
	
	/** List box index for single select dynamic data type. */
	private static final byte DT_INDEX_SINGLE_SELECT_DYNAMIC = 13;
	
	/** List box index for gps data type. */
	private static final byte DT_INDEX_GPS = 14;

	/** Table used for organising widgets in a table format. */
	private FlexTable table = new FlexTable();
	
	/** Widget for displaying the list of data types. */
	private ListBox cbDataType = new ListBox(false);
	
	/** Widget for setting the visibility property. */
	private CheckBox chkVisible = new CheckBox();
	
	/** Widget for setting the enabled property. */
	private CheckBox chkEnabled = new CheckBox();
	
	/** Widget for setting the locked property. */
	private CheckBox chkLocked = new CheckBox();
	
	/** Widget for setting the required property. */
	private CheckBox chkRequired = new CheckBox();
	
	/** Widget for setting the text property. */
	private TextBox txtText = new TextBox();
	
	/** Widget for setting the help text property. */
	private TextBox txtHelpText = new TextBox();
	
	/** Widget for setting the binding property. */
	private TextBox txtBinding = new TextBox();
	
	/** Widget for setting the default value property. */
	private TextBox txtDefaultValue = new TextBox();
	
	/** Widget for setting the description template property. */
	private TextBox txtDescTemplate = new TextBox();
	
	/** Widget for setting the form key property. */
	private TextBox txtFormKey = new TextBox();
	
	/** Widget for selecting fields which define the description template. */
	private DescTemplateWidget btnDescTemplate; // = new Button("Create/Edit");

	/** The selected object which could be FormDef, PageDef, QuestionDef or OptionDef */
	private Object propertiesObj;
	
	/** Listener to form change events. */
	private IFormChangeListener formChangeListener;
	
	/** Widget for defining skip rules. */
	private SkipRulesView skipRulesView = new SkipRulesView();
	
	/** Widget for defining validation rules. */
	private ValidationRulesView validationRulesView = new ValidationRulesView();
	
	/** Widget for defining dynamic selection lists. */
	private DynamicListsView dynamicListsView = new DynamicListsView();
	
	/** Listener to form action events. */
	private IFormActionListener formActionListener;

	
	/**
	 * Creates a new instance of the properties view widget.
	 */
	public PropertiesView(){

		btnDescTemplate = new DescTemplateWidget(this);

		//chkVisible.setStyleName("gwt-CheckBox");

		table.setWidget(0, 0, new Label(LocaleText.get("text")));
		table.setWidget(1, 0, new Label(LocaleText.get("helpText")));
		table.setWidget(2, 0, new Label(LocaleText.get("type")));
		table.setWidget(3, 0, new Label(LocaleText.get("binding")));
		table.setWidget(4, 0, new Label(LocaleText.get("visible")));
		table.setWidget(5, 0, new Label(LocaleText.get("enabled")));
		table.setWidget(6, 0, new Label(LocaleText.get("locked")));
		table.setWidget(7, 0, new Label(LocaleText.get("required")));
		table.setWidget(8, 0, new Label(LocaleText.get("defaultValue")));
		//table.setWidget(9, 0, new Label("Control Type"));
		table.setWidget(9, 0, new Label(LocaleText.get("descriptionTemplate")));
		table.setWidget(10, 0, new Label(LocaleText.get("formKey")));

		table.setWidget(0, 1, txtText);
		table.setWidget(1, 1, txtHelpText);
		table.setWidget(2, 1, cbDataType);
		table.setWidget(3, 1, txtBinding);
		table.setWidget(4, 1, chkVisible);
		table.setWidget(5, 1, chkEnabled);
		table.setWidget(6, 1, chkLocked);
		table.setWidget(7, 1, chkRequired);
		table.setWidget(8, 1, txtDefaultValue);

		HorizontalPanel panel = new HorizontalPanel();
		panel.add(txtDescTemplate);
		panel.add(btnDescTemplate);
		panel.setCellWidth(btnDescTemplate, "20%");
		FormUtil.maximizeWidget(txtDescTemplate);
		FormUtil.maximizeWidget(panel);
		table.setWidget(9, 1, panel);
		table.setWidget(10, 1, txtFormKey);

		table.setStyleName("cw-FlexTable");

		cbDataType.addItem(LocaleText.get("qtnTypeText"));
		cbDataType.addItem(LocaleText.get("qtnTypeNumber"));
		cbDataType.addItem(LocaleText.get("qtnTypeDecimal"));
		cbDataType.addItem(LocaleText.get("qtnTypeDate"));
		cbDataType.addItem(LocaleText.get("qtnTypeTime"));
		cbDataType.addItem(LocaleText.get("qtnTypeDateTime"));
		cbDataType.addItem(LocaleText.get("qtnTypeBoolean"));
		cbDataType.addItem(LocaleText.get("qtnTypeSingleSelect"));
		cbDataType.addItem(LocaleText.get("qtnTypeMultSelect"));
		cbDataType.addItem(LocaleText.get("qtnTypeRepeat"));
		cbDataType.addItem(LocaleText.get("qtnTypePicture"));
		cbDataType.addItem(LocaleText.get("qtnTypeVideo"));
		cbDataType.addItem(LocaleText.get("qtnTypeAudio"));
		cbDataType.addItem(LocaleText.get("qtnTypeSingleSelectDynamic"));
		cbDataType.addItem(LocaleText.get("qtnTypeGPS"));

		FlexCellFormatter cellFormatter = table.getFlexCellFormatter();
		cellFormatter.setHorizontalAlignment(15, 1, HasHorizontalAlignment.ALIGN_CENTER);

		table.setWidth("100%");
		cellFormatter.setWidth(0, 0, "20%");
		//cellFormatter.setColSpan(0, 0, 2);

		txtText.setWidth("100%");
		txtHelpText.setWidth("100%");
		txtBinding.setWidth("100%");
		txtDefaultValue.setWidth("100%");
		cbDataType.setWidth("100%");
		txtFormKey.setWidth("100%");

		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.add(table);

		DecoratedTabPanel tabs = new DecoratedTabPanel();
		tabs.add(skipRulesView, LocaleText.get("skipLogic"));
		tabs.add(validationRulesView, LocaleText.get("validationLogic"));
		tabs.add(dynamicListsView, LocaleText.get("dynamicLists"));

		tabs.selectTab(0);
		verticalPanel.add(tabs);
		FormUtil.maximizeWidget(tabs);

		FormUtil.maximizeWidget(verticalPanel);
		initWidget(verticalPanel);

		setupEventListeners();

		cbDataType.setSelectedIndex(-1);

		enableQuestionOnlyProperties(false);
		txtText.setEnabled(false);
		txtFormKey.setEnabled(false);
		txtDescTemplate.setEnabled(false);
		btnDescTemplate.setEnabled(false);

		txtText.setTitle(LocaleText.get("questionTextDesc"));
		txtHelpText.setTitle(LocaleText.get("questionDescDesc"));
		txtBinding.setTitle(LocaleText.get("questionIdDesc"));
		txtDefaultValue.setTitle(LocaleText.get("defaultValDesc"));
		cbDataType.setTitle(LocaleText.get("questionTypeDesc"));

		DOM.sinkEvents(getElement(), Event.ONKEYDOWN | DOM.getEventsSunk(getElement()));
	}

	/**
	 * Sets up event listeners.
	 */
	private void setupEventListeners(){
		//Check boxes.
		chkVisible.addClickListener(new ClickListener(){
			public void onClick(Widget widget){
				((QuestionDef)propertiesObj).setVisible(chkVisible.isChecked());
				formChangeListener.onFormItemChanged(propertiesObj);
			}
		});

		chkEnabled.addClickListener(new ClickListener(){
			public void onClick(Widget widget){
				((QuestionDef)propertiesObj).setEnabled(chkEnabled.isChecked());
				formChangeListener.onFormItemChanged(propertiesObj);
			}
		});

		chkLocked.addClickListener(new ClickListener(){
			public void onClick(Widget widget){
				((QuestionDef)propertiesObj).setLocked(chkLocked.isChecked());
				formChangeListener.onFormItemChanged(propertiesObj);
			}
		});

		chkRequired.addClickListener(new ClickListener(){
			public void onClick(Widget widget){
				((QuestionDef)propertiesObj).setRequired(chkRequired.isChecked());
				formChangeListener.onFormItemChanged(propertiesObj);
			}
		});

		//Text boxes.
		txtDefaultValue.addChangeListener(new ChangeListener(){
			public void onChange(Widget sender){
				updateDefaultValue();
			}
		});
		txtDefaultValue.addKeyboardListener(new KeyboardListenerAdapter(){
			public void onKeyUp(Widget sender, char keyCode, int modifiers) {
				updateDefaultValue();
			}
		});

		txtHelpText.addChangeListener(new ChangeListener(){
			public void onChange(Widget sender){
				updateHelpText();
			}
		});
		txtHelpText.addKeyboardListener(new KeyboardListenerAdapter(){
			public void onKeyUp(Widget sender, char keyCode, int modifiers) {
				updateHelpText();
			}
			public void onKeyDown(Widget sender, char keyCode, int modifiers) {
				if(keyCode == KeyboardListener.KEY_ENTER || keyCode == KeyboardListener.KEY_DOWN)
					cbDataType.setFocus(true);
				else if(keyCode == KeyboardListener.KEY_UP){
					txtText.setFocus(true);
					txtText.selectAll();
				}
			}
		});

		txtBinding.addChangeListener(new ChangeListener(){
			public void onChange(Widget sender){
				updateBinding();
			}
		});
		txtBinding.addKeyboardListener(new KeyboardListenerAdapter(){
			public void onKeyUp(Widget sender, char keyCode, int modifiers) {
				String s = txtBinding.getText();

				s = s.replace("%", "");
				s = s.replace("(", "");
				s = s.replace("!", "");
				s = s.replace("&", "");
				s = s.replace(".", "");
				s = s.replace("'", "");
				s = s.replace("\"", "");
				s = s.replace("$", "");
				s = s.replace("#", "");

				txtBinding.setText(s);
				updateBinding();
			}

			public void onKeyDown(Widget sender, char keyCode, int modifiers) {
				if(keyCode == KeyboardListener.KEY_UP){
					if(cbDataType.isEnabled())
						cbDataType.setFocus(true);
					else{
						txtText.setFocus(true);
						txtText.selectAll();
					}
				}
			}

			public void onKeyPress(Widget sender, char keyCode, int modifiers) {
				if(propertiesObj instanceof PageDef){
					if(!Character.isDigit(keyCode)){
						((TextBox) sender).cancelKey(); 
						return;
					}
				}
				else if(propertiesObj instanceof FormDef || propertiesObj instanceof QuestionDef){
					if(((TextBox) sender).getCursorPos() == 0){
						if(!isAllowedXmlNodeNameStartChar(keyCode)){
							((TextBox) sender).cancelKey(); 
							return;
						}
					}
					else if(!isAllowedXmlNodeNameChar(keyCode)){
						((TextBox) sender).cancelKey(); 
						return;
					}
				} //else OptionDef varname can be anything
			}
		});

		txtText.addChangeListener(new ChangeListener(){
			public void onChange(Widget sender){
				updateText();

				if(propertiesObj != null && Context.allowBindEdit() && !Context.isStructureReadOnly()){
					String name = FormDesignerUtil.getXmlTagName(txtText.getText());
					if(propertiesObj instanceof FormDef && ((FormDef)propertiesObj).getVariableName().startsWith("newform"))
						((FormDef)propertiesObj).setVariableName(name);
					else if(propertiesObj instanceof QuestionDef && ((QuestionDef)propertiesObj).getVariableName().startsWith("question"))
						((QuestionDef)propertiesObj).setVariableName(name);
					else if(propertiesObj instanceof OptionDef && ((OptionDef)propertiesObj).getVariableName().startsWith("option"))
						((OptionDef)propertiesObj).setVariableName(name);
				}
			}
		});
		txtText.addKeyboardListener(new KeyboardListenerAdapter(){
			public void onKeyUp(Widget sender, char keyCode, int modifiers) {
				updateText();
			}
			public void onKeyDown(Widget sender, char keyCode, int modifiers) {
				if(keyCode == KeyboardListener.KEY_ENTER || keyCode == KeyboardListener.KEY_DOWN){
					if(txtHelpText.isEnabled())
						txtHelpText.setFocus(true);
					else{
						txtBinding.setFocus(true);
						txtBinding.selectAll();
					}
				}
			}
		});

		txtDescTemplate.addChangeListener(new ChangeListener(){
			public void onChange(Widget sender){
				updateDescTemplate();
			}
		});
		txtDescTemplate.addKeyboardListener(new KeyboardListenerAdapter(){
			public void onKeyUp(Widget sender, char keyCode, int modifiers) {
				updateDescTemplate();
			}
		});

		//Combo boxes
		cbDataType.addClickListener(new ClickListener(){
			public void onClick(Widget sender){
				updateDataType();
			}
		});
		cbDataType.addChangeListener(new ChangeListener(){
			public void onChange(Widget sender){
				updateDataType();
			}
		});
		cbDataType.addKeyboardListener(new KeyboardListenerAdapter(){
			public void onKeyDown(Widget sender, char keyCode, int modifiers) {
				if(keyCode == KeyboardListener.KEY_ENTER || keyCode == KeyboardListener.KEY_DOWN){
					txtBinding.setFocus(true);
					txtBinding.selectAll();
				}
				else if(keyCode == KeyboardListener.KEY_UP){
					txtHelpText.setFocus(true);
					txtHelpText.selectAll();
				}
			}
		});
		
		txtFormKey.addChangeListener(new ChangeListener(){
			public void onChange(Widget widget){
				updateFormKey();
			}
		});
		txtFormKey.addKeyboardListener(new KeyboardListenerAdapter(){
			public void onKeyUp(Widget sender, char keyCode, int modifiers) {
				updateFormKey();
			}
		});
	}

	/**
	 * Checks if a given character is allowed to begin an xml node name.
	 * 
	 * @param keyCode the character code.
	 * @return true if is allowed, else false.
	 */
	private boolean isAllowedXmlNodeNameStartChar(char keyCode){
		return ((keyCode >= 'a' && keyCode <= 'z') || (keyCode >= 'A' && keyCode <= 'Z') || isControlChar(keyCode));
	}

	/**
	 * Checks if a character is allowed in an xml node name.
	 * 
	 * @param keyCode the character code.
	 * @return true if allowed, else false.
	 */
	private boolean isAllowedXmlNodeNameChar(char keyCode){
		return isAllowedXmlNodeNameStartChar(keyCode) || Character.isDigit(keyCode) || keyCode == '-' || keyCode == '_' || keyCode == '.';
	}

	/**
	 * Check if a character is a control character. Examples of control characters are
	 * ALT, CTRL, ESCAPE, DELETE, SHIFT, HOME, PAGE_UP, BACKSPACE, ENTER, TAB, LEFT, and more.
	 * 
	 * @param keyCode the character code.
	 * @return true if yes, else false.
	 */
	private boolean isControlChar(char keyCode){
		int code = keyCode;
		return (code == KeyboardListener.KEY_ALT || code == KeyboardListener.KEY_BACKSPACE ||
				code == KeyboardListener.KEY_CTRL || code == KeyboardListener.KEY_DELETE ||
				code == KeyboardListener.KEY_DOWN || code == KeyboardListener.KEY_END ||
				code == KeyboardListener.KEY_ENTER || code == KeyboardListener.KEY_ESCAPE ||
				code == KeyboardListener.KEY_HOME || code == KeyboardListener.KEY_LEFT ||
				code == KeyboardListener.KEY_PAGEDOWN || code == KeyboardListener.KEY_PAGEUP ||
				code == KeyboardListener.KEY_RIGHT || code == KeyboardListener.KEY_SHIFT ||
				code == KeyboardListener.KEY_TAB || code == KeyboardListener.KEY_UP);
	}

	/**
	 * Updates the selected object with the new text as typed by the user.
	 */
	private void updateText(){
		if(propertiesObj == null)
			return;

		if(propertiesObj instanceof QuestionDef)
			((QuestionDef)propertiesObj).setText(txtText.getText());
		else if(propertiesObj instanceof OptionDef)
			((OptionDef)propertiesObj).setText(txtText.getText());
		else if(propertiesObj instanceof PageDef)
			((PageDef)propertiesObj).setName(txtText.getText());
		else if(propertiesObj instanceof FormDef)
			((FormDef)propertiesObj).setName(txtText.getText());

		formChangeListener.onFormItemChanged(propertiesObj);
	}

	/**
	 * Updates the selected object with the new description template as typed by the user.
	 */
	private void updateDescTemplate(){
		if(propertiesObj == null)
			return;

		else if(propertiesObj instanceof FormDef){
			((FormDef)propertiesObj).setDescriptionTemplate(txtDescTemplate.getText());
			formChangeListener.onFormItemChanged(propertiesObj);
		}
	}
	
	private void updateFormKey(){
		if(propertiesObj == null)
			return;

		if(propertiesObj instanceof FormDef)
			((FormDef)propertiesObj).setFormKey(txtFormKey.getText());

		formChangeListener.onFormItemChanged(propertiesObj);
	}

	/**
	 * Updates the selected object with the new binding as typed by the user.
	 */
	private void updateBinding(){
		if(propertiesObj == null)
			return;

		if(txtBinding.getText().trim().length() == 0)
			return;

		if(propertiesObj instanceof QuestionDef)
			((QuestionDef)propertiesObj).setVariableName(txtBinding.getText());
		else if(propertiesObj instanceof OptionDef)
			((OptionDef)propertiesObj).setVariableName(txtBinding.getText());
		else if(propertiesObj instanceof FormDef)
			((FormDef)propertiesObj).setVariableName(txtBinding.getText());
		else if(propertiesObj instanceof PageDef){
			try{
				((PageDef)propertiesObj).setPageNo(Integer.parseInt(txtBinding.getText()));
			}catch(Exception ex){
				return;
			}
		}

		formChangeListener.onFormItemChanged(propertiesObj);
	}

	/**
	 * Updates the selected object with the new help text as typed by the user.
	 */
	private void updateHelpText(){
		if(propertiesObj == null)
			return;

		((QuestionDef)propertiesObj).setHelpText(txtHelpText.getText());
		formChangeListener.onFormItemChanged(propertiesObj);
	}

	/**
	 * Updates the selected object with the new default value as typed by the user.
	 */
	private void updateDefaultValue(){
		if(propertiesObj == null)
			return;

		((QuestionDef)propertiesObj).setDefaultValue(txtDefaultValue.getText());
		formChangeListener.onFormItemChanged(propertiesObj);
	}

	/**
	 * Updates the selected object with the new data type as typed by the user.
	 */
	private void updateDataType(){
		if(propertiesObj == null)
			return;

		boolean deleteKids = false;
		int index = cbDataType.getSelectedIndex();
		QuestionDef questionDef = (QuestionDef)propertiesObj;
		if((questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE ||
				questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE) &&
				!(index == DT_INDEX_SINGLE_SELECT || index == DT_INDEX_MULTIPLE_SELECT)){
			if(!Window.confirm(LocaleText.get("changeWidgetTypePrompt"))){
				index = (questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE) ? DT_INDEX_SINGLE_SELECT : DT_INDEX_MULTIPLE_SELECT;
				cbDataType.setSelectedIndex(index);
				return;
			}
			deleteKids = true;
		}
		else if((questionDef.getDataType() == QuestionDef.QTN_TYPE_REPEAT) &&
				!(index == DT_INDEX_REPEAT)){
			if(!Window.confirm(LocaleText.get("changeWidgetTypePrompt"))){
				index = DT_INDEX_REPEAT;
				cbDataType.setSelectedIndex(index);
				return;
			}
			deleteKids = true;
		}

		//cbDataType.setSelectedIndex(index);
		setQuestionDataType((QuestionDef)propertiesObj);
		formChangeListener.onFormItemChanged(propertiesObj);
		if(deleteKids)
			formChangeListener.onDeleteChildren(propertiesObj);
	}

	/**
	 * Sets the data type of a question definition object basing on selection
	 * in the type selection list box widget.
	 * 
	 * @param questionDef the question definition object.
	 */
	private void setQuestionDataType(QuestionDef questionDef){
		int dataType = QuestionDef.QTN_TYPE_TEXT;

		switch(cbDataType.getSelectedIndex()){
		case DT_INDEX_NUMBER:
			dataType = QuestionDef.QTN_TYPE_NUMERIC;
			break;
		case DT_INDEX_DECIMAL:
			dataType = QuestionDef.QTN_TYPE_DECIMAL;
			break;
		case DT_INDEX_DATE:
			dataType = QuestionDef.QTN_TYPE_DATE;
			break;
		case DT_INDEX_TIME:
			dataType = QuestionDef.QTN_TYPE_TIME;
			break;
		case DT_INDEX_DATE_TIME:
			dataType = QuestionDef.QTN_TYPE_DATE_TIME;
			break;
		case DT_INDEX_BOOLEAN:
			dataType = QuestionDef.QTN_TYPE_BOOLEAN;
			break;
		case DT_INDEX_SINGLE_SELECT:
			dataType = QuestionDef.QTN_TYPE_LIST_EXCLUSIVE;
			break;
		case DT_INDEX_MULTIPLE_SELECT:
			dataType = QuestionDef.QTN_TYPE_LIST_MULTIPLE;
			break;
		case DT_INDEX_REPEAT:
			dataType = QuestionDef.QTN_TYPE_REPEAT;
			break;
		case DT_INDEX_IMAGE:
			dataType = QuestionDef.QTN_TYPE_IMAGE;
			break;
		case DT_INDEX_VIDEO:
			dataType = QuestionDef.QTN_TYPE_VIDEO;
			break;
		case DT_INDEX_AUDIO:
			dataType = QuestionDef.QTN_TYPE_AUDIO;
			break;
		case DT_INDEX_SINGLE_SELECT_DYNAMIC:
			dataType = QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC;
			break;
		case DT_INDEX_GPS:
			dataType = QuestionDef.QTN_TYPE_GPS;
			break;
		}

		if(dataType == QuestionDef.QTN_TYPE_REPEAT && 
				questionDef.getDataType() != QuestionDef.QTN_TYPE_REPEAT)
			questionDef.setRepeatQtnsDef(new RepeatQtnsDef(questionDef));

		questionDef.setDataType(dataType);

		if(questionDef.getDataType() != QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC)
			dynamicListsView.setEnabled(false);
		else if(!dynamicListsView.isEnabled())
			dynamicListsView.setQuestionDef(questionDef);
	}

	/**
	 * Sets the listener for form change events.
	 * 
	 * @param formChangeListener the listener.
	 */
	public void setFormChangeListener(IFormChangeListener formChangeListener){
		this.formChangeListener = formChangeListener;
	}

	/**
	 * Sets values for widgets which deal with form definition properties.
	 * 
	 * @param formDef the form definition object.
	 */
	private void setFormProperties(FormDef formDef){
		enableQuestionOnlyProperties(false);

		txtText.setEnabled(true);
		txtFormKey.setEnabled(true);
		txtDescTemplate.setEnabled(Context.isStructureReadOnly() ? false : true);
		btnDescTemplate.setEnabled(Context.isStructureReadOnly() ? false : true);

		txtText.setText(formDef.getName());
		txtBinding.setText(formDef.getVariableName());
		txtFormKey.setText(formDef.getFormKey());
		//skipRulesView.setFormDef(formDef);

		txtDescTemplate.setText(formDef.getDescriptionTemplate());

		btnDescTemplate.setFormDef(formDef);
	}

	/**
	 * Sets values for widgets which deal with page definition properties.
	 * 
	 * @param pageDef the page definition object.
	 */
	private void setPageProperties(PageDef pageDef){
		enableQuestionOnlyProperties(false);

		txtText.setEnabled(true);
		txtDescTemplate.setEnabled(false);
		btnDescTemplate.setEnabled(false);
		txtFormKey.setEnabled(false);

		txtText.setText(pageDef.getName());
		txtBinding.setText(String.valueOf(pageDef.getPageNo()));
		//skipRulesView.updateSkipRule();
	}

	/**
	 * Sets values for widgets which deal with question definition properties.
	 * 
	 * @param questionDef the question definition object.
	 */
	private void setQuestionProperties(QuestionDef questionDef){
		enableQuestionOnlyProperties(true);
		txtDescTemplate.setEnabled(false);
		btnDescTemplate.setEnabled(false);
		txtFormKey.setEnabled(false);

		txtText.setText(questionDef.getText());
		txtBinding.setText(questionDef.getVariableName());
		txtHelpText.setText(questionDef.getHelpText());
		txtDefaultValue.setText(questionDef.getDefaultValue());

		chkVisible.setChecked(questionDef.isVisible());
		chkEnabled.setChecked(questionDef.isEnabled());
		chkLocked.setChecked(questionDef.isLocked());
		chkRequired.setChecked(questionDef.isRequired());

		setDataType(questionDef.getDataType());

		//Skip logic processing is a bit slow and hence we wanna update the 
		//UI with the rest of simple quick properties as we process skip logic
		DeferredCommand.addCommand(new Command(){
			public void execute() {
				skipRulesView.setQuestionDef((QuestionDef)propertiesObj);
				validationRulesView.setQuestionDef((QuestionDef)propertiesObj);
				dynamicListsView.setQuestionDef((QuestionDef)propertiesObj);
			}
		});
	}

	/**
	 * Sets values for widgets which deal with question option definition properties.
	 * 
	 * @param optionDef the option definition object.
	 */
	private void setQuestionOptionProperties(OptionDef optionDef){
		enableQuestionOnlyProperties(false);
		txtDescTemplate.setEnabled(false);
		btnDescTemplate.setEnabled(false);
		txtFormKey.setEnabled(false);

		txtText.setText(optionDef.getText());
		txtBinding.setText(optionDef.getVariableName());
		//skipRulesView.updateSkipRule();
	}

	/**
	 * Sets whether to enable question property widgets.
	 * 
	 * @param enabled true to enable them, false to disable them.
	 */
	private void enableQuestionOnlyProperties(boolean enabled){
		//boolean enable = (enabled && !Context.isStructureReadOnly()) ? true : false;
		boolean enable2 = (enabled && !Context.inLocalizationMode()) ? true : false;

		cbDataType.setEnabled(enable2);
		chkVisible.setEnabled(enable2);
		chkEnabled.setEnabled(enable2);
		chkLocked.setEnabled(enable2);
		chkRequired.setEnabled(enable2);
		txtDefaultValue.setEnabled(enable2);
		txtHelpText.setEnabled(enabled); //We allow localisation of help text.
		skipRulesView.setEnabled(enable2);
		validationRulesView.setEnabled(enable2);
		dynamicListsView.setEnabled(enable2);

		clearProperties();
	}

	/**
	 * Selects the current question's data type in the data types drop down listbox.
	 * 
	 * @param type the current question's data type.
	 */
	private void setDataType(int type){
		int index = DT_INDEX_NONE;

		switch(type){
		case QuestionDef.QTN_TYPE_DATE:
			index = DT_INDEX_DATE;
			break;
		case QuestionDef.QTN_TYPE_BOOLEAN:
			index = DT_INDEX_BOOLEAN;
			break;
		case QuestionDef.QTN_TYPE_DATE_TIME:
			index = DT_INDEX_DATE_TIME;
			break;
		case QuestionDef.QTN_TYPE_DECIMAL:
			index = DT_INDEX_DECIMAL;
			break;
		case QuestionDef.QTN_TYPE_LIST_EXCLUSIVE:
			index = DT_INDEX_SINGLE_SELECT;
			break;
		case QuestionDef.QTN_TYPE_LIST_MULTIPLE:
			index = DT_INDEX_MULTIPLE_SELECT;
			break;
		case QuestionDef.QTN_TYPE_NUMERIC:
			index = DT_INDEX_NUMBER;
			break;
		case QuestionDef.QTN_TYPE_REPEAT:
			index = DT_INDEX_REPEAT;
			break;
		case QuestionDef.QTN_TYPE_TEXT:
			index = DT_INDEX_TEXT;
			break;
		case QuestionDef.QTN_TYPE_TIME:
			index = DT_INDEX_TIME;
			break;
		case QuestionDef.QTN_TYPE_IMAGE:
			index = DT_INDEX_IMAGE;
			break;
		case QuestionDef.QTN_TYPE_VIDEO:
			index = DT_INDEX_VIDEO;
			break;
		case QuestionDef.QTN_TYPE_AUDIO:
			index = DT_INDEX_AUDIO;
			break;
		case QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC:
			index = DT_INDEX_SINGLE_SELECT_DYNAMIC;
			break;
		case QuestionDef.QTN_TYPE_GPS:
			index = DT_INDEX_GPS;
			break;
		}

		cbDataType.setSelectedIndex(index);
	}

	/**
	 * Clears values from all widgets.
	 */
	public void clearProperties(){
		cbDataType.setSelectedIndex(DT_INDEX_NONE);
		chkVisible.setChecked(false);
		chkEnabled.setChecked(false);
		chkLocked.setChecked(false);
		chkRequired.setChecked(false);
		txtDefaultValue.setText(null);
		txtHelpText.setText(null);
		txtText.setText(null);
		txtBinding.setText(null);
		txtDescTemplate.setText(null);
		txtFormKey.setText(null);
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormSelectionListener#onFormItemSelected(java.lang.Object)
	 */
	public void onFormItemSelected(Object formItem) {
		propertiesObj = formItem;

		clearProperties();

		//For now these may be options for boolean question types (Yes & No)
		if(formItem == null){
			enableQuestionOnlyProperties(false);
			txtText.setEnabled(false);
			txtFormKey.setEnabled(false);
			txtDescTemplate.setEnabled(false);
			btnDescTemplate.setEnabled(false);
			return;
		}

		txtBinding.setEnabled(Context.allowBindEdit() && !Context.isStructureReadOnly());

		if(formItem instanceof FormDef)
			setFormProperties((FormDef)formItem);
		else if(formItem instanceof PageDef)
			setPageProperties((PageDef)formItem);
		else if(formItem instanceof QuestionDef)
			setQuestionProperties((QuestionDef)formItem);
		else if(formItem instanceof OptionDef)
			setQuestionOptionProperties((OptionDef)formItem);
	}

	/**
	 * Sets focus to the first input widget.
	 */
	public void setFocus(){
		txtText.setFocus(true);
		txtText.selectAll();
	}

	/**
	 * @see com.google.gwt.user.client.WindowResizeListener#onWindowResized(int, int)
	 */
	public void onWindowResized(int width, int height){
		setWidth("100%");
		setHeight("100%");
		validationRulesView.onWindowResized(width, height);
	}

	/**
	 * Retrieves changes from all widgets and updates the selected object.
	 */
	public void commitChanges(){
		skipRulesView.updateSkipRule();
		validationRulesView.updateValidationRule();
		dynamicListsView.updateDynamicLists();
	}

	/**
	 * @see org.purc.purcforms.client.controller.ItemSelectionListener#onItemSelected(Object, Object)
	 */
	public void onItemSelected(Object sender, Object item) {
		if(sender instanceof DescTemplateWidget){
			txtDescTemplate.setText(txtDescTemplate.getText() + item);
			updateDescTemplate(); //Added for IE which does not properly throw change events for the desc template textbox
			txtDescTemplate.setFocus(true);
		}
	}

	/**
	 * @see org.purc.purcforms.client.controller.ItemSelectionListener#onStartItemSelection(Object)
	 */
	public void onStartItemSelection(Object sender) {

	}

	/**
	 * Sets the listener to form action events.
	 * 
	 * @param formActionListener the listener.
	 */
	public void setFormActionListener(IFormActionListener formActionListener){
		this.formActionListener = formActionListener;
	}

	@Override
	public void onBrowserEvent(Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONKEYDOWN:
			if(!isVisible())
				return;

			int keyCode = event.getKeyCode();
			if(event.getCtrlKey()){
				if(keyCode == 'N' || keyCode == 'n'){
					formActionListener.addNewItem();
					DOM.eventPreventDefault(event);
				}
				else if(keyCode == KeyboardListener.KEY_RIGHT){
					formActionListener.moveToChild();
					DOM.eventPreventDefault(event);
				}
				else if(keyCode == KeyboardListener.KEY_LEFT){
					formActionListener.moveToParent();
					DOM.eventPreventDefault(event);
				}
				else if(keyCode == KeyboardListener.KEY_UP){
					formActionListener.moveUp();
					DOM.eventPreventDefault(event);
				}
				else if(keyCode == KeyboardListener.KEY_DOWN){
					formActionListener.moveDown();
					DOM.eventPreventDefault(event);
				}
			}
		}
	}
}
