/******************************************************************************
	Dictionary.java
	
	Simple program that takes an English word and a translation file as input
	and returns the word's translation as output. 
	
	Translation file must be formated as:
	*QueryWord* *tab* *TranslatedWord*
	
*******************************************************************************
	To run:
		java Dictionary *englishWord* *translationFile*
		
	Example:
		> java Dictionary boy English2Russian.txt
		мальчик
		>
		> java Dictionary fnord Englist2Russian.txt
		FNORD not found in translation file "English2Russian.txt". 

*******************************************************************************
	Maintenance Log
	Fix #	Date		Name	Description
	0001	20 May 2014	Keith	First draft. Many back and forth revisions to 
								fix issues with Cyrillic output to Windows
								console. 
	0002	21 May 2014	Keith	More fiddling to fix Cyrillic output to 
								Windows console. 
	0003	25 May 2014	Keith	Minor variable name changes for clarity.
	004		26 May 2014	Keith	Change from Unix style command line utility
								to interactive server. Begin by reading whole 
								word file into memory and sort it. Then 
								binary search for word translation in response
								to input.



******************************************************************************/

import java.util.Scanner;			// to parse the translation file
import java.util.Arrays;			// sort() and binarySearch()
import java.util.ArrayList;			// collection to hold translation file data
import java.io.File;				// to work with scanner to open trans file
import java.io.PrintStream; 		// to force printing in Unicode

public class Dictionary {
	static class Word implements Comparable<Word> {
		String word;
		String translation;

		Word (String w, String t){
			word = w;
			translation = t;
		}		

		public int compareTo(Word w) {
			return word.compareToIgnoreCase( w.getWord() );
		}
		
		String getWord() { return word; }
		String getTranslation() { return translation; }
		
	}

	public static void main(String[] args) 
		throws java.io.FileNotFoundException, 
			   java.io.UnsupportedEncodingException {
		
		//	Parse commandline arguments.///////////////////////////////////////
		//	args[0] must be the filename for the translation file//////////////
		String transFile = args[0];
		
		// Open the translation file and read every line into memory as a 
		// collection of Word objects, each consisting of a word and its 
		// translation as represented in the translation file by two words
		// separated by a tab.
		Scanner parser = new Scanner(new File(transFile), "UTF-8");
		ArrayList<Word> wordList = new ArrayList<Word>();
		
		while (parser.hasNextLine()) {
			String line = parser.nextLine();
			line = line.trim();
			String[] fields = line.split("\t"); 
			Word wordpair = new Word(fields[0], fields[1]);
			wordList.add(wordpair);
		}
		
		// Arrays.binarySearch() requires a pre-sorted array so we must
		// convert the collection to an array and then sort it. 
		wordList.trimToSize();
		Word[] wordArray = wordList.toArray( new Word[wordList.size()]);
		wordList = null;	// force garbage collection to save memory
		Arrays.sort( wordArray );
		
		// Create input and output objects as Unicode to/from System In/Out.
		Scanner input = new Scanner(System.in, "UTF-8");
		PrintStream output = new PrintStream(System.out, true, "UTF-8");
		
		// Main loop //////////////////////////////////////////////////////////
		// Take query word from System.in, wrap as a Word object and perform 
		// binarySearch to find it memory. If successful, print its translation.
		// If not (binarySearch returns a negative int), print an error 
		// message with the query word and the file name.
		while ( true ) {
			String query = input.next(); 
			int queryIndex = Arrays.binarySearch(wordArray, new Word(query, ""));
			if (queryIndex < 0) {
				output.println( 
					query.toUpperCase() + " not found in " + transFile + ".");
			}
			else {
				output.println( wordArray[queryIndex].getTranslation());
			}
		}
	}
}