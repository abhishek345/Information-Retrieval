/* Homework_1
Subject name : Information Retrieval
Subject code : CS 6322.501
Intrsuctor 	 : Dr. Sanda Harabagiu
Grader		 : Travis Goodwin
Student name : Abhishek Jagwani
Class timing : MW 5:30pm-6:45pm
Implementation of Tokens and Stems via TreeMap
*/



import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class Token_stem_homework
{    

	static TreeMap<String, Integer> token = new TreeMap<String, Integer>();		//gives the token-frequency relationship
    static TreeMap<String, Integer> stem = new TreeMap<String, Integer>();		//gives the stem-frequency relationship
	static int total_number_of_tokens=0;	//gives the total number of tokens in Cranfield Text Collection
    static int total_number_of_stems=0;		//gives the total number of stems in Cranfield Text Collection
    static int total_files=0;				//gives the total number of files in Cranfield Text Collection
	
   

   private static void get_files(String text_path) throws FileNotFoundException		//used to get and read all the files from the Cranfield Text Collection
   {
        File file = new File(text_path);
        File file_list[] = file.listFiles();

        for(int i=0;i<file_list.length;i++)
		{
            if(file_list[i].isFile())
			{
                total_files++;
                get_details(file_list[i]);
            }

        }
    }

    static int count_unique(TreeMap<String, Integer> token)		//used to count unique tokens and stems from the Cranfield Text Collection
	{
        int unique_count=0;
		
        Iterator<Map.Entry<String, Integer>> unique_count_iterator = token.entrySet().iterator();
		
        while(unique_count_iterator.hasNext())
		{
            Map.Entry<String, Integer> list = unique_count_iterator.next();
            if(list.getValue()==1)
            {
				unique_count++;
			}
        }
        return unique_count;
    }

    public static TreeMap<String, Integer> frequent(final TreeMap<String, Integer> token)	//used to count frequent 30 tokens and stems from Cranfield Text Collection
	{
        Comparator<String> comparator = new Comparator<String>() 
		{
            public int compare(String string1, String string2) 	//used to compare tokens and stems for frequent 30 tokens and stems
			{
                if (token.get(string2).compareTo(token.get(string1)) == 0)
				{
                    return 1;
                }
				else
				{
					return token.get(string2).compareTo(token.get(string1));
				}
			}
        };

        TreeMap<String, Integer> sorted_token = new TreeMap<String, Integer>(comparator);	//used to sort the frequent 30 tokens and stems in decreasing order of their frequency occurence
        sorted_token.putAll(token);
        return sorted_token;
    }

    static void get_details(File file) throws FileNotFoundException 	//used to get the details of the files and ignore the special cases from the Cranfield Text Collection
	{
        Scanner scanner = new Scanner(file);

        while(scanner.hasNextLine())
		{
            String current = scanner.nextLine();
			
            if(current!=null && !(current.contains("<") && current.contains(">")))	//checks if the string has contains '<' or '>' within it
			{
                current = current.replaceAll("[-]", " ");	//if the string contains '<' or '>' within it then just replace the whole string with a blank space
                
				StringTokenizer tokenizer = new StringTokenizer(current);
                
				while(tokenizer.hasMoreTokens())
				{
                    String current_token = tokenizer.nextToken().toLowerCase();		//replaces every uppercase character with its respective lowercase character
                    String current_token_updated = current_token.replaceAll("[^a-zA-Z]", "");	//replaces eveything with a blank space which does not contain lowercase, uppercase and numbers

                    if(current_token_updated.equals(""))
					{
                        continue;
                    }
					else
					{
                        //loads every updated token into the TreeMap
                        total_number_of_tokens++;
                        if(token.get(current_token_updated) == null)
						{
                            token.put(current_token_updated, 1);
						}
                        else
						{
                            token.put(current_token_updated, token.get(current_token_updated) + 1);
						}

						
                        //Uses Stemmer class for each updated token to find its stem
                        Stemmer stemmer= new Stemmer();
                        char stem_char[] = current_token_updated.toCharArray();
                        stemmer.add(stem_char, stem_char.length);
                        stemmer.stem();

                        //loads every updated stem ino the TreeMap
                        total_number_of_stems++;
                        if(stem.get(stemmer.toString()) == null)
						{
                            stem.put(stemmer.toString(), 1);
						}
                        else
						{
                            stem.put(stemmer.toString(), stem.get(stemmer.toString()) + 1);
						}
                    }
                }
            }
        }
    }
	
	
	
	
	
	 public static void main(String args[])
	{
        String text_path = args[0].toString();		//text_path is passed as the path of the Cranfield Text Collection in te command line argument
        long starting_time = Calendar.getInstance().getTimeInMillis();		//used to get the time taken to perform the algorithm

        try 
		{
            get_files(text_path);
        }
		catch (FileNotFoundException e) 
		{
            e.printStackTrace();
        }

		
		//To print each and every token of the Cranfield Text Collection with their respective frequencies
		//If you want to print every token and stem, remove the multi-line comment from the next section
/* 
		Iterator<Map.Entry<String, Integer>> iterator = token.entrySet().iterator();
        while(iterator.hasNext()) 
		{
            Map.Entry<String, Integer> list = iterator.next();
            System.out.println(list.getKey() + " " + list.getValue());
        }
*/
	
		//questions related to token implementation
		System.out.println("");
		System.out.println("");
		System.out.println("------------------------------------------------------------------------------------------------------------------------");
		System.out.println("\t\t\t\t\tTokenization and Stemming");
		System.out.println("------------------------------------------------------------------------------------------------------------------------");
		System.out.println("");
		System.out.println("");
		System.out.println("------------------------------------------------------------------------------------------------------------------------");
		System.out.println("\t\t\t\t\tPart 1 : Tokenization");
		System.out.println("------------------------------------------------------------------------------------------------------------------------");
		System.out.println("");
        System.out.println("The Total Number of Tokens in the Cranfield text Collection : " + total_number_of_tokens);
        System.out.println("The Total Number of Unique Tokens in the Cranfield text Collection : " + token.size());
        System.out.println("The Total Number of tokens that occur only once in the Cranfiield Text Collection : " + count_unique(token));
        System.out.println("The 30 most frequent tokens in the Cranfield Text Collection : ");
		System.out.println("");

        TreeMap<String, Integer> frequent30Token = frequent(token);	//used to get the frequent 30 tokens from the Cranfield Text Collection
        Iterator<Map.Entry<String, Integer>> frequent_iterator = frequent30Token.entrySet().iterator();
        
		System.out.format("%15s%15s%15s", "No.", "Stems", "Frequency");
		System.out.println("");
		int frequent_count=1;
        while(frequent_count<=30)
		{
            Map.Entry<String, Integer> list = frequent_iterator.next();
            System.out.format("%15s%15s%15s",frequent_count + ") ", list.getKey(), list.getValue());	//prints frequent 30 tokens from Cranfield Text Collection
			System.out.println("");
            frequent_count++;
        }
		System.out.println("");
		System.out.println("The Average Number of word tokens per document in the Cranfield Text Collection : " + total_number_of_tokens/total_files);

		System.out.println("");
		System.out.println("");
		
		
		//questions related to program description
        System.out.println("Total Time taken to acquire the text characteristics: " + (Calendar.getInstance().getTimeInMillis()-starting_time) + "ms");

		System.out.println("");
		System.out.println("");
		
		
	
		//questions related to stem implementation
		System.out.println("------------------------------------------------------------------------------------------------------------------------");
		System.out.println("\t\t\t\t\tPart 2: Stemming");
		System.out.println("------------------------------------------------------------------------------------------------------------------------");
		System.out.println("");
        System.out.println("The Total Number of Distinct stems in the Cranfield Text Collection : " + stem.size());
        System.out.println("The Total Number of stems that occur only once in the Cranfield Text Collectiion : " + count_unique(stem));
        System.out.println("The 30 most frequent stems in the Cranfield text Collectiion : ");
		System.out.println("");
        TreeMap<String, Integer> frequent30Stem= frequent(stem);	//used to get frequent 30 stems from the Cranfield Text Collection
        Iterator<Map.Entry<String, Integer>> frequent_iterator_stem = frequent30Stem.entrySet().iterator();
       
		System.out.format("%15s%15s%15s", "No.", "Stems", "Frequency");
		System.out.println("");
		frequent_count=1;
        while(frequent_count<=30)
		{
            Map.Entry<String, Integer> list = frequent_iterator_stem.next();
			System.out.format("%15s%15s%15s",frequent_count + ") ", list.getKey(), list.getValue());	//prints frequent 30 stem from Cranfield Text Collection
			System.out.println("");
			frequent_count++;
        }
		
		System.out.println("");
		System.out.println("The Average Number of Word Stems per Document : " + total_number_of_stems/total_files);
		System.out.println("");
		System.out.println("");

    }
}