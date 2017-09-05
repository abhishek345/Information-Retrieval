import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

// class used to store the document and query information
public class StorageManager 
{
	private final Map<String, Integer>				lemmaMap;
	private static Map<String, DocumentProperty>	docProperties	= new HashMap<>();;
	private static Set<String>						stopwords;

	public StorageManager(final Set<String> stopwords) 		//constructor of the class
	{
		this.lemmaMap = new HashMap<>();
		StorageManager.stopwords = stopwords;
	}
	
	private static String getLine(final File file) throws IOException 		//get the lines of the text documents
	{
		final String data = new String(Files.readAllBytes(file.toPath()));

		final Pattern pattern = Pattern.compile("<.?title>", Pattern.CASE_INSENSITIVE);
		final String[] parts = pattern.split(data);
		if (parts.length > 1) 
		{
			return parts[1].replace("\n", "");
		}
		else 
		{
			return "";
		}
	}

	public void store(final String word, final List<String> lemma, final File file) throws IOException 		//store the informatin regarding stopwords, document properties
	{
		final String doc = file.getName().replaceAll("[^\\d]", "");
		if (!docProperties.containsKey(doc)) 
		{
			docProperties.put(doc, new DocumentProperty());
		}

		final String headline = getLine(file);
		docProperties.get(doc).setHeadline(headline);

		if (!StorageManager.stopwords.contains(word)) 
		{
			int count = 0;

			for (final String string : lemma) 
			{
				count = this.lemmaMap.containsKey(string) ? this.lemmaMap.get(string) : 0;
				this.lemmaMap.put(string, count + 1);

				docProperties.get(doc).getWords().add(string);
			}

			if (docProperties.get(doc).getMaxFreq() < count + 1) 
			{
				docProperties.get(doc).setMaxFreq(count + 1);
			}

			final int len = docProperties.get(doc).getDoclen();
			docProperties.get(doc).setDoclen(len + 1);
		}
	}

	public final Map<String, Integer> getLemmaMap() 		//gives the mapTree of the lemmas
	{
		return this.lemmaMap;
	}

	public static final Map<String, DocumentProperty> getDocProperties() 		//gives the properties of the documents
	{
		return StorageManager.docProperties;
	}
}