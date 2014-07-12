package uk.ac.ebi.rdf.demo.web.search.condition;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import uk.ac.ebi.rdf.demo.utils.SemWebUtils;
import uk.ac.ebi.rdf.xml2rdf_demo.utils.EFOResolver;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * TODO: Comment me!
 *
 * <dl><dt>date</dt><dd>6 Jul 2014</dd></dl>
 * @author Marco Brandizi
 *
 */
public class ConditionSearch
{
	public List<Condition> searchConditions ( String condition ) throws IOException
	{
		List<Condition> result = new LinkedList<> ();
		
		if ( (condition = StringUtils.trimToNull ( condition )) == null ) return result;
		if ( condition.startsWith ( "\"" ) ) condition = condition.substring ( 1 );
		if ( condition.endsWith ( "\"" ) ) condition = condition.substring ( 0, condition.length () - 1 );
		if ( condition.length () == 0 ) return result;
		
		String diseaseTermUri = EFOResolver.getInstance ().getEFOTerm ( condition );
		if ( diseaseTermUri == null ) return result;
		
		// A foo model, needed to create nodes 
		Model m = ModelFactory.createDefaultModel ();

		// This is conceptually very similar to preparred statements in JDBC 
		QuerySolutionMap params = new QuerySolutionMap ();
		params.add ( "searchCondition", m.createResource ( diseaseTermUri ) );

		for ( 
			// See the implementation of this method for details
			ResultSet qresult = SemWebUtils.execQuery ( 
				"http://localhost:3030/demo/query", "/sparql/conditions.sparql", params );
			qresult.hasNext ();
		)
		{
			// Every solution has several extraction options, this is to get RDF objects
			QuerySolution tuple = qresult.nextSolution ();
			
			Condition exportedResult = new Condition ();
			// Here we populate a model object with nodes coming from the tuple
			exportedResult.conditionTermUri = tuple.getResource ( "condition" ).getURI ();
			exportedResult.conditionLabel = tuple.getLiteral ( "condLabel" ).getString ();
			exportedResult.proteinCount = tuple.getLiteral ( "proteinCount" ).getInt ();
			
			// About the same for the proteins (an application-managed subquery)
			//
			for ( 
				ResultSet qprots = SemWebUtils.execQuery ( 
					"http://localhost:3030/demo/query", "/sparql/condition_proteins.sparql", params );
					qprots.hasNext ();
			)
			{
				QuerySolution tupleProt = qprots.nextSolution ();

				Protein exportedProt = new Protein ();
				exportedProt.proteinUri = tupleProt.getResource ( "protein" ).getURI ();
				exportedProt.uniProtId = tupleProt.getLiteral ( "uniProtId" ).getString ();
				
				exportedResult.proteins.add ( exportedProt );
			}
			
			result.add ( exportedResult );
		}
		
		return result;
	}
}
