package org.apache.medex;


import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
//import org.apache.uima.ResourceFactory;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Triplet;

import org.apache.medex.semantic_rules.*;
import org.apache.NLPTools.*;

/**
* @author Anushi Shaha
* @version 1.0
* @since 2012-11-1
*/
public class SemanticRuleEngine 
{
	
	
	String LEX_FILE; 
	static BufferedWriter out;
	
	String t1;
	String t2;
	int t3;
	int t4;
	
	final static KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
	final static KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
	

	//final static StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
	
	final KnowledgeBuilder kbuilder2 = KnowledgeBuilderFactory.newKnowledgeBuilder();
    final static KnowledgeBase kbase2 = KnowledgeBaseFactory.newKnowledgeBase();
    
    final KnowledgeBuilder kbuilder3 = KnowledgeBuilderFactory.newKnowledgeBuilder();

    final static KnowledgeBase kbase3 = KnowledgeBaseFactory.newKnowledgeBase();
    
	/**
	 * Constructor for SemanticRuleEngine, load the rule into the drool engine.
	 * 
	 * @see        SemanticRuleEngine
	 */
	public SemanticRuleEngine() throws IOException
	{
		
		//System.out.println("** SemanticTagger ctr, load lexicon");
		
		kbuilder.add( ResourceFactory.newClassPathResource( "disambiguation.drl",org.apache.medex.semantic_rules.tags.class ),
	            ResourceType.DRL );

		kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
		
		kbuilder2.add( ResourceFactory.newClassPathResource( "transformation1.drl",org.apache.medex.semantic_rules.tags.class ),
                ResourceType.DRL );
		
		kbase2.addKnowledgePackages( kbuilder2.getKnowledgePackages() );
		
		kbuilder3.add( ResourceFactory.newClassPathResource( "transformation2.drl",org.apache.medex.semantic_rules.tags.class ),
                ResourceType.DRL );
		
		kbase3.addKnowledgePackages( kbuilder3.getKnowledgePackages() );
		
		
		
		
     // System.out.println("End ctr");
	}
	
	/**
	 * Update the tags based on a set of predefined rules 
	 * 
	 * @param  obj	an instance of class "semanticRuleEngine"
	 * @param tokens	token list e.g. {"warfarin", "50", "mg", "po"}
	 * @param tags	tag list generated by dictionary-lookup algorithm	e.g. {"DIN", "TK", "DOSEUNIT", "RUT"}
	 * @param tag_index	index of tag e.g. {0, 1, 2, 3}
	 * @param sents_token_map
	 * @return 	a list of tagging result
	 * @see  SemanticRuleEngine  
	*/
	public ArrayList<Quartet<String,String,Integer,Pair<Pair<String, Integer>, Integer> >> transform(SemanticRuleEngine obj,String[] tokens,String[] tags, int[] tag_index, ArrayList sents_token_map,String section, BufferedWriter out, String if_drool_engine) throws IOException,  ClassNotFoundException
	{
		
		ArrayList<Quartet<String,String,Integer,Pair<Pair<String, Integer>, Integer> >> token_tags = RegTagger(obj,tokens,tags, tag_index, sents_token_map);
		//long start = System.currentTimeMillis();
		//System.out.println("regular tags result:");
		//System.out.println(token_tags);
		//out.write("regular tags result:"+token_tags+"\n");
		
		
		
		long start = System.currentTimeMillis();
		if(if_drool_engine.equals("n")){
			token_tags = medicationTransformer2(token_tags,section);
		}
		else{
			token_tags = medicationTransformer(obj,token_tags,section);
		}
		long end1 = System.currentTimeMillis();
		//System.out.println(String.valueOf(end1-start));
		
		//ArrayList<Quartet<String,String,Integer,Pair<Pair<String, Integer>, Integer> >> token_tags2 = medicationTransformer2(token_tags, section);
		/*
		if(!token_tags.equals(token_tags2)){
			System.out.println("-------------------------------");
			System.out.println("before transform result:"+token_tags+"\n");
			System.out.println(token_tags);
			System.out.println(token_tags2+"\n");
		}*/
		long end2 = System.currentTimeMillis();
		//System.out.println(String.valueOf(end2-end1));
	    //System.out.println(token_tags);
		token_tags = medicationContext(obj,token_tags,section);
		//System.out.println("context result:"+token_tags+"\n");
		//end = System.currentTimeMillis();
		//System.out.println("context result");
		//System.out.println(token_tags);
		return token_tags;
		
	}
	
	
	/**
	 * Tagging the tokens by list of regular expression and merged the result into the one generated with the dictionary-lookup  
	 * 
	 * @param  obj	an instance of class "semanticRuleEngine"
	 * @param tokens	token list e.g. {"warfarin", "50", "mg", "po"}
	 * @param tags	tag list generated by dictionary-lookup algorithm	e.g. {"DIN", "TK", "DOSEUNIT", "RUT"}
	 * @param tag_index	index of tag e.g. {0, 1, 2, 3}
	 * @return 	a list of tagging result
	 * @param sents_token_map
	 * @see  SemanticRuleEngine  
	*/
	
	public ArrayList<Quartet<String,String,Integer,Pair<Pair<String, Integer>, Integer> >> RegTagger(org.apache.medex.SemanticRuleEngine obj, String[] tokens,String tags[], int[] tag_index, ArrayList sents_token_map) throws IOException
	{
		/*
		for(int i = 0; i < tokens.length; i++){
			System.out.println("look_up_tagger "+tokens[i]);
			System.out.println("look_up_tagger "+tags[i]);
		}*/
		ArrayList senttag = new ArrayList();
		int startPos = 0;
		int endPos = -1;
		int idx = 0;
		
		
		// Iterate through token length.
		while(startPos < tokens.length)
		{
			//System.out.println("------------------------------");
			//System.out.println("startpos:"+startPos);
			String term = tokens[startPos];
			//System.out.println("term:"+term);
			String tag = "TK";
			
			String lookedup_term = "";
			//System.out.println("fristStartPOS:"+startPos);
			if(!tags[startPos].equals("TK")){
				endPos = startPos;
				//System.out.println("tokens.length:"+tokens.length);
				//System.out.println("tags[endPos]:"+tags[endPos]);
				//System.out.println("endPos:"+endPos);
				
				int first_tag_index = tag_index[endPos];
				while(!tags[endPos].equals("TK") && endPos<tokens.length-1 && first_tag_index==tag_index[endPos] )
				{
					//System.out.println("**********");			
					lookedup_term +=tokens[endPos] + " ";
					//System.out.println("endpos++");
					endPos++;
					//System.out.println("tags[endPos]:"+tags[endPos]);
					//System.out.println("endPos:"+endPos);
					//System.out.println("first_tag:"+first_tag);
				}
				
				
				
				if(endPos==tokens.length-1 )
				{
					if(!tags[endPos].equals("TK") && first_tag_index == tag_index[endPos])
					{
						lookedup_term +=tokens[endPos] + " ";
						endPos++;
					}
				}
				endPos--;
			}
			
			lookedup_term = lookedup_term.trim();
			//System.out.println("lookedup_term:"+lookedup_term);
			//System.out.println("endPos:"+endPos);
			//System.out.println("startPos:"+startPos);
			// Call regexTagger method
			Triplet<Integer,String, String> temptuple = regexTagger(obj,startPos, StringUtils.join(tokens," ",startPos,tokens.length));
			int endPosReg = temptuple.getValue0();
			String termReg = temptuple.getValue1();
			String tagReg = temptuple.getValue2();
			//System.out.println(String.valueOf(startPos)+" "+String.valueOf(endPosReg)+" "+termReg + " "+tagReg );
			//System.out.println("endPosReg:"+endPosReg);
			
			if(endPosReg > -1)
			{ 
				boolean isUpdate = true;
				if(endPosReg < tokens.length)
				{
					if(!tags[endPosReg].equals("TK") && !tags[endPosReg-1].equals("TK") && tag_index[endPosReg]==(tag_index[endPosReg-1]))
					{
						isUpdate = false;
					}
				}
				if(isUpdate)
				{
					tag = tagReg;
					term = termReg;
					startPos = endPosReg-1;
					//System.out.println("from 1:");
				}
				else
				{
					if(endPos>-1)
					{
						tag = tags[startPos];
						startPos=endPos;
						
						term = lookedup_term;
						//System.out.println("from 2:");
					}
					else
					{
						tag = tags[startPos];
						
						//System.out.println("from 3:");
					}
					
				}
			}
			else{
				if(endPos>-1)
				{
					tag = tags[startPos];
					//System.out.println("tag:"+tag);
					startPos=endPos;
					term = lookedup_term;
					//System.out.println("from 2:");
				}
				else
				{
					tag = tags[startPos];
					//System.out.println("from 3:");
				}
			}
			
			//System.out.println("startPos:"+startPos);
			
			Pair<Pair<String,Integer>,Integer> pair = (Pair<Pair<String, Integer>, Integer>) sents_token_map.get(startPos);
			Quartet<String,String,Integer,Pair<Pair<String,Integer>,Integer>> tagtuple = Quartet.with(term, tag, idx, pair);
			//System.out.println(tagtuple);
			senttag.add(tagtuple);
			startPos ++;
			idx = idx + 1;
			endPos =-1;
		}
		//System.out.println(senttag);
		return senttag;
	}
	
	/**
	 * A regular expression based semantic tagger
	 * 
	 * @param  obj	an instance of class "semanticRuleEngine"
	 * @param start	start index
	 * @param strtokens	search string
	 * @return a list that contains three items: 1>offset of end position of identified tag("-1", if no tag found), 2>tagged phrase 3> tagged type
	 * @see  SemanticRuleEngine  
	*/
	private Triplet<Integer,String, String> regexTagger(SemanticRuleEngine obj,int start, String strtokens) throws IOException
	{
		//System.out.println("strtokens:"+strtokens);
		int end = -1;
		String term = "";
		String tag = "";
		
		Map regexlist = new Hashtable();
		regexlist.put("^(\\d+[\\.]\\d+|\\.\\d+|\\d\\/\\d|\\d \\d\\/\\d|\\d+\\,\\d+|\\d+ \\, \\d+|\\d+)( |$)","NUM");
		regexlist.put("^[\\d\\.]+ (to|-) [\\d\\.]+( |$)","NUM");
		regexlist.put("^[\\d\\.]+[\\-\\/][\\d\\.]+( |$)","NUM");
		regexlist.put("^\\d+\\%( |$)","NUM");
		regexlist.put("^x( |)\\d+( |$)","FREQ");
		regexlist.put("^(\\d+|\\d+\\.\\d+)(mg|doz|oz|ml|l|mb|meq)", "DOSE");
		//regexlist.put("^[\\d\\.]+ (to|-)( |$)","NUM");
		regexlist.put("^several (min|minute|minutes|h|hr|hrs|hour|hours|d|day|days|wk|week|weeks|m|month|months)( |$)","DRT");
		regexlist.put("^x\\d+(min|minute|minutes|h|hr|hrs|hour|hours|d|day|days|wk|week|weeks|m|month|months)( |$)","DRT");
		//regexlist.put("^(\\d+|one|two|three|four|five|six|seven|eight|nine|ten|eleven|twelve|thirteen|fourteen|fifteen|sixteen|seventeen|eighteen|nineteen|twenty|thirty)( more | )(to|-|)(\\d+|one|two|three|four|five|six|seven|eight|nine|ten|eleven|twelve|thirteen|fourteen|fifteen|sixteen|seventeen|eighteen|nineteen|twenty|thirty|)( |)(min|minute|minutes|h|hr|hrs|hour|hours|d|day|days|wk|week|weeks|m|month|months)( |$)","DRT");
		regexlist.put("^(\\d+|one|two|three|four|five|six|seven|eight|nine|ten|eleven|twelve|thirteen|fourteen|fifteen|sixteen|seventeen|eighteen|nineteen|twenty|thirty)( more | )(min|minute|minutes|h|hr|hrs|hour|hours|d|day|days|wk|week|weeks|m|month|months)( |$)","DRT");
		regexlist.put("^\\d{1,2}\\/\\d{1,2} \\- \\d{1,2}\\/\\d{1,2}( |$)","DRT");
		regexlist.put("^x(min|minute|minutes|d|day|days|wk|week|weeks|m|month|months)( |$)","TUNIT");
		regexlist.put("^\\d( |)x( |)(daily|weekly|monthly|a day|a week|a month|per day|per week|per month)","FREQ");
		regexlist.put("^\\d+( |)(am|pm|clock)","TOD");
		regexlist.put("^(every|q|qdaily) (m|mon|t|tue|tues|w|wed|r|thu|thur|f|fri|sat|sun|monday|tuesday|wednesday|thursday|friday|saturday|sunday|,| |and)+( |$)","FREQ");
		regexlist.put("^(m|mon|t|tue|w|wed|r|thu|f|fri|sat|sun|monday|tuesday|wednesday|thursday|friday|saturday|sunday) and (m|mon|t|tue|w|wed|r|thu|f|fri|sat|sun|monday|tuesday|wednesday|thursday|friday|saturday|sunday)( |$)","FREQ");
		regexlist.put("^(one|two|three|four|five|six|seven|eight|nine|ten) to (one|two|three|four|five|six|seven|eight|nine|ten)( |$)","NUM");
		
		
		regexlist.put("^q(\\.|)\\d+( |$)","FREQ");
		regexlist.put("^q( \\. | |\\.|)( |)(\\d+|\\d\\-\\d|\\d \\- \\d)?( |)(hrs|hr|h|m|min|d|hour|hours|day|days|minute|minutes|wk|week|weeks|month|months)(prn|)(\\.|)( |$)","FREQ");
		//regexlist.put("^q(\\.|)\\d+( |$)","FREQ");
		regexlist.put("^(\\d+|one|two|three|four|five|six|seven|eight|nine|ten) times (daily|weekly|monthly|a day|a week|a month|per day|per week|per month)( |$)","FREQ");
		regexlist.put("^(every|each|per) (minute|hour|morning|afternoon|evening|day|wk|week|month|bedtime|breakfast|lunch|dinner)( |$)","FREQ");
		regexlist.put("^every (\\d+|\\d+\\-\\d+|\\d+ to \\d+|\\d+ \\- \\d+|one|two|three|four|five|six|seven|eight|nine|ten|eleven|twelve|thirteen|fourteen|fifteen|sixteen|seventeen|eighteen|nineteen|twenty|twenty-one|twenty-two|twenty-three|twenty-four) ?(hrs|hr|h|m|min|d|hour|hours|day|days|minute|minutes|wk|week|weeks|month|months)( |$)","FREQ");
		
		//r'^\d( |)x( |)(daily|weekly|monthly|a day|a week|a month|per day|per week|per month)', 'FREQ'), 	# 2x daily , modified 11/2/09
		int maxlen = 0;
		int num_rows = 18;
		int num_cols = 2;
		Iterator it = regexlist.entrySet().iterator();
		
		
		while (it.hasNext()) 
		{
			 	  Map.Entry pairs = (Map.Entry)it.next();
		    	  String regexp = (String) pairs.getKey();
		    	  String stag = (String) pairs.getValue();
		    	  
		    	  
		    	  Pattern r = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
		    	  Matcher m = r.matcher(strtokens);
			      if (m.find()) 
			      { 
			    	  String matchstr = m.group(0);
			    	  matchstr = matchstr.trim();
			    	  int matchlen = matchstr.split(" ").length;
			    	  
			    	  if (matchlen > maxlen)
			    	  {
			    		  maxlen = matchlen;
			    		  end = start + matchlen;
			    		  term = rTrim(matchstr);
			    		  tag = stag;
			    	  }
			    }
		}
		
		//System.out.println("Regex :"+term+"\t"+tag);
		Triplet<Integer,String, String> temptuple = Triplet.with(end, term, tag);
		return temptuple;
	
	}

	
	/**
	 * This method trims white spaces from right side of the string.
	 * 
	 * @param  in	input string
	 * @return 	output string
	 * @see  SemanticRuleEngine  
	*/
	private String rTrim( String in ) 
	{
		int len = in.length();
		while ( len > 0 ) 
		{
			if ( ! Character.isWhitespace( in.charAt( --len ))) 
			{
		       return in.substring( 0, len + 1 );
			}
		}
		return "";
	}

	/**
	 * This method executes rules for medication transformation.
	 * simple rule engine is used for executing rules.
	 * 
	 * @param  obj an instance of class "SemanticRuleEngine"
	 * @param 	tags output from the method "RegTagger"
	 * @param	section	current section name
	 * @return	 transformed tagging result
	 * @see  SemanticRuleEngine  
	*/
	private ArrayList<Quartet<String,String,Integer,Pair<Pair<String, Integer>, Integer> >> medicationTransformer2(ArrayList<Quartet<String,String,Integer,Pair<Pair<String,Integer>,Integer>>> tags,String section) throws ClassNotFoundException, RemoteException, IOException
	{
		
		Tag_rule_engine simple_engine = new Tag_rule_engine(tags, section);
		
		simple_engine.disambiguation();
		
		//System.out.println("\nafter disambiguation:");
		//simple_engine.printTagToken();
		simple_engine.transform_1();
		//System.out.println("\nafter transform1:");
		//simple_engine.printTagToken();
		simple_engine.transform_2();
		//System.out.println("\nafter transform2:");
		//simple_engine.printTagToken();
		return simple_engine.get_tag_result();
			
	}
	
	
	
	
	/**
	 * This method executes rules for medication transformation.
	 * Drools RuleEngine is used for executing rules.
	 * 
	 * @param  obj an instance of class "SemanticRuleEngine"
	 * @param 	tags output from the method "RegTagger"
	 * @param	section	current section name
	 * @return	 transformed tagging result
	 * @see  SemanticRuleEngine  
	*/
	private ArrayList<Quartet<String,String,Integer,Pair<Pair<String, Integer>, Integer> >> medicationTransformer(SemanticRuleEngine obj, ArrayList<Quartet<String,String,Integer,Pair<Pair<String,Integer>,Integer>>> tags,String section) throws ClassNotFoundException, RemoteException, IOException
	{
		//System.out.println("\n\n*****************************************");
		//System.out.println("** section **"+section);
		//for(int i = 0; i < tags.size(); i++)
		//	System.out.println("Transformation input "+i+"="+tags.get(i));
		
		int index = 0;
		// Iterate through each tags list
        while(index < tags.size())
        {
        	//System.out.println("index"+"--"+index);
        	String token = tags.get(index).getValue0();
        	String tag = tags.get(index).getValue1();
        	int off1 = tags.get(index).getValue2();
        	Pair<Pair<String,Integer>,Integer>  off2 = tags.get(index).getValue3();
        	
        	
        	Pattern r = Pattern.compile("-");
        	Matcher m = r.matcher(tag);
        	
        	//System.out.println(tag+"--"+token);
        	
        	/// ****************  NEED to separate transformation rules as they will be executed only if tag has "-" *************************//
        	if(m.find())
        	{	
        		//System.out.println("in if");
        		result_bean result = new result_bean();
        		final StatefulKnowledgeSession ksession= kbase.newStatefulKnowledgeSession();  
        	    ksession.setGlobal("result", result);
        	    //result.setFinal_tag(tag.split("-")[0]);
        	    result.setFinal_token(token);
        	    result.setDefault_tag(tag.split("-")[0]);
        	    
        		for(int i = 0; i < tags.size(); i++)
        		{
        			//int index,int pos,String term,String tag,int start, int end
        			String prevTag = ""; 
        			String prevTerm = "";
         	    	if(i==0)
         	    	{
         	    		prevTag = "TK";
         			}
         	    	else
         	    	{
         	    		prevTag = tags.get(i-1).getValue1();
         	    	}
        			ksession.insert(new tags(index,
        										i,
        										tags.get(i).getValue0(),
        										tags.get(i).getValue1(),
        										prevTag,
        										prevTerm,
        										tags.get(i).getValue2(),
        										tags.get(i).getValue3()));
        			
        			//System.out.println(index+"\t"+i+"\t"+tags.get(i).getValue0()+"\t"+tags.get(i).getValue1()+"\t"+tags.get(i).getValue2()+"\t"+tags.get(i).getValue3());
        			//System.out.println(i);
        		}
               
        		ksession.fireAllRules();
        		
        		tag = result.getFinal_tag();
                token = result.getFinal_token();
                //System.out.println("**  "+tag+"\t"+token);
                ksession.dispose();
        	}// End of if
        	else
        	{
        		if(tags.get(index).getValue0().equals("pt"))
        			if(section != null && section.equals("MED"))
        				tag = "RUT";
        			else
        				tag = "TK";
        	
        	}
        	//System.out.println("##  "+tag+"\t"+token);
        	
        	Quartet<String,String,Integer,Pair<Pair<String, Integer>, Integer> > temp = Quartet.with(token, tag, off1, off2);
        	temp.add(token, tag, off1, off2);
        	tags.set(index,temp);
        	
        	index = index + 1;
        	
      
        }// end of while
        
        //return tags;
        //for(int i = 0; i < tags.size(); i++)
   		 	//System.out.println("Diamsbi --"+i+"="+tags.get(i));
        //System.out.println("============== End of disambiguation rules =======================");
        
     
        ArrayList<Quartet<String,String,Integer,Pair<Pair<String, Integer>, Integer> >> dtags = new ArrayList<Quartet<String,String,Integer,Pair<Pair<String, Integer>, Integer> >>();
        
       
        
        index = 0;
        
        while(index < tags.size())
        {
        	final StatefulKnowledgeSession ksession2= kbase2.newStatefulKnowledgeSession();
        	String token = tags.get(index).getValue0();
        	String tag = tags.get(index).getValue1();
        	//System.out.println(index+" -- "+tag+" -- "+token);
        	int off1 = tags.get(index).getValue2();
        	//int final_index = index;
        	 		
        	// System.out.println("*tags "+tags.get(index));
        	//final StatefulKnowledgeSession ksession2= kbase2.newStatefulKnowledgeSession();
        	result_bean result = new result_bean();
        	ksession2.setGlobal("result", result);
     	    result.setFinal_tag(tag.split("-")[0]);
     	    result.setFinal_token(token);
     	    result.setFinal_index(index);
     	    //System.out.println(tags);
        	
     	    for(int i = index; i < tags.size(); i++)
     		{
     			//int index,int pos,String term,String tag,int start, int end
     	    	//System.out.println(index+"--"+i+"--"+tags.get(i).getValue0()+"--"+tags.get(i).getValue1()+"--"+tags.get(i).getValue2()+"--"+tags.get(i).getValue0());
     			String prevTag = ""; 
     			String prevTerm = "";
     	    	if(i==0)
     	    	{
     	    		prevTag = "TK";
     			}
     	    	else
     	    	{
     	    		prevTag = tags.get(i-1).getValue1();
     	    		prevTerm = tags.get(i-1).getValue0();
     	    	}
     	    	
     	    	ksession2.insert(new tags(index,
     										i,
     										tags.get(i).getValue0(),
     										tags.get(i).getValue1(),
     										prevTag,
     										prevTerm,
     										tags.get(i).getValue2(),
     										tags.get(i).getValue3()));
     		}
            
     		ksession2.fireAllRules();
     		
     		
            tag = result.getFinal_tag();
            index = result.getFinal_index();
        	token = result.getFinal_token();
        	//System.out.println("tag : "+result.getFinal_tag());
        	Pair<Pair<String, Integer>, Integer> off2 = tags.get(index).getValue3();
        	Quartet<String, String, Integer, Pair<Pair<String, Integer>, Integer> > temp = Quartet.with(token, tag, off1, off2);
        	temp.add(token, tag, off1, off2); 
        	dtags.add(temp);
        	ksession2.dispose();
            index = index + 1;
            
        }// while 
        
       // for(int i = 0; i < dtags.size(); i++)
        //	System.out.println("Transformation phase 2 ***"+dtags.get(i));
        
        ArrayList<Quartet<String,String,Integer,Pair<Pair<String, Integer>, Integer> >> ttags = new ArrayList<Quartet<String,String,Integer,Pair<Pair<String, Integer>, Integer> >>();
        //final StatefulKnowledgeSession ksession3= kbase3.newStatefulKnowledgeSession();
        index = 0;
        
        while(index < dtags.size())
        {
        	
        	String token = dtags.get(index).getValue0();
        	String tag = dtags.get(index).getValue1();
        	//System.out.println(index+"--"+tag+"--"+token);
        	int off1 = dtags.get(index).getValue2();
        	//int final_index = index;
        	 		
        	// System.out.println("*tags "+tags.get(index));
        	        	 
        	result_bean result = new result_bean();
   	       
        	final StatefulKnowledgeSession ksession3= kbase3.newStatefulKnowledgeSession();
        	
     	    ksession3.setGlobal("result", result);
     	    result.setFinal_tag(tag.split("-")[0]);
     	    result.setFinal_token(token);
     	    result.setFinal_index(index);
     	    //System.out.println("tag : "+result.getFinal_tag());
        	
     	    for(int i = index; i < dtags.size(); i++)
     		{
     			//int index,int pos,String term,String tag,int start, int end
     	    	//System.out.println(index+"--"+i+"--"+dtags.get(i).getValue0()+"--"+dtags.get(i).getValue1()+"--"+dtags.get(i).getValue2()+"--"+dtags.get(i).getValue0());
     	    	String prevTag = ""; 
     	    	String prevTerm = "";
     	    	if(i==0)
     	    	{
     	    		prevTag = "TK";
     	    		
     			}
     	    	else
     	    	{
     	    		prevTag = dtags.get(i-1).getValue1();
     	    		prevTerm = dtags.get(i-1).getValue0();
     	    	}
     	    	//if(tags.get(i).getValue0().equals("one"))
     	    	//	System.out.println(prevTag+"|");
     	    	ksession3.insert(new tags(index,
     										i,
     										dtags.get(i).getValue0(),
     										dtags.get(i).getValue1(),
     										prevTag,
     										prevTerm,
     										dtags.get(i).getValue2(),
     										dtags.get(i).getValue3()));
     		}
            
     		ksession3.fireAllRules();
     		
     		
            tag = result.getFinal_tag();
            index = result.getFinal_index();
        	token = result.getFinal_token();
        	//System.out.println("index : "+index+" tag :"+tag+" -- token :"+token);
        	 //System.out.println("token :"+token);
        	// if(tag != "TK")
         	//{
        	Pair<Pair<String, Integer>, Integer> off2 = dtags.get(index).getValue3();
        	Quartet<String, String, Integer, Pair<Pair<String, Integer>, Integer> > temp = Quartet.with(token, tag, off1, off2);
        	temp.add(token, tag, off1, off2); 
        	ttags.add(temp);
         	//}
            index = index + 1;
            ksession3.dispose();
       }
        
        //for(int i = 0; i < ttags.size(); i++)
			//System.out.println("Transformation phase 3 ***"+ttags.get(i));
        return ttags;
        
    }// method end
        

	/**
	 * This method is for  anti-context rules to remove false positives.
     * It will check single drug in a non-medication section for anti-context rules.
	 * 
	 * @param 	ttags output from the method "medicationTransformer"
	 * @param	section	current section name
	 * @return	 updated tagging result
	 * @see  SemanticRuleEngine  
	*/
    private ArrayList<Quartet<String,String,Integer,Pair<Pair<String, Integer>, Integer> >> medicationContext(SemanticRuleEngine obj,ArrayList<Quartet<String,String,Integer,Pair<Pair<String, Integer>, Integer> >> ttags, String section)
    {
    	//System.out.println("start context check:");
    	//System.out.println(ttags);
    	String[] antiContextarray = {"elevated", "lab", "clinic", "labs", "neg", "neg.", "negative", "pos", "pos.", "positive", "def", "deficiency", "teaching", "test", "labtest", "normalized", "gradient", "allergy", "allergic", "allergies", "laboratory", "admission labratory", "labs/studies", "studies", "study"};
    	String[] favorContextarray = {"DDF", "FREQ", "RUT", "on", "initiate", "initiates", "take", "taking", "took", "takes", "taken", "start", "starts", "started", "treatment", "treat", "treats", "treated", "regimen", "controlled", "continued", "continue", "given", "begin", "begins", "began", "discontinued", "discontiue", "dose"};
    	String[] restrictedFavorContextarray = {"DDF", "FREQ", "RUT"};
    	List<String> antiContext = Arrays.asList(antiContextarray);
    	List<String> favorContext = Arrays.asList(favorContextarray);
    	List<String> restrictedFavorContext = Arrays.asList(restrictedFavorContextarray);
    	
    	ArrayList<Quartet<String,String,Integer,Pair<Pair<String, Integer>, Integer> >> ctags = new ArrayList<Quartet<String,String,Integer,Pair<Pair<String, Integer>, Integer> >>();
    	int index = 0;
    	
    	while(index < ttags.size())
    	{                         
            String token = ttags.get(index).getValue0();
        	String tag = ttags.get(index).getValue1();
        	int off_left = ttags.get(index).getValue2();
        	Pair<Pair<String, Integer>, Integer>  off_right = ttags.get(index).getValue3();
        	
        	ArrayList lab_test = new ArrayList();
        	lab_test.add("sodium");
        	lab_test.add("potassium");
        	lab_test.add("na");
        	lab_test.add("k");
        	lab_test.add("ferritin");
        	lab_test.add("iron");
        	
        	if(lab_test.contains(token))
        	{
        		if((index + 2) < ttags.size())
        		{
        			
        			String regexp = "^[\\d\\.]+$";
        			Pattern r = Pattern.compile(regexp);
  		    	  	Matcher m1 = r.matcher(ttags.get(index + 1).getValue0());
  		    	  	Matcher m2 = r.matcher(ttags.get(index + 2).getValue0());
  		    	  	if (m1.find() || m2.find()) 
  		    	  	{
  		    	  		tag = "TK";
  		    	  	}
        			
        		}
        	}
        	
        	if((tag.equals("DIN") || tag.equals("DBN") || tag.equals("DPN")) && !section.equals("MED"))
        	{
        		int start, end;
        		// set window size as +/- 5, to get context list
        		int window = 5;
        		if ((index - window) >= 0)
                    start = index - window;
                else
                    start = 0;
        		if ((index + window) < ttags.size())
                    end = index + window + 1;
                else
                    end = ttags.size(); 
        		
        		//check again anti- and favor- Context
        		ArrayList<Quartet<String,String,Integer,Pair<Pair<String, Integer>, Integer> >> context = new ArrayList<Quartet<String,String,Integer,Pair<Pair<String, Integer>, Integer> >> ();
        		
        		for(int i = start; i < index; i++)
        			context.add(ttags.get(i));
        		
        		for(int i = index+1; i < end; i++)
        			context.add(ttags.get(i));
        		
        		int flagA = 0;
                int flagF = 0;
                int flagRF = 0;
               //System.out.println(favorContext);
                for(int i = 0; i < context.size(); i++)
                {
        			String cword = context.get(i).getValue0();
        			String ctag = context.get(i).getValue1();
        			//System.out.println("cword:"+cword+".");
        			//System.out.println("ctag:"+ctag+".");
        			
        			
        			
        			if(antiContext.contains(cword))
        				flagA = 1;
        			
        			    
        			if(favorContext.contains(cword) || favorContext.contains(ctag)){
        				//System.out.println("enter here");
        				flagF = 1;
        			}
        			
        			if(restrictedFavorContext.contains(ctag))
        				flagRF = 1;
        			
                }
                //System.out.println("flagF:"+flagF+"\n");
                //System.out.println("flagRF:"+flagRF+"\n");
                //System.out.println("flagA:"+flagA+"\n");
                //System.out.println("section"+section+"\n");
                //System.out.println("hello");
                if((section.equals("ALLERGY") || section.equals("FAMILY_HISTORY")) && flagRF == 0){
                	//System.out.println("enter 1");
                	tag = "TK";
                }
                else {
                	if(flagF == 0){
                		if(section.equals("LABS") || flagA == 1){
                			tag = "TK";
                			//System.out.println("enter 2");
                		}
                	}
                }
               
                
        	}
        	//if(tag.equals(arg0))
            if(!tag.equals("NUM"))
            {
	        	Quartet<String,String,Integer,Pair<Pair<String, Integer>, Integer> > temp = Quartet.with(token, tag, off_left, off_right);
	        	
	            temp.add(token, tag, off_left, off_right); 
	            ctags.add(temp);
	            
	            
            }
            index = index + 1;
           
    	}// while
    	//System.out.println(ctags);
    	return ctags;
    	
    }// method end
    
    
}

