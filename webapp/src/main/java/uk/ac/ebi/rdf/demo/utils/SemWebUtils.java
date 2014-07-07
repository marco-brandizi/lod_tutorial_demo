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
	
	public static ResultSet execQuery ( String sparqlEndPointUrl, String queryResourceFilePath, QuerySolutionMap params ) 
	{
		try
		{
			String sparqlStr = IOUtils.readResource ( SemWebUtils.class, queryResourceFilePath );
			ParameterizedSparqlString sparql = new ParameterizedSparqlString ( sparqlStr, params );
			
			log.trace ( "Quering {} with {}", sparqlEndPointUrl, sparql.toString () );
			
			Query query = sparql.asQuery ();
			QueryExecution qe = QueryExecutionFactory.sparqlService ( sparqlEndPointUrl, query );
			return qe.execSelect ();
		} 
		catch ( IOException ex )
		{
			throw new RuntimeException ( "Cannot find sparql file: '" + queryResourceFilePath + "'", ex );
		} 
	}
}
