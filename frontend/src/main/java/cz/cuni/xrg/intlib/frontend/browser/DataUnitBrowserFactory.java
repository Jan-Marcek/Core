package cz.cuni.xrg.intlib.frontend.browser;

import java.io.File;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.DataUnitInfo;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionContextInfo;

/**
 * Factory for DataUnitBrowsers.
 *
 * @author Petyr
 *
 */
public class DataUnitBrowserFactory {

	/**
	 * Return browser for specified DataUnit.
	 * @param context The pipelineExecution context.
	 * @param dpuInstance Owner of DataUnit.
	 * @param dataUnitIndex Index of data unit.
	 * @return Browser or null if there is no browser for given type.
	 * @throws DataUnitNotFoundException
	 * @throws BrowserInitFailedException
	 */
	public static DataUnitBrowser getBrowser(
			ExecutionContextInfo context, DPUInstanceRecord dpuInstance, 
			DataUnitInfo info)
		throws DataUnitNotFoundException, BrowserInitFailedException{
		// get type and directory
		
		if (info == null) {
			// the context doesn't exist
			throw new DataUnitNotFoundException();
		}
		File directory = info.getDirectory();

		switch(info.getType()) {
		case RDF_Local:
			DataUnitBrowser localRdfBrowser = new LocalRdfBrowser();
			try {
				localRdfBrowser.loadDataUnit(directory, context.getDataUnitStorage(dpuInstance, info.getIndex()).getName());
			} catch (Exception e) {
				throw new BrowserInitFailedException(e);
			}
			return localRdfBrowser;
		case RDF_Virtuoso:
			return null;
		default:
			return null;
		}
	}

}
