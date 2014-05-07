package cz.cuni.mff.xrg.odcs.dpu.test.data;

import cz.cuni.mff.xrg.odcs.commons.app.dataunit.ManagableRdfDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.localrdf.LocalRDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.repositories.GraphUrl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

import org.openrdf.repository.RepositoryException;

/**
 * Create {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit}s that can be used
 * in {@link cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment}.
 *
 * @author Petyr
 */
public class TestDataUnitFactory {

	/**
	 * Counter for dataUnits id's and directories.
	 */
	private int dataUnitIdCounter = 0;
	
	private Object counterLock = new Object();
	/**
	 * Working directory.
	 */
	private final File workingDirectory;

	/**
	 * Create a {@link TestDataUnitFactory} that use given directory as working
	 * directory.
	 *
	 * @param workingDirectory Directory where to create working subdirectories
	 *                         for
	 *                         {@link cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit}
	 *                         that use local storage as RDF repository.
	 */
	public TestDataUnitFactory(File workingDirectory) {
		this.workingDirectory = workingDirectory;
	}
	
	/**
	 * Create RDF data unit.
	 *
	 * @param name Name of the DataUnit.
	 * @param useVirtuoso False to use local repository, True to use Virtuoso.
	 * @return New {@link ManagableRdfDataUnit}.
	 * @throws RepositoryException 
	 */
	public ManagableRdfDataUnit createRDFDataUnit(String name) {
		synchronized (counterLock) {
			final String id = "dpu-test_" + Integer.toString(dataUnitIdCounter++) + "_" + name;
			final String namedGraph = GraphUrl.translateDataUnitId(id);

			return new LocalRDFDataUnit(workingDirectory.toString(), name,
					namedGraph);
		}
	}
}