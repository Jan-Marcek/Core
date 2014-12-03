package eu.unifiedviews.commons.dataunit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;

import eu.unifiedviews.commons.rdf.ConnectionSource;
import eu.unifiedviews.commons.rdf.repository.ManagableRepository;
import eu.unifiedviews.commons.rdf.repository.RDFException;
import eu.unifiedviews.commons.rdf.repository.RepositoryFactory;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;

/**
 *
 * @author Škoda Petr
 */
public class WritableMetadataDataUnitImplTest {

    /**
     * Dummy version for test purpose.
     */
    class TestWritableMetadataDataUnit extends AbstractWritableMetadataDataUnit {

        public TestWritableMetadataDataUnit(String dataUnitName, String writeContextString,
                ConnectionSource connectionSource) {
            super(dataUnitName, writeContextString, connectionSource);
        }

        @Override
        public MetadataDataUnit.Iteration getIteration() throws DataUnitException {
            return null;
        }

        @Override
        public ManagableDataUnit.Type getType() {
            return null;
        }

        @Override
        public boolean isType(ManagableDataUnit.Type dataUnitType) {
            return false;
        }

        public void addReadContext(URI uri) {
            readContexts.add(uri);
        }

        public int getEntryCounter() {
            return entryCounter.get();
        }

    };

    private RepositoryFactory factory;

    private Path rootDir;

    private ManagableRepository repository;

    @Before
    public void prepare() throws IOException, DataUnitException {
        factory = new RepositoryFactory();
        rootDir = Files.createTempDirectory(FileUtils.getTempDirectory().toPath(), "uv-dataUnit-");
        factory.setLocalParameters(rootDir.toAbsolutePath().toString());
        try {
            repository = factory.create("1", ManagableRepository.Type.LOCAL_RDF);
        } catch (RDFException ex) {
            throw new DataUnitException(ex);
        }
    }

    @After
    public void cleanUp() throws IOException, DataUnitException {
        try {
            repository.delete();
        } catch (RDFException ex) {
            throw new DataUnitException(ex);
        } finally {
            Files.deleteIfExists(rootDir);
        }
    }

    @Test
    public void storeAndLoad() throws DataUnitException {        
        ValueFactory valueFactory = repository.getConnectionSource().getValueFactory();
        // Prepare data.
        String writeContextString = "http://unifiedviews.eu/test/write";
        URI readUri = valueFactory.createURI("http://unifiedviews.eu/test/read");
        // Create writable MedataDataUnit and put some data in.
        TestWritableMetadataDataUnit dataUnit = new TestWritableMetadataDataUnit("test", writeContextString, repository.getConnectionSource());
        dataUnit.addEntry("New entry");
        dataUnit.addReadContext(readUri);
        dataUnit.store();
        dataUnit.release();
        // Create a new with same parameters and load.
        dataUnit = new TestWritableMetadataDataUnit("test", writeContextString, repository.getConnectionSource());
        Assert.assertEquals("Read context should cotains only read context.", 1, dataUnit.getMetadataGraphnames().size());
        dataUnit.load();
        // Check.
        Assert.assertEquals("Read context should contains readUri and writeContext", 2, dataUnit.getMetadataGraphnames().size());
        Assert.assertEquals("We added one file", 1, dataUnit.getEntryCounter());
        dataUnit.release();
    }

    @Test
    public void storeAndClearAndLoad() throws DataUnitException {
        ValueFactory valueFactory = repository.getConnectionSource().getValueFactory();
        // Prepare data.
        String writeContextString = "http://unifiedviews.eu/test/write";
        URI readUri = valueFactory.createURI("http://unifiedviews.eu/test/read");
        // Create writable MedataDataUnit and put some data in.
        TestWritableMetadataDataUnit dataUnit = new TestWritableMetadataDataUnit("test", writeContextString, repository.getConnectionSource());
        dataUnit.addEntry("New entry");
        dataUnit.addReadContext(readUri);
        dataUnit.store();
        dataUnit.release();
        // Create a new with same parameters and clear
        dataUnit = new TestWritableMetadataDataUnit("test", writeContextString, repository.getConnectionSource());
        dataUnit.load();
        dataUnit.clear();
        dataUnit.release();
        // Create a new with same parameters and load.
        dataUnit = new TestWritableMetadataDataUnit("test", writeContextString, repository.getConnectionSource());
        try {
            dataUnit.load();
            Assert.fail("Load should fail as there are no data.");
        } catch (DataUnitException ex) {
            // Ok we are expecting this.
        }
        dataUnit.release();
    }

}
