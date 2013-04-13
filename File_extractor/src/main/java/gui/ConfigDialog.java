package gui;

import module.Config;

import com.vaadin.ui.*;

import cz.cuni.xrg.intlib.commons.configuration.*;

/**
 * Configuration dialog.
 * @author Petyr
 *
 */
public class ConfigDialog extends CustomComponent {

	private static final long serialVersionUID = 1L;
	
	/**
	 * TODO Implement your own configuration component. You can use vaadin visual editor if you like.
	 * Just remember don't use vaddin classes the ere not located directly in package com.vaadi.ui; 
	 */
	
	private GridLayout mainLayout;
	private Button buttonCanc;
	private Button buttonSave;
	private Button buttonDev;
	private ComboBox comboBoxFormat; //RDFFormat
	private Label labelFormat;
	private TextField textFieldOnly;
	private Label labelOnly;
	private CheckBox checkBoxWhole;
	private TextField textFieldPath; //Path
	private GridLayout gridLayoutName;
	private TextArea textAreaDescr; //Description
	private Label labelDescr;
	private TextField textFieldName; //NameDPU
	private Label labelName;
	private HorizontalLayout horizontalLayoutOnly;
	private HorizontalLayout horizontalLayoutButtons;
	private HorizontalLayout horizontalLayoutFormat;
		
	public ConfigDialog() {
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}
	
	/**
	 * Return current configuration from dialog. Can return null, if 
	 * current configuration is invalid.
	 * @return current configuration or null
	 */
	public Configuration getConfiguration() { 
		Configuration config = new Configuration();
		/**
		 * TODO Gather data from you dialog and store them into configuration. You can use 
		 * 	enum Config to make sure that you don't miss spell the ids of values.
		 * 	Also remember that you can return null in case of invalid configuration in dialog.
		 */

		config.setValue(Config.NameDPU.name(), textFieldName.getValue());
		config.setValue(Config.Description.name(), textAreaDescr.getValue());
		config.setValue(Config.Path.name(), textFieldPath.getValue());
		config.setValue(Config.RDFFormat.name(), comboBoxFormat.getValue());

		
		return config;
	}
	
	/**
	 * Load values from configuration into dialog.
	 * @throws ConfigurationException
	 * @param conf
	 */
	public void setConfiguration(Configuration conf) {
		/**
		 * TODO Load configuration from conf into dialog components. You can use 
		 * 	enum Config to make sure that you don't miss spell the ids of values.
		 *  The ConfigurationException can be thrown in case of invalid configuration.
		 */
		
		try
		{
			
            textFieldName.setValue( (String) conf.getValue(Config.NameDPU.name()));
			textAreaDescr.setValue( (String) conf.getValue(Config.Description.name()));
			textFieldPath.setValue( (String) conf.getValue(Config.Path.name()));
			comboBoxFormat.setValue( (String) conf.getValue(Config.RDFFormat.name()));

			
		
                }
                
		catch(Exception ex) {
			// throw setting exception
			throw new ConfigurationException();
		}
	}
	
	private GridLayout buildMainLayout() {
		
	/**
	 * TODO Build your component here.
	 */
		// common part: create layout
		mainLayout = new GridLayout(2, 6);
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(true);
		
		// top-level component properties
		setWidth("100%");
		setHeight("100%");
		
		// gridLayoutName
		gridLayoutName = buildGridLayoutName();
		mainLayout.addComponent(gridLayoutName, 0,0,1,1);
		mainLayout.setComponentAlignment(gridLayoutName, Alignment.TOP_CENTER);
		
		// textFieldPath
		textFieldPath = new TextField();
		textFieldPath.setCaption("Path to file or directory:");
		textFieldPath.setImmediate(false);
		textFieldPath.setWidth("85%");
		textFieldPath.setHeight("-1px");
		mainLayout.addComponent(textFieldPath, 0,2,1,2);
		
		// checkBoxWhole
		checkBoxWhole = new CheckBox();
		checkBoxWhole.setCaption("Process whole directory.");
		checkBoxWhole.setImmediate(false);
		checkBoxWhole.setWidth("-1px");
		checkBoxWhole.setHeight("-1px");
		mainLayout.addComponent(checkBoxWhole, 0,3);
		
		// layoutOnly
		horizontalLayoutOnly = buildHorizontalLayoutOnly();
		mainLayout.addComponent(horizontalLayoutOnly,1,3);
		
	

		// horizontalLayoutFormat
		horizontalLayoutFormat = buildHorizontalLayoutFormat();
		mainLayout.addComponent(horizontalLayoutFormat,0,4);
		
		// buttonDev
		buttonDev = new Button();
		buttonDev.setCaption("Develop");
		buttonDev.setImmediate(true);
		buttonDev.setWidth("-1px");
		buttonDev.setHeight("-1px");
		mainLayout.addComponent(buttonDev, 0,5);
	
		
		horizontalLayoutButtons = buildHorizontalLayout();
		mainLayout.addComponent(horizontalLayoutButtons,1,5);

		return mainLayout;
	}

	
	private GridLayout buildGridLayoutName() {
		// common part: create layout
		gridLayoutName = new GridLayout();
		gridLayoutName.setImmediate(false);
		gridLayoutName.setWidth("100%");
		gridLayoutName.setHeight("-1px");
		gridLayoutName.setMargin(false);
		gridLayoutName.setSpacing(true);
		gridLayoutName.setColumns(2);
		gridLayoutName.setRows(2);
		
		// labelName
		labelName = new Label();
		labelName.setImmediate(false);
		labelName.setWidth("-1px");
		labelName.setHeight("-1px");
		labelName.setValue("Name:");
		gridLayoutName.addComponent(labelName, 0, 0);
		
		// textFieldName
		textFieldName = new TextField();
		textFieldName.setImmediate(false);
		textFieldName.setWidth("267px");
		textFieldName.setHeight("-1px");
		gridLayoutName.addComponent(textFieldName, 1, 0);
		
		// labelDescr
		labelDescr = new Label();
		labelDescr.setImmediate(false);
		labelDescr.setWidth("-1px");
		labelDescr.setHeight("-1px");
		labelDescr.setValue("Description:");
		gridLayoutName.addComponent(labelDescr, 0, 1);
		
		// textAreaDescr
		textAreaDescr = new TextArea();
		textAreaDescr.setImmediate(false);
		textAreaDescr.setWidth("267px");
		textAreaDescr.setHeight("36px");
		gridLayoutName.addComponent(textAreaDescr, 1, 1);
		
		return gridLayoutName;
	}
	private HorizontalLayout buildHorizontalLayoutOnly() {
		// common part: create layout
		horizontalLayoutOnly = new HorizontalLayout();
		horizontalLayoutOnly.setImmediate(false);
		horizontalLayoutOnly.setWidth("-1px");
		horizontalLayoutOnly.setHeight("-1px");
		horizontalLayoutOnly.setMargin(false);
		horizontalLayoutOnly.setSpacing(true);
		
		// labelOnly
		labelOnly = new Label();
		labelOnly.setImmediate(false);
		labelOnly.setWidth("-1px");
		labelOnly.setHeight("-1px");
		labelOnly.setValue("Only files:");
		horizontalLayoutOnly.addComponent(labelOnly);
		
		// textFieldOnly
		textFieldOnly = new TextField();
		textFieldOnly.setImmediate(false);
		textFieldOnly.setWidth("52px");
		textFieldOnly.setHeight("-1px");
		horizontalLayoutOnly.addComponent(textFieldOnly);
		
		return horizontalLayoutOnly;
	}
	
	private HorizontalLayout buildHorizontalLayoutFormat() {
		// common part: create layout
		horizontalLayoutFormat = new HorizontalLayout();
		horizontalLayoutFormat.setImmediate(false);
		horizontalLayoutFormat.setWidth("-1px");
		horizontalLayoutFormat.setHeight("-1px");
		horizontalLayoutFormat.setMargin(false);
		horizontalLayoutFormat.setSpacing(true);
		
		// labelFormat
		labelFormat = new Label();
		labelFormat.setImmediate(false);
		labelFormat.setWidth("-1px");
		labelFormat.setHeight("-1px");
		labelFormat.setValue("RDF Format:");
		horizontalLayoutFormat.addComponent(labelFormat);
		
		// comboBoxFormat
		comboBoxFormat = new ComboBox();
		comboBoxFormat.setImmediate(false);
		comboBoxFormat.setWidth("-1px");
		comboBoxFormat.setHeight("-1px");
		horizontalLayoutFormat.addComponent(comboBoxFormat);
		
		return horizontalLayoutFormat;
	}


	private HorizontalLayout buildHorizontalLayout() {
		// common part: create layout
		horizontalLayoutButtons = new HorizontalLayout();
		horizontalLayoutButtons.setImmediate(false);
		horizontalLayoutButtons.setWidth("240px");
		horizontalLayoutButtons.setHeight("1px");
		horizontalLayoutButtons.setMargin(false);
		horizontalLayoutButtons.setSpacing(true);

		
		// buttonSave
		buttonSave = new Button();
		buttonSave.setCaption("Save & Commit");
		buttonSave.setImmediate(true);
		buttonSave.setWidth("-1px");

		buttonSave.setHeight("-1px");
		horizontalLayoutButtons.addComponent(buttonSave);
		
		// buttonCanc
		buttonCanc = new Button();
		buttonCanc.setCaption("Cancel");
		buttonCanc.setImmediate(true);
		buttonCanc.setWidth("-1px");
		buttonCanc.setHeight("-1px");
		horizontalLayoutButtons.addComponent(buttonCanc);
		
		return horizontalLayoutButtons;
	}


}
