import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

// class used to parse thorugh all the documents in the Cranfield text collection
public class DocumentParser
 {
	private final Set<String>	stopwords;		//used to store the stopwords
	private final Dictionary	dictionary;		// used to store the dictionary of words from the documents of the Cranfield text collection
	
	public DocumentParser(final File file) throws FileNotFoundException, IOException 		//constructor of the class
	{
		this.stopwords = new HashSet<>();
		this.dictionary = new Dictionary();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) 
		{
			for (String line; (line = reader.readLine()) != null;) 
			{
				this.stopwords.add(line.trim());		//stopwords added to the set
			}
		}
	}

	//reads file to build up the dictionary
	private void readFile(final File file) throws IOException 
	{
		if (file == null || !file.exists() || file.isDirectory()) 
		{
			return;
		}

		final Tokenizer tokenizer = new Tokenizer();
		final StorageManager storageManager = new StorageManager(this.stopwords);		//store the stopwords to be further removed from the query and documents
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) 
		{
			for (String line; (line = reader.readLine()) != null;) 
			{
				tokenizer.tokenize(file, line, storageManager);
			}
		}

		this.dictionary.append(storageManager, file);		//updated dictioanry with tokenized words and stopwords removed
	}
	
	public void parse(final File rootFile) throws IOException 
	{
		for (final File file : rootFile.listFiles()) 
		{
			if (file.isDirectory()) 		//checks if directory, then parses it further
			{
				this.parse(file);
			} 
			else 		// checks if file, reads the file
			{
				this.readFile(file);
			}
		}
	}

	public Set<String> getStopwords() 	// retrieves the stopwords from the path provided in 3rd command line argument and appended above in the function
	{
		return this.stopwords;
	}

	public final Dictionary getDictionary() 	// retrieves the dictionary of words which are appended above in the function
	{
		return this.dictionary;
	}
}