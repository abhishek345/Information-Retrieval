import java.io.File;
import java.io.IOException;
import java.util.List;


// class used to tokenize the text
public class Tokenizer 
{
	public static StanfordLemmatizer lemmatizer = new StanfordLemmatizer();		//used to find the lemmas of the tokens

	private String tokeniseText(String text) 
	{		
		text = text.replaceAll("\\<.*?>", " ");		// Replacing the SGML tags with space
	
		text = text.replaceAll("[\\d+]", "");		// Removing all the digits

		text = text.replaceAll("[+^:,?;=%#&~`$!@*_)/(}{\\.]", "");			// Removing all the special characters

		text = text.replaceAll("\\'s", "");		// Removing all the possessives

		text = text.replaceAll("\\'", " ");			// Replacing all the "'" with a space

		text = text.replaceAll("-", " ");		// Replacing all the "-" with space to count two different words

		text = text.replaceAll("\\s+", " ");		// Removing multiples white spaces

		text = text.trim().toLowerCase();		// Trimming and setting whole text to lower case
		
		return text;
	}
	
	public void tokenize(final File file, String line, final StorageManager storageManager) throws IOException 		//once the text is modified, it is tokenized here
	{
		line = this.tokeniseText(line);

		final String[] words = line.split(" ");		// each line split with a space to get the new word in the next line
		
		for (final String word : words)
		{
			if (word == null || word.length() < 1) 
			{
				continue;
			}
			final List<String> lemma = lemmatizer.lemmatize(word);		//creates a list to lemmatise the words
			storageManager.store(word, lemma, file);		// store the lemmatized words to retrieve its characteritics
		}
	}
}