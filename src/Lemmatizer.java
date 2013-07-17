import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

interface LemmaOrStem
{
	public String getLemma(String word);
}

public class Lemmatizer implements LemmaOrStem
{
	private File pathToLemm;
		
	private HashMap<String, String> dict;
	
	private Pattern exclude;
	
	private String lang;
	
	public Lemmatizer(String baseDirPath, String lang, Pattern exclude) throws IOException
	{
		pathToLemm = new File (baseDirPath + "lemma_dicts/" + lang + ".utf8.lex");
				
		dict = new HashMap<String, String> ();
		
		this.exclude = exclude;
		
		this.lang = lang;
		
		load();
	}

	private void load() throws IOException
	{
		String[] fields = null;
		
		String line = null;
		String temp0 = null;
		String temp1 = null;
		
		BufferedReader file = new BufferedReader (new FileReader (this.pathToLemm));
		
		while ((line = file.readLine()) != null)
		{
			fields = line.split("\\p{Space}+");
			
			if (fields.length < 3)
				continue;
			
			/*
			tempArr0 = this.pattern.matcher(fields[0].trim()).replaceAll(" ").split(" ");
			
			if (tempArr0.length < 1)
				continue;
			
			temp0 = tempArr0[0].trim();
			*/
			
			temp0 = exclude.matcher(fields[0].trim()).replaceAll("").trim();
			
			if (temp0 == null || temp0.length() < 3)
				continue;
			
			for (int i = 1; i < fields.length; i = i + 2)
			{
				if (lang.equals("en") || (	fields[i].contains("NOUN")	||
											fields[i].contains("NN")	||
											fields[i].contains("NE")	||
											fields[i].contains("NOM")	||
											fields[i].contains("NC")	||
											fields[i].contains("noun")))
				{
					if (fields[i+1].contains("|"))
						fields[i+1] = fields[i+1].split("|")[0].trim();
					
					/*
					tempArr1 = this.pattern.matcher(fields[i+1].trim()).replaceAll(" ").split(" ");
					
					if (tempArr1.length < 1)
						continue;
					*/
					
					temp1 = exclude.matcher(fields[i+1].trim()).replaceAll("").trim();
					
					if (temp1 != null && temp1.length() > 2)
						dict.put(temp0.toLowerCase(), temp1.toLowerCase());
					
					break;
				}
			}
		}
		
		file.close();
	}
	
	public String getLemma(String word)
	{
		String tempWord = this.dict.get(word);
		
		if (tempWord == null)
			return word;
		else
			return tempWord;
	}
}
