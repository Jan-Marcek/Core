package cz.cuni.mff.xrg.odcs.rdf;

import java.util.List;
import java.util.Map;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.help.OrderTupleQueryResult;

/**
 * Interface provides methods for working with RDF data repository.
 * 
 * @author Jiri Tomes
 * @author Petyr
 */
public interface RDFDataUnit extends RDFData, DataUnit {
    
    /**
     * Extracts metadata (held within the list of predicates) about certain
     * subjects (subject URIs)
     * 
     * @param subjectURI
     *            Subject URI for which metadata is searched
     * @param predicates
     *            Predicates being searched
     * @return Pairs predicate-value for the given subject URI
     */
    @Deprecated
    public Map<String, List<String>> getRDFMetadataForSubjectURI(
            String subjectURI,
            List<String> predicates);

    /**
     * Extracts metadata (held within the list of predicates) about certain
     * files (based on the file path)
     * 
     * @param filePath
     *            Path to the file.
     * @param predicates
     *            Predicates being searched
     * @return Pairs predicate-value for the given filePath
     */
    @Deprecated
    public Map<String, List<String>> getRDFMetadataForFile(String filePath,
            List<String> predicates);

    /**
     * Make ORDERED SELECT QUERY (select query contains ORDER BY keyword) over
     * repository data and return {@link OrderTupleQueryResult} class as result.
     * This ordered select query don´t have to containt LIMIT nad OFFSET
     * keywords.
     * For no problem behavior check you setting "MaxSortedRows" param in your
     * virtuoso.ini file before using. For more info
     * 
     * @see OrderTupleQueryResult class description.
     * @param orderSelectQuery
     *            String representation of SPARQL select query.
     * @return {@link OrderTupleQueryResult} representation of ordered select
     *         query.
     * @throws InvalidQueryException
     *             when query is not valid or containst LIMIT
     *             or OFFSET keyword.
     */
    @Deprecated
    public OrderTupleQueryResult executeOrderSelectQueryAsTuples(
            String orderSelectQuery) throws InvalidQueryException;
}
