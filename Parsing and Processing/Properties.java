
		
import java.util.HashMap;
import java.util.Map;

// class used to get and set the document properties
public class Properties 
{
	private int								docFreq;
	private Map<String, DocumentProperty>	postingFile	= new HashMap<>();
	private Map<String, Integer>			termFreq	= new HashMap<>();

	
	public final Map<String, Integer> getTermFreq() 		//used to get the term frequency in the document
	{
		return this.termFreq;
	}

	public final void setTermFreq(final Map<String, Integer> termFreq) 		//used to set the term frequency
	{
		this.termFreq = termFreq;
	}

	public final int getDocFreq() 		// used to get the document frequency
	{
		return this.docFreq;
	}

	public final void setDocFreq(final int docFreq) 		// used to set the document frequency
	{
		this.docFreq = docFreq;
	}

	public final Map<String, DocumentProperty> getPostingFile() 		//used to get the posting file of the words
	{
		return this.postingFile;
	}

	public final void setPostingFile(final Map<String, DocumentProperty> postingFile) 		//used to set the posting file of the words
	{
		this.postingFile = postingFile;
	}

}