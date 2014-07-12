package xml2rdf_demo;

import java.io.FileOutputStream;

import org.junit.Test;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * See the slides here: http://prezi.com/u1xb6yov6rwk/lod-tutorial-devsummer2014/
 *
 * <dl><dt>date</dt><dd>11 Jul 2014</dd></dl>
 * @author Marco Brandizi
 *
 */
public class Exercise1
{
	public void mapData1Fields ( OntModel model, String sampleId, String uniProtId, String efoId )
	{
		// The sample
		OntClass efoIndividual = model.createClass ( "http://www.ebi.ac.uk/efo/OBI_0000747" );
		Individual rdfSample = efoIndividual.createIndividual ( "http://rdf.ebi.ac.uk/demo/sample/" + sampleId );

		// And its label
		model.add ( rdfSample, RDFS.label, "Human Sample from Experimental Data Set 1, ID #" + sampleId );

		// The protein
		Resource rdfUniProt = model.createResource ( "http://purl.uniprot.org/uniprot/" + uniProtId );
		
		// The disease condition, we now have the EFO code, so we can build it directly
		Resource rdfDiseaseTerm = model.createResource ( "http://www.ebi.ac.uk/efo/" + efoId );
		
		
		// This protein is linked to this sample
		model.add ( 
			rdfSample, model.getProperty ( "http://rdf.ebi.ac.uk/terms/atlas/dbXref" ), rdfUniProt 
		);
		
		// And the sample is known to come from a patient having this condition
		model.add ( 
			rdfSample, 
			model.getProperty ( "http://rdf.ebi.ac.uk/terms/atlas/hasFactorValue" ), 
			rdfDiseaseTerm
		);
	}

	@Test
	public void testEx1 ()
	{
		OntModel m = ModelFactory.createOntologyModel ( OntModelSpec.OWL_MEM );
		mapData1Fields ( m, "fooSample1", "FOOPROT1", "EFO_123" );
		m.write ( System.out, "TURTLE" );
	}
}
