
//Perform NER and create all maps for having a Final Map which will be read later for finding influential persons

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.*;


import java.util.*;
import java.util.regex.Pattern;
import java.io.*;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceLowercase;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.IDSorter;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelSequence;

//Perform NER: find all person names in documents
/** This is a demo of calling CRFClassifier programmatically.
 *  <p>
 *  Usage: <code> java -mx400m -cp "stanford-ner.jar:." NERDemo [serializedClassifier [fileName]]</code>
 *  <p>
 *  If arguments aren't specified, they default to
 *  ner-eng-ie.crf-3-all2006.ser.gz and some hardcoded sample text.
 *  <p>
 *  To use CRFClassifier from the command line:
 *  java -mx400m edu.stanford.nlp.ie.crf.CRFClassifier -loadClassifier
 *      [classifier] -textFile [file]
 *  Or if the file is already tokenized and one word per line, perhaps in
 *  a tab-separated value format with extra columns for part-of-speech tag,
 *  etc., use the version below (note the 's' instead of the 'x'):
 *  java -mx400m edu.stanford.nlp.ie.crf.CRFClassifier -loadClassifier
 *      [classifier] -testFile [file]
 *
 *  @author Jenny Finkel
 *  @author Christopher Manning
 */

public class NERDemo {
	
	public static <K, V extends Comparable<V>> Map<K, V> RankScores(final Map<K, V> myMap)
    {
        Comparator<K> Comparator1 = new Comparator<K>() {
            public int compare(K k1, K k2)
                {
                    int compare = myMap.get(k2).compareTo(myMap.get(k1));
                    if (compare == 0)
                        return 1;
                    else return compare;
                }
        };
        Map<K, V> sortedMap = new TreeMap<K, V>(Comparator1);
        sortedMap.putAll(myMap);
        return sortedMap;
    }
	public ArrayList<String> traverseFiles(File inputDir, ArrayList<String> Documents2 )
	{
		//ArrayList <String> Documents=new ArrayList <String>();
		if (inputDir.isDirectory()) {
			//System.out.println("Checking for directory...");
	        String[] children = inputDir.list();
	        for (int i = 0; children != null && i < children.length; i++) {
	            traverseFiles(new File(inputDir, children[i]), Documents2);
	        }
	    }
	    if (inputDir.isFile()) 
	    	{ Documents2.add(inputDir.getAbsolutePath());}//change it if needed
	  
	    return Documents2;
	   // System.out.println(Documents.size());
	}

    @SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {

      String serializedClassifier = "classifiers/english.all.3class.distsim.crf.ser.gz";

      if (args.length > 0) {
        serializedClassifier = args[0];
      }

      AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);
   
	  try {
	 
	File folder=new File ("C:/Users/AAYUSHEE/Documents/final");
	//	  File folder=new File ("C:\\Users\\AAYUSHEE\\workspace\\Thesis\\Corrected");
		  HashMap<String, List<String>> hm = new HashMap<String, List<String>>();   //HASHMAP WITH PERSON NAME AND CORRESPONDING DOCUMENTS LIST
		  HashMap<String,HashMap< String,Integer>> PersonTFMap = new HashMap<String,HashMap<String,Integer>>(); //HASHMAP TO CALCULATE PERSON-DOCUMENT TF VALUES 
		  HashMap<Integer, List<String>> hm2 = new HashMap<Integer, List<String>>();  //HASHMAP WITH NUMBER OF DOCUMENTS AND CORRESPONDING PEOPLE LIST 
		  HashMap< String,Integer> hm3 = new HashMap<String,Integer>();	//HASHMAP WITH DOC IDS AND DOC NAMES
		   //HASHMAP WITH PERSON NAME AND THEIR CORRESPONDING TF VALUES
	   ArrayList <String> Documents= new ArrayList <String>();
		NERDemo obj=new NERDemo();
		Documents=obj.traverseFiles(folder,Documents);
		 System.out.println(Documents.size());
	//	BufferedWriter outp = new BufferedWriter(new FileWriter("NER/OutputofNERFinal.txt"));
		//File[] listOfFiles = null;
		for(int i=0;i<Documents.size();i++)
		
	
	{
				
			System.out.println(Documents.get(i));
			
			String fileContents = IOUtils.slurpFile(Documents.get(i));
			File f=new File(Documents.get(i));
			hm3.put(f.getName(),i);
			List<Triple<String, Integer, Integer>> entities =classifier.classifyToCharacterOffsets(fileContents);
		
			for (Triple<String, Integer, Integer> triple : entities) 
			{			
				String ename = fileContents.substring(triple.second,triple.third);
			
				String entity = ename.replaceAll("\\*", "").replaceAll("\\s+", " ").replaceAll("\\!", "");
			
				if (entity.length()>1) 
				{
				
				   if (triple.first.equalsIgnoreCase("PERSON"))
				   {
					 //  outp.write(entity.trim()+" "+f.getName()+"\n");
					   String e=entity.trim().toLowerCase();
				 
					
					   
					   
					   if(hm.containsKey(e))
				 {
					 List<String> val =hm.get(e);
					 if(!val.contains(f.getName()))
					 val.add(f.getName());
					 		hm.put(e,val);
					 		
					 		 //Code for calculating TF of each person entity
							if(PersonTFMap.containsKey(e))
							{
								HashMap<String,Integer> TFMap=new HashMap<String,Integer>();
								TFMap.putAll(PersonTFMap.get(e));
								//	HashMap<String,Integer> TFMap=PersonTFMap.get(e);
							if(TFMap.containsKey(f.getName()))
							{
							int Freq=TFMap.get(f.getName());
							Freq++;
							TFMap.put(f.getName(), Freq);
							PersonTFMap.put(e, TFMap);
							}
							else
							{
								int Freq=0;
								Freq++;
								TFMap.put(f.getName(),Freq);
								PersonTFMap.put(e,TFMap);
							}
							
							}
							
							
				 }
				 else
				 {
					 HashMap< String,Integer> TFMap = new HashMap<String,Integer>();
					 List<String> val=new ArrayList<String>();
					 	val.add(f.getName());	
					 	String split[]=e.split(" ");
						if(split.length>1)
		
						{
							hm.put(e,val);
						
					
							int Freq=0;
							Freq++;
							TFMap.put(f.getName(),Freq);
							PersonTFMap.put(e,TFMap);
							
						}	
		
				 }				    
				  // if (triple.first.equalsIgnoreCase("LOCATION"))
					//   output.write(entity.trim()+"\n")
				   }
				   
				   }
			}
		
	
}
		//PRINTING THE HASHMAP OF PERSONS WITH DOCUMENTS IN WHICH THEY OCCUR
		
		BufferedWriter output= new BufferedWriter(new FileWriter("Topic100FinalOutput/InvertedNEROutput1709.csv"));
		for (Map.Entry<String, List<String>> entry : hm.entrySet())
		{
			String key = entry.getKey();
			
			List<String> values = entry.getValue();
			//output.write(key+","+values.size()+"\n");
			//System.out.println("Key = " + key+" Number of documents: "+values.size());
			
			
			if(hm2.containsKey(values.size()))
			 {
			 
				List<String> val2 =hm2.get(values.size());
				 if(!val2.contains(key))
				 val2.add(key);
				 		hm2.put(values.size(),val2);
			 }
			 else
			 {
				 List<String> val2=new ArrayList<String>();
				 	val2.add(key);	
				 
			 hm2.put(values.size(),val2);
	
			 }
			
		}
		

		for (Map.Entry<Integer, List<String>> entry : hm2.entrySet()) {
			List<String> values2 = entry.getValue();
	    System.out.println(entry.getKey()+ " "+values2);
			output.write(entry.getKey()+","+values2.size()+"\n");
		}
		output.close();
		System.out.println("Size of hashmap:number of entities and their corresponding number of documents: "+hm.size());
		System.out.println("Size of hashmap3 Documents and their id: "+hm3.size());
		System.out.println("Size of Person entities map: "+PersonTFMap.size());

		//System.out.println("Number of entities with 1 document only: "+count);
		//outp.close();
		
		
		
		
		
		//LDA TOPIC MODEL CODE
		
    	
    	 
    	/* Map<Integer,Integer> DocTopicMap=new HashMap<Integer,Integer>();
 		Map<Integer,List<String>> hm4= new HashMap<Integer,List<String>>();


		// Begin by importing documents from text to feature sequences
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

		// Pipes: lowercase, tokenize, remove stopwords, map to features
		pipeList.add( new CharSequenceLowercase() );
		pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
		pipeList.add(new TokenSequenceLowercase());
		pipeList.add( new TokenSequenceRemoveStopwords(new File("C:/mallet/stoplists/en.txt"), "UTF-8", false, false, false) );
		pipeList.add( new TokenSequence2FeatureSequence() );

		InstanceList instances = new InstanceList (new SerialPipes(pipeList));

		Reader fileReader = new InputStreamReader(new FileInputStream(new File("AllArtLinedoc2.txt")), "UTF-8");
		instances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),3, 2, 1)); // data, label, name fields
		//FileIterator iterator=new FileIterator(new File("Corrected"),new TxtFilter(),FileIterator.LAST_DIRECTORY);
		//instances.addThruPipe(iterator);								   
											   
											   
		// Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
		//  Note that the first parameter is passed as the sum over topics, while
		//  the second is 
		int numTopics = 10;
		ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);

		model.addInstances(instances);

		// Use two parallel samplers, which each look at one half the corpus and combine
		//  statistics after every iteration.
		model.setNumThreads(2);

		// Run the model for 50 iterations and stop (this is for testing only, 
		//  for real applications, use 1000 to 2000 iterations)
		model.setNumIterations(50);
		model.estimate();

		// Show the words and topics in the first instance

		// The data alphabet maps word IDs to strings
		Alphabet dataAlphabet = instances.getDataAlphabet();
		for(int i=0;i<14020;i++)
		{
		FeatureSequence tokens = (FeatureSequence) model.getData().get(i).instance.getData();  //document no?
		LabelSequence topics = model.getData().get(i).topicSequence;  //document no?
		
		Formatter out = new Formatter(new StringBuilder(), Locale.US);
		for (int position = 0; position < tokens.getLength(); position++) {
			out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
		}
		System.out.println(out);
		
		// Estimate the topic distribution of the first instance, 
		//  given the current Gibbs state.
		double[] topicDistribution = model.getTopicProbabilities(i);  //array with probability distribution of each topic
		
		//sort the topic distribution array to get highest topic. topicDistribution.
	
	//	Arrays.sort(topicDistribution);
		
		
		Map<Integer,Float> TopicScoreMap = new HashMap<Integer,Float>();
	
		for (int j = 0; j < numTopics; j++) 
		//System.out.println("topic no: "+j+" topic probability: "+topicDistribution[j]);
		TopicScoreMap.put(j, (float)topicDistribution[j]);
		
		RankScores(TopicScoreMap);
	//System.out.println(RankScores(TopicScoreMap));
	Map.Entry<Integer,Float> maxEntry = null;

	for(Map.Entry <Integer,Float> entry : TopicScoreMap.entrySet()) {
	    if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
	        maxEntry = entry;
	    }
	}
		//System.out.println(maxEntry);
		
		DocTopicMap.put(i,maxEntry.getKey());
		//System.out.println("Document with its highest scoring topic:"+DocTopicMap);
		// Get an array of sorted sets of word ID/count pairs
		ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
		
		// Show top 5 words in topics with proportions for the first document
		for (int topic = 0; topic < numTopics; topic++) {
			Iterator<IDSorter> iterator1 = topicSortedWords.get(topic).iterator();
			List<String> TopicWords=new ArrayList <String>();
			out = new Formatter(new StringBuilder(), Locale.US);
			out.format("%d\t%.3f\t", topic, topicDistribution[topic]);
			int rank = 0;
			while (iterator1.hasNext() && rank < 5) {
				IDSorter idCountPair = iterator1.next();
				out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
				
				TopicWords.add(dataAlphabet.lookupObject(idCountPair.getID()).toString());
				hm4.put(topic,TopicWords);
				
				//System.out.println("for topic "+topic+"topic words are: "+dataAlphabet.lookupObject(idCountPair.getID()));
				rank++;
			}
			System.out.println(out);
		}
		
		// Create a new instance with high probability of topic 0
		StringBuilder topicZeroText = new StringBuilder();
		Iterator<IDSorter> iterator1 = topicSortedWords.get(i).iterator();

		int rank = 0;
		while (iterator1.hasNext() && rank < 5) {
			IDSorter idCountPair = iterator1.next();
			topicZeroText.append(dataAlphabet.lookupObject(idCountPair.getID()) + " ");
			rank++;
		}

		// Create a new instance named "test instance" with empty target and source fields.
		InstanceList testing = new InstanceList(instances.getPipe());
	//	System.out.println("Printing instance list:\t" + testing);
		testing.addThruPipe(new Instance(topicZeroText.toString(), null, "test instance", null));

		TopicInferencer inferencer = model.getInferencer();
		double[] testProbabilities = inferencer.getSampledDistribution(testing.get(i), 10, 1, 5);
		System.out.println("0\t" + testProbabilities[i]);
	}*/
	//System.out.println(DocTopicMap);	
		
		//BufferedWriter writ=new BufferedWriter(new FileWriter("PersonTopic.csv"));
	
		  File toRead2=new File("Topic100FinalOutput/DocTopicList100");

		FileInputStream fis2=new FileInputStream(toRead2);
        ObjectInputStream ois2=new ObjectInputStream(fis2);

        HashMap<String,Integer> DocTopicMap=(HashMap<String,Integer>)ois2.readObject();

        ois2.close();
        fis2.close();
		
		
		
	//	int docid=0;
		int doctopic=0;
		
		 Map<String,Map<String,Integer>> FinalMap=new HashMap<String,Map<String,Integer>>();
		 List <Integer> TopicsList=new ArrayList<Integer>();
	    	  for (Map.Entry<String, List<String>> entry : hm.entrySet()) {	  
	    		  Map<String,Integer> PersonDocTopic=new HashMap<String,Integer>();
	    	  String person=entry.getKey();
	    	  List <String> persondocuments=entry.getValue();
	    	 
	    	  for(String docfile: persondocuments)
	    	  {
	    	//  docid=hm3.get(docfile);
	    	  doctopic=DocTopicMap.get(docfile);
	    	  PersonDocTopic.put(docfile,doctopic);
	    	 //if (!(TopicsList.contains(doctopic)))
	    	  TopicsList.add(doctopic);
	    	  }
	    	  
	    	  FinalMap.put(person,PersonDocTopic);

	    	  //writ.write(person+","+TopicsList.size()+"\n");
	    	 //System.out.println(person+" "+PersonDocTopic); 
	    	 
	    	 }
		
	      System.out.println("Writing person doc map");
	    	  File fileO=new File("Topic100FinalOutput/PersonDocList1709");
	  	    FileOutputStream fos0=new FileOutputStream(fileO);
	  	        ObjectOutputStream oos0=new ObjectOutputStream(fos0);
	  	        oos0.writeObject(hm);
	  	        System.out.println(hm);
	  	        oos0.flush();
	  	        oos0.close();
	  	        fos0.close();
	  	        
	  	      System.out.println("Writing inverted person doc map");
	  	      File fileOn=new File("Topic100FinalOutput/InvertedPersonDocList1709");
	  	    FileOutputStream fos1=new FileOutputStream(fileOn);
	  	        ObjectOutputStream oos1=new ObjectOutputStream(fos1);
	  	        oos1.writeObject(hm2);
	  	        oos1.flush();
	  	        oos1.close();
	  	        fos1.close();
	  	        
	  	      System.out.println("Writing doc id map");
	  	        File file=new File("Topic100FinalOutput/DocIDList1709");
		  	    FileOutputStream fos2=new FileOutputStream(file);
		  	        ObjectOutputStream oos2=new ObjectOutputStream(fos2);
		  	        oos2.writeObject(hm3);
		  	        oos2.flush();
		  	        oos2.close();
		  	        fos2.close();    
	  	        
		  	        
			  	      System.out.println("Writing doc topic map");
		  	      File file1=new File("Topic100FinalOutput/DocTopicList1709");
			  	    FileOutputStream fos3=new FileOutputStream(file1);
			  	        ObjectOutputStream oos3=new ObjectOutputStream(fos3);
			  	        oos3.writeObject(DocTopicMap);
			  	        oos3.flush();
			  	        oos3.close();
			  	        fos3.close();
			  	        
			  	        
				/*  	      System.out.println("Writing Topic words map");  
			  	      File fileTwo=new File("Topic10FinalOutput/TopicWordsList");
				  	    FileOutputStream fos4=new FileOutputStream(fileTwo);
				  	        ObjectOutputStream oos4=new ObjectOutputStream(fos4);
				  	        oos4.writeObject(hm4);
				  	        oos4.flush();
				  	        oos4.close();
				  	        fos4.close();
	    	  */
				  	        
				  	      System.out.println("Writing Final map");
	      File fileOne=new File("Topic100FinalOutput/FinalMap1709");
	    	    FileOutputStream fos=new FileOutputStream(fileOne);
	    	        ObjectOutputStream oos=new ObjectOutputStream(fos);
	    	        oos.writeObject(FinalMap);
	    	        oos.flush();
	    	        oos.close();
	    	        fos.close();
	    	  
	    	        System.out.println("Writing Topics List");
	    		      File filem=new File("Topic100FinalOutput/PersonTopicsList1709");
	    		      BufferedWriter bw=new BufferedWriter(new FileWriter(filem));
	    		      for(Integer top:TopicsList)
	    		      {
	    		      bw.write(top+"\n");
	    		      }
	    	        bw.close();
	    	        
	    	      //Code for writing TF Map to disk
	    			System.out.println("Writing TFScore map");
	    		        File fileL=new File("Topic100FinalOutput/PersonTFValuesNER1709");
	    	  	    FileOutputStream fosL=new FileOutputStream(fileL);
	    	  	        ObjectOutputStream oosL=new ObjectOutputStream(fosL);
	    	  	        oosL.writeObject(PersonTFMap);
	    	  	        System.out.println(PersonTFMap);
	    	  	        oosL.flush();
	    	  	        oosL.close();
	    	  	        fosL.close();    
	    		   
}
		 catch (Exception e) 
    { 
       System.out.println("Exception "+e);
    }

       
		
		
		/*for (List<CoreLabel> sentence : out) {
          for (CoreLabel word : sentence) {
			if (word.get(AnswerAnnotation.class).equals("PERSON"))        
		outp.write(word.word() +"\n");
		if (word.get(AnswerAnnotation.class).equals("LOCATION"))        
		output.write(word.word()+ "\n");
        }
		 } 
        for (List<CoreLabel> sentence : out) {
          for (CoreLabel word : sentence) {
            System.out.print(word.word() + '/' + word.get(AnswerAnnotation.class) + ' ');
          }
        }*/

     
	  
    }

}
