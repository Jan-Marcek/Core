package cz.cuni.mff.xrg.odcs.commons;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import cz.cuni.mff.xrg.odcs.commons.app.dataunit.ManagableRdfDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.virtuoso.VirtuosoRDFDataUnit;

/**
 * @author Jiri Tomes
 */
@Category(IntegrationTest.class)
public class VirtuosoTest extends LocalRDFRepoTest {

    private static final String url = "jdbc:virtuoso://localhost:1111/charset=UTF-8/log_enable=2";

    private static final String user = "dba";

    private static final String password = "dba";

    private static final String defaultGraph = "http://default";

    /**
     * Basic setting before initializing test class.
     */
    @BeforeClass
    public static void setUpLogger() throws RepositoryException {

        rdfRepo = new VirtuosoRDFDataUnit(url, user, password, "", defaultGraph);
        RepositoryConnection connection = rdfRepo.getConnection();
        connection.clear(rdfRepo.getDataGraph());
        connection.close();
    }

    /**
     * Basic setting before test execution.
     */
    @Override
    public void setUp() {
        try {
            outDir = Files.createTempDirectory("intlib-out");
            testFileDir = VirtuosoTest.class.getResource("/repository")
                    .getPath();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());

        }
    }

    /**
     * Delete used repository files for testing.
     */
    @Override
    public void cleanUp() {
        deleteDirectory(new File(outDir.toString()));
    }

    /**
     * Cleaning after ending test class.
     */
    @AfterClass
    public static void cleaning() {
        rdfRepo.clear();
        rdfRepo.release();
    }

    /**
     * Extract file with size 2GB.
     */
    @Test
    public void BIGTwoGigaFileExtraction() {
        //extractTwoGigaFile();
    }

    /**
     * Create copy of repository.
     */
    @Test
    public void repositoryCopy() {
        ManagableRdfDataUnit goal = new VirtuosoRDFDataUnit(url, user, password, "", "http://goal");
        try {
            goal.merge(rdfRepo);
        } catch (IllegalArgumentException e) {
            fail(e.getMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
