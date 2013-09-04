package org.openrdf;

/**
* Contains list of org.openrdf packages that will be exported from frontend
* and backend.
* 
* @author Petyr
*
*/
public final class PackageList {

	/**
	 * List of OSGI packages to export. Does not start nor 
	 * end with separator.
	 */
	public static final String PACKAGES =
			"org.openrdf," + 
			"org.openrdf.console," + 
			"org.openrdf.http.client," + 
			"org.openrdf.http.protocol," + 
			"org.openrdf.http.protocol.error," + 
			"org.openrdf.http.protocol.transaction," + 
			"org.openrdf.http.protocol.transaction.operations," + 
			"org.openrdf.model," + 
			"org.openrdf.model.datatypes," + 
			"org.openrdf.model.impl," + 
			"org.openrdf.model.util," + 
			"org.openrdf.model.util.language," + 
			"org.openrdf.model.vocabulary," + 
			"org.openrdf.query," + 
			"org.openrdf.query.algebra," + 
			"org.openrdf.query.algebra.evaluation," + 
			"org.openrdf.query.algebra.evaluation.federation," + 
			"org.openrdf.query.algebra.evaluation.function," + 
			"org.openrdf.query.algebra.evaluation.function.datetime," + 
			"org.openrdf.query.algebra.evaluation.function.hash," + 
			"org.openrdf.query.algebra.evaluation.function.numeric," + 
			"org.openrdf.query.algebra.evaluation.function.rdfterm," + 
			"org.openrdf.query.algebra.evaluation.function.string," + 
			"org.openrdf.query.algebra.evaluation.impl," + 
			"org.openrdf.query.algebra.evaluation.iterator," + 
			"org.openrdf.query.algebra.evaluation.util," + 
			"org.openrdf.query.algebra.helpers," + 
			"org.openrdf.query.dawg," + 
			"org.openrdf.query.impl," + 
			"org.openrdf.query.parser," + 
			"org.openrdf.query.parser.serql," + 
			"org.openrdf.query.parser.serql.ast," + 
			"org.openrdf.query.parser.sparql," + 
			"org.openrdf.query.parser.sparql.ast," + 
			"org.openrdf.query.resultio," + 
			"org.openrdf.query.resultio.binary," + 
			"org.openrdf.query.resultio.sparqljson," + 
			"org.openrdf.query.resultio.sparqlxml," + 
			"org.openrdf.query.resultio.text," + 
			"org.openrdf.query.resultio.text.csv," + 
			"org.openrdf.query.resultio.text.tsv," + 
			"org.openrdf.repository," + 
			"org.openrdf.repository.base," + 
			"org.openrdf.repository.config," + 
			"org.openrdf.repository.contextaware," + 
			"org.openrdf.repository.contextaware.config," + 
			"org.openrdf.repository.dataset," + 
			"org.openrdf.repository.dataset.config," + 
			"org.openrdf.repository.event," + 
			"org.openrdf.repository.event.base," + 
			"org.openrdf.repository.event.util," + 
			"org.openrdf.repository.http," + 
			"org.openrdf.repository.http.config," + 
			"org.openrdf.repository.manager," + 
			"org.openrdf.repository.manager.util," + 
			"org.openrdf.repository.sail," + 
			"org.openrdf.repository.sail.config," + 
			"org.openrdf.repository.sparql," + 
			"org.openrdf.repository.sparql.config," + 
			"org.openrdf.repository.sparql.query," + 
			"org.openrdf.repository.util," + 
			"org.openrdf.rio," + 
			"org.openrdf.rio.binary," + 
			"org.openrdf.rio.helpers," + 
			"org.openrdf.rio.n3," + 
			"org.openrdf.rio.nquads," + 
			"org.openrdf.rio.ntriples," + 
			"org.openrdf.rio.rdfxml," + 
			"org.openrdf.rio.rdfxml.util," + 
			"org.openrdf.rio.trig," + 
			"org.openrdf.rio.trix," + 
			"org.openrdf.rio.turtle," + 
			"org.openrdf.sail," + 
			"org.openrdf.sail.config," + 
			"org.openrdf.sail.helpers," + 
			"org.openrdf.sail.inferencer," + 
			"org.openrdf.sail.inferencer.fc," + 
			"org.openrdf.sail.inferencer.fc.config," + 
			"org.openrdf.sail.memory," + 
			"org.openrdf.sail.memory.config," + 
			"org.openrdf.sail.memory.model," + 
			"org.openrdf.sail.nativerdf," + 
			"org.openrdf.sail.nativerdf.btree," + 
			"org.openrdf.sail.nativerdf.config," + 
			"org.openrdf.sail.nativerdf.datastore," + 
			"org.openrdf.sail.nativerdf.model," + 
			"org.openrdf.sail.rdbms," + 
			"org.openrdf.sail.rdbms.algebra," + 
			"org.openrdf.sail.rdbms.algebra.base," + 
			"org.openrdf.sail.rdbms.algebra.factories," + 
			"org.openrdf.sail.rdbms.config," + 
			"org.openrdf.sail.rdbms.evaluation," + 
			"org.openrdf.sail.rdbms.exceptions," + 
			"org.openrdf.sail.rdbms.iteration," + 
			"org.openrdf.sail.rdbms.iteration.base," + 
			"org.openrdf.sail.rdbms.managers," + 
			"org.openrdf.sail.rdbms.managers.base," + 
			"org.openrdf.sail.rdbms.managers.helpers," + 
			"org.openrdf.sail.rdbms.model," + 
			"org.openrdf.sail.rdbms.mysql," + 
			"org.openrdf.sail.rdbms.optimizers," + 
			"org.openrdf.sail.rdbms.postgresql," + 
			"org.openrdf.sail.rdbms.schema," + 
			"org.openrdf.sail.rdbms.util," + 
			"org.openrdf.util.iterators";
}			
