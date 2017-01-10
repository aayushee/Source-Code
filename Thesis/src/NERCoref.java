
//pERFORMING Coreference resolution after NER process for getting new TF for persons

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
//import edu.stanford.nlp.ie.AbstractSequenceClassifier;
//import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.io.IOUtils;
//import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
//import edu.stanford.nlp.util.*;


import java.util.*;
import java.util.Map.Entry;
//import java.util.regex.Pattern;
import java.io.*;



//find all person names in documents
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

public class NERCoref {
	
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

    //  String serializedClassifier = "classifiers/english.all.3class.distsim.crf.ser.gz";
    //	String serializedClassifier = "C:\\Users\\AAYUSHEE\\workspace\\Thesis\\stanford-corenlp-3.4-models\\edu\\stanford\\nlp\\models\\ner\\english.all.3class.distsim.crf.ser.gz";
  

      //AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);
   
	  try {
	 
//File folder=new File ("C:/Users/AAYUSHEE/Documents/Corrected");
		  File folder=new File ("C:\\Users\\AAYUSHEE\\Documents\\final");
	
		  
		  /*HashMap<String, List<String>> hm = new HashMap<String, List<String>>();   //HASHMAP WITH PERSON NAME AND CORRESPONDING DOCUMENTS LIST
		  HashMap<String,HashMap< String,Integer>> PersonTFMap = new HashMap<String,HashMap<String,Integer>>(); //HASHMAP TO CALCULATE PERSON-DOCUMENT TF VALUES 
		  HashMap<Integer, List<String>> hm2 = new HashMap<Integer, List<String>>();  //HASHMAP WITH NUMBER OF DOCUMENTS AND CORRESPONDING PEOPLE LIST 
		  HashMap< String,Integer> hm3 = new HashMap<String,Integer>();	//HASHMAP WITH DOC IDS AND DOC NAMES
		   //HASHMAP WITH PERSON NAME AND THEIR CORRESPONDING TF VALUES
	   */
		  
		  
		  ArrayList <String> Documents= new ArrayList <String>();
		NERCoref obj=new NERCoref();
		
		
		
		
		Documents=obj.traverseFiles(folder,Documents); 
		 System.out.println(Documents.size());
	//	BufferedWriter outp = new BufferedWriter(new FileWriter("NER/OutputofNERFinal.txt"));
		//File[] listOfFiles = null;
		  StanfordCoreNLP pipeline = new StanfordCoreNLP();
		    Annotation annotation;
		       HashMap<String,HashMap<String,Integer>>MyMap=new HashMap<String,HashMap<String,Integer>>();
		 for(int i=0;i<Documents.size();i++)
		
	
	{			File f=new File(Documents.get(i));

		    	//annotation = new Annotation("Kosgi Santosh sent an email to Stanford University. He didn't get a reply. Santosh also studied in Stanford University.");

	String fileContents;

			System.out.println(Documents.get(i));
			
			fileContents = new String (IOUtils.slurpFile(Documents.get(i)));
			annotation=new Annotation(fileContents);
			  //  System.out.println("this is working...");
			  
			    pipeline.annotate(annotation);

			    Map<Integer, CorefChain> graph = annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
			      ////////System.out.println(graph);
			       for (Entry<Integer, CorefChain> entry : graph.entrySet()) {
			    	   CorefChain c=entry.getValue();
			    	   HashMap<String,Integer> TFMap2=new HashMap<String,Integer>();

			    	   List<CorefMention> cms = c.getMentionsInTextualOrder();
			    	  String a= c.getRepresentativeMention().toString();
			    	  String b=a.replace("in sentence 1","" );
		    	 String d= b.replace("\"", "").trim();
			    	 
			         //System.out.println(d+"	"+cms.size());
			    	////// System.out.println  (cms + "	most rep: "+d);
			       //  System.out.println(a);
			    	// System.out.println("done with document "+Documents.get(i));
			    	// int fr=0;
			         if(MyMap.containsKey(d.toLowerCase()))
			    
			         {	 
			        	TFMap2.putAll(MyMap.get(d.toLowerCase()));
			       // System.out.println(TFMap2);
			        	/* if(TFMap2.containsKey(f.getName()))
			        	{
			        	 int fo=TFMap2.get(f.getName());
			        	
			        	 fr=fo+cms.size();
			        // MyMap.put(d.toLowerCase(), fr);
			 
			 
			         
			         TFMap2.put(f.getName(), fr);
						MyMap.put(d.toLowerCase(), TFMap2);
			        	}*/
			     if(!TFMap2.containsKey(f.getName()))
			    		 {
			        		TFMap2.put(f.getName(),cms.size());
							MyMap.put(d.toLowerCase(), TFMap2);
							}

			        	
			         }
			         else
			    	// MyMap.put(d.toLowerCase(), cms.size());
			        	 {TFMap2.put(f.getName(), cms.size());
						MyMap.put(d.toLowerCase(), TFMap2);}
			     // TFMap2.clear();
			      //cms.clear();
			     
			  //     System.out.println("Printing my map with each iteration:  "+MyMap);
			       }
			      // System.out.println("This is COREF MAP: "+MyMap);
		
		/*	       
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
							{	HashMap<String,Integer> TFMap=new HashMap<String,Integer>();
							TFMap.putAll(PersonTFMap.get(e));
							if(TFMap.containsKey(f.getName()))
							{
							int Freq=TFMap.get(f.getName());
							Freq++;
							TFMap.put(f.getName(), Freq);
							PersonTFMap.put(e.trim(), TFMap);
							}
							else
							{
								int Freq=0;
								Freq++;
								TFMap.put(f.getName(),Freq);
								PersonTFMap.put(e.trim(),TFMap);
							}
						//val.clear();
							//TFMap.clear();
							
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
							hm.put(e.trim(),val);
						
					
							int Freq=0;
							Freq++;
							TFMap.put(f.getName(),Freq);
							PersonTFMap.put(e.trim(),TFMap);
							
						}	
		//val.clear();
		//TFMap.clear();
				 }				    
				  // if (triple.first.equalsIgnoreCase("LOCATION"))
					//   output.write(entity.trim()+"\n")
				   }
				   
				   }
			}
		
	*/
}
		//System.out.println(hm);
		
		 
		 /*
		 //PRINTING THE HASHMAP OF PERSONS WITH DOCUMENTS IN WHICH THEY OCCUR
		
		BufferedWriter output= new BufferedWriter(new FileWriter("Topic100FinalOutput/InvertedNEROutput.csv"));
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
	    //System.out.println(entry.getKey()+ " "+values2);
			output.write(entry.getKey()+","+values2.size()+"\n");
		}
		output.close();
*/
		//READING THE MAP OF TF VALUES CREATED FROM NER HERE
        File fileX=new File("FinalOutput2310/PersonTFValuesNER");
        FileInputStream fisx=new FileInputStream(fileX);
        ObjectInputStream oisx=new ObjectInputStream(fisx);

        HashMap<String,HashMap<String, Integer>> PersonTFMap=(HashMap<String,HashMap<String, Integer>>)oisx.readObject();
     //   System.out.println(DocTopicMap);
        oisx.close();
        fisx.close();

		
		HashMap<String,HashMap<String,Integer>>NewMap=new HashMap<String,HashMap<String,Integer>>();
System.out.println("This is COREF MAP"+MyMap.size());

 System.out.println("This is NER MAP"+PersonTFMap.size());
		for (Map.Entry<String,HashMap<String, Integer>> entry : PersonTFMap.entrySet()) {	
	  
		String person=entry.getKey();
	//System.out.println("Trying to search for " +person+"	"+MyMap.containsKey(person.trim()));
			if(MyMap.containsKey(person.trim()))
					{
						NewMap.put(person.trim(),MyMap.get(person));
				
				//	HashMap <String,Integer> SmallMap=MyMap.get(person);
		//for every entry in small map of NER , get the correcponsding freq from SmallMap and if it higher, replace otherwise keep it
						//System.out.println(MyMap.get(person));
		//NewMap.put(person,nfreq);
		}
			else
			NewMap.put(person.trim(),PersonTFMap.get(person));
			
			}
	 System.out.println("tHIS IS pERSON TF Map using NER and Coref:  "+NewMap  + " size : "+NewMap.size());
	
	 // PersonTFMap.clear();
	  
	
	  /*
	  File toRead2=new File("C:/Users/AAYUSHEE/workspace/Thesis/Topic100FinalOutput/DocTopicList100");
      FileInputStream fis2=new FileInputStream(toRead2);
      ObjectInputStream ois2=new ObjectInputStream(fis2);

      HashMap<String,Integer> DocTopicMap=(HashMap<String,Integer>)ois2.readObject();
   //   System.out.println(DocTopicMap);
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
	    //	 System.out.println(person+" "+PersonDocTopic); 
	    	  
	    	 }
		
	    	  System.out.println("Writing person doc map");
	    	  File fileO=new File("Topic100FinalOutput/PersonDocList");
	  	    FileOutputStream fos0=new FileOutputStream(fileO);
	  	        ObjectOutputStream oos0=new ObjectOutputStream(fos0);
	  	        oos0.writeObject(hm);
	  	        oos0.flush();
	  	        oos0.close();
	  	        fos0.close();
	  	        
	  	      System.out.println("Writing inverted person doc map");
	  	      File fileOn=new File("Topic100FinalOutput/InvertedPersonDocList");
	  	    FileOutputStream fos1=new FileOutputStream(fileOn);
	  	        ObjectOutputStream oos1=new ObjectOutputStream(fos1);
	  	        oos1.writeObject(hm2);
	  	        oos1.flush();
	  	        oos1.close();
	  	        fos1.close();
	  	        
	  	      System.out.println("Writing doc id map");
	  	        File file=new File("Topic100FinalOutput/DocIDList");
		  	    FileOutputStream fos2=new FileOutputStream(file);
		  	        ObjectOutputStream oos2=new ObjectOutputStream(fos2);
		  	        oos2.writeObject(hm3);
		  	        oos2.flush();
		  	        oos2.close();
		  	        fos2.close();    
	  	        
		  	        
			  	      System.out.println("Writing doc topic map");
		  	      File file1=new File("Topic100FinalOutput/DocTopicList");
			  	    FileOutputStream fos3=new FileOutputStream(file1);
			  	        ObjectOutputStream oos3=new ObjectOutputStream(fos3);
			  	        oos3.writeObject(DocTopicMap);
			  	        oos3.flush();
			  	        oos3.close();
			  	        fos3.close();
			  	        
			  	        
				
				  	        
				  	      System.out.println("Writing Final map");
	      File fileOne=new File("Topic100FinalOutput/FinalMap");
	    	    FileOutputStream fos=new FileOutputStream(fileOne);
	    	        ObjectOutputStream oos=new ObjectOutputStream(fos);
	    	        oos.writeObject(FinalMap);
	    	        oos.flush();
	    	        oos.close();
	    	        fos.close();
	    	  
	    	        System.out.println("Writing Topics List");
	    		      File filem=new File("Topic100FinalOutput/PersonTopicsList");
	    		      BufferedWriter bw=new BufferedWriter(new FileWriter(filem));
	    		      for(Integer top:TopicsList)
	    		      {
	    		      bw.write(top+"\n");
	    		      }
	    	        bw.close();
	   */ 	        
	    	      //Code for writing TF Map to disk
	    			System.out.println("Writing TFScore map");
	    		        File fileL=new File("PersonTFValues2310");
	    	  	    FileOutputStream fosL=new FileOutputStream(fileL);
	    	  	        ObjectOutputStream oosL=new ObjectOutputStream(fosL);
	    	  	        oosL.writeObject(NewMap);
	    	  	        oosL.flush();
	    	  	        oosL.close();
	    	  	        fosL.close(); 
	  
	NewMap.clear();
	  
	  }
		 catch (Exception e) 
		    { 
		       System.out.println("Exception "+e);
		    }

		       
		
		
}}