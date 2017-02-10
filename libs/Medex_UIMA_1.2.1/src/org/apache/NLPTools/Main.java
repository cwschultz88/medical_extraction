/**
 * 
 */
package org.apache.NLPTools;

import java.io.File;
import java.util.Vector;

import org.apache.algorithms.SuffixArray;
import org.apache.medex.Lexicon;
import org.apache.NLPTools.Document;
/**
 * @author ywu4
 *
 */
public class Main implements Global{

	/**
	 * 
	 */
	public Main() {
		// TODO Auto-generated constructor stub
		
		
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String abbr_file = System.getProperty("user.dir") + File.separator + "resources" + File.separator + "lexicon.txt";
		
		String str="for information For your infor";
		try{
			Document doc=new Document(str,"test");
			Lexicon lex=new Lexicon(abbr_file);
			SuffixArray sa =new SuffixArray(doc,lex,'$',Global.SuffixArrayMode.ALL,Global.SuffixArrayCaseMode.NON_SENSITIVE);
			//sa.print_tree();
			//sa.traverse();
			String query="for";
			Vector<Integer> result=sa.SAsearch(query);
			
			//System.out.println("\n\nsearching : |"+query+"|, results:");
			/*
			for(int i=0;i<result.size();i++){
				System.out.println(""+i+" : "+result.get(i)+" "+doc.norm_str().substring(result.get(i)));
			}
			*/
		    query="infor";
			result=sa.SAsearch(query);
			
			//System.out.println("\n\nsearching : |"+query+"|, results:");
			for(int i=0;i<result.size();i++){
				System.out.println(""+i+" : "+result.get(i)+" "+doc.norm_str().substring(result.get(i)));
			}
		}
		catch (Exception e){
			System.err.println("Error: " + e.getMessage());	 
		}

	}

}
