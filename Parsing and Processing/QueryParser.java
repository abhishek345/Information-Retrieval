import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

// class used to parse the query
public class QueryParser 
{
	private final Set<String>		stopwords;
	private final List<Dictionary>	dictionaries;

	public final List<Dictionary> getDictionaries() 		//gets the dictionary to parse the words of the query through them
	{
		return this.dictionaries;
	}

	public void readFile(final File file) throws IOException 		//reads text file to parse the query
	{
		if (file == null || !file.exists() || file.isDirectory()) 
		{
			return;
		}

		final String data = new String(Files.readAllBytes(file.toPath()));
		final String[] parts = Pattern.compile("[Q0-9:]+").split(data);
		final List<String> queries = new ArrayList<>();
		for (final String part : parts) 
		{
			final String query = part.trim().replaceAll("\\r\\n", " ");		//trim the query and update it
			if (query.length() > 0) 
			{
				queries.add(query);		//add the updated query
			}
		}

		final Tokenizer tokenizer = new Tokenizer();
		for (final String query : queries) 
		{
			final StorageManager storageManager = new StorageManager(this.stopwords);
			tokenizer.tokenize(file, query, storageManager);		//tokenize the query by removing the stopwords

			final Dictionary dictionary = new Dictionary();
			dictionary.append(storageManager, file);		//append it to the dictionary
			this.dictionaries.add(dictionary);
		}
	}
	
	public QueryParser(final Set<String> stopwords) 		// constructor of the class
	{
		this.stopwords = stopwords;
		this.dictionaries = new ArrayList<>();
	}
}