/* Homework_3
Subject name : Information Retrieval
Subject code : CS 6322.501
Intrsuctor 	 : Dr. Sanda Harabagiu
Grader		 : Travis Goodwin
Student name : Abhishek Jagwani
Class timing : MW 5:30pm-6:45pm
Implementation of simple statistical relevance model based on vector relevance model using inverted list index
*/

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


public class Homework3 
{

	private static final TextCharacteristics characteristics = new TextCharacteristics(); 	//used to get the characteristics of the terms including tf, maxtf, df, doclen, avgdoclen, collectionsize

	private static void displayResults(final Map<String, Properties> lemmaDictionary, final double avgdoclen, final QueryParser queryParser)	//used to ouput the results
	{
		int number = 1;
			System.out.println("-----------------------------------------------------------------------------");
			System.out.println("                          Statistical Relevance Model                        ");
			System.out.println("-----------------------------------------------------------------------------");
		for (final Dictionary dictionary : queryParser.getDictionaries())
		{
			final QueryProcessor processor = new QueryProcessor(lemmaDictionary, avgdoclen);	// for each query, query processor is retrieved for average doclen
			processor.process(dictionary);		//query processor for each process retrieved above is processed here
			System.out.println("\n\n");
			System.out.println("                          ----------------");
			System.out.println("                          Query number " + number + "\n");	// displays the query number
			System.out.println("                          ----------------");
			final double avglen = characteristics.getAverageDocumentLength(dictionary.getLemmaDictionary());	// calculates the average document length of the dictionary
			final QueryProcessor queryProcessor = new QueryProcessor(dictionary.getLemmaDictionary(), avglen);		// for each query, query processor is retrieved for average len
			queryProcessor.process();		//query processor for each process retrieved above is processed here

			// The below section prints Vector Representation for W1
			System.out.println("--> The Vector Representation for W1 is as follows :");
			System.out.println(characteristics.getQueryRepresentation(queryProcessor.getW1()));		// displays the vector representation of respective query for W1
			System.out.println("\n--> Charcteristic Table for W1 :\n");
			System.out.println(characteristics.getTopFive(processor.getW1()));		// displays the top 5 ranked documents with theeir rank, score, external document identifier and headline
			System.out.println("\n--> The Vector Representation of Top 5 ranked documents : ");
			System.out.println(characteristics.getTopFiveDocumentRepresentation(processor.getW1(), processor.getW1Doc()));		// displays the vector representation of the top 5 ranked documents

			// The below section prints Vector Representation for W2
			System.out.println("-->The Vector Representation for W2 is as follows :");
			System.out.println(characteristics.getQueryRepresentation(queryProcessor.getW2()));		// displays the vector representation of respective query for W2
			System.out.println("\n--> Characteristic Table for W2 :\n");
			System.out.println(characteristics.getTopFive(processor.getW2()));		// displays the top 5 ranked documents with theeir rank, score, external document identifier and headline
			System.out.println("\n--> The Vector Representation of Top 5 ranked documents : ");
			System.out.println(characteristics.getTopFiveDocumentRepresentation(processor.getW2(), processor.getW2Doc()));		// displays the vector representation of the top 5 ranked documents
			number++;
		}

		System.out.println("__________________________________________________________________________________________________");
		System.out.println("\n");
	}
	
	public static void main(final String args[]) throws IOException 
	{
		final File query = new File(args[0]);		//1st command line argument as query
		final File folder = new File(args[1]);		//2nd command line argument as Cranfield collection path
		final File stopwords = new File(args[2]);		//3rd command line argument as stopwords
		
		final DocumentParser documentParser = new DocumentParser(stopwords);	//parses through all the documents in the ranfield Text collection and removes stopwords
		documentParser.parse(folder);

		final Map<String, Properties> lemmaDictionary = documentParser.getDictionary().getLemmaDictionary();		//used to create the dictionary of the tokens
		final double avgdoclen = characteristics.getAverageDocumentLength(lemmaDictionary);		//used to get the average document length in the collection


		final QueryParser queryParser = new QueryParser(documentParser.getStopwords());		// used to parse the query thorugh each document and compute the characteristics of each query
		queryParser.readFile(query);

		displayResults(lemmaDictionary, avgdoclen, queryParser);		//function used to compute the result
	}




}