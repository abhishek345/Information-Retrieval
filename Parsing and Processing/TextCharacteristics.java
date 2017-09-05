import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;


//class used to get the characteristics of the text including the rank, score, external document identifier and headline
public class TextCharacteristics
{
	public double getAverageDocumentLength(final Map<String, Properties> index) 		//used to get the average document length
	{
		double sum = 0.0;
		for (final String term : index.keySet()) 
		{
			final Properties properties = index.get(term);
			sum += properties.getDocFreq();
		}

		return sum / index.size();		//returns average document length
	}

	public String getTopFiveDocumentRepresentation(final Map<String, Double> queryMap, final Map<String, Map<String, Double>> docMap) 		//used to get top 5 document representation
	{
		final StringBuilder builder = new StringBuilder();
		final SortableMap sortableMap = new SortableMap(queryMap);
		final TreeMap<String, Double> sortedMap = new TreeMap<>(sortableMap);
		sortedMap.putAll(queryMap);

		int count = 0;
		for (final String key : sortedMap.descendingKeySet()) 
		{
			if (++count > 5) 
			{
				break;
			}

			final Set<String> words = StorageManager.getDocProperties().get(key).getWords();
			final Map<String, Double> map = docMap.get(key);

			builder.append("\nExternal Document Identifier: Cranfield" + key + "\n");		//gives the top ranked cranfield doument number
			for (final String string : words) 
			{
				if (map.containsKey(string)) 
				{
					builder.append(string + " : " + map.get(string) + "\n");		//append the word of the document and its vector representation
				}
				else 
				{
					builder.append(string + " : 0.0 \n");
				}
			}

			builder.append("\n");
		}

		return builder.toString();
	}
	

	public OutputFormatter getTopFive(final Map<String, Double> queryMap) 		//used to format the output
	{
		final OutputFormatter formatter = new OutputFormatter();		//used to create the characteristic table for W1 and W2
		formatter.addRow("RANK", "SCORE", "EXTERNAL DOCUMENT IDENTIFIER", "HEADLINE");
		final SortableMap sortableMap = new SortableMap(queryMap);
		final TreeMap<String, Double> sortedMap = new TreeMap<>(sortableMap);
		sortedMap.putAll(queryMap);

		int count = 0;
		for (final String key : sortedMap.descendingKeySet()) 
		{
			if (++count > 5) 
			{
				break;
			}

			final String headline = StorageManager.getDocProperties().get(key).getHeadline();
			final String score = String.valueOf(queryMap.get(key));

			formatter.addRow(String.valueOf(count), score, "Cranfield" + key, headline);		//add up rows in the table
		}

		return formatter;
	}

	public String getQueryRepresentation(final Map<String, Double> dictionary) 		//used to fill up the table 
	{
		final StringBuilder builder = new StringBuilder();
		for (final Entry<String, Double> term : dictionary.entrySet()) {
			builder.append(term.getKey() + " : " + term.getValue() + "\n");
		}

		return builder.toString().trim();
	}
}