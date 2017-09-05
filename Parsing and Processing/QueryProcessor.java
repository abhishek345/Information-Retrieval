import java.util.HashMap;
import java.util.Map;

//class used to process the query and get its properties with respect to the documents
public class QueryProcessor 
{
	private final Map<String, Properties>			index;
	private final Map<String, Double>				W1;
	private final Map<String, Double>				W2;
	private final Map<String, Map<String, Double>>	W1Doc;
	private final Map<String, Map<String, Double>>	W2Doc;
	private final double							avgdoclen;

	public QueryProcessor(final Map<String, Properties> index, final double avgdoclen) 		//constructor of the class
	{
		this.index = index;
		this.W1 = new HashMap<>();
		this.W2 = new HashMap<>();
		this.W1Doc = new HashMap<>();
		this.W2Doc = new HashMap<>();
		this.avgdoclen = avgdoclen;
	}
	
	public Map<String, Double> getW1() 		//get the W1 of the query
	{
		return this.W1;
	}

	public Map<String, Double> getW2() 		//get the W2 of the query
	{
		return this.W2;
	}

	public final Map<String, Map<String, Double>> getW1Doc() 		// get the W1 document of the query
	{
		return this.W1Doc;
	}

	public final Map<String, Map<String, Double>> getW2Doc() 		// get the W2 document of the query
	{
		return this.W2Doc;
	}

	public void process(final Dictionary query) 			// actual querry processed here
	{
		final int collectionSize = this.index.size();		// used to get the collection size
		for (final String term : query.getLemmaDictionary().keySet()) 		// get the properties of the query
		{
			final Properties properties = this.index.get(term);
			if (properties == null) 
			{
				continue;
			}

			final int df = properties.getDocFreq();
			final Map<String, DocumentProperty> postingFile = properties.getPostingFile();

			for (final String docID : postingFile.keySet()) 		//get the properties of the posting file
			{
				final int maxtf = postingFile.get(docID).getMaxFreq();
				final int doclen = postingFile.get(docID).getDoclen();
				final int tf = properties.getTermFreq().get(docID);

				this.updateWeights(collectionSize, term, df, docID, maxtf, doclen, tf);		//updates up the porperties of the query
			}
		}
	}
	
	public void process() 		// used to process the query
	{
		final int collectionSize = this.index.size();
		for (final String term : this.index.keySet()) 
		{
			final Properties properties = this.index.get(term);
			final int df = properties.getDocFreq();
			final Map<String, DocumentProperty> postingFile = properties.getPostingFile();

			for (final String docID : postingFile.keySet()) 
			{
				final int maxtf = postingFile.get(docID).getMaxFreq();
				final int doclen = postingFile.get(docID).getDoclen();
				final int tf = properties.getTermFreq().get(docID);

				final double w1 = this.W1(tf, maxtf, df, collectionSize);
				double weight = this.W1.containsKey(term) ? this.W1.get(term) : 0.0;
				this.W1.put(term, weight + w1);

				final double w2 = this.W2(tf, doclen, this.avgdoclen, df, collectionSize);
				weight = this.W2.containsKey(term) ? this.W2.get(term) : 0.0;
				this.W2.put(term, weight + w2);
			}
		}
	}

	private void updateWeights(final int collectionSize, final String term, final int df, final String docID, final int maxtf, final int doclen, final int tf) 		// used to update the weights of the query
	{
		final double w1 = this.W1(tf, maxtf, df, collectionSize);
		double weight = this.W1.containsKey(docID) ? this.W1.get(docID) : 0.0;
		this.W1.put(docID, weight + w1);

		if (this.W1Doc.get(docID) == null) 
		{
			this.W1Doc.put(docID, new HashMap<String, Double>());
		}

		weight = this.W1Doc.get(docID).containsKey(term) ? this.W1Doc.get(docID).get(term) : 0.0;
		this.W1Doc.get(docID).put(term, weight + w1);

		final double w2 = this.W2(tf, doclen, this.avgdoclen, df, collectionSize);
		weight = this.W2.containsKey(docID) ? this.W2.get(docID) : 0.0;
		this.W2.put(docID, weight + w2);

		if (this.W2Doc.get(docID) == null) 
		{
			this.W2Doc.put(docID, new HashMap<String, Double>());
		}

		weight = this.W2Doc.get(docID).containsKey(term) ? this.W2Doc.get(docID).get(term) : 0.0;
		this.W2Doc.get(docID).put(term, weight + w2);
	}


	public double W1(final int tf, final int maxtf, final int df, final int collectionSize) 		//gives the values of the Weighting function W1
	{
		double temp = 0;
		try 
		{
			temp = (0.4 + 0.6 * Math.log(tf + 0.5) / Math.log(maxtf + 1.0)) * (Math.log(collectionSize / (double) df) / Math.log(collectionSize));
		}
		catch (final Exception e) 
		{
			temp = 0;
		}

		return temp;
	}

	public double W2(final int tf, final int doclen, final double avgdoclen, final int df, final int collectionSize) 		//gives the values of the Weighting function W2
	{
		double temp = 0;
		try 
		{
			temp = 0.4 + 0.6 * (tf / (tf + 0.5 + 1.5 * (doclen / avgdoclen))) * Math.log(collectionSize / (double) df) / Math.log(collectionSize);
		}
		catch (final Exception e) 
		{
			temp = 0;
		}

		return temp;
	}

}