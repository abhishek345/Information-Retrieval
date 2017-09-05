import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


//class to build up the dictionary
public class Dictionary 
{
	private final Map<String, Properties>	lemmaDictionary;

	public Dictionary() 		//constructor of the class
	{
		this.lemmaDictionary = new HashMap<>();
	}
	
	private void appendToDictionary(final Map<String, Properties> appendTo, final Map<String, Integer> appendFrom, final Map<String, DocumentProperty> docProperties, final String file)		//appends the tokens to the dictionary 
	{
		for (final Entry<String, Integer> entry : appendFrom.entrySet()) 
		{
			Properties temp;
			if (appendTo.containsKey(entry.getKey())) 
			{
				temp = appendTo.get(entry.getKey());
				temp.setDocFreq(temp.getDocFreq() + 1);

				final Map<String, Integer> freq = temp.getTermFreq();
				freq.put(file, entry.getValue());
				temp.setTermFreq(freq);
			}
			else 
			{
				temp = new Properties();
				temp.setDocFreq(1);
				temp.getTermFreq().put(file, entry.getValue());
			}

			final DocumentProperty property = docProperties.get(file);		//gets the properties of the documents
			temp.getPostingFile().put(file, property);
			appendTo.put(entry.getKey(), temp);		//appends them to dictionary
		}
	}
	public void append(final StorageManager manager, final File file) 
	{
		final String doc = file.getName().replaceAll("[^\\d]", "");
		this.appendToDictionary(this.lemmaDictionary, manager.getLemmaMap(), StorageManager.getDocProperties(), doc);
	}

	public final Map<String, Properties> getLemmaDictionary() 
	{
		return this.lemmaDictionary;
	}
}
