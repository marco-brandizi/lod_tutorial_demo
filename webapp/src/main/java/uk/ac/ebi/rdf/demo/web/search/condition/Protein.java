package uk.ac.ebi.rdf.demo.web.search.condition;

/**
 * TODO: Comment me!
 *
 * <dl><dt>date</dt><dd>7 Jul 2014</dd></dl>
 * @author Marco Brandizi
 *
 */
public class Protein
{
	public String proteinUri, uniProtId;

	@Override
	public int hashCode ()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( proteinUri == null ) ? 0 : proteinUri.hashCode () );
		return result;
	}

	@Override
	public boolean equals ( Object obj )
	{
		if ( this == obj ) return true;
		if ( obj == null ) return false;
		if ( getClass () != obj.getClass () ) return false;
		Protein other = (Protein) obj;
		if ( proteinUri == null )
		{
			if ( other.proteinUri != null )
				return false;
		} else if ( !proteinUri.equals ( other.proteinUri ) )
			return false;
		return true;
	}

}
