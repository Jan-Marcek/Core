package cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf;


public interface RDFDataUnitFactory {
    ManagableRdfDataUnit create(String pipelineId, String dataUnitName, String dataGraph);
    void cleanPipeline(String pipelineId);
}
