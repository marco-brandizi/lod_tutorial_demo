package uk.ac.ebi.rdf.xml2rdf_demo;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import uk.ac.ebi.rdf.xml2rdf_demo.utils.EFOResolver;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * TODO: Comment me!
 *
 * <dl><dt>date</dt><dd>5 Jul 2014</dd></dl>
 * @author Marco Brandizi
 *
 */
public class Xml2Rdf
{
	private static Logger log = LoggerFactory.getLogger ( Xml2Rdf.class ); 
	
	public static void main ( String[] args ) throws Exception
	{
		OntModel outModel = ModelFactory.createOntologyModel ( OntModelSpec.OWL_MEM );
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document xdoc = dBuilder.parse ( args.length > 0 ? new FileInputStream ( args [ 0 ] ) : System.in );
		
		XPath xpath =  XPathFactory.newInstance().newXPath();
		
		NodeList xpatients  = xdoc.getElementsByTagName ( "patient" );
		for ( int i = 0; i < xpatients.getLength (); i++ )
		{
			
			Node xpatient = xpatients.item ( i );
			
			String patientId = ((Element) xpatient).getAttribute ( "id" );
			
			String diseaseLabel = xpath.compile ( "disease" ).evaluate ( xpatient );
			String diseaseTermUri = EFOResolver.getInstance ().getEFOTerm ( diseaseLabel );
			if ( diseaseTermUri == null ) {
				log.warn ( "No EFO term found for: '{}'", diseaseLabel );
				continue;
			}
			
			NodeList xpatientElems = (NodeList) xpath.compile ( "detected-proteins/protein" ).evaluate ( xpatient, XPathConstants.NODESET ); 
			for ( int j = 0; j < xpatientElems.getLength (); j++ )
			{
				Node xprotein = xpatientElems.item ( j );
				String uniProtId = xprotein.getFirstChild ().getNodeValue ();
				
				// How Jena creates the instance of an OWL class
				OntClass efoIndividual = outModel.createClass ( "http://www.ebi.ac.uk/efo/EFO_0000542" );
				Individual rdfPatient = efoIndividual.createIndividual ( "http://rdf.ebi.ac.uk/demo/patient/" + patientId );
				
				Resource rdfUniProt = outModel.createResource ( "http://purl.uniprot.org/uniprot/" + uniProtId );
				
				
				// Creating triples
				
				outModel.add ( 
					rdfPatient, outModel.getProperty ( "http://rdf.ebi.ac.uk/resource/atlas/dbXref" ), rdfUniProt 
				);
				
				outModel.add ( 
					rdfPatient, 
					outModel.getProperty ( "http://rdf.ebi.ac.uk/resource/atlas/hasFactorValue" ), 
				  outModel.createResource ( diseaseTermUri ) 
				);
				
				outModel.add ( rdfPatient, RDFS.label, "Patient from Experimental Data Set 2, ID #" + patientId );
				
				outModel.add ( rdfUniProt, RDFS.label, uniProtId );
			}
		}
		
		outModel.write ( args.length > 1 ? new FileOutputStream ( args [ 1 ] ) : System.out, "TURTLE" );
	}
	
}
