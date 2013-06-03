package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TabSheet.Tab;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.execution.DataUnitInfo;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionContextFactory;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionContextReader;
import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.commons.app.pipeline.ExecutionStatus;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Node;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.browser.BrowserInitFailedException;
import cz.cuni.xrg.intlib.frontend.browser.DataUnitBrowser;
import cz.cuni.xrg.intlib.frontend.browser.DataUnitBrowserFactory;
import cz.cuni.xrg.intlib.frontend.browser.DataUnitNotFoundException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bogo
 */
public class DebuggingView extends CustomComponent {

	private VerticalLayout mainLayout;
	private PipelineExecution pipelineExec;
	private ExecutionContextReader ctxReader;
	private DPUInstance debugDpu;
	private boolean isInDebugMode;
	private RecordsTable executionRecordsTable;
	private Tab browseTab;
	private Tab logTab;
	private Tab queryTab;
	private Tab infoTab;
	private TabSheet tabs;
	private TextArea logTextArea;
	private QueryView queryView;
	private HorizontalLayout refreshComponent;

	public DebuggingView(PipelineExecution pipelineExec, DPUInstance debugDpu, boolean debug) {
		//setCaption("Debug window");
		this.pipelineExec = pipelineExec;
		this.debugDpu = debugDpu;
		this.isInDebugMode = debug;
		buildMainLayout();
		setCompositionRoot(mainLayout);
		//this.setContent(mainLayout);
	}

	public final void buildMainLayout() {
		mainLayout = new VerticalLayout();

		executionRecordsTable = new RecordsTable();
		executionRecordsTable.setWidth("100%");
		executionRecordsTable.setHeight("160px");

		mainLayout.addComponent(executionRecordsTable);

		ComboBox dpuSelector = new ComboBox("Select DPU:");
		dpuSelector.setImmediate(true);
		for (Node node : pipelineExec.getPipeline().getGraph().getNodes()) {
			dpuSelector.addItem(node.getDpuInstance());
		}
		if(debugDpu != null) {
			dpuSelector.select(debugDpu);
		}
		dpuSelector.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				Object value = event.getProperty().getValue();
				if (value != null && value.getClass() == DPUInstance.class) {
					debugDpu = (DPUInstance) value;
					refreshContent();
				}
			}
		});
		mainLayout.addComponent(dpuSelector);

		tabs = new TabSheet();
		tabs.setSizeFull();

		browseTab = tabs.addTab(new Label("Browser"), "Browse");
		
		refreshComponent = buildRefreshComponent();

		logTextArea = new TextArea("Log from log4j", "Log file content");
		VerticalLayout logLayout = new VerticalLayout();
		logLayout.addComponent(refreshComponent);
		logLayout.addComponent(logTextArea);
		logLayout.setSizeFull();
		logTextArea.setRows(30);
		logTextArea.setSizeFull();
		logTab = tabs.addTab(logLayout, "Log");

		queryView = new QueryView(this);
		if(debugDpu != null) {
			queryView.setGraphs(debugDpu.getDpu().getType());
		}
		queryTab = tabs.addTab(queryView, "Query");

		mainLayout.setSizeFull();
		mainLayout.addComponent(tabs);

		fillContent();

	}

	public void fillContent() {
		boolean loadSuccessful = loadExecutionContextReader();

		//List<Record> records = debugDpu == null ? App.getDPUs().getAllDPURecords() : App.getDPUs().getAllDPURecords(debugDpu);
		List<Record> records = App.getDPUs().getAllDPURecords(pipelineExec);
		//records = filterRecords(records, pipelineExec);
		executionRecordsTable.setDataSource(records);

		boolean isRunFinished = !(pipelineExec.getExecutionStatus() == ExecutionStatus.SCHEDULED || pipelineExec.getExecutionStatus() == ExecutionStatus.RUNNING);

		//Table with data
		if (loadSuccessful && isInDebugMode && debugDpu != null && isRunFinished) {
			DataUnitBrowser browser = loadBrowser(false);
			if (browser != null) {
				tabs.removeTab(browseTab);
				browseTab = tabs.addTab(browser, "Browse");
				browseTab.setEnabled(true);
				tabs.setSelectedTab(browseTab);
			} else {
				browseTab.setEnabled(false);
				loadSuccessful = false;
			}
		} else {
			browseTab.setEnabled(false);
		}

		//Content of text log file
		if (loadSuccessful) {
			File logFile = ctxReader.getLogFile();
			String logText = "Log file is empty!";
			if (logFile.exists()) {
				try {
					Scanner scanner = new Scanner(logFile).useDelimiter("\\A");
					if (scanner.hasNext()) {
						logText = scanner.next();
					}
				} catch (FileNotFoundException ex) {
					Logger.getLogger(DebuggingView.class.getName()).log(Level.SEVERE, null, ex);
					logText = "Failed to load log file!";
				}
			} else {
				logText = "Log file doesn't exist!";
			}
			logTextArea.setValue(logText);
			logTab.setEnabled(true);
		} else {
			//logTab.setEnabled(false);
		}

		//Query View
		if (loadSuccessful && isInDebugMode && debugDpu != null && isRunFinished) {
			queryTab.setEnabled(true);
		} else {
			queryTab.setEnabled(false);
		}

		//Create tab with information about running pipeline and refresh button
		if(infoTab != null) {
			tabs.removeTab(infoTab);

		}
		
		boolean showRefresh = !loadSuccessful && isInDebugMode || !isRunFinished;
		refreshComponent.setVisible(showRefresh);
	}

	private void refreshContent() {
		pipelineExec = App.getPipelines().getExecution(pipelineExec.getId());
		fillContent();
		if(debugDpu != null) {
			queryView.setGraphs(debugDpu.getDpu().getType());
		}
		setCompositionRoot(mainLayout);
	}

	private boolean loadExecutionContextReader() {
		String workingDirPath = pipelineExec.getWorkingDirectory();
		File workingDir = new File(workingDirPath);
		try {
			ctxReader = ExecutionContextFactory.restoreAsRead(workingDir);
		} catch (FileNotFoundException ex) {
			Logger.getLogger(DebuggingView.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}
		return true;
	}

	private DataUnitBrowser loadBrowser(boolean showInput) {
		if (debugDpu == null) {
			return null;
		}
		Set<Integer> indexes = ctxReader.getIndexesForDataUnits(debugDpu);
		if (indexes == null) {
			return null;
		}

		Iterator<Integer> iter = indexes.iterator();
		while (iter.hasNext()) {
			Integer index = iter.next();
			DataUnitInfo duInfo = ctxReader.getDataUnitInfo(debugDpu, index);
			if (indexes.size() == 1 || duInfo.isInput() == showInput) {
				DataUnitBrowser duBrowser;
				try {
					String dumpDirName = "ex" + pipelineExec.getId() + "_dpu-" + index;
					duBrowser = DataUnitBrowserFactory.getBrowser(ctxReader, debugDpu, index, dumpDirName);
				} catch (DataUnitNotFoundException | BrowserInitFailedException ex) {
					Logger.getLogger(DebuggingView.class.getName()).log(Level.SEVERE, null, ex);
					return null;
				}
				
				duBrowser.enter();
				return duBrowser;
			}

		}
		return null;
	}

	private List<Record> filterRecords(List<Record> records, PipelineExecution pipelineExec) {
		List<Record> filteredRecords = new ArrayList<>();
		for (Record record : records) {
			if (record.getExecution().getId() == pipelineExec.getId()) {
				filteredRecords.add(record);
			}
		}
		return filteredRecords;
	}

	String getRepositoryPath(boolean onInputGraph) {
		if (debugDpu == null) {
			return null;
		}
		Set<Integer> indexes = ctxReader.getIndexesForDataUnits(debugDpu);
		if (indexes == null) {
			return null;
		}

		Iterator<Integer> iter = indexes.iterator();
		while (iter.hasNext()) {
			Integer index = iter.next();
			DataUnitInfo duInfo = ctxReader.getDataUnitInfo(debugDpu, index);
			if (indexes.size() == 1 || duInfo.isInput() == onInputGraph) {
				return "ex" + pipelineExec.getId() + "_dpu-" + index;
			}
		}
		return null;
	}

	File getRepositoryDirectory(boolean onInputGraph) {
		if (debugDpu == null) {
			return null;
		}
		Set<Integer> indexes = ctxReader.getIndexesForDataUnits(debugDpu);
		if (indexes == null) {
			return null;
		}

		Iterator<Integer> iter = indexes.iterator();
		while (iter.hasNext()) {
			Integer index = iter.next();
			DataUnitInfo duInfo = ctxReader.getDataUnitInfo(debugDpu, index);
			if (indexes.size() == 1 || duInfo.isInput() == onInputGraph) {
				return duInfo.getDirectory();
			}
		}
		return null;
	}
	
	/**
	 * Refresh component factory. Is to be displayed while pipeline is still
	 * running. Contains refresh button, which updates the content of
	 * debugging view and shows the most current data of given pipeline run.
	 * 
	 * @return layout with label and refresh button
	 */
	private HorizontalLayout buildRefreshComponent() {
		
		Button refreshButton = new Button("Refresh",
				new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				refreshContent();
			}
		});
		
		Label label = new Label("Pipeline is still running. Please click refresh to update status.");
		label.setStyleName("warning");
		label.setWidth(450, Unit.PIXELS);
		
		HorizontalLayout refreshLayout = new HorizontalLayout();
		refreshLayout.setWidth(100, Unit.PERCENTAGE);
		refreshLayout.addComponent(label);
		refreshLayout.addComponent(refreshButton);
		refreshLayout.setComponentAlignment(refreshButton, Alignment.MIDDLE_RIGHT);
		
		refreshLayout.setVisible(false);
		
		return refreshLayout;
	}
}