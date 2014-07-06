package uk.ac.ebi.rdf.demo.web;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
public class ProteinSearch
{
	public static class ProteinSearchResult
	{
		public String 
		  proteinUri, 
		  uniProtId,
		  proteinImgUrl,
		  conditionTermUri,
		  conditionLabel;
	}
	
	public List<ProteinSearchResult> searchProteins ( String disease ) throws IOException
	{
		List<ProteinSearchResult> result = new LinkedList<> ();
		if ( (disease = StringUtils.trimToNull ( disease )) == null ) return result;
		if ( disease.startsWith ( "\"" ) ) disease = disease.substring ( 1 );
		if ( disease.endsWith ( "\"" ) ) disease = disease.substring ( 0, disease.length () - 1 );
		if ( disease.length () == 0 ) return result;
		
		String diseaseTermUri = EFOResolver.getInstance ().getEFOTerm ( disease );
		if ( diseaseTermUri == null ) return result;
		
		Model m = ModelFactory.createDefaultModel ();
		QuerySolutionMap params = new QuerySolutionMap ();
		params.add ( "searchCondition", m.createResource ( diseaseTermUri ) );

		for ( 
			ResultSet qresult = SemWebUtils.execQuery ( "http://localhost:3030/demo/query", "/sparql/proteins.sparql", params );
			qresult.hasNext ();
		)
		{
			QuerySolution tuple = qresult.nextSolution ();
			
			ProteinSearchResult exportedResult = new ProteinSearchResult ();
			exportedResult.proteinUri = tuple.getResource ( "protein" ).getURI ();
			exportedResult.uniProtId = tuple.getLiteral ( "uniProtId" ).getString ();
			exportedResult.conditionTermUri = tuple.getResource ( "condition" ).getURI ();
			exportedResult.conditionLabel = tuple.getLiteral ( "condLabel" ).getString ();
			
			// Do you have an image URL?
			params = new QuerySolutionMap ();
			params.add ( "uniProtId", m.createLiteral ( exportedResult.uniProtId, "en" ) );
			ResultSet qresult1 = SemWebUtils.execQuery ( "http://dbpedia.org/sparql", "/sparql/proteins_image.sparql", params );
			if ( qresult1.hasNext () )
				exportedResult.proteinImgUrl = qresult1.nextSolution ().getResource ( "imgUrl" ).getURI ();
			
			result.add ( exportedResult );
		}
		
		return result;
	}
}
