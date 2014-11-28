package cz.cuni.mff.xrg.odcs.backend.spring;

import static org.mockito.Mockito.mock;

import java.io.File;

import cz.cuni.mff.xrg.odcs.backend.data.DataUnitFactory;
import eu.unifiedviews.commons.dataunit.ManagableDataUnit;

/**
 * Dummy {@link DataUnitFactory}. Does not create any real data unit.
 * 
 * @author Petyr
 */
public class DummyDataUnitFactory extends DataUnitFactory {

    @Override
    public ManagableDataUnit create(ManagableDataUnit.Type type,
            String pipelineId,
            String dataUnitUri,
            String dataUnitName,
            File dataUnitDirectory) {
        // just return mocked object
        return mock(ManagableDataUnit.class);
    }

}
