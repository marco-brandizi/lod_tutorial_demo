package uk.ac.ebi.rdf.xml2rdf_demo.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Maps a label string into the corresponding EFO ontology class. 
 *
 * <dl><dt>date</dt><dd>6 Jul 2014</dd></dl>
 * @author Marco Brandizi
 *
 */
public class EFOResolver
{
	private Map<String, String> efoTerms;
	private static EFOResolver instance = new EFOResolver ();
	
	private Logger log = LoggerFactory.getLogger ( this.getClass () ); 

	private EFOResolver () {}
	
	public static EFOResolver getInstance () {
		return instance;
	}
	
	{
		log.info ( "Loading EFO" );
		OntModel efoModel = ModelFactory.createOntologyModel ( OntModelSpec.RDFS_MEM_RDFS_INF );
		efoModel.read ( "http://www.ebi.ac.uk/efo/efo_inferred.owl" );
		
		// This is the root class in EFO, mapped into a Jena object
		OntClass rootTerm = efoModel.getOntClass ( "http://www.ebi.ac.uk/efo/EFO_0000001" );
		
		efoTerms = new HashMap<String, String> ();
		
		// Jena has nodes of different types (resources, literals, properties, OWL classes etc), with RDFNode at the top.
		RDFNode termLabelNode = rootTerm.getPropertyValue ( RDFS.label );
		Literal termLabelLiteral = termLabelNode.asLiteral ();
		
		// And nodes have bridge with plain old primitive types
		String termLabel = termLabelLiteral.getString ();
		
		// Let's cache the current result 
		efoTerms.put ( termLabel, rootTerm.getURI () );
		
		// Le'ts do the same for all the descendant classes
		//
		for ( ExtendedIterator<OntClass> subTerms = rootTerm.listSubClasses ( false ); subTerms.hasNext (); )
		{
			OntClass subTerm = subTerms.next ();
			
			termLabelNode = subTerm.getPropertyValue ( RDFS.label );
			if ( termLabelNode == null ) continue;
			
			Literal rdfsLabelL = termLabelNode.asLiteral ();
			termLabel = rdfsLabelL.getString ();
			if ( termLabel == null ) continue;
			
			efoTerms.put ( termLabel, subTerm.getURI () );
		}
		
		// efoTerms is now available for queries
	}
	
	public String getEFOTerm ( String label )
	{
		return efoTerms.get ( label );
	}

}
