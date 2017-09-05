import java.util.HashSet;
import java.util.Set;

// class used to get the properties of the document
public class DocumentProperty 
{
	private int					doclen;
	private int					maxFreq;
	private String				headline;
	private final Set<String>	words;

	public DocumentProperty() 		//contructor of the class
	{
		this.words = new HashSet<>();
	}

	public final int getDoclen() 		// get the document length
	{
		return this.doclen;
	}

	public final void setDoclen(final int doclen) 		//set the document length
	{
		this.doclen = doclen;
	}

	public final int getMaxFreq() 		//get maxtf
	{
		return this.maxFreq;
	}

	public final void setMaxFreq(final int maxFreq)		//set maxtf
	{
		this.maxFreq = maxFreq;
	}


	public String getHeadline() 		//get the  headline of the document
	{
		return this.headline;
	}

	public void setHeadline(final String headline) 		//set the headline of the document
	{
		this.headline = headline;
	}

	public final Set<String> getWords() 		//get the words from the set
	{
		return this.words;
	}
}