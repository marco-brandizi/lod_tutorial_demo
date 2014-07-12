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
 * Converts the LOD Demo file data2.xml into a suitable RDF representation.
 * Uses the Jena framework for that (http://jena.apache.org/)
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
		// First, traditional XML loading into DOM
		//
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document xdoc = dBuilder.parse ( args.length > 0 ? new FileInputStream ( args [ 0 ] ) : System.in );
		
		XPath xpath =  XPathFactory.newInstance().newXPath();

		// Here we start using Jena
		// a model is a data-set container. For instance, here it will generate a single file containing RDF statements.
		//
		// An OntModel allow us to use interfaces that reflect OWL (e.g., OWLClass). 
		// 
		// A model specification defines model features and behaviour. Here, it will be managed as an OWL-based dataset and 
		// will be kept in memory (backed by a database or a triple store, automatic reasoning are further options).
		//
		OntModel outModel = ModelFactory.createOntologyModel ( OntModelSpec.OWL_MEM );

		
		NodeList xpatients  = xdoc.getElementsByTagName ( "patient" );
		for ( int i = 0; i < xpatients.getLength (); i++ )
		{
			
			Node xpatient = xpatients.item ( i );
			
			String patientId = ((Element) xpatient).getAttribute ( "id" );
			
			String diseaseLabel = xpath.compile ( "disease" ).evaluate ( xpatient );
			
			// Because we don't have EFO codes, we try to map labels to EFO, again by means of Jena 
			// (see the invoked call for details)
			//
			// As usually, ontology classes are identified by means of URIs.
			//
			String diseaseTermUri = EFOResolver.getInstance ().getEFOTerm ( diseaseLabel );
			if ( diseaseTermUri == null ) {
				log.warn ( "No EFO term found for: '{}'", diseaseLabel );
				continue;
			}

			// How Jena creates the instance of an OWL class
			// This the class for an individual, in the sense of a patient or alike. This is also a subclass of 
			// 'specimen' (OBI_0100051), as it is 'material sample' (OBI_0000747) in data1.ttl. This allows us to query for 
			// instances of specimen and get instances from the two data sources.
			//
			OntClass efoIndividual = outModel.createClass ( "http://www.ebi.ac.uk/efo/EFO_0000542" );
			Individual rdfPatient = efoIndividual.createIndividual ( "http://rdf.ebi.ac.uk/demo/patient/" + patientId );

			NodeList xpatientElems = (NodeList) xpath.compile ( "detected-proteins/protein" ).evaluate ( xpatient, XPathConstants.NODESET ); 
			for ( int j = 0; j < xpatientElems.getLength (); j++ )
			{
				Node xprotein = xpatientElems.item ( j );
				String uniProtId = xprotein.getFirstChild ().getNodeValue ();
				
				// The protein resource, with the URI built from the UniProt ID.
				// We omit to define which OWL class it's instance of, since we'll be using the UniProt dataset, where this
				// is already defined
				Resource rdfUniProt = outModel.createResource ( "http://purl.uniprot.org/uniprot/" + uniProtId );
				
				// OK, now we can generate various statements 
				// 
				
				// This protein was detected in this patient
				outModel.add ( 
					rdfPatient, outModel.getProperty ( "http://rdf.ebi.ac.uk/terms/atlas/dbXref" ), rdfUniProt 
				);
				
				// And the patient is known to have this condition
				outModel.add ( 
					rdfPatient, 
					outModel.getProperty ( "http://rdf.ebi.ac.uk/terms/atlas/hasFactorValue" ), 
				  outModel.createResource ( diseaseTermUri ) 
				);
				
				
				// Let's add some attributes now
				//
				outModel.add ( rdfPatient, RDFS.label, "Patient from Experimental Data Set 2, ID #" + patientId );
				outModel.add ( rdfUniProt, RDFS.label, uniProtId );
			}
		}
		
		// Finished! Send all the data into a file, serialise in Turtle format 
		outModel.write ( args.length > 1 ? new FileOutputStream ( args [ 1 ] ) : System.out, "TURTLE" );
	}
	
}
