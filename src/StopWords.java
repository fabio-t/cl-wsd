import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.regex.Pattern;


public class StopWords
{
	private HashSet<String> stopWords;
		
	public StopWords (String lang, Pattern exclude) throws IOException
	{
		stopWords = new HashSet<String> ();
				
		BufferedReader stopFile = new BufferedReader(new FileReader("./data/stopwords/stopwords_" + lang));
		
		String line;
		
		while ((line = stopFile.readLine()) != null)
		{
			stopWords.add(exclude.matcher(line.trim().toLowerCase()).replaceAll(""));
		}
		
		stopFile.close();
	}
	
	public boolean check (String word)
	{
		if (stopWords.contains(word))
			return true;
		else
			return false;
	}
}
