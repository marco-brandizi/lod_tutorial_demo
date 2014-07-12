package uk.ac.ebi.rdf.demo.web.search.pathway;

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
public class PathwaySearch
{
	public List<Pathway> searchPathways ( String conditionUri ) throws IOException
	{
		List<Pathway> result = new LinkedList<> ();
		
		if ( ( conditionUri = StringUtils.trimToNull ( conditionUri ) ) == null ) return result;
		
		Model m = ModelFactory.createDefaultModel ();
		QuerySolutionMap params = new QuerySolutionMap ();
		params.add ( "searchCondition", m.createResource ( conditionUri ) );

		for ( 
			ResultSet qresult = SemWebUtils.execQuery ( 
				"http://localhost:3030/demo/query", "/sparql/pathways.sparql", params );
			qresult.hasNext ();
		)
		{
			QuerySolution tuple = qresult.nextSolution ();
			
			Pathway exportedResult = new Pathway ();
			exportedResult.pathwayUri = tuple.getResource ( "pathway" ).getURI ();
			exportedResult.pathwayName = tuple.getLiteral ( "pathwayName" ).getString ();
			exportedResult.moleculeUri = tuple.getResource ( "molecule" ).getURI ();
			exportedResult.moleculeId = tuple.getLiteral ( "moleculeId" ).getString ();
			exportedResult.moleculeAltLabel = tuple.getLiteral ( "altLabel" ).getString ();

			result.add ( exportedResult );
		}
		
		return result;
	}
}
