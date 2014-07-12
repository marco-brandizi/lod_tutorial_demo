package uk.ac.ebi.rdf.demo.utils;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.utils.io.IOUtils;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;

/**
 * TODO: Comment me!
 *
 * <dl><dt>date</dt><dd>6 Jul 2014</dd></dl>
 * @author Marco Brandizi
 *
 */
public class SemWebUtils
{
	private SemWebUtils () {}
	
	private static Logger log = LoggerFactory.getLogger ( SemWebUtils.class );
	
	/**
	 * Execute a SPARQL query against an endpoint 
   *
	 * @param sparqlEndPointUrl the URL of the endpoind (i.e., the REST webservice answering with the SPARQL protocol)
	 * @param queryResourceFilePath the file in the classpath that contains the SPARQL query
	 * @param params the parameter substitutions for prepared statement-like queries.
	 * 
	 * @return a Jena {@link ResultSet}, which is conceptually similar to the homonym class in Java
	 */
	public static ResultSet execQuery ( String sparqlEndPointUrl, String queryResourceFilePath, QuerySolutionMap params ) 
	{
		try
		{
			String sparqlStr = IOUtils.readResource ( SemWebUtils.class, queryResourceFilePath );
			
			// Jena, allows to replace ?varName with values specified in param
			ParameterizedSparqlString sparql = new ParameterizedSparqlString ( sparqlStr, params );
			
			// and then you can get the query text
			log.trace ( "Quering {} with {}", sparqlEndPointUrl, sparql.toString () );
			
			// or the query object
			Query query = sparql.asQuery ();
			
			// and you can send a query object to a SPARQL endpoint
			QueryExecution qe = QueryExecutionFactory.sparqlService ( sparqlEndPointUrl, query );
			
			// The result will be a tuple iterator
			return qe.execSelect ();
		} 
		catch ( IOException ex )
		{
			throw new RuntimeException ( "Cannot find sparql file: '" + queryResourceFilePath + "'", ex );
		} 
	}
	
}
