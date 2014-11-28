package eu.unifiedviews.commons.rdf.repository;

import java.io.File;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfig;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.manager.LocalRepositoryManager;
import org.openrdf.repository.manager.RepositoryProvider;
import org.openrdf.repository.sail.config.SailRepositoryConfig;
import org.openrdf.sail.memory.config.MemoryStoreConfig;

import eu.unifiedviews.commons.rdf.ConnectionSource;
import eu.unifiedviews.dataunit.DataUnitException;

/**
 * 
 * @author Škoda Petr
 */
class InMemoryRDF implements ManagableRepository {

    public static final String GLOBAL_REPOSITORY_ID = "uv_internal_repository_memory";

    private final Repository repository;

    /**
     *
     * @param repositoryPath Path for manager, should be the same for different pipelines.
     * @throws DataUnitException
     */
    public InMemoryRDF(String repositoryPath) throws DataUnitException {
        final File managerDir = new File(repositoryPath);
        if (!managerDir.isDirectory() && !managerDir.mkdirs()) {
            throw new DataUnitException("Could not create repository manager directory.");
        }
        try {
            final LocalRepositoryManager localRepositoryManager = RepositoryProvider.getRepositoryManager(managerDir);
            final Repository newRepository = localRepositoryManager.getRepository(GLOBAL_REPOSITORY_ID);
            if (newRepository == null) {
                // Create and add to the manager.
                localRepositoryManager.addRepositoryConfig(new RepositoryConfig(GLOBAL_REPOSITORY_ID, new SailRepositoryConfig(new MemoryStoreConfig())));
                repository = localRepositoryManager.getRepository(GLOBAL_REPOSITORY_ID);
            } else {
                repository = newRepository;
            }
        } catch (RepositoryConfigException | RepositoryException ex) {
            throw new DataUnitException("Could not initialize repository", ex);
        }
        if (repository == null) {
            throw new DataUnitException("Could not initialize repository");
        }
    }

    @Override
    public ConnectionSource getConnectionSource() {
        return new ConnectionSource(repository, false);
    }

    @Override
    public void release() throws DataUnitException {
        // Do nothing here.
    }

    @Override
    public void delete() throws DataUnitException {
        // Do nothing here.
    }

}
