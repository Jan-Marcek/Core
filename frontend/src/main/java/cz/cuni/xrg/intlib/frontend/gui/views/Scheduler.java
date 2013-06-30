package cz.cuni.xrg.intlib.frontend.gui.views;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import cz.cuni.xrg.intlib.commons.app.execution.ExecutionStatus;
import cz.cuni.xrg.intlib.commons.app.execution.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.gui.ViewComponent;
import cz.cuni.xrg.intlib.frontend.gui.ViewNames;
import cz.cuni.xrg.intlib.frontend.gui.components.IntlibPagedTable;
import cz.cuni.xrg.intlib.frontend.gui.components.SchedulePipeline;

class Scheduler extends ViewComponent {

	private VerticalLayout mainLayout;
	
	private Label label;
	private IntlibPagedTable schedulerTable;
	private IndexedContainer tableData;
	
	static String[] visibleCols = new String[]{"pipeline", "rule", "user",
		"last", "next", "status", "commands"};

	static String[] headers = new String[]{"pipeline", "Rule", "User", "Last",
		"Next", "Status", "Commands"};
	private SchedulerTableFilter tableDataFilter = null;
	private DateFormat localDateFormat = null;
	
	private DateField dateLastFilter;
	private DateField dateNextFilter;


	private TextField pipelineFilter;
	private TextField ruleFilter;

	private TextField userFilter;

	private ComboBox statusFilter;

	private ComboBox debugFilter;
	int style = DateFormat.MEDIUM;
	
	static String filter;


	public Scheduler() { }

	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
//		mainLayout.setWidth("600px");
//		mainLayout.setHeight("800px");
		
		// top-level component properties
		setWidth("100%");
		setHeight("100%");
		
			filter = new String();

			GridLayout filtersLayout = new GridLayout(7, 1);
			filtersLayout.setWidth("100%");
			filtersLayout.setSpacing(true);
			
			pipelineFilter = new TextField();
			pipelineFilter.setImmediate(true);
			pipelineFilter.setCaption("Pipeline:");
			pipelineFilter.setInputPrompt("name of pipeline");
			pipelineFilter.setWidth("110px");
			pipelineFilter.setTextChangeEventMode(TextChangeEventMode.LAZY);
			pipelineFilter.addTextChangeListener(new TextChangeListener() {
				@Override
				public void textChange(TextChangeEvent event) {

					tableDataFilter.setPipelineFilter(event.getText());
					tableData.removeAllContainerFilters();
					tableData.addContainerFilter(tableDataFilter);
					schedulerTable.refreshRowCache();

				}
			});

			filtersLayout.addComponent(pipelineFilter, 0, 0);
			filtersLayout.setColumnExpandRatio(1, 0.08f);
			filtersLayout.setComponentAlignment(pipelineFilter, Alignment.BOTTOM_LEFT);
			
			ruleFilter = new TextField();
			ruleFilter.setImmediate(true);
			ruleFilter.setCaption("Rule:");
			ruleFilter.setInputPrompt("rule");
			ruleFilter.setWidth("110px");
			ruleFilter.setTextChangeEventMode(TextChangeEventMode.LAZY);
			ruleFilter.addTextChangeListener(new TextChangeListener() {
				@Override
				public void textChange(TextChangeEvent event) {

					tableDataFilter.setRuleFilter(event.getText());
					tableData.removeAllContainerFilters();
					tableData.addContainerFilter(tableDataFilter);
					schedulerTable.refreshRowCache();

				}
			});

			filtersLayout.addComponent(ruleFilter, 1, 0);
			filtersLayout.setColumnExpandRatio(1, 0.08f);
			filtersLayout.setComponentAlignment(ruleFilter, Alignment.BOTTOM_LEFT);

			userFilter = new TextField();
			userFilter.setCaption("User:");
			userFilter.setInputPrompt("user name");
			userFilter.setWidth("110px");
			userFilter.addTextChangeListener(new TextChangeListener() {
				@Override
				public void textChange(TextChangeEvent event) {

					tableDataFilter.setUserFilter(event.getText());
					tableData.removeAllContainerFilters();
					tableData.addContainerFilter(tableDataFilter);
					schedulerTable.refreshRowCache();

				}
			});

			filtersLayout.addComponent(userFilter, 2, 0);
			filtersLayout.setColumnExpandRatio(2, 0.08f);
			filtersLayout.setComponentAlignment(userFilter, Alignment.BOTTOM_LEFT);

			
			dateLastFilter = new DateField();
			dateLastFilter.setImmediate(true);
			dateLastFilter.setCaption("Last date (from):");
			dateLastFilter.setWidth("110px");
			dateLastFilter.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					// TODO Auto-generated method stub

					if (event.getProperty().getValue() != null) {
						DateFormat df = DateFormat.getDateInstance(style, getLocale());
						String s = df.format(event.getProperty().getValue());

						tableDataFilter.setDateLastFilter(s);
						tableData.removeAllContainerFilters();
						tableData.addContainerFilter(tableDataFilter);
						schedulerTable.refreshRowCache();

					} else {
						tableDataFilter.setDateLastFilter("");
						tableData.removeAllContainerFilters();
						tableData.addContainerFilter(tableDataFilter);
						schedulerTable.refreshRowCache();
					}
				}
			});



			filtersLayout.addComponent(dateLastFilter, 3, 0);
			filtersLayout.setColumnExpandRatio(0, 0.08f);
			filtersLayout.setComponentAlignment(dateLastFilter, Alignment.BOTTOM_LEFT);

			
			dateNextFilter = new DateField();
			dateNextFilter.setImmediate(true);
			dateNextFilter.setCaption("Next date (from):");
			dateNextFilter.setWidth("110px");
			dateNextFilter.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					// TODO Auto-generated method stub

					if (event.getProperty().getValue() != null) {
						DateFormat df = DateFormat.getDateInstance(style, getLocale());
						String s = df.format(event.getProperty().getValue());

						//				Format formatter = new SimpleDateFormat("dd.MM.yyyy");
						//				String s = formatter.format(event.getProperty().getValue().toString().toUpperCase(locale));

						tableDataFilter.setDateNextFilter(s);
						tableData.removeAllContainerFilters();
						tableData.addContainerFilter(tableDataFilter);
						schedulerTable.refreshRowCache();

					} else {
						tableDataFilter.setDateNextFilter("");
						tableData.removeAllContainerFilters();
						tableData.addContainerFilter(tableDataFilter);
						schedulerTable.refreshRowCache();
					}
				}
			});



			filtersLayout.addComponent(dateNextFilter, 4, 0);
			filtersLayout.setColumnExpandRatio(0, 0.08f);
			filtersLayout.setComponentAlignment(dateNextFilter, Alignment.BOTTOM_LEFT);

			if (tableDataFilter == null) {
				DateFormat df =DateFormat.getDateInstance(style, getLocale());
				tableDataFilter = new SchedulerTableFilter(df);
			}



			statusFilter = new ComboBox();
			//statusFilter.setNullSelectionAllowed(false);
			statusFilter.setImmediate(true);
			statusFilter.setCaption("Status:");
			statusFilter.setInputPrompt("status");
			statusFilter.setWidth("110px");
			statusFilter.setTextInputAllowed(false);

			statusFilter.addItem("Enabled");
			statusFilter.addItem("Disabled");
			statusFilter.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					// TODO Auto-generated method stub
					Object a = event.getProperty().getValue();
					if (event.getProperty().getValue() != null) {
						tableDataFilter.setStatusFilter(event.getProperty()
								.getValue().toString());
						tableData.removeAllContainerFilters();
						tableData.addContainerFilter(tableDataFilter);
						schedulerTable.refreshRowCache();

					} else {
						tableDataFilter.setStatusFilter("");
						tableData.removeAllContainerFilters();
						tableData.addContainerFilter(tableDataFilter);
						schedulerTable.refreshRowCache();
					}
				}
			});
			filtersLayout.addComponent(statusFilter, 5, 0);
			filtersLayout.setColumnExpandRatio(3, 0.08f);
			filtersLayout.setComponentAlignment(statusFilter, Alignment.BOTTOM_LEFT);

	

			Button buttonDeleteFilters = new Button();
			buttonDeleteFilters.setCaption("Clear Filters");
			buttonDeleteFilters.setHeight("25px");
			buttonDeleteFilters.setWidth("110px");
			buttonDeleteFilters
					.addClickListener(new com.vaadin.ui.Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					// TODO Auto-generated method stub

					dateLastFilter.setValue(null);
					pipelineFilter.setValue("");
					userFilter.setValue("");
					statusFilter.setValue(null);
					dateLastFilter.setValue(null);
					tableDataFilter.setPipelineFilter("");
					tableDataFilter.setRuleFilter("");
					tableDataFilter.setUserFilter("");

					tableData.removeAllContainerFilters();
					tableData.addContainerFilter(tableDataFilter);

					schedulerTable.refreshRowCache();

				}
			});
			filtersLayout.addComponent(buttonDeleteFilters, 6, 0);
			filtersLayout.setColumnExpandRatio(5, 0.7f);
			filtersLayout.setComponentAlignment(buttonDeleteFilters,
					Alignment.BOTTOM_RIGHT);
			mainLayout.addComponent(filtersLayout);

		
		
		tableData = getTableData();
		

		schedulerTable = new IntlibPagedTable();
		schedulerTable.setSelectable(true);
		schedulerTable.setContainerDataSource(tableData);
		schedulerTable.setWidth("100%");
		schedulerTable.setHeight("100%");
		schedulerTable.setImmediate(true);
		schedulerTable.setVisibleColumns(visibleCols); // Set visible columns
		schedulerTable.setColumnHeaders(headers);
		
		schedulerTable.addGeneratedColumn("commands",
				new actionColumnGenerator());
		
		mainLayout.addComponent(schedulerTable);
		mainLayout.addComponent(schedulerTable.createControls());
		schedulerTable.setPageLength(10);

		Button addRuleButton = new Button();
		addRuleButton.setCaption("Add new scheduling rule");
		addRuleButton
		.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// open scheduler dialog

				SchedulePipeline  sch = new SchedulePipeline();
				openScheduler(sch);
				

			}
		});
		mainLayout.addComponent(addRuleButton);
		
		
		return mainLayout;
	}
	
	
	
	public static IndexedContainer getTableData() {

		String[] pipeline = { "Extract data from TED", "Legal pipeline 1", "Legal data cleaner" };
		String[] rule = { "Run on 1.9.2013 at 6:00 and then repeat every day", "Run on 3.9.2013 at 0:00 and then repeat every week", "Run after Legal pipeline 1 finishes" };
		String[] user = { "tomask", "tomask", "jirit" };
		String[] last = { "6.9.2013 6:06", "3.9.2013 1:56", "3.9.2013 2:22" };
		String[] next = { "7.9.2013 6:00", "10.9.2013 0:00", "N/A" };
		String[] status = { "Enabled", "Enabled", "Disabled" };


		IndexedContainer result = new IndexedContainer();

		for (String p : visibleCols) {
			result.addContainerProperty(p, String.class, "");
		}

		int  max = getMinLength(pipeline, rule, user,last,next,status);

		for (int i = 0; i < max; i++) {
			Object num = result.addItem();
			result.getContainerProperty(num, "pipeline").setValue(pipeline[i]);
			result.getContainerProperty(num, "rule").setValue(rule[i]);
			result.getContainerProperty(num, "user").setValue(user[i]);
			result.getContainerProperty(num, "last").setValue(last[i]);
			result.getContainerProperty(num, "next").setValue(next[i]);
			result.getContainerProperty(num, "status").setValue(status[i]);
			// result.getContainerProperty(num, "act").setValue(actions[i]);

		}

		return result;

	}
	
	private final static int UNDEFINED_LENGTH = -1;

	public static int getMinLength(String[]... arraysLength) {
		int min = UNDEFINED_LENGTH;
		for (int i = 0; i < arraysLength.length; i++) {
			if (min == UNDEFINED_LENGTH) {
				min = arraysLength[i].length;
			} else {
				min = Math.min(min, arraysLength[i].length);
			}
		}
		return min;

	}

	private void openScheduler(final SchedulePipeline schedule) {
		Window scheduleWindow = new Window("Schedule a pipeline", schedule);
		scheduleWindow.setImmediate(true);
		scheduleWindow.setWidth("820px");
		scheduleWindow.setHeight("550px");
		scheduleWindow.addCloseListener(new Window.CloseListener() {
			@Override
			public void windowClose(Window.CloseEvent e) {
				//closeDebug();
			}
		});
		scheduleWindow.addResizeListener(new Window.ResizeListener() {

			@Override
			public void windowResized(Window.ResizeEvent e) {
				schedule.resize(e.getWindow().getHeight());
			}
		});
		App.getApp().addWindow(scheduleWindow);
	}


	@Override
	public void enter(ViewChangeEvent event) {
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}
	
	class actionColumnGenerator implements com.vaadin.ui.Table.ColumnGenerator {
		private ClickListener clickListener = null;
		@Override
		public Object generateCell(final Table source, final Object itemId,
				Object columnId) {
			Property propStatus = source.getItem(itemId).getItemProperty("status");
			String testStatus = "---";

			
			HorizontalLayout layout = new HorizontalLayout();
			
			
			if (propStatus.getType().equals(String.class)) {
				
				testStatus = (String) propStatus.getValue().toString();

				if (testStatus == "Disabled") {
					Button enableButton = new Button("Enable");
					layout.addComponent(enableButton);
						

				}
				else{
					Button disableButton = new Button();
					disableButton.setCaption("Disable");
					layout.addComponent(disableButton);
				}
				
			}

		
			Button editButton = new Button();
			editButton.setCaption("Edit");
			editButton
			.addClickListener(new com.vaadin.ui.Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					// open scheduler dialog

					SchedulePipeline  sch = new SchedulePipeline();
					openScheduler(sch);
					

				}
			});
			layout.addComponent(editButton);

			Button deleteButton = new Button();
			deleteButton.setCaption("Delete");
			layout.addComponent(deleteButton);
			
			
			Button monitorButton = new Button();
			monitorButton.setCaption("View monitor");
			layout.addComponent(monitorButton);

			return layout;
		}

	}



}
	
	class SchedulerTableFilter implements Filter {

		/**
		 * Filters for Scheduler Table
		 */
		private static final long serialVersionUID = 1L;
		// private String needle;

		private Date dateLastFilter;
		private Date dateNextFilter;

		private String userFilter;

		private String pipelineFilter;
		private String ruleFilter;

		private String statusFilter;

		private String debugFilter;

		private DateFormat df = null;
		public SchedulerTableFilter(DateFormat dateFormat) {
			this.df = dateFormat;
			// this.needle = needle.toLowerCase();
		}

		public void setPipelineFilter(String value) {
			this.pipelineFilter = value.toLowerCase();

		}

		public void setRuleFilter(String value) {
			this.ruleFilter = value.toLowerCase();

		}


		public void setUserFilter(String value) {
			this.userFilter = value.toLowerCase();

		}

		public void setStatusFilter(String value) {
			this.statusFilter = value.toLowerCase();

		}

		public void setDebugFilter(String value) {
			this.debugFilter = value.toLowerCase();

		}

		public void setDateLastFilter(String value) {
			Date date = null;
			if (value != "") {
				try {
					date = df.parse(value);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			this.dateLastFilter = date;

		}
		
		public void setDateNextFilter(String value) {
			Date date = null;
			if (value != "") {
				try {
					date = df.parse(value);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			this.dateNextFilter = date;

		}

		private boolean stringIsSet(String value) {
			if (value != null && value.length() > 0) {
				return true;
			}
			return false;
		}
		
		private boolean dateIsSet(Date value) {
			if (value != null) {
				return true;
			}
			return false;
		}


		public boolean passesFilter(Object itemId, Item item) {

			if (stringIsSet(this.userFilter)) {
				String objectUser = ((String) item.getItemProperty("user")
						.getValue()).toLowerCase();
				if (objectUser.contains(this.userFilter) == false) {
					return false;
				}

			}
			if (stringIsSet(this.pipelineFilter)) {
				String objectPipeline = ((String) item.getItemProperty("pipeline")
						.getValue()).toLowerCase();
				if (objectPipeline.contains(this.pipelineFilter) == false) {
					return false;
				}
			}
			
			if (stringIsSet(this.ruleFilter)) {
				String objectRule = ((String) item.getItemProperty("rule")
						.getValue()).toLowerCase();
				if (objectRule.contains(this.ruleFilter) == false) {
					return false;
				}
			}

			if (stringIsSet(this.statusFilter)) {
				String objectStatus = ((String) item.getItemProperty("status")
						.getValue()).toLowerCase();
				if (objectStatus.contains(this.statusFilter) == false) {
					return false;
				}
			}

			if (stringIsSet(this.debugFilter)) {
				String objectDebug = ((String) item.getItemProperty("debug")
						.getValue()).toLowerCase();
				if (objectDebug.contains(this.debugFilter) == false) {
					return false;
				}
			}

			if (dateIsSet(this.dateLastFilter)) {
				String objectDate = ((String) item.getItemProperty("last")
						.getValue()).toString();

				Date date = null;
				if (objectDate != "") {
					try {
						date = df.parse(objectDate);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return false;
					}

					if (date.getTime() < this.dateLastFilter.getTime()) {
						return false;
					}
				} else
					return false;
			}
			
			if (dateIsSet(this.dateNextFilter)) {
				String objectDate = ((String) item.getItemProperty("next")
						.getValue()).toString();

				Date date = null;
				if (objectDate != "") {
					try {
						date = df.parse(objectDate);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return false;
					}

					if (date.getTime() < this.dateNextFilter.getTime()) {
						return false;
					}
				} else
					return false;
			}

			return true;
		}

		public boolean appliesToProperty(Object id) {
			return true;
		}
	}
