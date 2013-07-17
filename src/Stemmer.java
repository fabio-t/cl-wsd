import weka.core.stemmers.SnowballStemmer;


public class Stemmer implements LemmaOrStem
{
	private SnowballStemmer stemmer;
	
	public Stemmer(String lang)
	{
		stemmer = new SnowballStemmer(lang);
	}
	
	public String getLemma(String word)
	{
		return stemmer.stem(word);
	}
}
