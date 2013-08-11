package cz.cuni.xrg.intlib.backend.context.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.xrg.intlib.backend.data.DataUnitFactory;
import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import cz.cuni.xrg.intlib.commons.app.conf.ConfigProperty;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;

/**
 * Provide implementation of commons methods for
 * {@link ExtendedExtractContextImpl}, {@link ExtendedLoadContextImpl} and
 * {@link ExtendedTransformContextImpl}
 * 
 * For details about methods see
 * {@link cz.cuni.xrg.intlib.commons.context.ProcessingContext}
 * 
 * @author Petyr
 * 
 */
class ExtendedCommonImpl {

	/**
	 * Name of directory for shared DPU's data.
	 */
	private static final String DPU_DIR = "dpu";
	
	/**
	 * Name of sub-directory in {@link #DPU_DIR} for user
	 * related data storage.
	 */
	private static final String USER_DIR = "user";
	
	/**
	 * Custom data holder.
	 */
	protected Map<String, Object> customData;

	/**
	 * PipelineExecution. The one who caused run of this DPURecord.
	 */
	private PipelineExecution execution;

	/**
	 * DPUInstanceRecord as owner of this context.
	 */
	protected DPUInstanceRecord dpuInstance;

	/**
	 * Used factory.
	 */
	protected DataUnitFactory dataUnitFactory;

	/**
	 * Manage mapping context into execution's directory.
	 */
	protected ExecutionContextInfo context;

	/**
	 * Application configuration.
	 */
	protected AppConfig appConfig;

	/**
	 * Logger class for ExtendedCommonImpl class. 
	 */
	private static Logger LOG = LoggerFactory.getLogger(ExtendedCommonImpl.class);
	
	/**
	 * Time of last successful execution. Null if there is no such execution.
	 */
	private Date lastSuccExec;
	
	/**
	 * If the working directory for Context already exist then the ctor try to
	 * delete it.
	 * 
	 * @param execution Associated pipelineExecution.
	 * @param dpuInstance Associated dpuInstanceRecord ~ owner.
	 * @param context Access to context 'manager'.
	 * @param appCofig Application's configuration.
	 * @throws IOException
	 */
	public ExtendedCommonImpl(
			PipelineExecution execution,
			DPUInstanceRecord dpuInstance,
			ExecutionContextInfo context,
			AppConfig appConfig, 
			Date lastSuccExec) throws IOException {
		this.customData = new HashMap<String, Object>();
		this.execution = execution;
		this.dpuInstance = dpuInstance;
		this.context = context;
		this.appConfig = appConfig;
		this.dataUnitFactory = new DataUnitFactory(appConfig);
		this.lastSuccExec = lastSuccExec;
	}

	/**
	 * Check required type based on application configuration and return
	 * {@link DataUnitType} that should be created. Can thrown
	 * {@link DataUnitCreateException} in case of unknown {@link DataUnitType}.
	 * 
	 * @param type Required type.
	 * @return Type to create.
	 * @throws DataUnitCreateException
	 */
	protected DataUnitType checkType(DataUnitType type)
			throws DataUnitCreateException {
		if (type == DataUnitType.RDF) {
			// select other DataUnit based on configuration
			String defRdfRepo = appConfig
					.getString(ConfigProperty.BACKEND_DEFAULTRDF);
			if (defRdfRepo == null) {
				// use local
				type = DataUnitType.RDF_Local;
			} else {
				// choose based on value in appConfig
				if (defRdfRepo.compareToIgnoreCase("virtuoso") == 0) {
					// use virtuoso
					type = DataUnitType.RDF_Virtuoso;
				} else if (defRdfRepo.compareToIgnoreCase("localRDF") == 0) {
					// use local
					type = DataUnitType.RDF_Local;
				} else {
					throw new DataUnitCreateException(
							"The data unit type is unknown."
									+ "Check the value of the parameter "
									+ "backend.defaultRDF in config.properties");
				}
			}
		}
		return type;
	}

	/**
	 * Return existing DPU's execution working directory.
	 * 
	 * @return
	 */
	public File getWorkingDir() {
		File directory = new File(getGeneralWorkingDir(),
				context.getDPUTmpPath(dpuInstance));
		directory.mkdirs();
		return directory;
	}

	/**
	 * Return engine's general working directory.
	 * 
	 * @return
	 */
	public File getGeneralWorkingDir() {
		return new File(appConfig.getString(ConfigProperty.GENERAL_WORKINGDIR));
	}

	/**
	 * Return existing execution's result directory.
	 * 
	 * @return
	 */
	public File getResultDir() {
		File directory = new File(getGeneralWorkingDir(),
				context.getResultPath());
		directory.mkdirs();
		return directory;

	}

	/**
	 * Delete directory if exist. If error occur is logged and silently
	 * ignored.
	 * @param directory
	 */
	private void deleteDirectory(File directory) {
		if (directory.exists()) {
			try {
				FileUtils.deleteDirectory(directory);
			} catch (IOException e) {
				LOG.error("Can't delete directory {}", directory.toString(), e);
			}
		}
	}
	
	/**
	 * Delete all DPU context's directories.
	 */
	protected void deleteDirectories() {
		
		// DPU' tmp directory
		final File workingDir = getWorkingDir();
		deleteDirectory(workingDir);
				
		// DataUnit storage directory
		final File dpuStoragePath = new File(getGeneralWorkingDir(), 
				context.getDataUnitRootStoragePath(dpuInstance));		
		deleteDirectory(dpuStoragePath);
		
		// DataUnit tmp directory
		final File dpuTmpPath = new File(getGeneralWorkingDir(), 
				context.getDataUnitRootTmpPath(dpuInstance));
		deleteDirectory(dpuTmpPath);
		
		// Result directory is shared by whole pipeline
		// and so it's not deleted from here
	}
	
	/**
	 * Return identification of single DPU template shared by all
	 * templates with same name.
	 * @return
	 */
	private String getTemplateIdentification() {
		return dpuInstance.getJarPath();
	}
	
	// 	implementation of methods from backend.context.ExtendedContext		//
	
	public PipelineExecution getPipelineExecution() {
		return execution;
	}
	
	public DPUInstanceRecord getDPUInstance() {
		return dpuInstance;
	}
	
	//	implementation of methods from commons.context.ProcessingContext	//
	
	public boolean isDebugging() {
		return execution.isDebugging();
	}

	public Map<String, Object> getCustomData() {
		return customData;
	}

	public File getJarPath() {
		File path = new File(appConfig.getString(ConfigProperty.MODULE_PATH)
				+ dpuInstance.getJarPath());
		return path;
	}
	
	public Date getLastExecutionTime() {
		return lastSuccExec;
	}
	
	public File getGlobalDirectory() {
		File result = new File(getGeneralWorkingDir(), 
				DPU_DIR + File.separator +	getTemplateIdentification());
		result.mkdirs();
		return result;
	}
	
	public File getUserDirectory() {
		File result =  new File(getGeneralWorkingDir(), 
				DPU_DIR + File.separator + USER_DIR + File.separator +
				getTemplateIdentification());
		result.mkdirs();
		return result;
	}
}
