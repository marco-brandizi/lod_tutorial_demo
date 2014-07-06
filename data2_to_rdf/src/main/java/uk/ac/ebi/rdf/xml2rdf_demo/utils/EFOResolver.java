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
 * TODO: Comment me!
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
		
		// Viral disease, our experiments have annotations about this branch only
		OntClass rootTerm = efoModel.getOntClass ( "http://www.ebi.ac.uk/efo/EFO_0000763" );
		
		efoTerms = new HashMap<String, String> ();
		efoTerms.put ( rootTerm.getPropertyValue ( RDFS.label ).asLiteral ().getString (), rootTerm.getURI () );
		for ( ExtendedIterator<OntClass> subTerms = rootTerm.listSubClasses ( false ); subTerms.hasNext (); )
		{
			OntClass subTerm = subTerms.next ();
			
			RDFNode rdfsLabelN = subTerm.getPropertyValue ( RDFS.label );
			if ( rdfsLabelN == null ) continue;
			
			Literal rdfsLabelL = rdfsLabelN.asLiteral ();
			String termLabel = rdfsLabelL.getString ();
			if ( termLabel == null ) continue;
			
			efoTerms.put ( termLabel, subTerm.getURI () );
		}
	}
	
	public String getEFOTerm ( String label )
	{
		return efoTerms.get ( label );
	}

}
