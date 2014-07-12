package uk.ac.ebi.rdf.demo.web.search.condition;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * TODO: Comment me!
 *
 * <dl><dt>date</dt><dd>7 Jul 2014</dd></dl>
 * @author Marco Brandizi
 *
 */
public class Condition
{
	public String 
  conditionTermUri,
  conditionLabel;
	
	public int proteinCount;
 
	public List<Protein> proteins = new LinkedList<> ();
}
