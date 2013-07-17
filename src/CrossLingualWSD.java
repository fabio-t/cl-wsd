import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.DMNBtext;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.functions.LibSVM;
import weka.core.Instances;


public class CrossLingualWSD
{
	
	public static int searchInArray(double[] source, double key)
	{
		for (int i = 0; i < source.length; i++)
			if (source[i] == key)
				return i;
		
		return -1;
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		String[] targetWords = {"coach", "education", "execution", "figure", "job", "letter", "match", "mission", "mood", 
								"paper", "post", "pot", "range", "rest", "ring", "scene", "side", "soil", "strain", "test"};
		
		String baseDirPath = "./data/";
		
		String[] scoreTypes = {"best", "oof"};
		String[] stwOpts = {"bin", "tfidf", "tlogf", "tf"};
		String[] freqOpts = {"rmfreq5", "rmfreq6", "rmfreq7", "normfreq"};
		String[] uselessOpts = {"rmusel90", /*"rmusel95",*/ "rmusel99", "normusel"};
		String[] algos = {"dmnb", "dmnb30", "nbm", "svm"};
		String[] langs = {"it", "es", "de", "nl", "fr"};
		
		BufferedWriter semeval;
		
		for (String stw : stwOpts)
		{
			for (String freq : freqOpts)
			{
				for (String usel : uselessOpts)
				{
					for (String algo : algos)
					{
						for (String lang : langs)
						{
							String[] opts = {stw, freq, usel, lang};
							
							PreProcessing pp = new PreProcessing(baseDirPath, new ArrayList<String>(Arrays.asList(opts)));
							
							Classifier classifier = null;
							
							Instances trainData;
							Instances testData;
							
							for (String targetWord : targetWords)
							{
								System.out.println(targetWord);
								
								pp.parseDataSet("arff", targetWord);
								
								trainData = pp.getTrainSet();
								testData = pp.getTestSet();
								
								// save the pre-processed dataset to an arff file
								pp.writeToArff(targetWord, "training");
								pp.writeToArff(targetWord, "test");
								
								trainData.setClass(trainData.attribute("classes"));
								testData.setClass(testData.attribute("classes"));
								
								if (algo.equals("nbm"))
								{
									// Classifier: Naive Bayes Multinomial
									classifier = new NaiveBayesMultinomial();
								}
								else if (algo.equals("dmnb"))
								{
									// Classifier: Discriminative Multinomial Naive Bayes
									classifier = new DMNBtext();
								}
								else if (algo.equals("dmnb30"))
								{
									// Classifier: Discriminative Multinomial Naive Bayes, 30 iterations
									classifier = new DMNBtext();
									
									((DMNBtext) classifier).setNumIterations(30);
								}
								else if (algo.equals("svm"))
								{
									// Classifier: LibSVM
									classifier = new LibSVM();
								}
								
								classifier.buildClassifier(trainData);
								
								for (String type : scoreTypes)
								{
									String pathToWord = baseDirPath + "results/gold_"+ lang + "/" + type + "/" + algo + "_" 
									+ stw + "_" + freq + "_" + usel + "/";
									
									System.out.println(pathToWord);
									
									File dirToWord = new File(pathToWord);
									
									if (!dirToWord.exists())
									{
										if (!dirToWord.mkdir())
										{
											System.out.println("Error creating the directory: " + pathToWord);
											
											System.exit(1);
										}
									}
									
									semeval = new BufferedWriter(new FileWriter (pathToWord + targetWord));
									
									String writeBuff = "";
									
									if (type.equals("oof"))
									{
										System.out.println("oof");
										for (int i = 0; i < testData.numInstances(); i++)
										{
											writeBuff += targetWord + ".n." + lang + " " + (i+1) + " ::: ";
											
											testData.instance(i).setClassValue(classifier.classifyInstance(testData.instance(i)));
											
											double[] classes = classifier.distributionForInstance(testData.instance(i));
											
											double[] ordClasses = Arrays.copyOf(classes, classes.length);
											Arrays.sort(ordClasses);
											
											int classIndex;
											
											for (int j = ordClasses.length - 1; j > ordClasses.length - 6; j--)
											{
												classIndex = searchInArray(classes, ordClasses[j]);
												
												if (classIndex >= 0)
												{
													writeBuff += testData.classAttribute().value(classIndex) + ";";
													
													classes[classIndex] = -1;
												}
											}
											
											writeBuff += "\n";
										}
									}
									else if (type.equals("best"))
									{
										System.out.println("best");
										for (int i = 0; i < testData.numInstances(); i++)
										{
											writeBuff += targetWord + ".n."+ lang + " " + (i+1) + " :: ";
											
											double classIndex = classifier.classifyInstance(testData.instance(i));
											
											testData.instance(i).setClassValue(classIndex);
											
											writeBuff += testData.classAttribute().value((int) classIndex) + "\n";
										}
									}
									
									semeval.write(writeBuff);
									
									semeval.close();
								}
							}
						}
					}
				}
			}
		}
	}	
}
