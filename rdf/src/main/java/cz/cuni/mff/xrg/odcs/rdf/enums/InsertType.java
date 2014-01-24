package cz.cuni.mff.xrg.odcs.rdf.enums;

/**
 * Possibilies how to load RDF data insert part to SPARL endpoint.
 *
 * @author Jiri Tomes
 */
public enum InsertType {

	/**
	 * Load RDF data parts which have no errors. Other parts are skipped and
	 * warning is given about it.
	 */
	SKIP_BAD_PARTS,
	/**
	 * If some of parts for loading contains errors. No data parts are loading.
	 * Loading failed and it´s thrown LoadException.
	 */
	STOP_WHEN_BAD_PART,
	/**
	 * If any data part for loading contains errors, process clean all
	 * successfully loaded parts and start loading parts again from zero. 
	 */
	REPEAT_IF_BAD_PART;
}
