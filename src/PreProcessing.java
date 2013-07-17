import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.MultiFilter;


public class PreProcessing
{
	private String baseDirPath;
	
	private String trainDirPath;
	private String testDirPath;
	
	private Instances trainDataSet;
	private Instances testDataSet;
	
	private ArrayList<String> options;
	
	/**
	 * @param path
	 */
	public PreProcessing(String baseDirPath, ArrayList<String> options)
	{
		this.baseDirPath = baseDirPath;
		this.trainDirPath = baseDirPath + "training_words/" + options.get(3) + "/";
		this.testDirPath = baseDirPath + "test_words/" + options.get(3) + "/";		
		
		this.options = options;
		
		this.trainDataSet = null;
		this.testDataSet = null;
	}
	
	public void parseDataSet(String format, String targetWord) throws Exception
	{
		String[] wekaOptions;
		String stringOpt = null;
		
		String trainPath = this.trainDirPath + format + "/" + targetWord + "." + format;
		String testPath = this.testDirPath + format + "/" + targetWord + "." + format;
		
		DataSource trainSource = new DataSource(trainPath);
		DataSource testSource = new DataSource(testPath);
				
		Instances trainData = trainSource.getDataSet();
		Instances testData = testSource.getDataSet();
		
		trainData.setClassIndex(-1);
		testData.setClassIndex(-1);
		
		MultiFilter multiFilter = new MultiFilter();
		
		// TODO: aggiungere: tutti senza -O e/o con -N 1.
		if (options.contains("bin"))
			stringOpt = "-F \"weka.filters.unsupervised.attribute.StringToWordVector -O -S -W 100000\"";
		else if (options.contains("tfidf"))
			stringOpt = "-F \"weka.filters.unsupervised.attribute.StringToWordVector -O -C -I -S -W 100000\"";
		else if (options.contains("tlogf"))
			stringOpt = "-F \"weka.filters.unsupervised.attribute.StringToWordVector -O -C -T -S -W 100000\"";
		else if (options.contains("tf"))
			stringOpt = "-F \"weka.filters.unsupervised.attribute.StringToWordVector -O -C -S -W 100000\"";
		
		stringOpt += "-F \"weka.filters.unsupervised.attribute.Reorder -R 3-last,2\"";
		
		if (options.contains("rmfreq5"))
			stringOpt += "-F \"weka.filters.unsupervised.instance.RemoveFrequentValues -C last -N 5 -H\"";
		else if (options.contains("rmfreq6"))
			stringOpt += "-F \"weka.filters.unsupervised.instance.RemoveFrequentValues -C last -N 6 -H\"";
		else if (options.contains("rmfreq7"))
			stringOpt += "-F \"weka.filters.unsupervised.instance.RemoveFrequentValues -C last -N 7 -H\"";
		
		if (options.contains("rmusel90"))
			stringOpt += "-F \"weka.filters.unsupervised.attribute.RemoveUseless -M 90\"";
		else if (options.contains("rmusel95"))
			stringOpt += "-F \"weka.filters.unsupervised.attribute.RemoveUseless -M 95\"";
		else if (options.contains("rmusel99"))
			stringOpt += "-F \"weka.filters.unsupervised.attribute.RemoveUseless -M 99\"";

		wekaOptions = weka.core.Utils.splitOptions(stringOpt);
		
		multiFilter.setOptions(wekaOptions);
		multiFilter.setInputFormat(trainData);
		
		this.trainDataSet = Filter.useFilter(trainData, multiFilter);
		this.testDataSet = Filter.useFilter(testData, multiFilter);
	}
	
	public void writeToArff(String targetWord, String type) throws IOException
	{
		Instances instances;
		
		ArffSaver saver = new ArffSaver();
		
		if (type.equals("test"))
			instances = this.testDataSet;
		else if (type.equals("training"))
			instances = this.trainDataSet;
		else
			return;
		
		saver.setInstances(instances);
		saver.setFile(new File(this.baseDirPath + type + "_words/" + this.options.get(3) + "/arff/" + targetWord + ".vec.arff"));
		saver.writeBatch();
	}
	
	public Instances getTrainSet()
	{
		return this.trainDataSet;
	}
	
	public Instances getTestSet()
	{
		return this.testDataSet;
	}
}
