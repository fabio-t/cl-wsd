import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToString;


public class ArffGenerator
{
	private File corpus;
	
	private String baseDirPath;
	private String trainDirPath;
	private String testDirPath;
	
	private String destLang;
	
	private Pattern dWord;
	private Pattern exclude;
	private Pattern tWord;
	
	private Set<String> classes;
	
	private StopWords srcStopW;
	private StopWords destStopW;
	
	//private SnowballStemmer stemmer;
	private LemmaOrStem srcLemma;
	
	private LemmaOrStem destLemma;
	
	public ArffGenerator (String baseDirPath, String srcLang, String destLang, StopWords srcStopW, LemmaOrStem srcLemm, Pattern exclude) throws IOException
	{
		this.corpus = new File (baseDirPath + "full_corpora/corpus_word_aligned_" + destLang + "_" + srcLang);
		
		this.baseDirPath = baseDirPath;
		this.trainDirPath = baseDirPath + "training_words/" + destLang + "/";
		this.testDirPath = baseDirPath + "test_words/" + destLang + "/";
		
		this.destLang = destLang;
		
		// giza alignment format: 
		//		[...] dest_word ({ number number .. }) [...]
		//this.dWord = Pattern.compile(" ((?:\\p{L}\\p{M}*)+) \\(\\{(.*)\\}\\) ");
		this.dWord = Pattern.compile(" ((?:\\p{L}\\p{M}*)+-?_?(?:\\p{L}\\p{M}*)+) \\(\\{(.*?)\\}\\)");
		
		this.exclude = exclude;
		
		this.classes = new LinkedHashSet<String> ();
		
		this.srcStopW = srcStopW;
		this.destStopW = new StopWords(destLang, exclude);
		
		this.srcLemma = srcLemm;
		this.destLemma = new Lemmatizer(baseDirPath, destLang, exclude);
	}
	
	public void makeCsvTrainingSet (String targetWord) throws Exception
	{	
		String srcSentence = null;
		String destSentence = null;
		
		String[] srcWords = null;
		
		String writeBuffer = null;
		String srcWordLemma = null;
		String transl = null;
		String word = null;
		
		tWord = Pattern.compile(".* ((" + targetWord + ")(?:s|es)?) .*");
		
		Matcher srcMatcher;
		Matcher destMatcher;
		
		int id = 1;
		
		int i,j;
		int targetWordIndex = -1;
		
		BufferedReader in = new BufferedReader (new FileReader (corpus));
		BufferedWriter out = new BufferedWriter (new FileWriter(trainDirPath + "csv/" + targetWord + ".csv"));
		
		out.write("id,phrase,classes\n");

		while ((srcSentence = in.readLine()) != null)
		{
			// sometimes we have empty lines
			if (srcSentence.length() < 2)
				in.readLine();
			
			srcSentence = in.readLine();
			destSentence = in.readLine();
			
			srcMatcher = tWord.matcher(srcSentence);
			
			// we know that test words have "simple plurals": executions, coaches..
			if (srcMatcher.matches())
			{
				srcWords = srcSentence.split(" ");
				
				writeBuffer = id + ",";
				
				i = 1;
				j = 0;
				
				for (String raWord : srcWords)
				{
					/*
					tempArr = exclude.matcher(raWord).replaceAll(" ").split(" ");
					
					if (tempArr.length < 1)
						continue;
					
					word = tempArr[0].trim();
					*/

					word = exclude.matcher(raWord).replaceAll("").trim();
					
					if (word == null || word.length() < 3 || srcStopW.check(word))
					{
						i++;
						
						continue;
					}
					
					if (word.equals(srcMatcher.group(1)))
					{
						targetWordIndex = i;
					}
					else
					{						
						srcWordLemma = srcLemma.getLemma(word);
						
						if (srcWordLemma.length() > 2)
						{
							if (j > 0)
								writeBuffer += ":";
							
							writeBuffer += srcWordLemma;
							
							j++;
						}
					}
					
					i++;
				}
				
				if (writeBuffer.length() < (String.valueOf(id).length() + 5) || targetWordIndex == -1)
					continue;
				
				destMatcher = dWord.matcher(destSentence);
				
				while (destMatcher.find())
				{
					if (destMatcher.group(2).contains(" " + targetWordIndex + " "))
					{
						transl = destLemma.getLemma(exclude.matcher(destMatcher.group(1)).replaceAll("").trim());
						
						if (transl == null || transl.length() < 3 || destStopW.check(transl))
							continue;
						
						classes.add(transl);
												
						out.write(writeBuffer.trim() + "," + transl);
						out.newLine();
						
						id++;
						
						break;
					}
				}
			}
		}
		
		out.close();
		in.close();
	}
	
	public void makeCsvTestSet(String targetWord) throws IOException
	{
		String[] srcWords = null;
		//String[] tempArr = null;
		
		String srcSentence = null;		
		String writeBuffer = null;
		String lemmaWord = null;
		String word = null;
		
		int j;
		int id = 1;
				
		BufferedReader in = new BufferedReader (new FileReader (testDirPath + targetWord));
		BufferedWriter out = new BufferedWriter (new FileWriter(testDirPath + "csv/" + targetWord + ".csv"));
		
		out.write("id,phrase\n");
		
		while ((srcSentence = in.readLine()) != null)
		{
			srcWords = srcSentence.split("\\p{Space}+");
			
			writeBuffer = id + ",";
			
			j = 0;
			for (String raWord : srcWords)
			{
				/*
				tempArr = exclude.matcher(raWord).replaceAll(" ").split(" ");
				
				if (tempArr.length < 1)
					continue;
				
				word = tempArr[0].trim();
				*/
				
				word = exclude.matcher(raWord).replaceAll("").trim();
				
				if (word == null || word.length() < 3 || srcStopW.check(word))
					continue;
		
				lemmaWord = this.srcLemma.getLemma(word);
				
				if (lemmaWord.length() > 2)
				{
					if (j > 0)
						writeBuffer += ":";
					
					writeBuffer += lemmaWord;
					
					j++;
				}
			}
			
			if (writeBuffer.length() < (String.valueOf(id).length() + 5))
			{
				System.out.println("TestSet debug: wb too short: " + writeBuffer);
				continue;
			}
			
			out.write(writeBuffer.trim());
			out.newLine();
			
			id++;
		}
		
		out.close();
		in.close();
	}
	
	public void csvToArff(String type, String targetWord) throws Exception
	{
		ArffSaver saver = new ArffSaver();
				
		DataSource source = new DataSource(baseDirPath + type + "_words/" + destLang + "/csv/" + targetWord + ".csv");
		Instances instances = source.getDataSet();
		
		if (classes == null)
		{
			System.out.println("Error: no classes! --> " + targetWord);
			
			System.exit(-1);
		}
		
		if (type.equals("test"))
			instances.insertAttributeAt(new Attribute ("classes", new ArrayList<String> (classes)), instances.numAttributes());
		
		// convert nominal attributes (except for the class attribute) to string attribute
		// needed for "string to word vector" approaches
		NominalToString filter = new NominalToString();
		
		String[] options = weka.core.Utils.splitOptions("-C 2");
		
		filter.setOptions(options);
		filter.setInputFormat(instances);
		
		saver.setInstances(Filter.useFilter(instances, filter));
		saver.setFile(new File(baseDirPath + type + "_words/" + destLang + "/arff/" + targetWord + ".arff"));
		saver.writeBatch();
	}
	
	public void cleanClasses()
	{
		this.classes = new LinkedHashSet<String> ();
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		String[] targetWords = {"coach", "education", "execution", "figure", "job", "letter", "match", "mission", "mood", 
								"paper", "post", "pot", "range", "rest", "ring", "scene", "side", "soil", "strain", "test"};
		
		String srcLang = "en";
		String[] destLangs = {"it", "es", "de", "fr", "nl"};
		
		String baseDirPath = "./data/";
		
		// match every punctuation and numbers in a utf-8/unicode/ascii string
		//Pattern exclude = Pattern.compile("[\\p{P}\\p{N}\\p{C}\\p{Punct}]");
		//Pattern exclude = Pattern.compile("[\\p{N}\\p{C}.,\\[\\]{}'\"\\;\\|!&/()=?^*+:]");
		Pattern exclude = Pattern.compile("[\\p{P}\\p{N}\\p{Punct}\\p{C}&&[^-_]]");
		
		StopWords srcStopW = new StopWords(srcLang, exclude);
		
		// Choose one of these (lemmatizer extracted from europarl with treetagger or snowball stemmer)
		//LemmaOrStem srcLemm = new Lemmatizer(baseDirPath, srcLang, exclude);
		LemmaOrStem srcLemm = new Stemmer("english");
		
		for (String lang : destLangs)
		{
			ArffGenerator conv = new ArffGenerator (baseDirPath, srcLang, lang, srcStopW, srcLemm, exclude);
			
			System.out.println("\t===== " + lang.toUpperCase() + " =====");
			
			for (String targetWord : targetWords)
			{
				System.out.println(targetWord);
				
				// simple csv file of instances
				conv.makeCsvTrainingSet(targetWord);
				conv.makeCsvTestSet(targetWord);
				
				// final arff files with class attribute
				conv.csvToArff("training", targetWord);
				conv.csvToArff("test", targetWord);
				
				conv.cleanClasses();
			}
		}
	}
}
