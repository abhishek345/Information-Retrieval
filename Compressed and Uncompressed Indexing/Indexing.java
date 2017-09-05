import java.io.*;
import java.util.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.*
import edu.stanford.nlp.util.CoreMap;

public class Indexing {


	static PrintWriter out;		//used to write the output to the file
	static Stemmer stem = new Stemmer();		//used to get the stems from the tokens
	static double execTime = 0.0;		//used to get the elapsed time required to build any version of index
	
	


	private static void writeIndex(ArrayList<CompressedTermIndex> compressedTermIndex1, String filePath) throws Exception {		//used to write the data of compressed index versions of terms and stems
		RandomAccessFile index = new RandomAccessFile(filePath, "rw");
		index.writeBytes(CompressedTermIndex.termFrontCoding);		//write into the file using front coding
		index.writeBytes("\n");
		for (int i = 0; i < compressedTermIndex1.size(); i++) {
			CompressedTermIndex term = compressedTermIndex1.get(i);
			index.write(term.docFrequency);index.writeBytes("\t");		//writes document frequency of the term
			index.write(term.termFrequency);index.writeBytes("\t");		//writes term frequency of the term
			index.write(term.termPointer);index.writeBytes("\t");		//points to the posting list
			for (int j = 0; j < term.postingFile.size(); j++) {			//loop used to write the properties of each term
				 index.write(term.postingFile.get(j).docId);index.writeBytes("\t");		//writes the document id of the term
				 index.write(term.postingFile.get(j).termFrequency);		//writes the term frequency of the term in that document
			}
			index.writeBytes("\n");	
		}
		index.close();
	}


	private static void writeIndex(TreeMap<String, TermNode> wordMap, String filePath) throws Exception{		//used to write the data of uncompressed index versions of terms
		RandomAccessFile index = new RandomAccessFile(filePath, "rw");
		Iterator<String> it = wordMap.keySet().iterator();
		while(it.hasNext()){		//used for every term for tree_map that is uncompressed version of term
			String term = it.next();
			TermNode node = wordMap.get(term);
			index.writeBytes(term);		//writes the term
			index.writeBytes("\t");
			index.write(node.docFrequency);		//writes the document frequency of the term
			index.write(node.termFrequency);	//writes the term frequency of the respective term
			Iterator<Integer> it1 = node.postingFiles.keySet().iterator();			//used to create the posting file of the term
			while (it1.hasNext()) {
				Integer docId = (Integer) it1.next();
				Integer termFreq = node.postingFiles.get(docId);
				index.write(docId);		//writes the document id
				index.writeBytes("\t");
				index.write(termFreq);		//writes the term frequency within the document
				index.writeBytes("\t");
			}
			index.writeBytes("\n");	
		}
		index.close();
	}


	public static ArrayList<CompressedTermIndex> compressIndex(TreeMap<String, TermNode> map, int blockingFactor) {		//compresses the dictionary and posting files and thus returns back to the user
		ArrayList<CompressedTermIndex> compressedTermIndexList = compressDictionaryAndPostingFile(map, blockingFactor);
		return compressedTermIndexList;
	}


	public static ArrayList<CompressedTermIndex> compressDictionaryAndPostingFile(TreeMap<String, TermNode> map,		//used to compress dictionary and posting files 
			int blockingFactor) {
		Iterator<String> it = map.keySet().iterator();
		ArrayList<CompressedTermIndex> compressedTermIndexList = new ArrayList<CompressedTermIndex>();		//used to compress the index of the terms
		int count = -1;
		int termPointer = 0;
		String[] terms = new String[blockingFactor];
		StringBuffer result = new StringBuffer();
		while (it.hasNext()) {
			String term = it.next();
			CompressedTermIndex termNode = compressPostingFile(map, term);		//compresses the posting list
			if (count == -1) {
				termNode.termPointer = termPointer;
				count++;
				terms[count] = term;
				count++;
			} else if (count <= blockingFactor - 1) {
				terms[count] = term;
				count++;
			} else {
				String frontCodedTerm = performFrontCoding(terms);
				result.append(frontCodedTerm);
				termPointer += frontCodedTerm.length();
				count = -1;
			}
			compressedTermIndexList.add(termNode);
		}
		CompressedTermIndex.termFrontCoding = result.toString();
		return compressedTermIndexList;
	}

	public static String performFrontCoding(String[] terms) {		//performs frontcoding of the term to get compressed
		String temp = terms[0];
		String encode = "";
		encode += temp.length() + temp + "*";
		String x = "";
		for (int i = 0; i < terms.length; i++) {
			x = terms[i];
			x = x.replace(temp, "");
			encode += x.length() + x + "*";
		}
		return encode;
	}

	
	public static CompressedTermIndex compressPostingFile(TreeMap<String, TermNode> map, String term) {		//compresses the posting file of terms
		TermNode node = map.get(term);
		CompressedTermIndex termNode = new CompressedTermIndex();
		termNode.docFrequency = node.docFrequency;
		termNode.termFrequency = node.termFrequency;
		termNode.postingFile  = new ArrayList<>();
		termNode.postingFile.addAll(compressPostingFileUsingGammaCode(node.postingFiles));
		return termNode;
	}


	public static ArrayList<PostingFileNode> compressPostingFileUsingGammaCode(		//performs compressiion using gamma codes through the method mentioned
			TreeMap<Integer, Integer> postingFiles) {
		
		ArrayList<PostingFileNode> compressedList = new ArrayList<>();
		Iterator<Integer> it = postingFiles.keySet().iterator();
		Integer firstDocIdEntry = it.next();
		Integer termFreq = postingFiles.get(firstDocIdEntry);
		PostingFileNode node = new PostingFileNode();
		node.docId = ExtractGammaCode(firstDocIdEntry);		//computes the gamma code
		node.termFrequency = deltaCode(termFreq);		//computes delta code
		compressedList.add(node);		//adds the compressed version of the each node
		
		while(it.hasNext()){
			Integer docId = it.next();
			termFreq = postingFiles.get(docId);	
			
			node = new PostingFileNode();
			node.docId = ExtractGammaCode(docId - firstDocIdEntry);
			node.termFrequency = deltaCode(termFreq);
			compressedList.add(node);
			firstDocIdEntry = docId;
		}
		return compressedList;		//returns the compressed list performed by the gamma codes
	}


	public static byte[] deltaCode(Integer termFreq) {		//compression done via delta codes through the method mentioned 
		String binary = Integer.toBinaryString(termFreq);
		String gammaCode = gammaCode(binary.length());
		binary = binary.substring(1);
		String deltaCode = gammaCode + binary;
		BitSet bits = new BitSet();
		bits = fromString(deltaCode);
		return bits.toByteArray();
	}

	
	public static byte[] ExtractGammaCode(Integer docId){
		BitSet bits = new BitSet();
		bits = fromString(gammaCode(docId));
		return bits.toByteArray();
	}
	
	
	private static BitSet fromString(final String s) {
        return BitSet.valueOf(new long[] { Long.parseLong(s, 2) });
    }
	

	public static String gammaCode(Integer docId) {
		String binary = Integer.toBinaryString(docId);
		binary = binary.substring(1);
		String unaryCodeForLength = "";
		for (int i = 0; i < binary.length(); i++) {
			unaryCodeForLength += "1";
		}
		unaryCodeForLength += "0";
		String gamma = unaryCodeForLength + binary;
		return gamma;
	}

	
	public static ArrayList<String> extractStopWords(File stopwordsFile) throws FileNotFoundException, IOException {		//extracts the stop words from the stopwords file
		
		ArrayList<String> stopWordsList = new ArrayList<String>();
		String line = "";
		FileReader fileReader = new FileReader(stopwordsFile);
		@SuppressWarnings("resource")
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		while ((line = bufferedReader.readLine()) != null) {
			stopWordsList.add(line);
		}
		
		return stopWordsList;
	}


	public static void tokanizeDocuments(File[] listOfFiles, ArrayList<String> stopWordsList,
			TreeMap<String, TermNode> wordMap, TreeMap<String, TermNode> stemWordMap)
					throws FileNotFoundException, IOException {		//tokenizes rhe documents from the Cranfield collection

		long startTime = new Date().getTime();
		
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	
		
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String line = "";
				FileReader fileReader = new FileReader(listOfFiles[i].getAbsoluteFile());
				@SuppressWarnings("resource")
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				while ((line = bufferedReader.readLine()) != null) {
					line = line.toLowerCase();
					line = line.replaceAll("\\<.*?>", "");
					line = line.replaceAll("[^a-zA-Z\\s]", "").replaceAll("\\s+", " ");

					
					String text = line;		// read some text in the text variable

					
					Annotation document = new Annotation(text);		// create an empty Annotation just with the given text

					
					pipeline.annotate(document);		// run all Annotators on this text

				
					List<CoreMap> sentences = document.get(SentencesAnnotation.class);
					String word = "";
					for (CoreMap sentence : sentences) {
						
						for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
								
							word = token.get(LemmaAnnotation.class);
							if (stopWordsList.contains(word)) {
								continue;
							}
							String testWord = word.trim();
							if (!testWord.isEmpty()) {
								if (wordMap.containsKey(testWord)) {
									TermNode node = wordMap.get(testWord);
									node.termFrequency += 1;
									if (node.postingFiles.containsKey(i)) {
										int val = node.postingFiles.get(i);
										node.postingFiles.put(i, val + 1);
									} else {
										node.postingFiles.put(i, 1);
										node.docFrequency += 1;
									}
								} else {
									TermNode node = new TermNode();
									node.termFrequency = 1;
									node.postingFiles = new TreeMap<Integer, Integer>();
									if (node.postingFiles.containsKey(String.valueOf(i))) {
										int val = node.postingFiles.get(String.valueOf(i));
										node.postingFiles.put(i, val + 1);
									} else {
										node.postingFiles.put(i, 1);
										node.docFrequency += 1;
									}
									wordMap.put(testWord, node);
								}
							}

						}
					}

					String[] words = line.split(" ");
					for (int j = 0; j < words.length; j++) {
						if (stopWordsList.contains(words[j])) {
							continue;
						}
						String testWord = words[j].trim();

						stem.add(testWord.toCharArray(), testWord.length());
						stem.stem();
						String stemmedWord = stem.toString();

						if (!stemmedWord.isEmpty()) {
							if (stemWordMap.containsKey(stemmedWord)) {
								TermNode node = stemWordMap.get(stemmedWord);
								node.termFrequency += 1;
								if (node.postingFiles.containsKey(i)) {
									int val = node.postingFiles.get(i);
									node.postingFiles.put(i, val + 1);
								} else {
									node.postingFiles.put(i, 1);
									node.docFrequency += 1;
								}
							} else {
								TermNode node = new TermNode();
								node.termFrequency = 1;
								node.postingFiles = new TreeMap<Integer, Integer>();
								if (node.postingFiles.containsKey(i)) {
									int val = node.postingFiles.get(i);
									node.postingFiles.put(i, val + 1);
								} else {
									node.postingFiles.put(i, 1);
									node.docFrequency += 1;
								}
								stemWordMap.put(stemmedWord, node);
							}
						}

					}
				}
			}
		}
		
		long endtime = new Date().getTime();
		execTime = (endtime - startTime) / 1000;
		return;
	}


	public static SortedSet<Map.Entry<String, Integer>> entriesSortedByFrequency(Map<String, Integer> map) {		//sorts entries in the index based upon the frequencies
		SortedSet<Map.Entry<String, Integer>> sortedEntries = new TreeSet<Map.Entry<String, Integer>>(
				new Comparator<Map.Entry<String, Integer>>() {
					@Override
					public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
						int res = e1.getValue().compareTo(e2.getValue());
						if (res == -1) {
							res = 1;
						} else if (res == 1) {
							res = -1;
						}
						if (e1.getKey().equals(e2.getKey())) {
							return res;
						} else {
							return res != 0 ? res : 1;
						}
					}
				});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

	//various classes that initial defines the variables used to compute data
	public static class TermNode {
		int termFrequency;
		int docFrequency;
		TreeMap<Integer, Integer> postingFiles = new TreeMap<Integer, Integer>();
	}


	public static class CompressedTermIndex {
		static String termFrontCoding;
		int termFrequency;
		int docFrequency;
		int termPointer;
		ArrayList<PostingFileNode> postingFile = new ArrayList<PostingFileNode>();
	}
	

	public static class PostingFileNode{
		byte[] docId;
		byte[] termFrequency;
	}
	
	
	
		
	//main class
	public static void main(String[] args) throws Exception {
	
		String path = "";		//variable used to get the path of the argument specified
		
		if(args.length == 0){
			System.out.println("Provide a specific path to store the versions of index files.");	//Displays error message if no path is provided in argument for index storage
			Scanner scan = new Scanner(System.in);
			path = scan.next();
		}else{
			path = args[0];
		}
		
		TreeMap<String, TermNode> stemWordMap = new TreeMap<String, TermNode>();		//used to get the terms and their propeties
		TreeMap<String, TermNode> wordMap = new TreeMap<String, TermNode>();		////used to get the stems and their properties
		File file1un = new File(path + "/index1-uncompressed");		//used to get uncompressed version of file1
		File file1com = new File(path + "/index1-compressed");		//used to get compressed version of file1
		File file2un = new File(path + "/index2-uncompressed");		//used to get uncompressed version of file2	
		File file2com = new File(path + "/index2-compressed");		//used to get compressed version of file2
		
		File folder = new File("/people/cs/s/sanda/cs6322/Cranfield");		//the path to the cranfield text collection
		File stopwordsFile = new File("/people/cs/s/sanda/cs6322/resourcesIR/stopwords");		//the path to the stopword file
		
		File[] listOfFiles = folder.listFiles();		
		ArrayList<String> stopWordsList = extractStopWords(stopwordsFile);		//lists gives the stopwords
		
	
		tokanizeDocuments(listOfFiles, stopWordsList, wordMap, stemWordMap);		//get the tokens removing the stop words
		
		
		ArrayList<CompressedTermIndex> compressedTermIndex1 = compressIndex(wordMap, 8);		//gets the compressed index for terms
		ArrayList<CompressedTermIndex> compressedTermIndex2 = compressIndex(stemWordMap, 8);		//gets the compressed index for stems
	
	
		writeIndex(wordMap, file1un.getAbsolutePath());		//writes index to the specified path
		writeIndex(stemWordMap, file2un.getAbsolutePath());		//writes index to the specified path
		writeIndex(compressedTermIndex1, file1com.getAbsolutePath());		//writes index to the specified path
		writeIndex(compressedTermIndex2, file2com.getAbsolutePath());		//writes index to the specified path
		
		System.out.println("-----------------------------------------------------------------------------------------------------");
		System.out.println("\t\t\t\t\tIndexing");
		System.out.println("-----------------------------------------------------------------------------------------------------");
		System.out.println("1) Elapsed time ('wall-clock time') required to build any version of your index = " + execTime);
		System.out.println("2) The size of the index Version 1 in uncompressed format(bytes) = " + file1un.length());
		System.out.println("3) The size of the index Version 2 in uncompressed format(bytes) = " + file2un.length());
		System.out.println("4) The size of the index Version 1 in compressed format(bytes)   = " + file1com.length());
		System.out.println("5) The size of the index Version 2 in compressed format(bytes)   = " + file2com.length());
		System.out.println("6) The number of inverted lists in version 1 in uncompressed format = " + wordMap.size());
		System.out.println("7) The number of inverted lists in version 2 in uncompressed format = " + stemWordMap.size());
		System.out.println("8) The number of inverted lists in version 1 in compressed format = " + compressedTermIndex1.size());
		System.out.println("9) The number of inverted lists in version 2 in compressed format = " + compressedTermIndex2.size());
		
		
		//information about the special terms	
		String [] terms = {"Reynolds", "NASA", "Prandtl", "flow", "pressure", "boundary", "shock"};
		for (int i = 0; i < terms.length; i++) {
			String testWord = terms[i].toLowerCase();
			stem.add(testWord.toCharArray(), testWord.length());
			stem.stem();
			String stemmedWord = stem.toString();
			TermNode node = stemWordMap.get(stemmedWord);
			System.out.println("			Term  = " + testWord);
			System.out.println("			Document Frequency(df) 	= " + node.docFrequency);
			System.out.println("			Term Frequency(tf) 	= " + node.termFrequency);
			System.out.println("			Size of Posting file (in bytes) = " + node.postingFiles.size() * 2 * Integer.BYTES);
			System.out.println();
		}
		
	}
}
