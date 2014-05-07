package cz.cuni.mff.xrg.odcs.commons.app.dataunit;

import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.enums.HandlerExtractType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.openrdf.model.Graph;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;

/**
 * Interface provides managable methods for working with RDF data repository.
 *
 * @author Petyr
 * @author Jiri Tomes
 */
public interface ManagableRdfDataUnit extends RDFDataUnit, ManagableDataUnit {

	/**
	 * Set data graph storage for given data in RDF format.
	 *
	 * @param newDataGraph new graph representated as URI.
	 */
	public void setDataGraph(URI newDataGraph);

	/**
	 * Set new data graph as default storage for data in RDF format.
	 *
	 * @param newStringDataGraph String name of graph as URI - starts with
	 *                           prefix http://).
	 */
	public void setDataGraph(String newStringDataGraph);

	/**
	 * For Browsing all data in graph return its size {count of rows}.
	 *
	 * @return count of rows for browsing all data in graph.
	 * @throws InvalidQueryException if query for find out count of rows in not
	 *                               valid.
	 */
	public long getResultSizeForDataCollection() throws InvalidQueryException;

	/**
	 * For given valid SELECT of CONSTRUCT query return its size {count of rows
	 * returns for given query).
	 *
	 * @param query Valid SELECT/CONTRUCT query for asking.
	 * @return size for given valid query as long.
	 * @throws InvalidQueryException if query is not valid.
	 */
	public long getResultSizeForQuery(String query) throws InvalidQueryException;

}